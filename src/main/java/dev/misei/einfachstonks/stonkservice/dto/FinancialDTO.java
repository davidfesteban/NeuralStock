package dev.misei.einfachstonks.stonkservice.dto;

import lombok.With;
import org.springframework.lang.NonNull;

import java.time.LocalDate;

@With
public record FinancialDTO(LocalDate date, double open, double close, double high, double low,
                           long volume) implements Comparable<FinancialDTO> {
    @Override
    public int compareTo(@NonNull FinancialDTO o) {
        return date.compareTo(o.date);
    }

    public boolean isValid() {
        return date != null;
    }

    public boolean isComplete() {
        return open != 0 && close != 0 && high != 0 && low != 0;
    }
}
