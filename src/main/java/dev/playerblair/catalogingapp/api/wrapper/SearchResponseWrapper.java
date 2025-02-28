package dev.playerblair.catalogingapp.api.wrapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponseWrapper<T> {
    private Pagination pagination;
    private List<T> data;
}
