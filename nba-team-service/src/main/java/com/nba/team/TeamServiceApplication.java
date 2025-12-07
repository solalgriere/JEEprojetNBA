package com.nba.team;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.nba.team", "com.actorframework.core"})
public class TeamServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(TeamServiceApplication.class, args);
    }
}

