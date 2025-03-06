package dev.playerblair.catalogingapp.manga.service;

import dev.playerblair.catalogingapp.api.service.ApiService;
import dev.playerblair.catalogingapp.api.wrapper.GenreWrapper;
import dev.playerblair.catalogingapp.api.wrapper.MangaWrapper;
import dev.playerblair.catalogingapp.manga.dto.MangaCollectionUpdate;
import dev.playerblair.catalogingapp.manga.dto.MangaFilter;
import dev.playerblair.catalogingapp.manga.dto.MangaProgressUpdate;
import dev.playerblair.catalogingapp.manga.model.*;
import dev.playerblair.catalogingapp.manga.repository.AuthorRepository;
import dev.playerblair.catalogingapp.manga.repository.MangaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MangaServiceTest {

    @Mock
    private MangaRepository mangaRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private ApiService apiService;

    @InjectMocks
    private MangaServiceImpl mangaService;

    @Mock
    private Map<Long, MangaWrapper> mockSearchResults;

    private Author author1;
    private Author author2;

    private Manga manga1;
    private Manga manga2;

    private MangaWrapper mangaWrapper1;
    private MangaWrapper mangaWrapper2;

    @BeforeEach
    public void setUp() {
        author1 = Author.builder()
                .malId(1L)
                .name("Test Author 1")
                .build();

        author2 = Author.builder()
                .malId(2L)
                .name("Test Author 2")
                .build();

        manga1 = Manga.builder()
                .malId(1L)
                .title("Test Manga 1")
                .type(MangaType.MANGA)
                .authors(List.of(author1))
                .genres(List.of(MangaGenre.ROMANCE, MangaGenre.ACTION))
                .status(MangaStatus.FINISHED)
                .build();

        manga2 = Manga.builder()
                .malId(2L)
                .title("Test Manga 2")
                .type(MangaType.MANGA)
                .authors(List.of(author2))
                .genres(List.of(MangaGenre.ROMANCE, MangaGenre.ACTION))
                .status(MangaStatus.FINISHED)
                .build();

        mangaWrapper1 = new MangaWrapper(
                1L,
                "Test Manga 1",
                "Manga",
                100,
                10,
                "Finished",
                List.of(new Author(1L, "Test Author 1", "www.example.com")),
                List.of(new GenreWrapper("Action")),
                "www.example.com"
        );

        mangaWrapper2 = new MangaWrapper(
                2L,
                "Test Manga 2",
                "Manga",
                100,
                10,
                "Finished",
                List.of(new Author(2L, "Test Author 2", "www.example.com")),
                List.of(new GenreWrapper("Action")),
                "www.example.com"
        );
    }

    @Test
    public void whenListMangaIsCalled_returnManga() {
        List<Manga> mangaList = List.of(manga1, manga2);

        when(mangaRepository.findAll()).thenReturn(mangaList);

        List<Manga> manga = mangaService.listManga();

        assertThat(manga).hasSize(2);
        assertThat(manga.get(0).getTitle()).isEqualTo("Test Manga 1");
        assertThat(manga.get(1).getTitle()).isEqualTo("Test Manga 2");
    }

    @Test
    public void givenQuery_whenSearchMangaIsCalled_returnSearchResults() {
        List<MangaWrapper> manga = List.of(mangaWrapper1, mangaWrapper2);

        String query = "Manga";

        given(apiService.searchManga(query)).willReturn(manga);

        List<MangaWrapper> results = mangaService.searchManga(query);

        assertThat(results).hasSize(2);
        assertThat(manga.get(0).getTitle()).isEqualTo("Test Manga 1");
        assertThat(manga.get(1).getTitle()).isEqualTo("Test Manga 2");
    }

    @Test
    public void givenMangaWrapperObject_whenGenerateMangaIsCalled_returnManga() {
        Manga manga = mangaService.generateManga(mangaWrapper1);
        assertThat(manga.getMalId()).isEqualTo(1L);
        assertThat(manga.getAuthors()).isNotEmpty();
        assertThat(manga.getGenres()).isNotEmpty();
    }

    @Test
    public void givenId_whenAddMangaIsCalled_saveManga() {
        Long id = 1L;

        when(mockSearchResults.get(id)).thenReturn(mangaWrapper1);
        ReflectionTestUtils.setField(mangaService, "currentSearchResults", mockSearchResults);

        mangaService.addManga(id);

        verify(authorRepository).save(argThat(author -> author.getName().equals("Test Author 1")));
        verify(mangaRepository).save(argThat(manga -> manga.getTitle().equals("Test Manga 1")));
    }

    @Test
    public void givenMangaId_whenDeleteMangaIsCalled_deleteManga() {
        mangaService.deleteManga(1L);
        verify(mangaRepository).deleteById(1L);
    }

    @Test
    public void whenUpdateAllMangaIsCalled_updateManga() {
        List<Manga> mangaList = List.of(manga1, manga2);

        given(mangaRepository.findAll()).willReturn(mangaList);
        given(apiService.getManga(manga1.getMalId())).willReturn(mangaWrapper1);
        given(apiService.getManga(manga2.getMalId())).willReturn(mangaWrapper2);

        mangaService.updateAllManga();

        verify(mangaRepository, times(2)).save(any(Manga.class));
    }

    @Test
    public void givenMangaProgressUpdate_whenUpdateProgressIsCalled_updateProgress() {
        MangaProgressUpdate progressUpdate = new MangaProgressUpdate(
                1L,
                "FINISHED",
                100,
                10,
                10
        );

        given(mangaRepository.findById(1L)).willReturn(Optional.of(manga1));

        mangaService.updateProgress(progressUpdate);

        verify(mangaRepository).save(argThat(m ->
                m.getMalId().equals(progressUpdate.getMalId()) &&
                m.getProgress().equals(MangaProgress.valueOf(progressUpdate.getProgress())) &&
                m.getChaptersRead() == progressUpdate.getChaptersRead() &&
                m.getVolumesRead() == progressUpdate.getVolumesRead()
        ));
    }

    @Test
    public void givenCollectionUpdate_whenUpdateCollectionIsCalled_returnFilterManga() {
        MangaCollectionUpdate collectionUpdate = new MangaCollectionUpdate(
                1L,
                true,
                true,
                3,
                1,
                List.of(1),
                "Paperback"
        );

        given(mangaRepository.findById(1L)).willReturn(Optional.of(manga1));

        mangaService.updateCollection(collectionUpdate);

        verify(mangaRepository).save(argThat(m ->
                m.getMalId().equals(collectionUpdate.getMalId()) &&
                m.isDigitalCollection() &&
                m.isPhysicalCollection() &&
                m.getVolumesOwned() == collectionUpdate.getVolumesOwned() &&
                m.getVolumesAvailable() == collectionUpdate.getVolumesAvailable()
        ));
    }

    @Test
    public void givenTitle_whenFilterByTitleIsCalled_returnFilteredManga() {
        MangaFilter filter = new MangaFilter();
        filter.setQuery("Test Manga 1");

        List<Manga> expectedManga = List.of(new Manga());

        when(mangaRepository.findByDynamicCriteria(filter)).thenReturn(expectedManga);

        List<Manga> result = mangaService.filterManga(filter);

        verify(mangaRepository).findByDynamicCriteria(any(MangaFilter.class));
        assertThat(result).isEqualTo(expectedManga);
    }
}
