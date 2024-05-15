package dev.misei.einfachstonks.stonkservice.repository;

import dev.misei.einfachstonks.stonkservice.model.ETFBridgeType;
import dev.misei.einfachstonks.stonkservice.model.ETFIdentity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface ETFIdentityRepository extends MongoRepository<ETFIdentity, String> {
    boolean existsByEtfBridgeTypeAndTicker(ETFBridgeType etfBridgeType, String ticker);
    long deleteByInternalNameId(UUID internalNameId);
    ETFIdentity findByInternalNameId(UUID internalNameId);
}
