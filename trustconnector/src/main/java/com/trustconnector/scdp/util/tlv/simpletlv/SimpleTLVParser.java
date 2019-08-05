package com.trustconnector.scdp.util.tlv.simpletlv;

import com.trustconnector.scdp.util.tlv.bertlv.*;
import com.trustconnector.scdp.util.tlv.*;

public class SimpleTLVParser implements TLVParser
{
    @Override
    public TLV parse(final byte[] tlv, final int offset, final int maxLen) {
        return parseTLV(tlv, offset, maxLen);
    }
    
    public static TLV parseTLV(final byte[] tlv, final int offset, final int maxLen) {
        final SimpleTag tag = new SimpleTag();
        final int tagLen = tag.fromBytes(tlv, offset, maxLen);
        if (tagLen == -1) {
            return null;
        }
        final Length len = new BERLength();
        final int lenOfLen = len.fromBytes(tlv, offset + tagLen, maxLen - tagLen);
        if (lenOfLen == -1) {
            return null;
        }
        final int valueLen = len.getValue();
        if (valueLen > maxLen - tagLen - lenOfLen) {
            return null;
        }
        return SimpleTLVBuilder.buildTLV(tag, len, tlv, offset + tagLen + lenOfLen);
    }
    
    public static TLVList parseTLVList(final byte[] tlv, final int offset, final int maxLen) {
        final TLVList tlvList = new TLVList(tlv, offset, maxLen, new SimpleTLVParser());
        return tlvList;
    }
}
