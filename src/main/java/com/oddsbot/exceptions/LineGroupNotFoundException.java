package com.oddsbot.exceptions;

public class LineGroupNotFoundException extends Exception {

    public LineGroupNotFoundException(String category) {
        super(String.format("Line category %s not found", category));
    }
}
