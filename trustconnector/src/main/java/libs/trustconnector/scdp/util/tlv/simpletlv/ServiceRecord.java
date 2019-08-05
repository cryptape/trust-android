package libs.trustconnector.scdp.util.tlv.simpletlv;

import libs.trustconnector.scdp.util.tlv.*;

import libs.trustconnector.scdp.util.tlv.Length;
import libs.trustconnector.scdp.util.tlv.Tag;

public class ServiceRecord extends SimpleTLV
{
    public ServiceRecord(final Tag tag, final Length len, final byte[] v, final int vOff) {
        super(tag, len, v, vOff);
    }
    
    public ServiceRecord(final byte[] data) {
        super(65);
        this.appendValue(data);
    }
}
