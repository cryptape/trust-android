package com.trustconnector.scdp.util.tlv;

import com.trustconnector.scdp.util.*;
import com.trustconnector.scdp.util.tlv.simpletlv.*;
import java.util.*;
import com.trustconnector.scdp.util.tlv.bertlv.*;

public class TLVList extends Vector<TLV>
{
    private static final long serialVersionUID = 1L;
    
    public static void main(final String[] param) {
        final byte[] rsp = ByteArray.convert("D01C8103012301820281820D0D04496E70757453637265656E3111020001");
        final TLVList list = new TLVList(rsp, 2, 28, new SimpleTLVParser());
        System.out.println(list.size());
        for (final TLV tlv : list) {
            System.out.println(ByteArray.convert(tlv.toBytes()));
        }
    }
    
    public TLVList() {
    }
    
    public TLVList(final byte[] tlvs) {
        this.fromBytes(tlvs, 0, tlvs.length, new BERTLVParser());
    }
    
    public TLVList(final byte[] tlvs, final int offset, final int length) {
        this.fromBytes(tlvs, offset, length, new BERTLVParser());
    }
    
    public TLVList(final byte[] tlvs, final TLVParser tlvBuilder) {
        this.fromBytes(tlvs, 0, tlvs.length, tlvBuilder);
    }
    
    public TLVList(final byte[] tlvs, final int offset, final int length, final TLVParser tlvBuilder) {
        this.fromBytes(tlvs, offset, length, tlvBuilder);
    }
    
    public int fromBytes(final byte[] tlvs, final int offset, final int length, final TLVParser tlvParser) {
        this.removeAllElements();
        int leftLen = length;
        int curOff = offset;
        try {
            while (leftLen > 0) {
                final TLV tlv = tlvParser.parse(tlvs, curOff, leftLen);
                if (tlv == null) {
                    return -1;
                }
                this.add(tlv);
                leftLen -= tlv.length();
                curOff += tlv.length();
            }
        }
        catch (Exception e) {
            return curOff - offset;
        }
        return curOff - offset;
    }
    
    public byte[] toBytes() {
        final ByteArray bytes = new ByteArray();
        for (final TLV tlv : this) {
            bytes.append(tlv.toBytes());
        }
        return bytes.toBytes();
    }
    
    public int find(final Tag tag) {
        return this.find(tag, 0);
    }
    
    public int find(final Tag tag, final int startPos) {
        for (int count = this.size(), i = startPos; i < count; ++i) {
            final TLV tlv = this.get(i);
            if (tlv.compareTag(tag)) {
                return i;
            }
        }
        return -1;
    }
    
    public TLV findTLV(final Tag tag) {
        return this.findTLV(tag, 0);
    }
    
    public TLV findTLV(final Tag tag, final int startPos) {
        final int pos = this.find(tag, startPos);
        if (pos == -1) {
            return null;
        }
        return this.get(pos);
    }
    
    public TLV getTLV(final int index) {
        final int count = this.size();
        if (index < count) {
            return this.get(index);
        }
        return null;
    }
    
    public boolean update(final TLV tlv) {
        final int i = this.find(tlv.getTag());
        if (i == -1) {
            this.add(tlv);
            return false;
        }
        this.set(i, tlv);
        return true;
    }
    
    public void remove(final Tag tag) {
        for (int count = this.size(), i = 0; i < count; ++i) {
            final TLV tlv = this.get(i);
            if (tlv.compareTag(tag)) {
                this.remove(i);
            }
        }
    }
    
    public void insert(final int index, final TLV tlv) {
        this.add(index, tlv);
    }
    
    @Override
    public String toString() {
        final byte[] tlvlist = this.toBytes();
        return ByteArray.convert(tlvlist);
    }
    
    public int length() {
        final int count = this.size();
        int totalLen = 0;
        for (int i = 0; i < count; ++i) {
            final TLV tlv = this.get(i);
            totalLen += tlv.length();
        }
        return totalLen;
    }
    
    public static TLVList parseFromBytes(final byte[] tlvs, final TLVParser tlvParser) {
        return parseFromBytes(tlvs, 0, tlvs.length, tlvParser);
    }
    
    public static TLVList parseFromBytes(final byte[] tlvs, final int offset, final int length, final TLVParser tlvParser) {
        final TLVList list = new TLVList();
        final int len = list.fromBytes(tlvs, offset, length, tlvParser);
        if (len == -1) {
            return null;
        }
        return list;
    }
}
