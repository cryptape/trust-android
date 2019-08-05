package com.trustconnector.scdp.util.tlv.simpletlv;

import com.trustconnector.scdp.util.tlv.*;
import com.trustconnector.scdp.util.tlv.bertlv.*;

public class HelpRequest extends SimpleTLV
{
    public HelpRequest(final Tag tag, final Length len, final byte[] v, final int vOff) {
        super(tag, len, v, vOff);
    }
    
    public HelpRequest() {
        this.tag = new SimpleTag(21);
        this.len = new BERLength(0);
    }
}
