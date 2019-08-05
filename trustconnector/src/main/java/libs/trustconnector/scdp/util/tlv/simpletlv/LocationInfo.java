package libs.trustconnector.scdp.util.tlv.simpletlv;

import libs.trustconnector.scdp.util.tlv.*;
import libs.trustconnector.scdp.util.tlv.bertlv.*;
import libs.trustconnector.scdp.util.*;

import libs.trustconnector.scdp.util.ByteArray;
import libs.trustconnector.scdp.util.tlv.Length;
import libs.trustconnector.scdp.util.tlv.Tag;
import libs.trustconnector.scdp.util.tlv.bertlv.BERLength;

public class LocationInfo extends SimpleTLV
{
    public LocationInfo(final Tag tag, final Length len, final byte[] v, final int vOff) {
        super(tag, len, v, vOff);
    }
    
    public LocationInfo() {
        this.tag = new SimpleTag(19);
        this.len = new BERLength(0);
    }
    
    public LocationInfo(final byte[] info) {
        this.tag = new SimpleTag(19);
        this.len = new BERLength(info.length);
        this.value = new ByteArray(info);
    }
    
    public LocationInfo(final String info) {
        this.tag = new SimpleTag(19);
        final byte[] infoV = ByteArray.convert(info);
        this.len = new BERLength(infoV.length);
        this.value = new ByteArray(infoV);
    }
}
