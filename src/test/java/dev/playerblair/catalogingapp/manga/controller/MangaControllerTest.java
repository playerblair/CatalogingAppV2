package dev.playerblair.catalogingapp.manga.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.playerblair.catalogingapp.api.service.ApiService;
import dev.playerblair.catalogingapp.api.wrapper.GenreWrapper;
import dev.playerblair.catalogingapp.api.wrapper.MangaWrapper;
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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MangaController.class)
@AutoConfigureMockMvc
public class MangaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    public AuthorRepository authorRepository;

    @MockitoBean
    private MangaRepository mangaRepository;

    @MockitoBean
    public ApiService apiService;

    @MockitoBean
    public MangaService mangaService;

    private Manga manga1;
    private Manga manga2;

    @BeforeEach
    public void setUp() {
        manga1 = Manga.builder()
                .malId(1L)
                .title("Manga1")
                .type(MangaType.MANGA)
                .authors(List.of(new Author()))
                .genres(List.of(MangaGenre.ROMANCE, MangaGenre.ACTION))
                .status(MangaStatus.FINISHED)
                .build();

        manga2 = Manga.builder()
                .malId(2L)
                .title("Manga2")
                .type(MangaType.MANGA)
                .authors(List.of(new Author()))
                .genres(List.of(MangaGenre.ROMANCE, MangaGenre.ACTION))
                .status(MangaStatus.FINISHED)
                .build();
    }

    @Test
    public void whenListMangaIsCalled_returnManga() throws Exception {
        List<Manga> mangaList = List.of(manga1, manga2);
        when(mangaService.listManga()).thenReturn(mangaList);

        String jsonResponse = objectMapper.writeValueAsString(mangaList);

        mockMvc.perform(get("/manga/list"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));
    }

    @Test
    public void givenQuery_whenSearchMangaIsCalled_returnSearchResults() throws Exception {
        MangaWrapper mangaWrapper1 = new MangaWrapper(
                1L,
                "Manga1",
                "Manga",
                100,
                10,
                "Finished",
                List.of(new Author(1L, "Author", "www.example.com")),
                List.of(new GenreWrapper("Action")),
                "www.example.com"
        );

        MangaWrapper mangaWrapper2 = new MangaWrapper(
                2L,
                "Manga2",
                "Manga",
                20,
                2,
                "Finished",
                List.of(new Author(2L, "Author", "www.example.com")),
                List.of(new GenreWrapper("Action")),
                "www.example.com"
        );

        List<MangaWrapper> manga = List.of(mangaWrapper1, mangaWrapper2);

        String query = "Manga";
        String jsonResponse = objectMapper.writeValueAsString(manga);

        when(mangaService.searchManga(query)).thenReturn(manga);

        mockMvc.perform(get("/manga/search")
                .param("query", query))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));
    }

    @Test
    public void givenId_whenAddMangaIsCalled_saveManga() throws Exception {
        Long id = 1L;

        when((mangaService).addManga(id)).thenReturn(manga1);

        String jsonResponse = objectMapper.writeValueAsString(manga1);

        mockMvc.perform(post("/manga/add")
                .param("id", String.valueOf(id)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(status().isCreated())
                .andExpect(content().json(jsonResponse));
    }

    // TODO add update info

    @Test
    public void givenProgressUpdate_whenUpdateProgressIsCalled_updateProgress() throws Exception {
        MangaProgressUpdate progressUpdate = new MangaProgressUpdate(
                1L,
                "FINISHED",
                100,
                10,
                10
        );

        manga1.setProgress(MangaProgress.valueOf(progressUpdate.getProgress()));
        manga1.setChaptersRead(progressUpdate.getChaptersRead());
        manga1.setVolumesRead(progressUpdate.getVolumesRead());
        manga1.setRating(progressUpdate.getRating());

        String requestJson = objectMapper.writeValueAsString(progressUpdate);
        String jsonResponse = objectMapper.writeValueAsString(manga1);

        when(mangaService.updateProgress(progressUpdate)).thenReturn(manga1);

        mockMvc.perform(patch("/manga/update-progress")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));
    }

    @Test
    public void givenCollectionUpdate_whenUpdateCollectionIsCalled_updateCollection() throws Exception {
        MangaCollectionUpdate collectionUpdate = new MangaCollectionUpdate(
                1L,
                true,
                true,
                3,
                1,
                List.of(1),
                "Paperback"
        );

        manga1.setDigitalCollection(collectionUpdate.isDigitalCollection());
        manga1.setPhysicalCollection(collectionUpdate.isPhysicalCollection());
        manga1.setVolumesAvailable(collectionUpdate.getVolumesAvailable());
        manga1.setVolumesOwned(collectionUpdate.getVolumesOwned());
        manga1.setVolumesAcquired(collectionUpdate.getVolumesAcquired());
        manga1.setVolumesEdition(collectionUpdate.getVolumesEdition());

        String requestJson = objectMapper.writeValueAsString(collectionUpdate);
        String jsonResponse = objectMapper.writeValueAsString(manga1);

        when(mangaService.updateCollection(collectionUpdate)).thenReturn(manga1);

        mockMvc.perform(patch("/manga/update-collection")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));
    }

    @Test
    public void givenFilter_whenFilterMangaIsCalled_returnFilterManga() throws Exception {
        MangaFilter filter = new MangaFilter();
        filter.setQuery("Manga1");

        when(mangaService.filterManga(filter)).thenReturn(List.of(manga1));

        String requestJson = objectMapper.writeValueAsString(filter);

        String jsonResponse = objectMapper.writeValueAsString(List.of(manga1));

        mockMvc.perform(post("/manga/list/filter")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));
    }

}
