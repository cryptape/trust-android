package com.trustconnector.scdp.util.tlv.simpletlv;

import com.trustconnector.scdp.util.tlv.*;
import com.trustconnector.scdp.util.tlv.bertlv.*;
import com.trustconnector.scdp.util.*;

public class TimerIdentifier extends SimpleTLV
{
    public TimerIdentifier(final Tag tag, final Length len, final byte[] v, final int vOff) {
        super(tag, len, v, vOff);
    }
    
    public TimerIdentifier(final int timerID) {
        this.tag = new SimpleTag(36);
        this.len = new BERLength(1);
        (this.value = new ByteArray(1)).setByte(0, timerID);
    }
    
    public byte getTimerID() {
        return this.value.getByte(0);
    }
}
