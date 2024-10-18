package dev.misei.einfachml.neuralservice;

import dev.misei.einfachml.controller.dto.TopicResponse;
import dev.misei.einfachml.repository.DataPairRepository;
import dev.misei.einfachml.repository.model.DataPair;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class DataService {

    private DataPairRepository dataPairRepository;

    public Mono<Void> includeDataset(Flux<DataPair> dataPairList) {
        return dataPairRepository.saveAll(dataPairList).then();
    }

    public Flux<TopicResponse> retrieveTopics() {
        return dataPairRepository.findAll()
                .groupBy(DataPair::getTopic)
                .flatMap(groupedFlux ->
                        groupedFlux.count()
                                .map(count -> new TopicResponse(groupedFlux.key(), count))
                );
    }

    public Flux<DataPair> retrieve(String topic, Long createdAtStart, Long createdAtEnd, Integer lastAmount) {
        if (lastAmount != null) {
            return dataPairRepository.findByTopicIgnoreCaseOrderByCreatedAtDesc(topic)
                    .take(lastAmount)
                    .sort(Comparator.comparing(DataPair::getCreatedAt)); //ASC
        } else if (createdAtStart == null || createdAtEnd == null) {
            return dataPairRepository.findByTopicIgnoreCaseOrderByCreatedAtAsc(topic);
        }

        return dataPairRepository.findByTopicIgnoreCaseAndCreatedAtBetweenOrderByCreatedAtAsc(
                topic, createdAtStart, createdAtEnd);
    }

    public Mono<Void> deleteDataSetByUUID(Flux<UUID> dataSetUUID) {
        return dataSetUUID
                .flatMap(dataPairRepository::deleteById)
                .then();
    }

    public Mono<Void> deleteDataSetByTopic(String topic) {
        return dataPairRepository.deleteByTopicIgnoreCase(topic);
    }
}
