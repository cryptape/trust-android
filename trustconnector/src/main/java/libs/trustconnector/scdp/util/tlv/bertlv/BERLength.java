package libs.trustconnector.scdp.util.tlv.bertlv;

import libs.trustconnector.scdp.util.*;
import libs.trustconnector.scdp.util.tlv.*;

import libs.trustconnector.scdp.util.ByteArray;
import libs.trustconnector.scdp.util.tlv.Length;
import libs.trustconnector.scdp.util.tlv.TLVFormatException;

public class BERLength implements Length
{
    private int length;
    private byte[] v;
    
    public BERLength() {
        this.setValue(0);
    }
    
    public BERLength(final int v) {
        this.setValue(v);
    }
    
    public BERLength(final byte[] v) {
        if (v == null) {
            this.setValue(0);
        }
        else {
            this.setValue(v.length);
        }
    }
    
    public BERLength(final String v) {
        final byte[] value = ByteArray.convert(v);
        this.setValue(value.length);
    }
    
    @Override
    public int getValue() {
        return this.length;
    }
    
    @Override
    public void setValue(final int newValue) {
        this.length = newValue;
        byte[] vtemp = null;
        if (this.length <= 127) {
            vtemp = new byte[] { (byte)this.length };
        }
        else if (this.length < 256) {
            vtemp = new byte[] { -127, (byte)this.length };
        }
        else if (this.length < 65536) {
            vtemp = new byte[] { -126, (byte)(this.length >> 8), (byte)this.length };
        }
        else if (this.length < 16777216) {
            vtemp = new byte[] { -125, (byte)(this.length >> 8), (byte)this.length };
        }
        else {
            TLVFormatException.throwIt(0);
        }
        this.v = vtemp;
    }
    
    @Override
    public byte[] toBytes() {
        return this.v;
    }
    
    @Override
    public int length() {
        return this.v.length;
    }
    
    @Override
    public int fromBytes(final byte[] bts, final int offset, final int maxLength) {
        final int btsV0 = bts[offset] & 0xFF;
        if (btsV0 <= 127) {
            this.length = btsV0;
        }
        else if (bts[offset] == -127) {
            this.length = (bts[offset + 1] & 0xFF);
        }
        else if (bts[offset] == -126) {
            this.length = (bts[offset + 1] & 0xFF);
            this.length <<= 8;
            this.length |= (bts[offset + 2] & 0xFF);
        }
        else if (bts[offset] == -125) {
            this.length = (bts[offset + 1] & 0xFF);
            this.length <<= 8;
            this.length |= (bts[offset + 2] & 0xFF);
            this.length <<= 8;
            this.length |= (bts[offset + 3] & 0xFF);
        }
        else {
            TLVFormatException.throwIt(0);
        }
        this.setValue(this.length);
        return this.v.length;
    }
    
    @Override
    public String toString() {
        return ByteArray.convert(this.v);
    }
}
