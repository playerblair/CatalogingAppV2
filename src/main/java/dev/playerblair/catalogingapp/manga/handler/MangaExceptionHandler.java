package dev.playerblair.catalogingapp.manga.handler;

import dev.playerblair.catalogingapp.manga.exception.MangaNotFoundException;
import dev.playerblair.catalogingapp.manga.exception.MangaSearchResultNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "dev.playerblair.catalogingapp.manga")
public class MangaExceptionHandler {

    @ExceptionHandler(MangaSearchResultNotFoundException.class)
    public ResponseEntity<String> handlerMangaSearchResultNotFoundException(MangaSearchResultNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler(MangaNotFoundException.class)
    public ResponseEntity<String> handlerMangaNotFoundException(MangaNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }
}
