package dev.stevenposterick.fileshare.api.data;

import java.time.LocalDateTime;
import java.util.function.Function;

public enum ExpirationDate {
    MINUTE(x-> x.plusMinutes(1)),
    HOUR(x-> x.plusHours(1)),
    DAY(x-> x.plusDays(1)),
    WEEK(x-> x.plusWeeks(1)),
    MONTH(x-> x.plusMonths(1));

    private final Function<LocalDateTime, LocalDateTime> converter;

    ExpirationDate(Function<LocalDateTime, LocalDateTime> converter) {
        this.converter = converter;
    }

    public Function<LocalDateTime, LocalDateTime> getExpirationFunction() {
        return converter;
    }
}
