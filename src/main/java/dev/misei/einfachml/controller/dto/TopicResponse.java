package dev.misei.einfachml.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class TopicResponse {
    String topic;
    Long count;
}
