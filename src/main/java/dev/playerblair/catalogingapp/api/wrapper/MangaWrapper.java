package dev.playerblair.catalogingapp.api.wrapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.playerblair.catalogingapp.manga.model.Author;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MangaWrapper {

    @JsonProperty("mal_id")
    private Long malId;
    private String title;
    private String type;
    private int chapters;
    private int volumes;
    private String status;
    private List<Author> authors;
    private List<GenreWrapper> genres;
    private String url;
}
