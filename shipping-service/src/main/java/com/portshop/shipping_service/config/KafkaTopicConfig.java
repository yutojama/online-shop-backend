package com.portshop.shipping_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${app.kafka.topic.shipping.partitions}")
    private int partitions;

    @Value("${app.kafka.topic.shipping.replicas}")
    private int replicas;

    @Bean
    public NewTopic ShippingTopic() {
        return TopicBuilder
                .name("shipping-events")
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }

}
