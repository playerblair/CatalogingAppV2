package dev.playerblair.catalogingapp.manga.repository;

import dev.playerblair.catalogingapp.manga.model.Manga;
import dev.playerblair.catalogingapp.manga.model.MangaGenre;
import dev.playerblair.catalogingapp.manga.model.MangaProgress;
import dev.playerblair.catalogingapp.manga.model.MangaStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface MangaRepository extends MongoRepository<Manga, Long> {
    List<Manga> findByTitleLike(String title);

    @Query("{'genres': {$in: ?0}}")
    List<Manga> findByGenresOR(List<MangaGenre> genres);

    @Query("{'genres': {$all: ?0}}")
    List<Manga> findByGenresAND(List<MangaGenre> genres);

    @Query("{ 'authors.name': { '$regex': ?0, '$options': 'i' } }")
    List<Manga> findByAuthor(String author);

    List<Manga> findByStatus(MangaStatus status);

    List<Manga> findByProgress(MangaProgress progress);
}
