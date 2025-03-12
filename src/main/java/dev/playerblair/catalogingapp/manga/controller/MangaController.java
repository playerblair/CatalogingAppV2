package dev.playerblair.catalogingapp.manga.controller;

import dev.playerblair.catalogingapp.api.wrapper.MangaWrapper;
import dev.playerblair.catalogingapp.manga.dto.MangaCollectionUpdate;
import dev.playerblair.catalogingapp.manga.dto.MangaFilter;
import dev.playerblair.catalogingapp.manga.dto.MangaProgressUpdate;
import dev.playerblair.catalogingapp.manga.model.Manga;
import dev.playerblair.catalogingapp.manga.service.MangaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(
            summary = "List stored manga.",
            description = "Returns an list of all the manga stored in the user's Manga collection.",
            tags = {"manga", "get"}
    )
    @ApiResponse(
            responseCode = "200",
            content = @Content(
                    schema = @Schema(implementation = Manga.class, type = "array"),
                    mediaType = "application/json"
            )
    )
    @GetMapping("/list")
    public ResponseEntity<List<Manga>> listManga() {
        List<Manga> manga = mangaService.listManga();
        return ResponseEntity.status(HttpStatus.OK).body(manga);
    }

    @Operation(
            summary = "Search external API for manga.",
            description = "Searches the Jikan API for manga based on the provided query.",
            tags = {"manga", "search", "get"}
    )
    @ApiResponse(
            responseCode = "200",
            content = @Content(
                    schema = @Schema(implementation = MangaWrapper.class, type = "array"),
                    mediaType = "application/json"
            )
    )
    @GetMapping("/search")
    public ResponseEntity<List<MangaWrapper>> searchManga(@RequestParam String query) {
        List<MangaWrapper> manga = mangaService.searchManga(query);
        return ResponseEntity.status(HttpStatus.OK).body(manga);
    }

    @Operation(
            summary = "Adds manga to collection.",
            description = "Adds manga from the search results to collection using provided ID.",
            tags = {"manga", "add", "post"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    content = @Content(
                            schema = @Schema(implementation = Manga.class),
                            mediaType = "application/json"
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Manga with provided ID not found in search results.",
                    content = @Content(
                            schema = @Schema(implementation = String.class),
                            mediaType = "application/json"
                    )
            )
    })
    @PostMapping("/add")
    public ResponseEntity<Manga> addManga(@RequestParam Long id) {
        Manga addedManga = mangaService.addManga(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedManga);
    }

    @Operation(
            summary = "Deletes manga from collection.",
            description = "Deletes selected manga from collection using provided ID",
            tags = {"manga", "delete"})
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            schema = @Schema(implementation = Manga.class),
                            mediaType = "application/json"
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Manga with the provided ID does not exist in collection.",
                    content = @Content(
                            schema = @Schema(implementation = String.class),
                            mediaType = "application/json"
                    )
            )
    })
    @DeleteMapping("/delete")
    public ResponseEntity<Manga> deleteManga(@RequestParam Long id) {
        Manga deletedManga = mangaService.deleteManga(id);
        return ResponseEntity.status(HttpStatus.OK).body(deletedManga);
    }

    @Operation(
            summary = "Updates all manga metadata.",
            description = "Updates the metadata of all manga stored in collection using Jikan API",
            tags = {"manga", "update", "patch"})
    @ApiResponse(responseCode = "200")
    @PatchMapping("/update-info")
    public ResponseEntity<Void> updateInfo() {
        mangaService.updateAllMangaInformation();
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Update a manga's progress.",
            description = "Updates the user's progress of selected manga.",
            tags = {"manga", "update", "patch"})
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            schema = @Schema(implementation = Manga.class),
                            mediaType = "application/json"
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Manga with the provided ID does not exist in collection.",
                    content = @Content(
                            schema = @Schema(implementation = String.class),
                            mediaType = "application/json"
                    )
            )
    })
    @PatchMapping("/update-progress")
    public ResponseEntity<Manga> updateProgress(@RequestBody MangaProgressUpdate progressUpdate) {
        Manga updatedManga = mangaService.updateProgress(progressUpdate);
        return ResponseEntity.status(HttpStatus.OK).body(updatedManga);
    }

    @Operation(
            summary = "Update a manga's collection information.",
            description = "Updates the information about a user's collection for selected manga.",
            tags = {"manga", "update", "patch"})
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            schema = @Schema(implementation = Manga.class),
                            mediaType = "application/json"
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Manga with the provided ID does not exist in collection.",
                    content = @Content(
                            schema = @Schema(implementation = String.class),
                            mediaType = "application/json"
                    )
            )
    })
    @PatchMapping("/update-collection")
    public ResponseEntity<Manga> updateCollection(@RequestBody MangaCollectionUpdate collectionUpdate) {
        Manga updatedManga = mangaService.updateCollection(collectionUpdate);
        return ResponseEntity.status(HttpStatus.OK).body(updatedManga);
    }

    @Operation(
            summary = "Filters stored manga.",
            description = "Returns a filter list of manga based on provided criteria.",
            tags = {"manga", "filter", "post"})
    @ApiResponse(
            responseCode = "200",
            content = @Content(
                    schema = @Schema(implementation = Manga.class, type = "array"),
                    mediaType = "application/json"
            )
    )
    @PostMapping("/list/filter")
    public ResponseEntity<List<Manga>> filterManga(@RequestBody MangaFilter filter) {
        List<Manga> filteredManga = mangaService.filterManga(filter);
        return ResponseEntity.status(HttpStatus.OK).body(filteredManga);
    }
}
