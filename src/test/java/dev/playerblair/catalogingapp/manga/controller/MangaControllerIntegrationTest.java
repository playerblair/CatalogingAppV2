package dev.playerblair.catalogingapp.manga.controller;

import dev.playerblair.catalogingapp.api.service.ApiService;
import dev.playerblair.catalogingapp.manga.dto.MangaCollectionUpdate;
import dev.playerblair.catalogingapp.manga.dto.MangaFilter;
import dev.playerblair.catalogingapp.manga.dto.MangaProgressUpdate;
import dev.playerblair.catalogingapp.manga.model.*;
import dev.playerblair.catalogingapp.manga.repository.AuthorRepository;
import dev.playerblair.catalogingapp.manga.repository.MangaRepository;
import dev.playerblair.catalogingapp.manga.service.MangaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class MangaControllerIntegrationTest {

    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @DynamicPropertySource
    public static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MangaRepository mangaRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private MangaService mangaService;

    @Autowired
    private ApiService apiService;

    private Author testAuthor;
    private Manga testManga1;
    private Manga testManga2;

    @BeforeEach
    public void setUp() {
        mangaRepository.deleteAll();
        authorRepository.deleteAll();

        testAuthor = Author.builder()
                .malId(1L)
                .name("Test Author 1")
                .build();

        authorRepository.save(testAuthor);

        testManga1 = Manga.builder()
                .malId(10L)
                .title("Test Manga 1")
                .type(MangaType.MANGA)
                .authors(List.of(testAuthor))
                .genres(List.of(MangaGenre.ROMANCE, MangaGenre.ACTION))
                .status(MangaStatus.FINISHED)
                .build();

        testManga2 = Manga.builder()
                .malId(20L)
                .title("Test Manga 2")
                .type(MangaType.MANGA)
                .authors(List.of(testAuthor))
                .genres(List.of(MangaGenre.ROMANCE))
                .status(MangaStatus.PUBLISHING)
                .build();

        mangaRepository.saveAll(List.of(testManga1, testManga2));
    }

    @Test
    public void whenListMangaIsCalled_returnManga() throws Exception {
        mockMvc.perform(get("/manga/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").exists())
                .andExpect(jsonPath("$[1].title").exists())
                .andExpect(jsonPath("$").isArray());

    }

    @Test
    public void givenQuery_whenSearchMangaIsCalled_returnSearchResults() throws Exception {
        mockMvc.perform(get("/manga/search")
                .param("query", "Monster"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void givenQuery_whenAddMangaIsCalled_saveManga() throws Exception {
        mockMvc.perform(get("/manga/search")
                .param("query", "Monster"));

        mockMvc.perform(post("/manga/add")
                .param("id", "1"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Monster"));

        assertThat(mangaRepository.findAll()).hasSize(3);
    }

    @Test
    public void givenProgressUpdate_whenUpdateProgressIsCalled_updateManga() throws Exception {
        MangaProgressUpdate progressUpdate = new MangaProgressUpdate(
                10L,
                "FINISHED",
                100,
                10,
                10
        );

        String requestJson = objectMapper.writeValueAsString(progressUpdate);

        mockMvc.perform(patch("/manga/update-progress")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.progress").value("FINISHED"))
                .andExpect(jsonPath("$.chaptersRead").value(100))
                .andExpect(jsonPath("$.volumesRead").value(10))
                .andExpect(jsonPath("$.rating").value(10));

        assertThat(mangaRepository.findById(10L).get().getProgress()).isEqualTo(MangaProgress.FINISHED);
    }

    @Test
    public void givenCollectionUpdate_whenUpdateCollectionIsCalled_updateManga() throws Exception {
        MangaCollectionUpdate collectionUpdate = new MangaCollectionUpdate(
                10L,
                true,
                true,
                3,
                1,
                List.of(1),
                "Paperback"
        );

        String requestJson = objectMapper.writeValueAsString(collectionUpdate);

        mockMvc.perform(patch("/manga/update-collection")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.digitalCollection").value(true))
                .andExpect(jsonPath("$.physicalCollection").value(true))
                .andExpect(jsonPath("$.volumesAvailable").value(3))
                .andExpect(jsonPath("$.volumesOwned").value(1))
                .andExpect(jsonPath("$.volumesAcquired[0]").value(1))
                .andExpect(jsonPath("$.volumesEdition").value("Paperback"));

        assertThat(mangaRepository.findById(10L).get().isDigitalCollection()).isTrue();
    }

    @Test
    public void givenFilter_whenFilterMangaIsCalled_returnFilteredManga() throws Exception {
        MangaFilter filter = new MangaFilter();
        filter.setQuery("Manga 1");

        String requestJson = objectMapper.writeValueAsString(filter);

        mockMvc.perform(post("/manga/list/filter")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.malId == 10)].title").value("Test Manga 1"));
    }
}
