package com.oddsbot.model.line;

import com.oddsbot.enums.Bookmakers;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Handicap extends Line {
    private String team;
    private String value;

    public Handicap(Bookmakers bookmaker, String team, String coef, String value) {
        super(bookmaker, coef);
        this.team = team;
        this.value = value;
    }
}
