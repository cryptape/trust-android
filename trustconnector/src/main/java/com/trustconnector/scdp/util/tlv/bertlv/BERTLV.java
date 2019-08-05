package com.trustconnector.scdp.util.tlv.bertlv;

import com.trustconnector.scdp.util.*;
import com.trustconnector.scdp.util.tlv.*;

public class BERTLV extends TLV
{
    public BERTLV(final Tag tag, final byte[] value) {
        int length = 0;
        if (value != null) {
            length = value.length;
        }
        this.tag = tag;
        this.len = new BERLength(length);
        this.value = new ByteArray(value);
    }
    
    public BERTLV(final int tag) {
        super(new BERTag(tag), new BERLength(), null);
    }
    
    public BERTLV(final int tag, final byte[] value) {
        super(new BERTag(tag), new BERLength(value.length), value);
    }
    
    public BERTLV(final int tag, final byte[] value, final int offset, final int length) {
        super(new BERTag(tag), new BERLength(length), value, offset);
    }
    
    public BERTLV(final byte[] tlv) {
        this(tlv, 0, tlv.length);
    }
    
    public BERTLV(final byte[] tlv, final int offset, final int maxLen) {
        final Tag tag = new BERTag();
        final int tagLen = tag.fromBytes(tlv, 0, maxLen);
        if (tagLen == -1) {
            return;
        }
        final Length len = new BERLength();
        final int lenOfLen = len.fromBytes(tlv, tagLen, maxLen - tagLen);
        if (lenOfLen == -1) {
            return;
        }
        final int valueLen = len.getValue();
        if (valueLen > maxLen - tagLen - lenOfLen) {
            return;
        }
        this.tag = tag;
        this.len = len;
        this.value = new ByteArray(tlv, tagLen + lenOfLen, maxLen - tagLen - lenOfLen);
    }
    
    public BERTLV(final int tag, final String value) {
        this.tag = new BERTag(tag);
        this.value = new ByteArray(value);
        this.len = new BERLength(this.value.length());
    }
}
