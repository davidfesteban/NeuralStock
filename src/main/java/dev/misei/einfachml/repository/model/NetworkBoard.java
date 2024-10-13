package dev.misei.einfachml.repository.model;

import dev.misei.einfachml.neuralservice.domain.Status;
import dev.misei.einfachml.neuralservice.domain.algorithm.Algorithm;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@Document
@AllArgsConstructor
@Data
public class NetworkBoard {

    @Id
    UUID networkId;

    AlgorithmBoard algorithmBoard;

    //Current Status
    Status status;

    int datasetSize;
    int predictionsSize;
}
