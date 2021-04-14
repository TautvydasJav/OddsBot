package com.oddsbot.model.line;


import com.oddsbot.enums.Bookmakers;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Line {
    private Bookmakers bookmaker;
    private String coef;
}
