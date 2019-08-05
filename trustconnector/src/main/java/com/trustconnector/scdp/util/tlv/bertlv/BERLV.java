package com.trustconnector.scdp.util.tlv.bertlv;

import com.trustconnector.scdp.util.tlv.*;
import com.trustconnector.scdp.util.*;

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
