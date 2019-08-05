package com.trustconnector.scdp.util.tlv.simpletlv;

import com.trustconnector.scdp.util.tlv.*;
import com.trustconnector.scdp.util.*;

public class AlphaIdentifier extends SimpleTLV
{
    public AlphaIdentifier(final Tag tag, final Length len, final byte[] v, final int vOff) {
        super(tag, len, v, vOff);
    }
    
    public AlphaIdentifier(final String alpha) {
        super(5);
        byte[] data = null;
        if (Util.isAsciiStr(alpha)) {
            data = alpha.getBytes();
        }
        else {
            data = ByteArray.convert(alpha, StringFormat.UCS2);
            final byte[] datat = new byte[data.length + 1];
            System.arraycopy(data, 0, datat, 1, data.length);
            datat[0] = -128;
            data = datat;
        }
        this.appendValue(data);
    }
    
    public String getAlphaText() {
        final byte dcs = this.value.getByte(0);
        if (dcs == -128) {
            return ByteArray.convert(this.value.toBytes(1, this.value.length() - 1), StringFormat.UCS2);
        }
        if ((dcs & 0x80) == 0x0) {
            return ByteArray.convert(this.value.toBytes(), StringFormat.ASCII);
        }
        return "";
    }
    
    public byte[] getAlphaTextBytes() {
        if (this.isUCS2()) {
            return this.value.toBytes(1, this.value.length() - 1);
        }
        return this.value.toBytes();
    }
    
    public boolean isUCS2() {
        return this.value.length() != 0 && this.value.getByte(0) == -128;
    }
    
    @Override
    public String toString() {
        String res = "Alpha Identifier=" + super.toString();
        res += "\n    -Alpha Identifier content:";
        if (this.value.length() > 0) {
            res += this.getAlphaText();
        }
        return res;
    }
}
