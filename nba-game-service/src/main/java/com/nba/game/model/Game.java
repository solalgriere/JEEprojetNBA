package com.nba.game.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Game implements Serializable {
    private String id;
    private String homeTeamId;
    private String awayTeamId;
    private String status; // SCHEDULED, IN_PROGRESS, FINISHED
    private int homeScore = 0;
    private int awayScore = 0;
    private int quarter = 1;
    private int timeRemaining = 720; // secondes (12 minutes)
    private java.util.List<String> homePlayerIds = new java.util.ArrayList<>();
    private java.util.List<String> awayPlayerIds = new java.util.ArrayList<>();
}

