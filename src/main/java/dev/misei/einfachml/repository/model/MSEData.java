package dev.misei.einfachml.repository.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@AllArgsConstructor
@Data
@Document
public class MSEData {
    UUID networkId;
    int epochHappened;
    Double error;
}
