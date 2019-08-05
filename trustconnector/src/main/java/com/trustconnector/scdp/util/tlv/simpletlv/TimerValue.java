package com.trustconnector.scdp.util.tlv.simpletlv;

import com.trustconnector.scdp.util.tlv.*;
import com.trustconnector.scdp.util.tlv.bertlv.*;
import com.trustconnector.scdp.util.*;

public class TimerValue extends SimpleTLV
{
    public TimerValue(final Tag tag, final Length len, final byte[] v, final int vOff) {
        super(tag, len, v, vOff);
    }
    
    public TimerValue(final int timerValue) {
        this.tag = new SimpleTag(37);
        this.len = new BERLength(3);
        this.value = new ByteArray(Util.intToBytes(timerValue, 3));
    }
    
    public int getTimerValue() {
        return Util.bytesToInt(this.value.toBytes());
    }
    
    public TimerValue(final byte[] v, final int vOff) {
        this.tag = new SimpleTag(37);
        this.len = new BERLength(3);
        this.value = new ByteArray(v, vOff, 3);
    }
}
