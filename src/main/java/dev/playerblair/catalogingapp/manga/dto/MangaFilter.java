package dev.playerblair.catalogingapp.manga.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MangaFilter {

    private String query;
    private List<String> genres;
    private String status;
    private String author;
    private String progress;

    @JsonProperty("digital_collection")
    private boolean digitalCollection;

    @JsonProperty("physical_collection")
    private boolean physicalCollection;

}
