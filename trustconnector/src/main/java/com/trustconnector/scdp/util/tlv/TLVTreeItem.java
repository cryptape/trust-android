package com.trustconnector.scdp.util.tlv;

import java.util.*;
import com.trustconnector.scdp.util.*;

public class TLVTreeItem
{
    TLVTreeItem parent;
    Vector<TLVTreeItem> children;
    TLV tlv;
    TLVParser parser;
    
    public TLVTreeItem(final TLVTreeItem parent, final TLV tlv, final TLVParser parser) {
        this.parent = parent;
        this.tlv = tlv;
        this.parser = parser;
        this.children = new Vector<TLVTreeItem>();
        if (tlv != null) {
            final byte[] v = tlv.getValue();
            if (v != null) {
                this.updateValue(v);
            }
        }
    }
    
    public TLVTreeItem getChild(final int index) {
        if (index < this.children.size()) {
            return this.children.elementAt(index);
        }
        return null;
    }
    
    public TLVTreeItem getChild(final Tag tag) {
        return this.getChild(tag, 0);
    }
    
    public TLVTreeItem getChild(final Tag tag, final int index) {
        int e = 0;
        for (final TLVTreeItem t : this.children) {
            if (tag.equals(t.tlv.getTag())) {
                if (e == index) {
                    return t;
                }
                ++e;
            }
        }
        return null;
    }
    
    public Vector<TLVTreeItem> getChildren(final Tag tag) {
        final Vector<TLVTreeItem> items = new Vector<TLVTreeItem>();
        for (final TLVTreeItem t : this.children) {
            if (tag.equals(t.tlv.getTag())) {
                items.add(t);
            }
        }
        return items;
    }
    
    public int getChildnum(final Tag tag) {
        int num = 0;
        for (final TLVTreeItem t : this.children) {
            if (tag.equals(t.tlv.getTag())) {
                ++num;
            }
        }
        return num;
    }
    
    public int getChildCount() {
        return this.children.size();
    }
    
    public TLVTreeItem getParent() {
        return this.parent;
    }
    
    public void updateValue(final byte[] value) {
        int count = this.children.size();
        if (count > 0) {
            this.children.removeAllElements();
            this.tlv.updateValue(null);
        }
        boolean bSubTLV = false;
        final TLVList ts = new TLVList();
        if (value.length > 2 && ts.fromBytes(value, 0, value.length, this.parser) == value.length) {
            bSubTLV = true;
        }
        if (bSubTLV) {
            count = ts.size();
            for (int i = 0; i < count; ++i) {
                final TLV tlv = ts.getTLV(i);
                final TLVTreeItem item = new TLVTreeItem(this, tlv, this.parser);
                this.children.addElement(item);
            }
        }
        else {
            this.tlv.updateValue(value);
        }
    }
    
    public byte[] getValue() {
        final Iterator<TLVTreeItem> ite = this.children.iterator();
        if (ite.hasNext()) {
            final ByteArray a = new ByteArray();
            while (ite.hasNext()) {
                final TLVTreeItem t = ite.next();
                a.append(t.toBytes());
            }
            return a.toBytes();
        }
        return this.tlv.getValue();
    }
    
    public void appendChild(final TLV tlv) {
        final TLVTreeItem item = new TLVTreeItem(this, tlv, this.parser);
        this.children.addElement(item);
    }
    
    public void removeChild(final Tag tag) {
        final Iterator<TLVTreeItem> ite = this.children.iterator();
        while (ite.hasNext()) {
            final TLVTreeItem t = ite.next();
            if (t.tlv.compareTag(tag)) {
                ite.remove();
            }
        }
    }
    
    public byte[] toBytes() {
        if (this.tlv == null || this.parent == null) {
            final Iterator<TLVTreeItem> ite = this.children.iterator();
            final ByteArray a = new ByteArray();
            while (ite.hasNext()) {
                final TLVTreeItem t = ite.next();
                a.append(t.toBytes());
            }
            return a.toBytes();
        }
        final Iterator<TLVTreeItem> ite = this.children.iterator();
        if (ite.hasNext()) {
            this.tlv.updateValue(null);
            while (ite.hasNext()) {
                final TLVTreeItem t2 = ite.next();
                this.tlv.appendValue(t2.toBytes());
            }
        }
        return this.tlv.toBytes();
    }
}
