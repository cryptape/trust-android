package com.trustconnector.scdp.smartcard;

import com.trustconnector.scdp.util.*;

public final class AID
{
    ByteArray aid;
    public static final int AID_MIN_LEN = 5;
    public static final int AID_MAX_LEN = 16;
    
    public AID(final byte[] aid) {
        this.aid = new ByteArray(aid);
    }
    
    public AID(final byte[] aid, final int offset, final int length) {
        this.aid = new ByteArray(aid, offset, length);
    }
    
    public AID(final byte[] aidLV, final int Loffset) {
        this.aid = new ByteArray(aidLV, Loffset, false);
    }
    
    public AID(final String aid) {
        this.aid = new ByteArray(aid);
    }
    
    public byte[] getRID() {
        return this.aid.toBytes(0, 5);
    }
    
    public byte[] getPIX() {
        return this.aid.right(this.aid.length() - 5);
    }
    
    public byte[] toBytes() {
        return this.aid.toBytes();
    }
    
    public byte[] toLV() {
        return this.aid.toLV();
    }
    
    public byte[] toTLV() {
        return this.toTLV(79);
    }
    
    public byte[] toTLV(final int tag) {
        final int l = this.aid.length();
        final byte[] r = new byte[2 + l];
        r[0] = (byte)tag;
        r[1] = (byte)l;
        this.aid.getBytes(0, r, 2, l);
        return r;
    }
    
    public int length() {
        return this.aid.length();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof AID) {
            final AID objAID = (AID)obj;
            return objAID.aid.equals(this.aid);
        }
        if (obj instanceof byte[]) {}
        return super.equals(obj);
    }
    
    @Override
    public String toString() {
        return this.aid.toString();
    }
}
