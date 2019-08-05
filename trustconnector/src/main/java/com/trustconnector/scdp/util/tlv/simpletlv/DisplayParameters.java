package com.trustconnector.scdp.util.tlv.simpletlv;

import com.trustconnector.scdp.util.tlv.*;

public class DisplayParameters extends SimpleTLV
{
    public DisplayParameters(final Tag tag, final Length len, final byte[] v, final int vOff) {
        super(tag, len, v, vOff);
    }
    
    public DisplayParameters(final byte[] param) {
        super(64);
        this.appendValue(param);
    }
    
    public byte[] getParam() {
        return this.value.toBytes();
    }
}
