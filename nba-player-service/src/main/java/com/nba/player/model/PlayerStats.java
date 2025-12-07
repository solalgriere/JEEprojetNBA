package com.nba.player.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class PlayerStats implements Serializable {
    private int points = 0;
    private int rebounds = 0;
    private int assists = 0;
    private int steals = 0;
    private int blocks = 0;
    private int minutesPlayed = 0;
    
    public void addPoints(int points) {
        this.points += points;
    }
    
    public void addRebounds(int rebounds) {
        this.rebounds += rebounds;
    }
    
    public void addAssists(int assists) {
        this.assists += assists;
    }
    
    public void addSteals(int steals) {
        this.steals += steals;
    }
    
    public void addBlocks(int blocks) {
        this.blocks += blocks;
    }
    
    public void addMinutes(int minutes) {
        this.minutesPlayed += minutes;
    }
}

