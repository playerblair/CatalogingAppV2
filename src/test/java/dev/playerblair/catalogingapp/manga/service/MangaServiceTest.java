package dev.playerblair.catalogingapp.manga.service;

import dev.playerblair.catalogingapp.api.wrapper.GenreWrapper;
import dev.playerblair.catalogingapp.api.wrapper.MangaWrapper;
import dev.playerblair.catalogingapp.manga.dto.MangaCollectionUpdate;
import dev.playerblair.catalogingapp.manga.dto.MangaProgressUpdate;
import dev.playerblair.catalogingapp.manga.model.*;
import dev.playerblair.catalogingapp.manga.repository.AuthorRepository;
import dev.playerblair.catalogingapp.manga.repository.MangaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MangaServiceTest {

    @Mock
    private MangaRepository mangaRepository;

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private MangaServiceImpl mangaService;

    @Test
    public void givenMangaWrapperObject_whenGenerateMangaIsCalled_returnManga() {
        MangaWrapper mangaWrapper = new MangaWrapper(
                1L,
                "Example",
                "Manga",
                100,
                10,
                "Finished",
                List.of(new Author()),
                List.of(new GenreWrapper("Action")),
                "www.example.com"
        );
        Manga manga = mangaService.generateManga(mangaWrapper);
        assertThat(manga.getMalId()).isEqualTo(1L);
        assertThat(manga.getAuthors()).isNotEmpty();
        assertThat(manga.getGenres()).isNotEmpty();
    }

    @Test
    public void givenMangaWrapperObject_whenSaveMangaIsCalled_saveManga() {
        MangaWrapper mangaWrapper = new MangaWrapper(
                1L,
                "Example",
                "Manga",
                100,
                10,
                "Finished",
                List.of(new Author(1L, "Author", "www.example.com")),
                List.of(new GenreWrapper("Action")),
                "www.example.com"
        );

        mangaService.saveManga(mangaWrapper);

        verify(authorRepository).save(argThat(author -> author.getName().equals("Author")));
        verify(mangaRepository).save(argThat(manga -> manga.getTitle().equals("Example")));
    }

    @Test
    public void givenManga_whenSaveMangaIsCalled_saveManga() {
        Manga mangaToSave = Manga.builder()
                .malId(1L)
                .title("Example")
                .authors(List.of(new Author(1L, "Author", "www.example.com")))
                .build();

        mangaService.saveManga(mangaToSave);

        verify(authorRepository).save(argThat(author -> author.getName().equals("Author")));
        verify(mangaRepository).save(argThat(manga -> manga.getTitle().equals("Example")));
    }

    @Test
    public void givenMangaId_whenDeleteMangaIsCalled_deleteManga() {
        mangaService.deleteManga(1L);
        verify(mangaRepository).deleteById(1L);
    }

    //updateAllMangaTest

    @Test
    public void givenMangaProgressUpdate_whenUpdateProgressIsCalled_updateProgress() {
        MangaProgressUpdate progressUpdate = new MangaProgressUpdate(
                1L,
                "FINISHED",
                100,
                10
        );

        Manga manga = Manga.builder()
                .malId(1L)
                .title("Manga1")
                .type(MangaType.MANGA)
                .authors(List.of(new Author()))
                .genres(List.of(MangaGenre.ROMANCE, MangaGenre.ACTION))
                .status(MangaStatus.FINISHED)
                .build();

        given(mangaRepository.findById(1L)).willReturn(Optional.of(manga));

        mangaService.updateProgress(progressUpdate);

        verify(mangaRepository).save(argThat(m ->
                m.getMalId().equals(progressUpdate.getMalId()) &&
                m.getProgress().equals(MangaProgress.valueOf(progressUpdate.getProgress())) &&
                m.getChaptersRead() == progressUpdate.getChaptersRead() &&
                m.getVolumesRead() == progressUpdate.getVolumesRead()
        ));
    }

    @Test
    public void givenCollectionUpdate_whenFilterByGenresAND_returnFilterManga() {
        MangaCollectionUpdate collectionUpdate = new MangaCollectionUpdate(
                1L,
                true,
                1,
                3
        );

        Manga manga = Manga.builder()
                .malId(1L)
                .title("Manga1")
                .type(MangaType.MANGA)
                .authors(List.of(new Author()))
                .genres(List.of(MangaGenre.ROMANCE, MangaGenre.ACTION))
                .status(MangaStatus.FINISHED)
                .build();

        given(mangaRepository.findById(1L)).willReturn(Optional.of(manga));

        mangaService.updateCollection(collectionUpdate);

        verify(mangaRepository).save(argThat(m ->
                m.getMalId().equals(collectionUpdate.getMalId()) &&
                m.isDigitalCopy() &&
                m.getVolumesOwned() == collectionUpdate.getVolumesOwned() &&
                m.getVolumesAvailable() == collectionUpdate.getVolumesAvailable()
        ));
    }

    @Test
    public void givenTitle_whenFilterByTitleIsCalled_returnFilteredManga() {
        mangaService.filterByTitle("Title");
        verify(mangaRepository).findByTitleLike("Title");
    }

    @Test
    public void givenGenres_whenFilterByGenresORIsCalled_returnFilteredManga() {
        mangaService.filterByGenresOR(List.of("Action", "Romance", "Slice of Life"));
        verify(mangaRepository).findByGenresOR(List.of(MangaGenre.ACTION, MangaGenre.ROMANCE, MangaGenre.SLICE_OF_LIFE));
    }

    @Test
    public void givenGenres_whenFilterByGenresANDIsCalled_returnFilteredManga() {
        mangaService.filterByGenresAND(List.of("Action", "Romance", "Slice of Life"));
        verify(mangaRepository).findByGenresAND(List.of(MangaGenre.ACTION, MangaGenre.ROMANCE, MangaGenre.SLICE_OF_LIFE));
    }

    @Test
    public void givenAuthor_whenFilterByAuthorIsCalled_returnFilteredManga() {
        mangaService.filterByAuthor("Author");
        verify(mangaRepository).findByAuthor("Author");
    }

    @Test
    public void givenStatus_whenFilterByStatusIsCalled_returnFilteredManga() {
        mangaService.filterByStatus("Finished");
        verify(mangaRepository).findByStatus(MangaStatus.FINISHED);
    }

    @Test
    public void giveProgress_whenFilterByProgressIsCalled_returnFilteredManga() {
        mangaService.filterByProgress("FINISHED");
        verify(mangaRepository).findByProgress(MangaProgress.FINISHED);
    }
}
