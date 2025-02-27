package dev.playerblair.catalogingapp.manga.repository;

import dev.playerblair.catalogingapp.manga.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

@ExtendWith(SpringExtension.class)
@DataMongoTest
public class MangaRepositoryTest {

    @Autowired
    private MangaRepository mangaRepository;

    @Autowired
    private AuthorRepository authorRepository;

    private List<Manga> mangaList;
    private List<Author> authors;

    @BeforeEach
    public void setUp() {
        Author author1 = Author.builder()
                .malId(1L)
                .name("Author1")
                .build();

        Author author2 = Author.builder()
                .malId(2L)
                .name("Author2")
                .build();

        authors = List.of(author1, author2);
        authors.forEach(author -> authorRepository.save(author));

        Manga manga1 = Manga.builder()
                .malId(1L)
                .title("Manga1")
                .type(MangaType.MANGA)
                .authors(List.of(author1))
                .genres(List.of(MangaGenre.ROMANCE, MangaGenre.ACTION))
                .status(MangaStatus.FINISHED)
                .progress(MangaProgress.FINISHED)
                .digitalCopy(true)
                .volumesOwned(2)
                .volumes(2)
                .build();

        Manga manga2 = Manga.builder()
                .malId(2L)
                .title("Manga1: Rebirth")
                .type(MangaType.MANGA)
                .authors(List.of(author1))
                .genres(List.of(MangaGenre.ROMANCE, MangaGenre.ACTION))
                .status(MangaStatus.DISCONTINUED)
                .progress(MangaProgress.FINISHED)
                .digitalCopy(true)
                .volumesOwned(0)
                .volumes(2)
                .build();

        Manga manga3 = Manga.builder()
                .malId(3L)
                .title("Manga3")
                .type(MangaType.MANHUA)
                .authors(List.of(author2))
                .genres(List.of(MangaGenre.ACTION))
                .status(MangaStatus.PUBLISHING)
                .progress(MangaProgress.READING)
                .digitalCopy(true)
                .volumesOwned(0)
                .volumes(0)
                .build();

        mangaList = List.of(manga1, manga2, manga3);
        mangaList.forEach(manga -> mangaRepository.save(manga));
    }

    @Test
    public void shouldNotBeEmpty() {
        assertThat(authorRepository.findAll()).isNotEmpty();
        assertThat(mangaRepository.findAll()).isNotEmpty();
    }

    @Test
    public void givenValidId_whenDeleteIsCalled_removeManga() {
        mangaRepository.deleteById(2L);
        assertThat(mangaRepository.findAll()).hasSize(2);
        assertThat(mangaRepository.findById(2L)).isEmpty();
    }

    @Test
    public void givenInvalidId_whenDeleteIsCalled_nothingIsRemoved() {
        mangaRepository.deleteById(4L);
        assertThat(mangaRepository.findAll()).hasSize(3);
    }

    @Test
    public void givenValidId_whenMangaInfoIsUpdated_sizeRemainsTheSame() {
        Manga manga = mangaRepository.findById(3L).get();
        manga.setStatus(MangaStatus.FINISHED);
        mangaRepository.save(manga);
        assertThat(mangaRepository.findAll()).hasSize(3);
        assertThat(mangaRepository.findById(3L).get().getStatus()).isEqualTo(MangaStatus.FINISHED);
    }

    @Test
    public void givenValidId_whenMangaProgressIsUpdated_sizeRemainsTheSame() {
        Manga manga = mangaRepository.findById(3L).get();
        manga.setProgress(MangaProgress.DROPPED);
        mangaRepository.save(manga);
        assertThat(mangaRepository.findAll()).hasSize(3);
        assertThat(mangaRepository.findById(3L).get().getProgress()).isEqualTo(MangaProgress.DROPPED);
    }

    @Test
    public void givenValidId_whenMangaCollectionIsUpdated_sizeRemainsTheSame() {
        Manga manga = mangaRepository.findById(2L).get();
        manga.setVolumesOwned(1);
        mangaRepository.save(manga);
        assertThat(mangaRepository.findAll()).hasSize(3);
        assertThat(mangaRepository.findById(2L).get().getVolumesOwned()).isEqualTo(1);
    }

    @Test
    public void whenFindAllIsCalled_returnAllManga() {
        assertThat(mangaRepository.findAll()).hasSize(3);
    }

    @Test
    public void givenTitle_whenFindByTitleIsCalled_returnFilteredManga() {
        assertThat(mangaRepository.findByTitleLike("Manga1")).hasSize(2);
    }

    @Test
    public void givenGenres_whenFindByGenresORIsCalled_returnFilteredManga() {
        assertThat(mangaRepository.findByGenresOR(List.of(MangaGenre.ROMANCE, MangaGenre.ACTION)))
                .hasSize(3);
    }

    @Test
    public void givenGenres_whenFindByGenresANDIsCalled_returnFilteredManga() {
        assertThat(mangaRepository.findByGenresAND(List.of(MangaGenre.ROMANCE, MangaGenre.ACTION)))
                .hasSize(2);
    }

    @Test
    public void givenExistingAuthor_whenFindByAuthor_returnFilteredManga() {
        assertThat(mangaRepository.findByAuthor("Author1")).hasSize(2);
    }

    @Test
    public void givenNonExistingAuthor_whenFindByAuthorIsCalled_returnNothing() {
        assertThat(mangaRepository.findByAuthor("Donkey")).isEmpty();
    }

    @Test
    public void givenStatus_whenFindByStatusIsCalled_returnFilterManga() {
        assertThat(mangaRepository.findByStatus(MangaStatus.FINISHED)).hasSize(1);
    }

    @Test
    public void givenNoMangaWithStatus_whenFindByStatusIsCalled_returnFilterManga() {
        assertThat(mangaRepository.findByStatus(MangaStatus.ON_HIATUS)).isEmpty();
    }

    @Test
    public void givenProgress_whenFindByProgressIsCalled_returnFilterManga() {
        assertThat(mangaRepository.findByProgress(MangaProgress.FINISHED)).hasSize(2);
    }

    @Test
    public void givenNoMangaWithProgress_whenFindByProgressIsCalled_returnFilterManga() {
        assertThat(mangaRepository.findByProgress(MangaProgress.DROPPED)).isEmpty();
    }
}
