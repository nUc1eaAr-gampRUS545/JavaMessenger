package ru.senla.javacourse.mutovin.messenger.impl.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@Configuration
@EnableKafka
public class KafkaConfiguration {

    @Bean
    public NewTopic newTopic() {
        return new NewTopic("notification", 1, (short) 1);
    }
}
