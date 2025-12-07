package com.nba.game;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.nba.game", "com.actorframework.core"})
public class GameServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(GameServiceApplication.class, args);
    }
}

