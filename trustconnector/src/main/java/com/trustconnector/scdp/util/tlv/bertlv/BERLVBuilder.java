package com.trustconnector.scdp.util.tlv.bertlv;

import com.trustconnector.scdp.util.tlv.*;

public class BERLVBuilder
{
    public static LV buildLV(final byte[] v) {
        final int l = (v == null) ? 0 : v.length;
        return buildLV(v, 0, l);
    }
    
    public static LV buildLV(final byte[] v, final int offset, final int length) {
        return new BERLV(v, offset, length);
    }
    
    public static LV buildLV(final String v) {
        return new BERLV(v);
    }
}
