package com.trustconnector.scdp.util.tlv;

public class TLVFormatException extends RuntimeException
{
    private static final long serialVersionUID = 5700194937201939208L;
    private int reasonCode;
    public static final int INVALID_LENGTH = 0;
    public static final int INVALID_TAG_FORMAT = 1;
    
    TLVFormatException(final int rc) {
        this.reasonCode = rc;
    }
    
    public int getReasonCode() {
        return this.reasonCode;
    }
    
    public static void throwIt(final int reasoncode) {
        throw new TLVFormatException(reasoncode);
    }
}
