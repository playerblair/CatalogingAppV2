package dev.playerblair.catalogingapp.manga.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MangaProgressUpdate {

    private Long malId;
    private String progress;
    private int chaptersRead;
    private int volumesRead;
}
