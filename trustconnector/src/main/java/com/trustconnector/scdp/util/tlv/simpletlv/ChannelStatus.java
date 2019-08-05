package com.trustconnector.scdp.util.tlv.simpletlv;

import com.trustconnector.scdp.util.tlv.*;

public class ChannelStatus extends SimpleTLV
{
    public ChannelStatus(final Tag tag, final Length len, final byte[] v, final int vOff) {
        super(tag, len, v, vOff);
    }
    
    public ChannelStatus(final byte channelID, final boolean bEstablished) {
        super(56);
        final byte[] value = { (byte)(channelID & 0x7), 0 };
        if (bEstablished) {
            final byte[] array = value;
            final int n = 0;
            array[n] |= (byte)128;
        }
        this.appendValue(value);
    }
    
    public byte getChannelID() {
        return (byte)(this.value.getByte(0) & 0x7);
    }
    
    public boolean isLinkEstablished() {
        return (this.value.getByte(0) & 0xFFFFFF80) == 0xFFFFFF80;
    }
    
    public boolean isLinkDropped() {
        return this.value.getByte(1) == 5;
    }
    
    @Override
    public String toString() {
        String res = "Chennle Status" + super.toString();
        res = res + "\n    -Chennel ID=0x" + String.format("%02X", this.getChannelID());
        res = res + "\n    -Chennel Status=" + (this.isLinkEstablished() ? "Established" : "Closed");
        res = res + "\n    -Chennel Drop=" + (this.isLinkDropped() ? "True" : "False");
        return res;
    }
}
