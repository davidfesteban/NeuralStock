package dev.misei.einfachstonks.stonkservice.model;

import java.time.LocalDate;
import java.util.DoubleSummaryStatistics;
import java.util.List;

public class HistoryToCompositeUtil {
    public static Double calculate52WeekHigh(List<ETFHistory> records) {
        LocalDate oneYearAgo = LocalDate.now().minusWeeks(52);
        Double result = records.stream()
                .filter(record -> record.getDayPrecision().isAfter(oneYearAgo))
                .mapToDouble(ETFHistory::getPriceHigh)
                .max()
                .orElse(Double.NaN);

        if(result.isNaN()) {
            return records.stream()
                    .mapToDouble(ETFHistory::getPriceHigh)
                    .max()
                    .orElse(Double.NaN);
        }

        return result;
    }

    public static Double calculate52WeekLow(List<ETFHistory> records) {
        LocalDate oneYearAgo = LocalDate.now().minusWeeks(52);
        Double result = records.stream()
                .filter(record -> record.getDayPrecision().isAfter(oneYearAgo))
                .mapToDouble(ETFHistory::getPriceLow)
                .max()
                .orElse(Double.NaN);

        if(result.isNaN()) {
            return records.stream()
                    .mapToDouble(ETFHistory::getPriceLow)
                    .max()
                    .orElse(Double.NaN);
        }

        return result;
    }

    public static Double calculateAverageVolume(List<ETFHistory> records) {
        return records.stream()
                .mapToLong(ETFHistory::getVolume)
                .average()
                .orElse(Double.NaN); // Return NaN if no records
    }

    public static Double calculateETFVolatility(List<ETFHistory> records) {
        DoubleSummaryStatistics stats = records.stream()
                .mapToDouble(ETFHistory::getPriceClose)
                .summaryStatistics();
        double mean = stats.getAverage();
        double variance = records.stream()
                .mapToDouble(record -> Math.pow(record.getPriceClose() - mean, 2))
                .average()
                .orElse(Double.NaN);
        return Math.sqrt(variance);
    }

}
