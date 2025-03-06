package dev.playerblair.catalogingapp.manga.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Manga {

    @Id
    private Long malId;
    private String title;
    private MangaType type;
    private int chapters;
    private int volumes;
    private MangaStatus status;
    private List<Author> authors;
    private List<MangaGenre> genres;
    private String url;

    private MangaProgress progress;
    private int chaptersRead;
    private int volumesRead;
    private int rating;

    private boolean digitalCollection;
    private boolean physicalCollection;
    private int volumesAvailable;
    private int volumesOwned;
    private List<Integer> volumesAcquired;
    private String volumesEdition;

}
