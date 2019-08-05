package com.trustconnector.scdp.smartcard.javacard.cap;

import com.trustconnector.scdp.smartcard.*;

public class ComponentApplet extends Component
{
    public static final int DATA_OFF_APPLET_COUNT = 3;
    public static final int DATA_OFF_APPLET = 4;
    
    public int getAppletCount() {
        return this.data.getUnsignedByte(3);
    }
    
    public AID[] getAppletsAID() {
        final int c = this.data.getUnsignedByte(3);
        if (c == 0) {
            return null;
        }
        final AID[] aids = new AID[c];
        int i = 0;
        int offset = 4;
        final byte[] d = this.data.toBytes();
        while (i < c) {
            final AID a = new AID(d, offset);
            aids[i] = a;
            offset += a.length() + 1 + 2;
            ++i;
        }
        return aids;
    }
    
    public short[] getAppletsInstallOffset() {
        final int c = this.data.getUnsignedByte(3);
        if (c == 0) {
            return null;
        }
        final short[] offsets = new short[c];
        int i = 0;
        int offset = 4;
        while (i++ < c) {
            final int aidLen = this.data.getUnsignedByte(offset);
            offset += 1 + aidLen;
            offsets[i] = (short)this.data.getInt2(offset);
            offset += 2;
        }
        return offsets;
    }
}
