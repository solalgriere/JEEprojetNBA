package com.nba.player;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.nba.player", "com.actorframework.core"})
public class PlayerServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(PlayerServiceApplication.class, args);
    }
}

