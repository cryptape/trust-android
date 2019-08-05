package libs.trustconnector.scdp.util.tlv.bertlv;

import libs.trustconnector.scdp.util.tlv.*;

import libs.trustconnector.scdp.util.tlv.LV;

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
