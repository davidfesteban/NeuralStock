package dev.misei.einfachml.repository.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@AllArgsConstructor
@Document
@Data
public class NetworkBackup {

    @Id
    UUID networkId;

    String network;
}
