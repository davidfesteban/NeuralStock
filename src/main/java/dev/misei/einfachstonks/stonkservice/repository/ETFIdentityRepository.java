package dev.misei.einfachstonks.stonkservice.repository;

import dev.misei.einfachstonks.stonkservice.model.ETFIdentity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface ETFIdentityRepository extends MongoRepository<ETFIdentity, String> {
    ETFIdentity findByInternalNameId(UUID internalNameId);
    boolean existsByTicketYahooIgnoreCase(String ticketYahoo);
    boolean existsByWknNameJustEtfIgnoreCase(String wknNameJustEtf);
    boolean existsByIsinJustEtfIgnoreCase(String isinJustEtf);
}
