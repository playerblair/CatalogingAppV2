package dev.playerblair.catalogingapp.manga.repository;

import dev.playerblair.catalogingapp.manga.model.Author;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

@DataMongoTest
@Testcontainers
public class AuthorRepositoryTest {

    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @DynamicPropertySource
    public static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private AuthorRepository authorRepository;

    private List<Author> authors;

    @BeforeEach
    public void setUp() {
        authorRepository.deleteAll();

        Author author1 = Author.builder()
                .malId(1L)
                .name("Author1")
                .build();

        Author author2 = Author.builder()
                .malId(2L)
                .name("Author2")
                .build();

        authors = List.of(author1, author2);
        authors.forEach(author -> authorRepository.save(author));
    }

    @Test
    public void shouldNotBeEmpty() {
        assertThat(authorRepository.findAll()).isNotEmpty();
    }

    @Test
    public void givenValidId_whenDeleteIsCalled_removeAuthor() {
        authorRepository.deleteById(1L);
        assertThat(authorRepository.findAll()).hasSize(1);
        assertThat(authorRepository.findById(1L)).isEmpty();
    }

    @Test
    public void givenInvalidId_whenDeleteIsCalled_nothingIsRemoved() {
        authorRepository.deleteById(3L);
        assertThat(authorRepository.findAll()).hasSize(2);
    }
}
