package com.trustconnector.scdp.util.tlv.bertlv;

import com.trustconnector.scdp.util.tlv.*;

public class BERTLVParser implements TLVParser
{
    @Override
    public TLV parse(final byte[] tlv, final int offset, final int maxLen) {
        return parseTLV(tlv, offset, maxLen);
    }
    
    public static TLV parseTLV(final byte[] tlv, final int offset, final int maxLen) {
        final Tag tag = new BERTag();
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
        return new TLV(tag, len, tlv, offset + tagLen + lenOfLen);
    }
    
    public static TLV parseTLV(final byte[] tlv) {
        return parseTLV(tlv, 0, tlv.length);
    }
    
    public static TLVList parseTLVList(final byte[] tlv, final int offset, final int maxLen) {
        final TLVList tlvList = new TLVList(tlv, offset, maxLen, new BERTLVParser());
        return tlvList;
    }
}
