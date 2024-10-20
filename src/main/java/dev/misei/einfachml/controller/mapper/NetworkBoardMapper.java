package dev.misei.einfachml.controller.mapper;

import dev.misei.einfachml.neuralservice.domain.Network;
import dev.misei.einfachml.neuralservice.domain.Status;
import dev.misei.einfachml.repository.model.AlgorithmBoard;
import dev.misei.einfachml.repository.model.NetworkBoard;

import java.util.UUID;

public class NetworkBoardMapper {
    public static NetworkBoard from(UUID uuid, AlgorithmBoard algorithmBoard) {
        return new NetworkBoard(uuid,
                algorithmBoard,
                new Status(uuid, false, 0, null, 0));
    }

    public static NetworkBoard from(Network network) {
        return new NetworkBoard(
                network.getStatus().getNetworkId(),
                AlgorithmBoardMapper.to(network.getAlgorithm()),
                network.getStatus()
        );
    }
}
