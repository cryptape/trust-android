package com.trustconnector.scdp.util.tlv.simpletlv;

import com.trustconnector.scdp.util.tlv.*;

public class BufferSize extends SimpleTLV
{
    public BufferSize(final Tag tag, final Length len, final byte[] v, final int vOff) {
        super(tag, len, v, vOff);
    }
    
    public BufferSize(final int size) {
        super(57, size, 2);
    }
    
    public int getBufferSize() {
        return this.value.getInt2(0);
    }
    
    @Override
    public String toString() {
        return String.format("Buffer Size=0x%04X", this.value.getInt2(0));
    }
}
