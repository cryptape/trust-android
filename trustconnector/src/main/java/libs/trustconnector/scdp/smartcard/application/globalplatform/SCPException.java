package libs.trustconnector.scdp.smartcard.application.globalplatform;

import libs.trustconnector.scdp.*;

import libs.trustconnector.scdp.SCDPException;

public class SCPException extends SCDPException
{
    private static final long serialVersionUID = -8405785182237793959L;
    
    private SCPException(final String message) {
        super(message);
    }
    
    public static void throwIt(final String message) {
        throw new SCPException(message);
    }
}
