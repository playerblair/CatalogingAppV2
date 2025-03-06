package dev.playerblair.catalogingapp.manga.repository;

import dev.playerblair.catalogingapp.manga.dto.MangaFilter;
import dev.playerblair.catalogingapp.manga.model.Manga;

import java.util.List;

public interface CustomMangaRepository {
    List<Manga> findByDynamicCriteria(MangaFilter filter);
}
