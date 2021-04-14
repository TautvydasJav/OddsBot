package com.oddsbot.exceptions;

import com.oddsbot.constants.Messages;

public class MatchNotFoundException extends Exception {

    public MatchNotFoundException() {
        super(Messages.EXCEPTION_NO_MATCH);
    }
}
