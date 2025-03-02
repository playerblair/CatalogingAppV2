package dev.playerblair.catalogingapp.manga.service;

import dev.playerblair.catalogingapp.api.service.ApiService;
import dev.playerblair.catalogingapp.api.wrapper.MangaWrapper;
import dev.playerblair.catalogingapp.manga.dto.MangaCollectionUpdate;
import dev.playerblair.catalogingapp.manga.dto.MangaProgressUpdate;
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

    private Map<Long, MangaWrapper> currentSearchResults = new HashMap<>();

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
    public void addManga(Long id) {
        Manga manga = generateManga(currentSearchResults.get(id));
        manga.getAuthors().forEach(authorRepository::save);
        mangaRepository.save(manga);
    }

    @Override
    public void deleteManga(Long id) {
        mangaRepository.deleteById(id);
    }

    @Override
    public void updateAllManga() {
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
    public void updateProgress(MangaProgressUpdate progressUpdate) {
        Optional<Manga> optionalManga = mangaRepository.findById(progressUpdate.getMalId());
        if (optionalManga.isPresent()) {
            Manga manga = optionalManga.get();
            manga.setProgress(MangaProgress.valueOf(progressUpdate.getProgress()));
            manga.setChaptersRead(progressUpdate.getChaptersRead());
            manga.setVolumesRead(progressUpdate.getVolumesRead());
            mangaRepository.save(manga);
        }
    }

    @Override
    public void updateCollection(MangaCollectionUpdate collectionUpdate) {
        Optional<Manga> optionalManga = mangaRepository.findById(collectionUpdate.getMalId());
        if (optionalManga.isPresent()) {
            Manga manga = optionalManga.get();
            manga.setDigitalCopy(collectionUpdate.isDigitalCopy());
            manga.setVolumesOwned(collectionUpdate.getVolumesOwned());
            manga.setVolumesAvailable(collectionUpdate.getVolumesAvailable());
            mangaRepository.save(manga);
        }
    }

    @Override
    public List<Manga> filterByTitle(String title) {
        return mangaRepository.findByTitleLike(title);
    }

    @Override
    public List<Manga> filterByGenresOR(List<String> genres) {
        List<MangaGenre> genresList = genres.stream()
                .map(MangaGenre::fromCode)
                .toList();
        return mangaRepository.findByGenresOR(genresList);
    }

    @Override
    public List<Manga> filterByGenresAND(List<String> genres) {
        List<MangaGenre> genresList = genres.stream()
                .map(MangaGenre::fromCode)
                .toList();
        return mangaRepository.findByGenresAND(genresList);
    }

    @Override
    public List<Manga> filterByAuthor(String author) {
        return mangaRepository.findByAuthor(author);
    }

    @Override
    public List<Manga> filterByStatus(String status) {
        return mangaRepository.findByStatus(MangaStatus.fromCode(status));
    }

    @Override
    public List<Manga> filterByProgress(String progress) {
        return mangaRepository.findByProgress(MangaProgress.valueOf(progress));
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
            System.out.println(manga);
        }
    }
}
