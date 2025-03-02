package dev.playerblair.catalogingapp.manga.service;

import dev.playerblair.catalogingapp.api.wrapper.MangaWrapper;
import dev.playerblair.catalogingapp.manga.dto.MangaCollectionUpdate;
import dev.playerblair.catalogingapp.manga.dto.MangaProgressUpdate;
import dev.playerblair.catalogingapp.manga.model.Manga;

import java.util.List;

public interface MangaService {

    List<Manga> listManga();

    List<MangaWrapper> searchManga(String query);

    void addManga(Long id);

    void deleteManga(Long id);

    void updateAllManga();

    void updateProgress(MangaProgressUpdate progressUpdate);

    void updateCollection(MangaCollectionUpdate collectionUpdate);

    List<Manga> filterByTitle(String title);

    List<Manga> filterByGenresOR(List<String> genres);

    List<Manga> filterByGenresAND(List<String> genres);

    List<Manga> filterByAuthor(String author);

    List<Manga> filterByStatus(String status);

    List<Manga> filterByProgress(String progress);
}
