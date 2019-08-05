package com.trustconnector.scdp.util.tlv.simpletlv;

import com.trustconnector.scdp.util.tlv.*;

public class BrowserTerminationCause extends SimpleTLV
{
    public static final byte CAUSE_USER_TERMINATION = 0;
    public static final byte CAUSE_ERROR_TERMINATION = 1;
    
    public BrowserTerminationCause(final Tag tag, final Length len, final byte[] v, final int vOff) {
        super(tag, len, v, vOff);
    }
    
    public BrowserTerminationCause(final byte cause) {
        super(52);
        final byte[] data = { cause };
        this.appendValue(data);
    }
    
    public byte getCause() {
        return this.value.getByte(0);
    }
}
