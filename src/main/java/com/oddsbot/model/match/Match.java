package com.oddsbot.model.match;

import com.oddsbot.enums.Bookmakers;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Match {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String team1;
    private String team2;
    private String name;
    private String url;
    private Bookmakers bookmaker;


    public Match(int id, String name, String url, Bookmakers bookmaker) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.bookmaker = bookmaker;
        this.team1 = name.split(" - ")[0];
        this.team2 = name.split(" - ")[1];
    }

    public Match(int id, String team1, String team2, String url, Bookmakers bookmaker) {
        this.id = id;
        this.url = url;
        this.bookmaker = bookmaker;
        this.team1 = team1;
        this.team2 = team2;
        this.name = String.format("%s - %s", team1, team2);
    }
}
