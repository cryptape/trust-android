package com.trustconnector.scdp.smartcard;

import com.trustconnector.scdp.*;

public class SmartCardReaderException extends SCDPException
{
    private static final long serialVersionUID = 1L;
    
    public SmartCardReaderException(final String message) {
        super(message);
    }
    
    public static void throwIt(final String message) {
        throw new SmartCardReaderException(message);
    }
}
