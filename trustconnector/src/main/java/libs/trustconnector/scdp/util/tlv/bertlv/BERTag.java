package libs.trustconnector.scdp.util.tlv.bertlv;

import libs.trustconnector.scdp.util.tlv.*;
import libs.trustconnector.scdp.util.*;

import libs.trustconnector.scdp.util.ByteArray;
import libs.trustconnector.scdp.util.tlv.Tag;

public class BERTag implements Tag
{
    private byte[] v;
    public static final int Class_Universal = 0;
    public static final int Class_Application = 64;
    public static final int Class_Context = 128;
    public static final int Class_Private = 192;
    public static final int Class_Undefined = 240;
    public static final byte PROACTIVE_COMMAND = -48;
    public static final byte SMS_PP_DOWNLOAD = -47;
    public static final byte CELL_BROADCAST_DOWNLOAD = -46;
    public static final byte MENU_SELECTION = -45;
    public static final byte CALL_CONTROL = -44;
    public static final byte MO_SHORT_MESSAGE_CONTROL = -43;
    public static final byte EVENT_DOWNLOAD = -42;
    public static final byte TIMER_EXPIRATION = -41;
    public static final byte USSD_DOWNLOAD = -39;
    public static final byte MMS_TRANSFER_STATUS = -38;
    public static final byte MMS_NOTIFICATION_DOWNLOAD = -37;
    public static final byte TERMINAL_APPLICATION_TAG = -36;
    public static final byte GEO_LOC_REPORT_TAG = -35;
    public static final byte ENVELOPE_CONTAINER = -34;
    
    public BERTag() {
    }
    
    public BERTag(final int tag) {
        byte[] tags = null;
        if (tag <= 255) {
            tags = new byte[] { (byte)tag };
        }
        else if (tag < 65535) {
            tags = new byte[] { (byte)(tag >> 8), (byte)tag };
        }
        if (tags != null) {
            this.fromBytes(tags, 0, tags.length);
        }
    }
    
    public BERTag(final byte[] tag) {
        this.fromBytes(tag, 0, tag.length);
    }
    
    @Override
    public byte[] toBytes() {
        return this.v.clone();
    }
    
    @Override
    public int fromBytes(final byte[] bts, final int offset, final int maxLength) {
        if (maxLength < 1) {
            return -1;
        }
        int tagLen = 1;
        if ((bts[offset] & 0x1F) == 0x1F) {
            if (maxLength < 2) {
                return -1;
            }
            while ((bts[offset + tagLen] & 0x80) == 0x80) {
                ++tagLen;
                if (maxLength < tagLen) {
                    return -1;
                }
            }
            ++tagLen;
        }
        final byte[] vt = new byte[tagLen];
        System.arraycopy(bts, offset, vt, 0, tagLen);
        this.v = vt;
        return tagLen;
    }
    
    @Override
    public int length() {
        if (this.v == null) {
            return 0;
        }
        return this.v.length;
    }
    
    @Override
    public String getDescription() {
        return "BER Tag:" + ByteArray.convert(this.v);
    }
    
    public boolean isPrimitive() {
        return this.v != null && (this.v[0] & 0x20) == 0x20;
    }
    
    public int getCls() {
        if (this.v == null) {
            return 240;
        }
        return this.v[0] & 0xC0;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (super.equals(obj)) {
            return true;
        }
        if (obj instanceof BERTag) {
            final BERTag o = (BERTag)obj;
            final int Vlength = this.v.length;
            if (Vlength != o.v.length) {
                return false;
            }
            for (int i = 0; i < Vlength; ++i) {
                if (this.v[i] != o.v[i]) {
                    return false;
                }
            }
        }
        return true;
    }
}
