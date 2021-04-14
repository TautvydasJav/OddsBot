package com.oddsbot.exceptions;

import com.oddsbot.constants.Messages;

public class LineNotFoundException extends Exception {

    public LineNotFoundException() {
        super(Messages.EXCEPTION_NO_ELEMENT);
    }
}
