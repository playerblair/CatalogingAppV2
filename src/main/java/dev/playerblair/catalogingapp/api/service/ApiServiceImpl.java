package dev.playerblair.catalogingapp.api.service;

import dev.playerblair.catalogingapp.api.wrapper.GetResponseWrapper;
import dev.playerblair.catalogingapp.api.wrapper.MangaWrapper;
import dev.playerblair.catalogingapp.api.wrapper.SearchResponseWrapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class ApiServiceImpl implements ApiService {

    private final WebClient webClient;

    public ApiServiceImpl(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("https://api.jikan.moe/v4").build();
    }

    @Override
    public List<MangaWrapper> searchManga(String query) {
        SearchResponseWrapper<MangaWrapper> response = this.webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/manga")
                        .queryParam("q", query)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<SearchResponseWrapper<MangaWrapper>>() {})
                .block();
        return response.getData();
    }

    @Override
    public MangaWrapper getManga(Long id) {
        GetResponseWrapper<MangaWrapper> response = this.webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/manga/" + id)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<GetResponseWrapper<MangaWrapper>>() {})
                .block();
        return response.getData();
    }
}
