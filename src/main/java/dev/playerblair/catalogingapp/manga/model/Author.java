package dev.playerblair.catalogingapp.manga.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Author {

    @Id
    @JsonProperty(value = "mal_id", access = JsonProperty.Access.WRITE_ONLY)
    private Long malId;
    private String name;
    private String url;
}
