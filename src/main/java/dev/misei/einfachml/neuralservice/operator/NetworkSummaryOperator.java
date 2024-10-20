package dev.misei.einfachml.neuralservice.operator;

import dev.misei.einfachml.controller.mapper.NetworkBoardMapper;
import dev.misei.einfachml.neuralservice.domain.Network;
import dev.misei.einfachml.repository.NetworkBoardRepository;
import dev.misei.einfachml.repository.model.NetworkBoard;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@AllArgsConstructor
@Component
@Slf4j
public class NetworkSummaryOperator {

    private NetworkBoardRepository networkBoardRepository;

    public Mono<Void> saveSummary(Network network) {
        return networkBoardRepository.save(NetworkBoardMapper.from(network))
                .doOnNext(a -> log.info("Saved Network Summary"))
                .doOnTerminate(() -> log.info("Terminated Summary"))
                .then();
    }

    public Mono<Void> deleteSummary(UUID networkId) {
        return networkBoardRepository.deleteById(networkId);
    }

    public Flux<NetworkBoard> getAllSummaries() {
        return networkBoardRepository.findAll();
    }
}
