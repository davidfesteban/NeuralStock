package dev.misei.einfachml.repository;

import dev.misei.einfachml.repository.model.NetworkBackup;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import java.util.UUID;

public interface NetworkBackupRepository extends ReactiveMongoRepository<NetworkBackup, UUID> {
}
