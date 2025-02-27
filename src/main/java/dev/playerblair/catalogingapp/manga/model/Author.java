package dev.playerblair.catalogingapp.manga.model;

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
    private Long malId;
    private String name;
    private String url;
}
