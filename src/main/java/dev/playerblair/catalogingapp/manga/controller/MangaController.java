package dev.playerblair.catalogingapp.manga.controller;

import dev.playerblair.catalogingapp.api.wrapper.MangaWrapper;
import dev.playerblair.catalogingapp.manga.dto.MangaCollectionUpdate;
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
    public ResponseEntity<Void> addManga(@RequestParam Long id) {
        mangaService.addManga(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update-progress")
    public ResponseEntity<Void> updateProgress(@RequestBody MangaProgressUpdate progressUpdate) {
        mangaService.updateProgress(progressUpdate);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update-collection")
    public ResponseEntity<Void> updateCollection(@RequestBody MangaCollectionUpdate collectionUpdate) {
        mangaService.updateCollection(collectionUpdate);
        return ResponseEntity.ok().build();
    }
}
