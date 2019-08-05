package com.trustconnector.scdp.util.tlv;

public interface Length
{
    int getValue();
    
    void setValue(final int p0);
    
    byte[] toBytes();
    
    int length();
    
    int fromBytes(final byte[] p0, final int p1, final int p2);
}
