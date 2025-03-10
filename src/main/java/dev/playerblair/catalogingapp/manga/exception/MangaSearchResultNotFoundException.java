package dev.playerblair.catalogingapp.manga.exception;

public class MangaSearchResultNotFoundException extends RuntimeException {
    public MangaSearchResultNotFoundException(Long id) {
        super("Manga with ID " + id + " not found in search results.");
    }
}
