package dev.misei.einfachml.repository;

import dev.misei.einfachml.repository.model.NetworkBoard;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NetworkBoardRepository extends MongoRepository<NetworkBoard, UUID> {
}
