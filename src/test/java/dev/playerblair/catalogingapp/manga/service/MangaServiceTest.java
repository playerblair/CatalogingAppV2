package dev.playerblair.catalogingapp.manga.service;

import dev.playerblair.catalogingapp.api.service.ApiService;
import dev.playerblair.catalogingapp.api.wrapper.GenreWrapper;
import dev.playerblair.catalogingapp.api.wrapper.MangaWrapper;
import dev.playerblair.catalogingapp.manga.dto.MangaCollectionUpdate;
import dev.playerblair.catalogingapp.manga.dto.MangaFilter;
import dev.playerblair.catalogingapp.manga.dto.MangaProgressUpdate;
import dev.playerblair.catalogingapp.manga.exception.MangaNotFoundException;
import dev.playerblair.catalogingapp.manga.exception.MangaSearchResultNotFoundException;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
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

    private Manga manga1;
    private Manga manga2;

    private MangaWrapper mangaWrapper1;
    private MangaWrapper mangaWrapper2;

    @BeforeEach
    public void setUp() {
        Author author1 = Author.builder()
                .malId(1L)
                .name("Test Author 1")
                .build();

        Author author2 = Author.builder()
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

        when(apiService.searchManga(query)).thenReturn(manga);

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
    public void givenValidId_whenAddMangaIsCalled_saveManga() {
        Long id = 1L;

        when(mockSearchResults.get(id)).thenReturn(mangaWrapper1);
        ReflectionTestUtils.setField(mangaService, "currentSearchResults", mockSearchResults);

        mangaService.addManga(id);

        verify(authorRepository).save(argThat(author -> author.getName().equals("Test Author 1")));
        verify(mangaRepository).save(argThat(manga -> manga.getTitle().equals("Test Manga 1")));
    }

    @Test
    public void givenInvalidId_whenAddMangaIsCalled_throwException() {
        Long id = 1L;

        when(mockSearchResults.get(id)).thenReturn(null);
        ReflectionTestUtils.setField(mangaService, "currentSearchResults", mockSearchResults);

        assertThatThrownBy(() -> mangaService.addManga(id))
                .isInstanceOf(MangaSearchResultNotFoundException.class)
                .hasMessage("Manga with ID 1 not found in search results.");
    }

    @Test
    public void givenValidId_whenDeleteMangaIsCalled_deleteManga() {
        Long id = 1L;

        when(mangaRepository.findById(id)).thenReturn(Optional.of(manga1));

        mangaService.deleteManga(id);

        verify(mangaRepository).delete(manga1);
    }

    @Test
    public void givenInvalidId_whenDeleteMangaIsCalled_throwException() {
        Long id = 1L;

        when(mangaRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mangaService.deleteManga(id))
                .isInstanceOf(MangaNotFoundException.class)
                .hasMessage("Manga with ID 1 not found.");
    }

    @Test
    public void whenUpdateAllMangaInformationIsCalled_updateManga() {
        List<Manga> mangaList = List.of(manga1, manga2);

        when(mangaRepository.findAll()).thenReturn(mangaList);
        when(apiService.getManga(manga1.getMalId())).thenReturn(mangaWrapper1);
        when(apiService.getManga(manga2.getMalId())).thenReturn(mangaWrapper2);

        mangaService.updateAllMangaInformation();

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

        when(mangaRepository.findById(1L)).thenReturn(Optional.of(manga1));

        mangaService.updateProgress(progressUpdate);

        verify(mangaRepository).save(argThat(m ->
                m.getMalId().equals(progressUpdate.getMalId()) &&
                m.getProgress().equals(MangaProgress.valueOf(progressUpdate.getProgress())) &&
                m.getChaptersRead() == progressUpdate.getChaptersRead() &&
                m.getVolumesRead() == progressUpdate.getVolumesRead()
        ));
    }

    @Test
    public void givenProgressUpdateWithInvalidId_whenUpdateCollectionIsCalled_throwException() {
        MangaProgressUpdate progressUpdate = new MangaProgressUpdate(
                1L,
                "FINISHED",
                100,
                10,
                10
        );

        when(mangaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mangaService.updateProgress(progressUpdate))
                .isInstanceOf(MangaNotFoundException.class)
                .hasMessage("Manga with ID 1 not found.");
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

        when(mangaRepository.findById(1L)).thenReturn(Optional.of(manga1));

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
    public void givenCollectionUpdateWithInvalidId_whenUpdateCollectionIsCalled_throwException() {
        MangaCollectionUpdate collectionUpdate = new MangaCollectionUpdate(
                1L,
                true,
                true,
                3,
                1,
                List.of(1),
                "Paperback"
        );

        when(mangaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mangaService.updateCollection(collectionUpdate))
                .isInstanceOf(MangaNotFoundException.class)
                .hasMessage("Manga with ID 1 not found.");
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
