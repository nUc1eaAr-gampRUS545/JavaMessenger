package ru.senla.javacourse.mutovin.messenger.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@ComponentScan(basePackages = {"ru.senla.javacourse.mutovin.messenger"})
@EnableCaching
@EnableKafka
public class MessengerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MessengerApplication.class,args);
    }

}
