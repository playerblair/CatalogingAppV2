package dev.playerblair.catalogingapp.manga.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public enum MangaGenre {
    ACTION("Action"),
    ADVENTURE("Adventure"),
    AVANT_GARDE("Avant Garde"),
    AWARD_WINNING("Award Winning"),
    BOYS_LOVE("Boys Love"),
    COMEDY("Comedy"),
    DRAMA("Drama"),
    FANTASY("Fantasy"),
    GIRLS_LOVE("Girls Love"),
    GOURMET("Gourmet"),
    HORROR("Horro"),
    MYSTERY("Mystery"),
    ROMANCE("Romance"),
    SCI_FI("Sci-Fi"),
    SLICE_OF_LIFE("Slice of Life"),
    SPORTS("Sports"),
    SUPERNATURAL("Supernatual"),
    SUSPENSE("Suspense");

    private final String code;

    MangaGenre(String code) {
        this.code = code;
    }

    public static MangaGenre fromCode(String code) {
        for (MangaGenre type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown code: " + code);
    }
}
