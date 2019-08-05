package com.trustconnector.scdp;

public class SCDPException extends RuntimeException
{
    private static final long serialVersionUID = -3260364470441319030L;
    protected String message;
    
    public SCDPException(final String message) {
        super(message);
        this.message = message;
    }
    
    @Override
    public String getMessage() {
        return this.message;
    }
}
