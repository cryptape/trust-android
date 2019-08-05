package com.trustconnector.scdp.util.tlv;

import java.util.*;
import com.trustconnector.scdp.util.*;
import com.trustconnector.scdp.util.tlv.bertlv.*;

public class TLVTree
{
    TLVTreeItem root;
    
    public TLVTree() {
        this.root = new TLVTreeItem(null, null, new BERTLVParser());
    }
    
    public TLVTree(final byte[] tlvs) {
        this.fromBytes(tlvs, 0, tlvs.length, new BERTLVParser());
    }
    
    public TLVTree(final byte[] tlvs, final int offset, final int length) {
        this();
        this.fromBytes(tlvs, offset, length, new BERTLVParser());
    }
    
    public TLVTree(final byte[] tlvs, final TLVParser tlvParser) {
        this.fromBytes(tlvs, 0, tlvs.length, tlvParser);
    }
    
    public TLVTree(final byte[] tlvs, final int offset, final int length, final TLVParser tlvParser) {
        this.fromBytes(tlvs, offset, length, tlvParser);
    }
    
    public int fromBytes(final byte[] tlvs, final int offset, final int length, final TLVParser tlvBuilder) {
        this.root = new TLVTreeItem(null, null, tlvBuilder);
        final TLVList t = new TLVList(tlvs, offset, length, tlvBuilder);
        for (int count = t.size(), i = 0; i < count; ++i) {
            final TLV tlv = t.getTLV(i);
            final TLVTreeItem item = new TLVTreeItem(this.root, tlv, tlvBuilder);
            this.root.children.addElement(item);
        }
        return t.length();
    }
    
    public byte[] toBytes() {
        return this.root.toBytes();
    }
    
    public TLVTreeItem findTLV(final TagList tagPath) {
        final int c = tagPath.size();
        TLVTreeItem t = this.root;
        TLVTreeItem res = null;
        for (int i = 0; i < c; ++i) {
            res = t.getChild(tagPath.elementAt(i));
            if (res == null) {
                return null;
            }
            t = res;
        }
        return res;
    }
    
    public Vector<TLVTreeItem> findTLVs(final TagList tagPath) {
        final int c = tagPath.size() - 1;
        TLVTreeItem t = this.root;
        TLVTreeItem res = null;
        for (int i = 0; i < c; ++i) {
            res = t.getChild(tagPath.elementAt(i));
            if (res == null) {
                return null;
            }
            t = res;
        }
        if (res != null) {
            return res.getChildren(tagPath.get(c));
        }
        return null;
    }
    
    public TLVTreeItem findTLV(final TagList tagPath, final int index) {
        final int c = tagPath.size();
        TLVTreeItem t = this.root;
        TLVTreeItem res = t.getChild(tagPath.elementAt(0), index);
        for (int i = 1; i < c; ++i) {
            res = t.getChild(tagPath.elementAt(i));
            if (res == null) {
                return null;
            }
            t = res;
        }
        return res;
    }
    
    public int getTLVNum(final Tag tagPath) {
        return this.root.getChildnum(tagPath);
    }
    
    public boolean updateTLVValue(final TagList tagPath, final byte[] newValue) {
        final int c = tagPath.size();
        TLVTreeItem t = this.root;
        boolean bAdd = false;
        for (int i = 0; i < c; ++i) {
            TLVTreeItem res = t.getChild(tagPath.elementAt(i));
            if (res == null) {
                final TLV tlv = new BERTLV(tagPath.elementAt(i), null);
                res = new TLVTreeItem(this.root, tlv, new BERTLVParser());
                t.children.addElement(res);
                bAdd = true;
            }
            t = res;
        }
        t.updateValue(newValue);
        return bAdd;
    }
    
    public boolean updateTLVValue(final TagList tagPath, final String newValue) {
        return this.updateTLVValue(tagPath, ByteArray.convert(newValue));
    }
    
    public boolean updateTLVValue(final TagList tagPath, final int value) {
        return this.updateTLVValue(tagPath, Util.intToBytes(value));
    }
    
    public boolean updateTLVValue(final TagList tagPath, final int value, final int expValueLen) {
        return this.updateTLVValue(tagPath, Util.intToBytes(value, expValueLen));
    }

    public static void main(String[] param) {
        byte[] t = new byte[]{98, 39, -126, 2, 120, 33, -125, 2, 63, 0, -91, 7, -128, 1, 97, -64, 2, 0, 1, -118, 1, 5, -117, 3, 47, 6, 12, -58, 12, -112, 1, -128, -107, 1, 0, -125, 1, 17, -125, 1, 1};
        byte[] pcmd = new byte[]{-48, 28, -127, 3, 1, 37, 0, -126, 2, -127, -126, 5, 15, -128, 0, 67, 0, 83, 0, 73, 0, 77, 83, 97, 94, -108, 117, 40, -113, 0};
        TLVTree tree = new TLVTree();
        tree.updateTLVValue(new TagList("7F2193"), ByteArray.convert("1122334455"));
        tree.updateTLVValue(new TagList("7F2142"), ByteArray.convert("1122334455"));
        tree.updateTLVValue(new TagList("7F215F20"), ByteArray.convert("1122334455"));
        tree.updateTLVValue(new TagList("7F2195"), Util.intToBytes(1));
        tree.updateTLVValue(new TagList("7F215F25"), ByteArray.convert("20171030"));
        tree.updateTLVValue(new TagList("7F215F24"), ByteArray.convert("20171130"));
        tree.updateTLVValue(new TagList("7F2173C8"), Util.intToBytes(1));
        tree.updateTLVValue(new TagList("7F2173C9"), ByteArray.convert("00112233445566778899AABBCCDDEEFF00112233445566778899AABBCCDDEEFF"));
        tree.updateTLVValue(new TagList("7F217F49"), ByteArray.convert("11223344"));
        byte[] a = tree.toBytes();
        System.out.println(ByteArray.convert(a));
        TLVTreeItem root = tree.findTLV(new TagList("7F21"));
        byte[] signdata = root.getValue();
        System.out.println(ByteArray.convert(signdata));
        int len = tree.fromBytes(t, 0, t.length, new BERTLVParser());
        System.out.println(len);
        System.out.println("src:" + ByteArray.convert(t));
        System.out.println("dst:" + ByteArray.convert(tree.toBytes()));
        TagList tagPath = new TagList();
        tagPath.add(new BERTag(98));
        tagPath.add(new BERTag(130));
        TLVTreeItem item = tree.findTLV(tagPath);
        System.out.println(ByteArray.convert(item.getValue()));
        item.updateValue(ByteArray.convert("7822"));
        System.out.println(ByteArray.convert(item.getValue()));
        System.out.println(ByteArray.convert(item.toBytes()));
        System.out.println("dst:" + ByteArray.convert(tree.toBytes()));
        tree.fromBytes(pcmd, 0, pcmd.length, new BERTLVParser());
        item = tree.findTLV(BERTLVBuilder.buildTagList("D081"));
        System.out.println(ByteArray.convert(item.getValue()));
        System.out.println(ByteArray.convert(item.toBytes()));
    }
}
