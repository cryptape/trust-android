package libs.trustconnector.scdp.util.tlv.simpletlv;

import libs.trustconnector.scdp.util.tlv.*;

import libs.trustconnector.scdp.util.tlv.Length;
import libs.trustconnector.scdp.util.tlv.Tag;

public class BrowsingStatus extends SimpleTLV
{
    public BrowsingStatus(final Tag tag, final Length len, final byte[] v, final int vOff) {
        super(tag, len, v, vOff);
    }
    
    public BrowsingStatus(final byte[] status) {
        super(100);
        this.appendValue(status);
    }
    
    public byte[] getStatus() {
        return this.value.toBytes();
    }
}
