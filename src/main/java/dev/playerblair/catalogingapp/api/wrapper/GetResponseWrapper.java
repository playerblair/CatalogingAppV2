package dev.playerblair.catalogingapp.api.wrapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetResponseWrapper<T> {
    private T data;
}
