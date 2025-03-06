package dev.playerblair.catalogingapp.manga.repository;

import dev.playerblair.catalogingapp.manga.dto.MangaFilter;
import dev.playerblair.catalogingapp.manga.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

@DataMongoTest
@Testcontainers
public class MangaRepositoryTest {

    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @DynamicPropertySource
    public static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private MangaRepository mangaRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @BeforeEach
    public void setUp() {
        authorRepository.deleteAll();
        mangaRepository.deleteAll();

        Author author1 = Author.builder()
                .malId(1L)
                .name("Test Author 1")
                .build();

        Author author2 = Author.builder()
                .malId(2L)
                .name("Test Author 2")
                .build();

        List<Author> authors = List.of(author1, author2);
        authors.forEach(author -> authorRepository.save(author));

        Manga manga1 = Manga.builder()
                .malId(1L)
                .title("Test Manga 1")
                .type(MangaType.MANGA)
                .authors(List.of(author1))
                .genres(List.of(MangaGenre.ROMANCE, MangaGenre.ACTION))
                .status(MangaStatus.FINISHED)
                .progress(MangaProgress.FINISHED)
                .digitalCollection(true)
                .volumesOwned(2)
                .volumes(2)
                .build();

        Manga manga2 = Manga.builder()
                .malId(2L)
                .title("Test Manga 1: Rebirth")
                .type(MangaType.MANGA)
                .authors(List.of(author1))
                .genres(List.of(MangaGenre.ROMANCE, MangaGenre.ACTION))
                .status(MangaStatus.DISCONTINUED)
                .progress(MangaProgress.FINISHED)
                .digitalCollection(true)
                .volumesOwned(0)
                .volumes(2)
                .build();

        Manga manga3 = Manga.builder()
                .malId(3L)
                .title("Test Manga 2")
                .type(MangaType.MANHUA)
                .authors(List.of(author2))
                .genres(List.of(MangaGenre.ACTION))
                .status(MangaStatus.PUBLISHING)
                .progress(MangaProgress.READING)
                .digitalCollection(true)
                .volumesOwned(0)
                .volumes(0)
                .build();

        List<Manga> mangaList = List.of(manga1, manga2, manga3);
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
    public void givenTitle_whenFindByDynamicCriteriaIsCalled_returnFilteredManga() {
        MangaFilter filter = new MangaFilter();
        filter.setQuery("Test Manga 1");
        assertThat(mangaRepository.findByDynamicCriteria(filter)).hasSize(2);
    }

    @Test
    public void givenGenres_whenFindByDynamicCriteriaIsCalled_returnFilteredManga() {
        MangaFilter filter = new MangaFilter();
        filter.setGenres(List.of("ROMANCE", "ACTION"));
        assertThat(mangaRepository.findByDynamicCriteria(filter)).hasSize(2);
    }

    @Test
    public void givenExistingAuthor_whenFindByDynamicCriteriaIsCalled_returnFilteredManga() {
        MangaFilter filter = new MangaFilter();
        filter.setAuthor("Test Author 1");
        assertThat(mangaRepository.findByDynamicCriteria(filter)).hasSize(2);
    }

    @Test
    public void givenNonExistingAuthor_whenFindByDynamicCriteriaIsCalled_returnNothing() {
        MangaFilter filter = new MangaFilter();
        filter.setAuthor("Donkey");
        assertThat(mangaRepository.findByDynamicCriteria(filter)).isEmpty();
    }

    @Test
    public void givenStatus_whenFindByDynamicCriteriaIsCalled_returnFilterManga() {
        MangaFilter filter = new MangaFilter();
        filter.setStatus("FINISHED");
        assertThat(mangaRepository.findByDynamicCriteria(filter)).hasSize(1);
    }

    @Test
    public void givenNoMangaWithStatus_whenFindByDynamicCriteriaIsCalled_returnFilterManga() {
        MangaFilter filter = new MangaFilter();
        filter.setStatus("ON_HIATUS");
        assertThat(mangaRepository.findByDynamicCriteria(filter)).isEmpty();
    }

    @Test
    public void givenProgress_whenFindByDynamicCriteriaIsCalled_returnFilterManga() {
        MangaFilter filter = new MangaFilter();
        filter.setProgress("FINISHED");
        assertThat(mangaRepository.findByDynamicCriteria(filter)).hasSize(2);
    }

    @Test
    public void givenNoMangaWithProgress_whenFindByDynamicCriteriaIsCalled_returnFilterManga() {
        MangaFilter filter = new MangaFilter();
        filter.setProgress("DROPPED");
        assertThat(mangaRepository.findByDynamicCriteria(filter)).isEmpty();
    }
}
