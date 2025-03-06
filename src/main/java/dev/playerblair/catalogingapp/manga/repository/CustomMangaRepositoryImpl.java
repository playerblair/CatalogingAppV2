package dev.playerblair.catalogingapp.manga.repository;

import dev.playerblair.catalogingapp.manga.dto.MangaFilter;
import dev.playerblair.catalogingapp.manga.model.Manga;
import dev.playerblair.catalogingapp.manga.model.MangaGenre;
import dev.playerblair.catalogingapp.manga.model.MangaProgress;
import dev.playerblair.catalogingapp.manga.model.MangaStatus;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;

public class CustomMangaRepositoryImpl implements CustomMangaRepository {

    private final MongoTemplate mongoTemplate;

    public CustomMangaRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Manga> findByDynamicCriteria(MangaFilter filter) {
        Query query = new Query();
        List<Criteria> criteria = new ArrayList<>();

        if (filter.getQuery() != null && !filter.getQuery().isEmpty()) {
            criteria.add(Criteria.where("title").regex(filter.getQuery(), "i"));
        }

        if (filter.getGenres() != null && !filter.getGenres().isEmpty()) {
            List<MangaGenre> genres = filter.getGenres().stream().map(MangaGenre::valueOf).toList();
            criteria.add(Criteria.where("genres").all(genres));
        }

        if (filter.getStatus() != null && !filter.getStatus().isEmpty()) {
            criteria.add(Criteria.where("status").is(MangaStatus.valueOf(filter.getStatus())));
        }

        if (filter.getAuthor() != null && !filter.getAuthor().isEmpty()) {
            criteria.add(Criteria.where("authors.name").regex(filter.getAuthor(), "i"));
        }

        if (filter.getProgress() != null && !filter.getProgress().isEmpty()) {
            criteria.add(Criteria.where("progress").is(MangaProgress.valueOf(filter.getProgress())));
        }

        if (filter.isDigitalCollection()) {
            criteria.add(Criteria.where("digitalCollection").is(true));
        };

        if (filter.isPhysicalCollection()) {
            criteria.add(Criteria.where("physicalCollection").is(true));
        };

        if (!criteria.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[0])));
        }

        return mongoTemplate.find(query, Manga.class);
    }
}
