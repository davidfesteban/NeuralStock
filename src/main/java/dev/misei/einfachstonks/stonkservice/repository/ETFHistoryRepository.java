package dev.misei.einfachstonks.stonkservice.repository;

import dev.misei.einfachstonks.stonkservice.model.ETFHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ETFHistoryRepository extends MongoRepository<ETFHistory, String> {
    long deleteByHistoryId(UUID historyId);
    long deleteByInternalNameIdAndDayPrecision(UUID internalNameId, LocalDate dayPrecision);
    ETFHistory findByInternalNameIdAndDayPrecision(UUID internalNameId, LocalDate dayPrecision);

    List<ETFHistory> findByInternalNameId(UUID internalNameId);

    List<ETFHistory> deleteByInternalNameIdAndDayPrecisionBetween(UUID internalNameId, LocalDate dayPrecisionStart, LocalDate dayPrecisionEnd);

    List<ETFHistory> findByInternalNameIdAndDayPrecisionLessThanEqual(UUID internalNameId, LocalDate dayPrecision);
}
