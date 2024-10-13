package dev.misei.einfachml.repository.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.UUID;

//DataPairDocument_NetworkId  (Relation N:1)

@Document
@AllArgsConstructor
@Data
public class DataPair implements Comparable<DataPair> {

    private UUID networkId;
    @Id
    private UUID uuid;
    private long createdAt;
    private List<Double> inputs;
    private List<Double> expected;

    @Override
    public int compareTo(@NonNull DataPair o) {
        return Long.compare(createdAt, o.createdAt);
    }
}
