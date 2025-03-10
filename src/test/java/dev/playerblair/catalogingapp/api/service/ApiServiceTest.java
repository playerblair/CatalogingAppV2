package dev.playerblair.catalogingapp.api.service;

import dev.playerblair.catalogingapp.api.wrapper.GenreWrapper;
import dev.playerblair.catalogingapp.api.wrapper.GetResponseWrapper;
import dev.playerblair.catalogingapp.api.wrapper.MangaWrapper;
import dev.playerblair.catalogingapp.api.wrapper.SearchResponseWrapper;
import dev.playerblair.catalogingapp.manga.model.Author;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ApiServiceTest {

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private UriBuilder uriBuilder;

    private ApiServiceImpl apiService;

    private MangaWrapper mangaWrapper1;
    private MangaWrapper mangaWrapper2;

    @BeforeEach
    public void setUp() {
        mangaWrapper1 = new MangaWrapper(
                1L,
                "Test Manga 1",
                "Manga",
                100,
                10,
                "Finished",
                List.of(new Author()),
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
                List.of(new Author()),
                List.of(new GenreWrapper("Romance")),
                "www.example.com"
        );

        when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        apiService = new ApiServiceImpl(webClientBuilder);
    }

    @Test
    public void givenTitle_whenSearchMangaIsCalled_returnSearchResults() {
        String query = "Test Manga";

        SearchResponseWrapper<MangaWrapper> searchResponse = new SearchResponseWrapper<>();
        searchResponse.setData(List.of(mangaWrapper1, mangaWrapper2));

        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(searchResponse));

        List<MangaWrapper> results = apiService.searchManga(query);

        assertThat(results).isNotNull();
        assertThat(results.size()).isEqualTo(2);
        assertThat(results.get(0).getTitle()).isEqualTo("Test Manga 1");
        assertThat(results.get(1).getTitle()).isEqualTo("Test Manga 2");

        verify(webClient).get();
        verify(requestHeadersSpec).retrieve();
    }

    @Test
    public void givenId_whenGetMangaIsCalled_returnMangaWrapper() {
        Long id = 1L;

        GetResponseWrapper<MangaWrapper> getResponse = new GetResponseWrapper<>();
        getResponse.setData(mangaWrapper1);

        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(getResponse));

        MangaWrapper mangaWrapper = apiService.getManga(id);

        assertThat(mangaWrapper).isNotNull();
        assertThat(mangaWrapper.getTitle()).isEqualTo("Test Manga 1");

        verify(webClient).get();
        verify(requestHeadersSpec).retrieve();
    }

}
