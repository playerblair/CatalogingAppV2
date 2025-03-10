package dev.playerblair.catalogingapp.manga.service;

import dev.playerblair.catalogingapp.api.service.ApiService;
import dev.playerblair.catalogingapp.api.wrapper.MangaWrapper;
import dev.playerblair.catalogingapp.manga.dto.MangaCollectionUpdate;
import dev.playerblair.catalogingapp.manga.dto.MangaFilter;
import dev.playerblair.catalogingapp.manga.dto.MangaProgressUpdate;
import dev.playerblair.catalogingapp.manga.exception.MangaNotFoundException;
import dev.playerblair.catalogingapp.manga.exception.MangaSearchResultNotFoundException;
import dev.playerblair.catalogingapp.manga.model.*;
import dev.playerblair.catalogingapp.manga.repository.AuthorRepository;
import dev.playerblair.catalogingapp.manga.repository.MangaRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MangaServiceImpl implements MangaService{

    private final MangaRepository mangaRepository;
    private final AuthorRepository authorRepository;

    private final ApiService apiService;

    private final Map<Long, MangaWrapper> currentSearchResults = new HashMap<>();

    public MangaServiceImpl(MangaRepository mangaRepository, AuthorRepository authorRepository, ApiService apiService) {
        this.mangaRepository = mangaRepository;
        this.authorRepository = authorRepository;
        this.apiService = apiService;
    }

    @Override
    public List<Manga> listManga() {
        return mangaRepository.findAll();
    }

    @Override
    public List<MangaWrapper> searchManga(String query) {
        List<MangaWrapper> results = apiService.searchManga(query);
        storeSearchResults(results);
        return results;
    }

    @Override
    public Manga addManga(Long id) {
        MangaWrapper mangaWrapper = currentSearchResults.get(id);
        if (mangaWrapper == null) {
            throw new MangaSearchResultNotFoundException(id);
        }
        Manga manga = generateManga(currentSearchResults.get(id));
        manga.getAuthors().forEach(authorRepository::save);
        return mangaRepository.save(manga);
    }

    @Override
    public Manga deleteManga(Long id) {
        Optional<Manga> optionalManga = mangaRepository.findById(id);
        if (optionalManga.isPresent()) {
            Manga manga = optionalManga.get();
            mangaRepository.delete(manga);
            return manga;
        }
       throw new MangaNotFoundException(id);
    }

    @Override
    public void updateAllMangaInformation() {
        List<Manga> mangaList = mangaRepository.findAll();
        mangaList.forEach(manga -> {
            MangaWrapper updatedManga = apiService.getManga(manga.getMalId());
            manga.setChapters(updatedManga.getChapters());
            manga.setVolumes(updatedManga.getVolumes());
            manga.setStatus(MangaStatus.fromCode(updatedManga.getStatus()));
            mangaRepository.save(manga);
        });
    }

    @Override
    public Manga updateProgress(MangaProgressUpdate progressUpdate) {
        Optional<Manga> optionalManga = mangaRepository.findById(progressUpdate.getMalId());
        if (optionalManga.isPresent()) {
            Manga manga = optionalManga.get();
            manga.setProgress(MangaProgress.valueOf(progressUpdate.getProgress()));
            manga.setChaptersRead(progressUpdate.getChaptersRead());
            manga.setVolumesRead(progressUpdate.getVolumesRead());
            manga.setRating(progressUpdate.getRating());
            return mangaRepository.save(manga);
        }
        throw new MangaNotFoundException(progressUpdate.getMalId());
    }

    @Override
    public Manga updateCollection(MangaCollectionUpdate collectionUpdate) {
        Optional<Manga> optionalManga = mangaRepository.findById(collectionUpdate.getMalId());
        if (optionalManga.isPresent()) {
            Manga manga = optionalManga.get();
            manga.setDigitalCollection(collectionUpdate.isDigitalCollection());
            manga.setPhysicalCollection(collectionUpdate.isPhysicalCollection());
            manga.setVolumesAvailable(collectionUpdate.getVolumesAvailable());
            manga.setVolumesOwned(collectionUpdate.getVolumesOwned());
            manga.setVolumesAcquired(collectionUpdate.getVolumesAcquired());
            manga.setVolumesEdition(collectionUpdate.getVolumesEdition());
            return mangaRepository.save(manga);
        }
        throw new MangaNotFoundException(collectionUpdate.getMalId());
    }

    @Override
    public List<Manga> filterManga(MangaFilter filter) {
        return mangaRepository.findByDynamicCriteria(filter);
    }

    public Manga generateManga(MangaWrapper mangaWrapper) {
        List<MangaGenre> genres = mangaWrapper.getGenres().stream()
                .map(genre -> MangaGenre.fromCode(genre.getName()))
                .toList();
        return Manga.builder()
                .malId(mangaWrapper.getMalId())
                .title(mangaWrapper.getTitle())
                .type(MangaType.fromCode(mangaWrapper.getType()))
                .chapters(mangaWrapper.getChapters())
                .volumes(mangaWrapper.getVolumes())
                .status(MangaStatus.fromCode(mangaWrapper.getStatus()))
                .authors(mangaWrapper.getAuthors())
                .genres(genres)
                .url(mangaWrapper.getUrl())
                .build();
    }

    public void storeSearchResults(List<MangaWrapper> results) {
        currentSearchResults.clear();

        for (MangaWrapper manga: results) {
            currentSearchResults.put(manga.getMalId(), manga);
        }
    }
}
