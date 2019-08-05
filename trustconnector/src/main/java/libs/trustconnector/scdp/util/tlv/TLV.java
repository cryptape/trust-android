package libs.trustconnector.scdp.util.tlv;

import libs.trustconnector.scdp.util.*;

import libs.trustconnector.scdp.util.ByteArray;
import libs.trustconnector.scdp.util.Util;

public class TLV
{
    protected Tag tag;
    protected Length len;
    protected ByteArray value;
    
    protected TLV() {
    }
    
    public TLV(final Tag tag, final Length len, final byte[] v) {
        this.tag = tag;
        this.len = len;
        this.value = new ByteArray(v);
    }
    
    public TLV(final Tag tag, final Length len, final byte[] v, final int vOff) {
        this.tag = tag;
        this.len = len;
        this.value = new ByteArray(v, vOff, len.getValue());
    }
    
    @Override
    public boolean equals(final Object tlv) {
        if (super.equals(tlv)) {
            return true;
        }
        if (tlv instanceof TLV) {
            final byte[] a = ((TLV)tlv).toBytes();
            final byte[] b = this.toBytes();
            if (a.length == b.length && Util.arrayCompare(a, 0, b, 0, a.length)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean compareTag(final Tag tag) {
        return tag.equals(this.tag);
    }
    
    public boolean compareTag(final TLV tlv) {
        return this.tag.equals(tlv.tag);
    }
    
    public Tag getTag() {
        return this.tag;
    }
    
    public Length getLength() {
        return this.len;
    }
    
    public int getValueLen() {
        return this.value.length();
    }
    
    public int length() {
        return this.tag.length() + this.len.length() + this.value.length();
    }
    
    public byte[] getValue() {
        return this.value.toBytes();
    }
    
    public byte[] toBytes() {
        final ByteArray t = new ByteArray(this.tag.toBytes());
        t.append(this.len.toBytes());
        if (this.value != null) {
            t.append(this.value);
        }
        return t.toBytes();
    }
    
    public int toBytes(final byte[] b, final int offset) {
        final ByteArray t = new ByteArray(this.tag.toBytes());
        t.append(this.len.toBytes());
        if (this.value != null) {
            t.append(this.value);
        }
        t.getBytes(0, b, offset, t.length());
        return t.length();
    }
    
    public void updateValue(final byte[] value) {
        if (value == null) {
            this.updateValue(null, 0, 0);
        }
        else {
            this.updateValue(value, 0, value.length);
        }
    }
    
    public void updateValue(final byte[] value, final int offset, final int length) {
        if (value == null) {
            this.value.reinit();
            this.len.setValue(0);
            return;
        }
        this.len.setValue(length);
        this.value = new ByteArray(value, offset, length);
    }
    
    public void appendValue(final byte value) {
        final byte[] v = { value };
        this.appendValue(v);
    }
    
    public void appendValue(final byte[] value) {
        this.appendValue(value, 0, (value == null) ? 0 : value.length);
    }
    
    public void appendValue(final byte[] value, final int offset, final int length) {
        if (value == null || length == 0) {
            return;
        }
        if (offset < 0 || length < 0 || offset + length > value.length) {
            return;
        }
        if (this.value == null) {
            this.value = new ByteArray(value, offset, length);
        }
        else {
            this.value.append(value, offset, length);
        }
        this.len.setValue(this.len.getValue() + length);
    }
    
    @Override
    public String toString() {
        final byte[] a = this.toBytes();
        return ByteArray.convert(a);
    }
}
