package libs.trustconnector.scdp.util.tlv;

import libs.trustconnector.scdp.util.tlv.bertlv.*;
import libs.trustconnector.scdp.util.*;
import java.util.*;

import libs.trustconnector.scdp.util.ByteArray;
import libs.trustconnector.scdp.util.Util;
import libs.trustconnector.scdp.util.tlv.bertlv.BERTag;

public class TagList extends Vector<Tag>
{
    private static final long serialVersionUID = -7234212493024341329L;
    
    public TagList() {
    }
    
    public TagList(final String tagList) {
        final byte[] tagListV = ByteArray.convert(tagList);
        int tagLenN;
        for (int tagsLen = tagListV.length, offset = 0; tagsLen > 0; tagsLen -= tagLenN, offset += tagLenN) {
            final Tag tag = new BERTag();
            tagLenN = tag.fromBytes(tagListV, offset, tagsLen);
            if (tagLenN == -1) {
                break;
            }
            this.add(tag);
        }
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (super.equals(obj)) {
            return true;
        }
        if (!(obj instanceof TagList)) {
            return false;
        }
        final TagList tagList = (TagList)obj;
        final int c = this.size();
        if (tagList.size() != c) {
            return false;
        }
        for (int i = 0; i < c; ++i) {
            final Tag a = this.get(i);
            final Tag b = tagList.get(i);
            final byte[] at = a.toBytes();
            final byte[] bt = b.toBytes();
            if (at.length != bt.length) {
                return false;
            }
            if (!Util.arrayCompare(at, 0, bt, 0, at.length)) {
                return false;
            }
        }
        return true;
    }
    
    public int length() {
        int length = 0;
        for (final Tag a : this) {
            length += a.length();
        }
        return length;
    }
    
    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder();
        for (int c = this.size(), i = 0; i < c; ++i) {
            final Tag a = this.get(i);
            s.append(ByteArray.convert(a.toBytes()));
        }
        return s.toString();
    }
}
