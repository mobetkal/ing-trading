package pl.ing.trading.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OrderStatus {
    FILLED("Filled"),
    SUBMITTED("Submitted"),
    EXPIRED("Expired");

    @JsonValue
    private final String displayName;
}
