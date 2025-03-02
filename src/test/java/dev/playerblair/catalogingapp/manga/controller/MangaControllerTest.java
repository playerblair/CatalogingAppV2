package dev.playerblair.catalogingapp.manga.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.playerblair.catalogingapp.api.service.ApiService;
import dev.playerblair.catalogingapp.api.wrapper.GenreWrapper;
import dev.playerblair.catalogingapp.api.wrapper.MangaWrapper;
import dev.playerblair.catalogingapp.manga.dto.MangaCollectionUpdate;
import dev.playerblair.catalogingapp.manga.dto.MangaProgressUpdate;
import dev.playerblair.catalogingapp.manga.model.*;
import dev.playerblair.catalogingapp.manga.repository.AuthorRepository;
import dev.playerblair.catalogingapp.manga.repository.MangaRepository;
import dev.playerblair.catalogingapp.manga.service.MangaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    private List<Manga> mangaList;

    @BeforeEach
    public void setUp() {
        Manga manga1 = Manga.builder()
                .malId(1L)
                .title("Manga1")
                .type(MangaType.MANGA)
                .authors(List.of(new Author()))
                .genres(List.of(MangaGenre.ROMANCE, MangaGenre.ACTION))
                .status(MangaStatus.FINISHED)
                .build();

        Manga manga2 = Manga.builder()
                .malId(2L)
                .title("Manga2")
                .type(MangaType.MANGA)
                .authors(List.of(new Author()))
                .genres(List.of(MangaGenre.ROMANCE, MangaGenre.ACTION))
                .status(MangaStatus.FINISHED)
                .build();

        mangaList = List.of(manga1, manga2);
    }

    @Test
    public void whenListMangaIsCalled_returnManga() throws Exception {
        given(mangaService.listManga()).willReturn(mangaList);

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

        given(mangaService.searchManga(query)).willReturn(manga);

        mockMvc.perform(get("/manga/search")
                .param("query", query))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));
    }

    @Test
    public void givenId_whenAddMangaIsCalled_saveManga() throws Exception {
        Long id = 1L;

        doNothing().when(mangaService).addManga(id);

        mockMvc.perform(post("/manga/add")
                .param("id", String.valueOf(id)))
                .andExpect(status().isOk());
    }

    @Test
    public void givenProgressUpdate_whenUpdateProgressIsCalled_updateProgress() throws Exception {
        MangaProgressUpdate progressUpdate = new MangaProgressUpdate(
                1L,
                "FINISHED",
                100,
                10,
                10
        );

        String requestJson = objectMapper.writeValueAsString(progressUpdate);

        mockMvc.perform(put("/manga/update-progress")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk());
    }

    @Test
    public void givenCollectionUpdate_whenUpdateCollectionIsCalled_updateCollection() throws Exception {
        MangaCollectionUpdate collectionUpdate = new MangaCollectionUpdate(
                1L,
                true,
                1,
                3
        );

        String requestJson = objectMapper.writeValueAsString(collectionUpdate);

        mockMvc.perform(put("/manga/update-collection")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk());
    }

}
