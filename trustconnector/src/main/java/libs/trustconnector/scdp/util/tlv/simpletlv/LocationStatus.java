package libs.trustconnector.scdp.util.tlv.simpletlv;

import libs.trustconnector.scdp.util.tlv.*;
import libs.trustconnector.scdp.util.tlv.bertlv.*;
import libs.trustconnector.scdp.util.*;

import libs.trustconnector.scdp.util.ByteArray;
import libs.trustconnector.scdp.util.tlv.Length;
import libs.trustconnector.scdp.util.tlv.Tag;
import libs.trustconnector.scdp.util.tlv.bertlv.BERLength;

public class LocationStatus extends SimpleTLV
{
    public static final byte STATUS_NORMAL_SERVICE = 0;
    public static final byte STATUS_LIMITED_SERVICE = 1;
    public static final byte STATUS_NO_SERVICE = 2;
    
    public LocationStatus(final Tag tag, final Length len, final byte[] v, final int vOff) {
        super(tag, len, v, vOff);
    }
    
    public LocationStatus(final byte status) {
        this.tag = new SimpleTag(27);
        this.len = new BERLength(1);
        (this.value = new ByteArray(1)).setByte(0, status);
    }
    
    public byte getStatus() {
        return this.value.getByte(0);
    }
}
