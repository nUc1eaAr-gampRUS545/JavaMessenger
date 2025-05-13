package ru.senla.javacourse.mutovin.messanger.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"ru.senla.javacourse.mutovin.messenger"})
public class MessengerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MessengerApplication.class,args);
    }

}
