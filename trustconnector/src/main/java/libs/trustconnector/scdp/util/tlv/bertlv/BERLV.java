package libs.trustconnector.scdp.util.tlv.bertlv;

import libs.trustconnector.scdp.util.tlv.*;
import libs.trustconnector.scdp.util.*;

import libs.trustconnector.scdp.util.ByteArray;
import libs.trustconnector.scdp.util.tlv.LV;

public class BERLV extends LV
{
    public BERLV(final byte[] v) {
        super(new BERLength(v), v);
    }
    
    public BERLV(final byte[] v, final int offset, final int length) {
        super(new BERLength(length), v, offset);
    }
    
    public BERLV(final String v) {
        super(new BERLength(v), ByteArray.convert(v));
    }
}
