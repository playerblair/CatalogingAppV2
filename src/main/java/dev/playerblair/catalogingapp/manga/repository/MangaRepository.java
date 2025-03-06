package dev.playerblair.catalogingapp.manga.repository;

import dev.playerblair.catalogingapp.manga.model.Manga;
import dev.playerblair.catalogingapp.manga.model.MangaGenre;
import dev.playerblair.catalogingapp.manga.model.MangaProgress;
import dev.playerblair.catalogingapp.manga.model.MangaStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface MangaRepository extends MongoRepository<Manga, Long>, CustomMangaRepository {
}
