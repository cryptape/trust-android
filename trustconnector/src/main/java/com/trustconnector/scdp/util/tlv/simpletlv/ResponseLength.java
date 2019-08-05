package com.trustconnector.scdp.util.tlv.simpletlv;

import com.trustconnector.scdp.util.tlv.*;

public class ResponseLength extends SimpleTLV
{
    public ResponseLength(final Tag tag, final Length len, final byte[] v, final int vOff) {
        super(tag, len, v, vOff);
    }
    
    public int getMinLength() {
        return this.value.getByte(0) & 0xFF;
    }
    
    public int getMaxLength() {
        return this.value.getByte(1) & 0xFF;
    }
}
