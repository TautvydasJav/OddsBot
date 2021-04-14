package com.oddsbot.exceptions;

import com.oddsbot.enums.Bookmakers;

public class BookmakerNotFoundException extends Exception {

    public BookmakerNotFoundException(Bookmakers bookmaker) {
        super(String.format("Bookmaker %s not found, check application.properties", bookmaker));
    }
}
