package com.nba.team.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Team implements Serializable {
    private String id;
    private String name;
    private List<String> playerIds = new ArrayList<>();
    private int wins = 0;
    private int losses = 0;
}

