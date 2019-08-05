package com.trustconnector.scdp.util.tlv.simpletlv;

import com.trustconnector.scdp.util.tlv.*;
import com.trustconnector.scdp.util.tlv.bertlv.*;
import com.trustconnector.scdp.util.*;

public class IMEI extends SimpleTLV
{
    public IMEI(final SimpleTag tag, final Length len, final byte[] v, final int vOff) {
        super(tag, len, v, vOff);
    }
    
    public IMEI(final byte[] IEMI) {
        this.tag = new SimpleTag(20);
        this.len = new BERLength(IEMI.length);
        this.value = new ByteArray(IEMI);
    }
    
    public IMEI(final String IEMI) {
        this.tag = new SimpleTag(20);
        final byte[] iemi = ByteArray.convert(IEMI);
        this.len = new BERLength(iemi.length);
        this.value = new ByteArray(iemi);
    }
}
