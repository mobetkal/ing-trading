package pl.ing.trading.dto;

import java.util.List;

public record ResultListResponse<T>(
        List<T> results
) {

}
