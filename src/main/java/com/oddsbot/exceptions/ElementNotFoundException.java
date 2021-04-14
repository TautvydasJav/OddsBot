package com.oddsbot.exceptions;


import com.oddsbot.constants.Messages;

public class ElementNotFoundException extends Exception {

    public ElementNotFoundException() {
        super(Messages.EXCEPTION_NO_ELEMENT);
    }
}
