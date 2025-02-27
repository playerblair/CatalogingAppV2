package dev.playerblair.catalogingapp.manga.repository;

import dev.playerblair.catalogingapp.manga.model.Author;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuthorRepository extends MongoRepository<Author, Long> {
}
