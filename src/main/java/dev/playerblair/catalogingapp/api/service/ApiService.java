package dev.playerblair.catalogingapp.api.service;

import dev.playerblair.catalogingapp.api.wrapper.MangaWrapper;

import java.util.List;

public interface ApiService {
    List<MangaWrapper> searchManga(String query);

    MangaWrapper getManga(Long id);
}
