package dev.playerblair.catalogingapp.manga.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MangaCollectionUpdate {

    private Long malId;
    private boolean digitalCopy;
    private int volumesOwned;
    private int volumesAvailable;
}
