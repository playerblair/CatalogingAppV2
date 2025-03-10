package dev.playerblair.catalogingapp.manga.service;

import dev.playerblair.catalogingapp.api.wrapper.MangaWrapper;
import dev.playerblair.catalogingapp.manga.dto.MangaCollectionUpdate;
import dev.playerblair.catalogingapp.manga.dto.MangaFilter;
import dev.playerblair.catalogingapp.manga.dto.MangaProgressUpdate;
import dev.playerblair.catalogingapp.manga.model.Manga;

import java.util.List;

public interface MangaService {

    List<Manga> listManga();

    List<MangaWrapper> searchManga(String query);

    Manga addManga(Long id);

    Manga deleteManga(Long id);

    void updateAllMangaInformation();

    Manga updateProgress(MangaProgressUpdate progressUpdate);

    Manga updateCollection(MangaCollectionUpdate collectionUpdate);

    List<Manga> filterManga(MangaFilter filter);
}
