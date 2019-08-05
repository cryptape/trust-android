package libs.trustconnector.scdp.util.tlv.simpletlv;

import libs.trustconnector.scdp.util.tlv.*;

import libs.trustconnector.scdp.util.tlv.Length;
import libs.trustconnector.scdp.util.tlv.Tag;

public class ChannelDataLength extends SimpleTLV
{
    public ChannelDataLength(final Tag tag, final Length len, final byte[] v, final int vOff) {
        super(tag, len, v, vOff);
    }
    
    public ChannelDataLength(final int dataLen) {
        super(55);
        final byte[] v = { (byte)dataLen };
        this.appendValue(v);
    }
    
    public int getDataLength() {
        return this.value.getByte(0) & 0xFF;
    }
    
    @Override
    public String toString() {
        return String.format("Channel Data Length=0x%02X", this.getDataLength());
    }
}
