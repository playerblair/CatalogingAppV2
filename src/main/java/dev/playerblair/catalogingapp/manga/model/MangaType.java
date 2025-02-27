package dev.playerblair.catalogingapp.manga.model;

import lombok.Getter;

@Getter
public enum MangaType {
    MANGA("Manga"),
    MANHWA("Manhwa"),
    MANHUA("Manhua"),
    DOUJINSHI("Doujinshi"),
    ONE_SHOT("One-shot");

    private final String code;

    MangaType(String code) {
        this.code = code;
    }

    public static MangaType fromCode(String code) {
        for (MangaType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown code: " + code);
    }
}
