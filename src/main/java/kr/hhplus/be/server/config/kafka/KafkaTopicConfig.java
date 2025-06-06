package kr.hhplus.be.server.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic orderCompleteTopic() {
        return TopicBuilder
                .name("order-complete")
                .partitions(3)
                .replicas(3)
                .build();
    }

    @Bean
    public NewTopic couponIssueRequestTopic() {
        return TopicBuilder
                .name("coupon-issue-request")
                .partitions(6)
                .replicas(3)
                .build();
    }
}
