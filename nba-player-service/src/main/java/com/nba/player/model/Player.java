package com.nba.player.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player implements Serializable {
    private String id;
    private String name;
    private String position;
    private int jerseyNumber;
    private String teamId;
}

