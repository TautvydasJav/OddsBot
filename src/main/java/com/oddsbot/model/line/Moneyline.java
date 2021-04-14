package com.oddsbot.model.line;

import com.oddsbot.enums.Bookmakers;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Moneyline extends Line {
    private String team1;
    private String team2;
    private String coef2;

    public Moneyline(String team1, String team2, String coef, String coef2, Bookmakers bookmaker) {
        super(bookmaker, coef);
        this.team1 = team1;
        this.team2 = team2;
        this.coef2 = coef2;
    }
}