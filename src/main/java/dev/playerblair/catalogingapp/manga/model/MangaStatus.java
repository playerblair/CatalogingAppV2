package dev.playerblair.catalogingapp.manga.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public enum MangaStatus {
    FINISHED("Finished"),
    PUBLISHING("Publishing"),
    ON_HIATUS("On Hiatus"),
    DISCONTINUED("Discontinued");

    private final String code;

    MangaStatus(String code) {
        this.code = code;
    }

    public static MangaStatus fromCode(String code) {
        for (MangaStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown code: " + code);
    }
}
