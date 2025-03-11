package dev.playerblair.catalogingapp.manga.exception;

public class MangaNotFoundException extends RuntimeException {
    public MangaNotFoundException(Long id) {
        super("Manga with ID " + id + " not found.");
    }
}
