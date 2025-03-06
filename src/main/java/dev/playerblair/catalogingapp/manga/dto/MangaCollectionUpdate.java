package dev.playerblair.catalogingapp.manga.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MangaCollectionUpdate {

    private Long malId;
    private boolean digitalCollection;
    private boolean physicalCollection;
    private int volumesAvailable;
    private int volumesOwned;
    private List<Integer> volumesAcquired;
    private String volumesEdition;
}
