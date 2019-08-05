package libs.trustconnector.scdp.util.tlv.bertlv;

import libs.trustconnector.scdp.util.tlv.*;
import libs.trustconnector.scdp.util.*;

import libs.trustconnector.scdp.util.ByteArray;
import libs.trustconnector.scdp.util.Util;
import libs.trustconnector.scdp.util.tlv.Length;
import libs.trustconnector.scdp.util.tlv.TLV;
import libs.trustconnector.scdp.util.tlv.Tag;
import libs.trustconnector.scdp.util.tlv.TagList;

public class BERTLVBuilder
{
    public static TLV buildTLV(final int tag) {
        return new TLV(new BERTag(tag), new BERLength(), null);
    }
    
    public static TLV buildTLV(final int tag, final byte value) {
        final byte[] v = { value };
        return new TLV(new BERTag(tag), new BERLength(1), v);
    }
    
    public static TLV buildTLV(final int tag, final byte[] value) {
        return buildTLV(tag, value, 0, (value == null) ? 0 : value.length);
    }
    
    public static TLV buildTLV(final int tag, final byte[] value, final int vOff, final int vLen) {
        final byte[] tagBytes = Util.intToBytes(tag);
        return buildTLV(tagBytes, 0, tagBytes.length, value, vOff, vLen);
    }
    
    public static TLV buildTLV(final byte[] tag, final byte[] value) {
        return buildTLV(tag, 0, tag.length, value, 0, value.length);
    }
    
    public static TLV buildTLV(final byte[] tag, final int tagOff, final int tagLen, final byte[] value, final int vOff, final int vLen) {
        final Tag tagT = new BERTag();
        final int tagLenN = tagT.fromBytes(tag, 0, tagLen);
        if (tagLenN == -1) {
            return null;
        }
        final Length len = new BERLength(vLen);
        return new TLV(tagT, len, value, 0);
    }
    
    public static TagList buildTagList(final byte[] tags, int offset, int tagsLen) {
        final TagList list = new TagList();
        while (tagsLen > 0) {
            final Tag tag = new BERTag();
            final int tagLenN = tag.fromBytes(tags, offset, tagsLen);
            if (tagLenN == -1) {
                break;
            }
            list.add(tag);
            tagsLen -= tagLenN;
            offset += tagLenN;
        }
        return list;
    }
    
    public static TagList buildTagList(final String tagList) {
        final byte[] ts = ByteArray.convert(tagList);
        return buildTagList(ts, 0, ts.length);
    }
}
