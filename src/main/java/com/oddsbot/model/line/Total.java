package com.oddsbot.model.line;

import com.oddsbot.enums.Bookmakers;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Total extends Line {
    private String value;

    public Total(Bookmakers bookmaker, String coef, String value) {
        super(bookmaker, coef);
        this.value = value;
    }
}
