package dev.misei.einfachstonks.stonkservice.dto;

import java.time.LocalDate;

public record FinancialDTO(LocalDate date, double open, double close, double high, double low, long volume) {
}
