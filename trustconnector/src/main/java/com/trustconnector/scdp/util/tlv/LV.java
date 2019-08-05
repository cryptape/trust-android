package com.trustconnector.scdp.util.tlv;

import com.trustconnector.scdp.util.*;
import com.trustconnector.scdp.util.tlv.bertlv.*;

public class LV
{
    Length len;
    ByteArray value;
    
    public LV(final byte[] v) {
        this.len = new BERLength((v == null) ? 0 : v.length);
        this.value = new ByteArray(v);
    }
    
    public LV(final Length len, final byte[] v) {
        if (len.getValue() != v.length) {
            TLVFormatException.throwIt(0);
        }
        this.len = len;
        this.value = new ByteArray(v);
    }
    
    public LV(final Length len, final byte[] v, final int offset) {
        final int vlen = v.length - offset;
        if (len.getValue() != vlen) {
            TLVFormatException.throwIt(0);
        }
        this.len = len;
        this.value = new ByteArray(v, offset, vlen);
    }
    
    public byte[] toBytes() {
        final ByteArray lvBytes = new ByteArray(this.len.toBytes());
        lvBytes.append(this.value);
        return lvBytes.toBytes();
    }
    
    public int toBytes(final byte[] b, final int offset) {
        final ByteArray lvBytes = new ByteArray(this.len.toBytes());
        lvBytes.append(this.value);
        final int lengthTatal = lvBytes.length();
        lvBytes.getBytes(offset, b, 0, lengthTatal);
        return lengthTatal;
    }
    
    public void append(final byte[] v) {
        if (v != null) {
            this.value.append(v);
            this.len.setValue(this.value.length());
        }
    }
    
    public int getTotalLength() {
        return this.len.length() + this.len.getValue();
    }
}
