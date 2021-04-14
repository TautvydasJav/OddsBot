package com.oddsbot.exceptions;

import com.oddsbot.constants.Messages;

public class ProfileNotFoundException extends Exception {

    public ProfileNotFoundException() {
        super(Messages.EXCEPTION_NO_PROFILE);
    }
}
