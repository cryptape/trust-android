package com.trustconnector.scdp.util.tlv;

public interface Tag
{
    byte[] toBytes();
    
    int fromBytes(final byte[] p0, final int p1, final int p2);
    
    int length();
    
    String getDescription();
}
