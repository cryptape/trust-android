package libs.trustconnector.scdp.smartcard;

import libs.trustconnector.scdp.*;

import libs.trustconnector.scdp.SCDPException;

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
