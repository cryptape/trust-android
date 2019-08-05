package libs.trustconnector.scdp.util.tlv.simpletlv;

import libs.trustconnector.scdp.util.tlv.*;
import libs.trustconnector.scdp.util.tlv.bertlv.*;
import libs.trustconnector.scdp.util.*;

import libs.trustconnector.scdp.util.ByteArray;
import libs.trustconnector.scdp.util.tlv.Length;
import libs.trustconnector.scdp.util.tlv.Tag;
import libs.trustconnector.scdp.util.tlv.bertlv.BERLength;

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
