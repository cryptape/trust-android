package com.trustconnector.scdp.util.tlv.simpletlv;

import com.trustconnector.scdp.util.tlv.*;

public class NetworkSearchMode extends SimpleTLV
{
    public static final byte SEARCH_MODE_MANUAL = 0;
    public static final byte SEARCH_MODE_AUTOMATIC = 1;
    
    public NetworkSearchMode(final Tag tag, final Length len, final byte[] v, final int vOff) {
        super(tag, len, v, vOff);
    }
    
    public NetworkSearchMode(final byte searchMode) {
        super(101);
        final byte[] v = { searchMode };
        this.appendValue(v);
    }
    
    public byte getSearchMode() {
        return this.value.getByte(0);
    }
}
