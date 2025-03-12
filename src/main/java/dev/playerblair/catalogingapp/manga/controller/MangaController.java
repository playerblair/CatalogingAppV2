package dev.playerblair.catalogingapp.manga.controller;

import dev.playerblair.catalogingapp.api.wrapper.MangaWrapper;
import dev.playerblair.catalogingapp.manga.dto.MangaCollectionUpdate;
import dev.playerblair.catalogingapp.manga.dto.MangaFilter;
import dev.playerblair.catalogingapp.manga.dto.MangaProgressUpdate;
import dev.playerblair.catalogingapp.manga.model.Manga;
import dev.playerblair.catalogingapp.manga.service.MangaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/manga")
public class MangaController {

    private final MangaService mangaService;

    public MangaController(MangaService mangaService) {
        this.mangaService = mangaService;
    }

    @GetMapping("/list")
    public ResponseEntity<List<Manga>> listManga() {
        List<Manga> manga = mangaService.listManga();
        return ResponseEntity.status(HttpStatus.OK).body(manga);
    }

    @GetMapping("/search")
    public ResponseEntity<List<MangaWrapper>> searchManga(@RequestParam String query) {
        List<MangaWrapper> manga = mangaService.searchManga(query);
        return ResponseEntity.status(HttpStatus.OK).body(manga);
    }

    @PostMapping("/add")
    public ResponseEntity<Manga> addManga(@RequestParam Long id) {
        Manga addedManga = mangaService.addManga(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedManga);
    }


    @DeleteMapping("/delete")
    public ResponseEntity<Manga> deleteManga(@RequestParam Long id) {
        Manga deletedManga = mangaService.deleteManga(id);
        return ResponseEntity.status(HttpStatus.OK).body(deletedManga);
    }

    @PatchMapping("/update-info")
    public ResponseEntity<Void> updateInfo() {
        mangaService.updateAllMangaInformation();
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/update-progress")
    public ResponseEntity<Manga> updateProgress(@RequestBody MangaProgressUpdate progressUpdate) {
        Manga updatedManga = mangaService.updateProgress(progressUpdate);
        return ResponseEntity.status(HttpStatus.OK).body(updatedManga);
    }

    @PatchMapping("/update-collection")
    public ResponseEntity<Manga> updateCollection(@RequestBody MangaCollectionUpdate collectionUpdate) {
        Manga updatedManga = mangaService.updateCollection(collectionUpdate);
        return ResponseEntity.status(HttpStatus.OK).body(updatedManga);
    }

    @PostMapping("/list/filter")
    public ResponseEntity<List<Manga>> filterManga(@RequestBody MangaFilter filter) {
        List<Manga> filteredManga = mangaService.filterManga(filter);
        return ResponseEntity.status(HttpStatus.OK).body(filteredManga);
    }
}
