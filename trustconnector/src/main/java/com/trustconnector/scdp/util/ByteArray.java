package com.trustconnector.scdp.util;

import com.trustconnector.scdp.util.tlv.bertlv.*;
import com.trustconnector.scdp.util.tlv.*;

public final class ByteArray
{
    private byte[] byteValue;
    static final int[] SHIFT_MASK;
    static final int[] SHIFT_MASK_RIGHT;
    private static final char[] charHex;
    
    public ByteArray() {
    }
    
    public ByteArray(final int initLen) {
        this.byteValue = new byte[initLen];
    }
    
    public ByteArray(final byte[] byr) {
        if (byr != null) {
            this.byteValue = byr.clone();
        }
    }
    
    public ByteArray(final byte[] byr, final int offset, final boolean bBERLV) {
        int length = byr[offset] & 0xFF;
        int vOff = 1;
        if (bBERLV) {
            if (length == 129) {
                length = (byr[offset + 1] & 0xFF);
                vOff = 2;
            }
            else if (length == 130) {
                length = (byr[offset + 1] & 0xFF);
                length <<= 8;
                length |= (byr[offset + 2] & 0xFF);
                vOff = 3;
            }
            else if (length == 131) {
                length = (byr[offset + 1] & 0xFF);
                length <<= 8;
                length |= (byr[offset + 2] & 0xFF);
                length <<= 8;
                length |= (byr[offset + 3] & 0xFF);
                vOff = 4;
            }
            else {
                if (length != 132) {
                    throw new DataFormatException("new ByteArray from Berlv failed:length overflow! max length is 0xFFFFFFFF");
                }
                length = (byr[offset + 1] & 0xFF);
                length <<= 8;
                length |= (byr[offset + 2] & 0xFF);
                length <<= 8;
                length |= (byr[offset + 3] & 0xFF);
                length <<= 8;
                length |= (byr[offset + 4] & 0xFF);
                vOff = 5;
            }
        }
        System.arraycopy(byr, offset + vOff, this.byteValue = new byte[length], 0, length);
    }
    
    public ByteArray(final byte[] byr, final int offset, final int length) {
        this.byteValue = new byte[length];
        if (byr != null) {
            System.arraycopy(byr, offset, this.byteValue, 0, length);
        }
    }
    
    public ByteArray(final String hex) {
        this.byteValue = convert(hex, StringFormat.HEX);
    }
    
    public ByteArray(final String str, final StringFormat format) {
        this.byteValue = convert(str, format);
    }
    
    public int length() {
        if (this.byteValue == null) {
            return 0;
        }
        return this.byteValue.length;
    }
    
    public void setByte(final int index, final int v) {
        this.byteValue[index] = (byte)v;
    }
    
    public void setInt2(final int index, final int v) {
        this.byteValue[index] = (byte)(v >> 8);
        this.byteValue[index + 1] = (byte)(v & 0xFF);
    }
    
    public void setInt3(final int index, final int v) {
        this.byteValue[index] = (byte)(v >> 16 & 0xFF);
        this.byteValue[index + 1] = (byte)(v >> 8 & 0xFF);
        this.byteValue[index + 2] = (byte)(v & 0xFF);
    }
    
    public void setInt(final int index, final int v) {
        this.setInt4(index, v);
    }
    
    public void setInt4(final int index, final int v) {
        this.byteValue[index + 0] = (byte)(v >> 24 & 0xFF);
        this.byteValue[index + 1] = (byte)(v >> 16 & 0xFF);
        this.byteValue[index + 2] = (byte)(v >> 8 & 0xFF);
        this.byteValue[index + 3] = (byte)(v & 0xFF);
    }
    
    public byte getByte(final int index) {
        return this.byteValue[index];
    }
    
    public int getUnsignedByte(final int index) {
        return this.byteValue[index] & 0xFF;
    }
    
    public int getInt2(final int index) {
        int v = this.byteValue[index] & 0xFF;
        v <<= 8;
        v |= (this.byteValue[index + 1] & 0xFF);
        return v;
    }
    
    public int getInt3(final int index) {
        int v = this.byteValue[index] & 0xFF;
        v <<= 8;
        v |= (this.byteValue[index + 1] & 0xFF);
        v <<= 8;
        v |= (this.byteValue[index + 2] & 0xFF);
        return v;
    }
    
    public int getInt(final int index) {
        return this.getInt4(index);
    }
    
    public int getInt4(final int index) {
        int v = this.byteValue[index] & 0xFF;
        v <<= 8;
        v |= (this.byteValue[index + 1] & 0xFF);
        v <<= 8;
        v |= (this.byteValue[index + 2] & 0xFF);
        v <<= 8;
        v |= (this.byteValue[index + 3] & 0xFF);
        return v;
    }
    
    public int getInt(final int index, final int len) {
        return Util.bytesToInt(this.byteValue, index, len);
    }
    
    public byte[] toBytes() {
        if (this.byteValue == null) {
            return null;
        }
        return this.byteValue.clone();
    }
    
    public byte[] toBytes(final int index, final int length) {
        final byte[] v = new byte[length];
        System.arraycopy(this.byteValue, index, v, 0, length);
        return v;
    }
    
    public void getBytes(final int index, final byte[] out, final int offset, final int length) {
        System.arraycopy(this.byteValue, index, out, offset, length);
    }
    
    public void setBytes(final int index, final byte[] content) {
        System.arraycopy(content, 0, this.byteValue, index, content.length);
    }
    
    public void setBytes(final int index, final byte[] content, final int offset, final int length) {
        System.arraycopy(content, offset, this.byteValue, index, length);
    }
    
    public void and(final ByteArray byr) {
        if (this.byteValue == null || byr.byteValue == null) {
            return;
        }
        final int length1 = this.byteValue.length;
        final int length2 = byr.byteValue.length;
        for (int length3 = (length1 > length2) ? length2 : length1, i = 0; i < length3; ++i) {
            final byte[] byteValue = this.byteValue;
            final int n = i;
            byteValue[n] &= byr.byteValue[i];
        }
    }
    
    public void or(final byte[] byr, final int offset, final int length) {
        if (this.byteValue == null || byr == null) {
            return;
        }
        final int length2 = this.byteValue.length;
        final int length3 = length;
        for (int len = (length2 > length3) ? length3 : length2, i = 0; i < len; ++i) {
            final byte[] byteValue = this.byteValue;
            final int n = i;
            byteValue[n] |= byr[i];
        }
    }
    
    public void or(final ByteArray byr) {
        this.or(byr.byteValue, 0, (byr == null) ? 0 : byr.byteValue.length);
    }
    
    public void xor(final byte[] byr, final int offset, final int length) {
        if (this.byteValue == null || byr == null) {
            return;
        }
        final int length2 = this.byteValue.length;
        final int length3 = length;
        for (int len = (length2 > length3) ? length3 : length2, i = 0; i < len; ++i) {
            final byte[] byteValue = this.byteValue;
            final int n = i;
            byteValue[n] ^= byr[i];
        }
    }
    
    public void xor(final ByteArray byr) {
        this.xor(byr.byteValue, 0, (byr == null) ? 0 : byr.byteValue.length);
    }
    
    public void not() {
        if (this.byteValue == null) {
            return;
        }
        for (int length = this.byteValue.length, i = 0; i < length; ++i) {
            this.byteValue[i] ^= -1;
        }
    }
    
    public byte[] left(final int length) {
        final byte[] v = new byte[length];
        System.arraycopy(this.byteValue, 0, v, 0, length);
        return v;
    }
    
    public byte[] right(final int length) {
        final byte[] v = new byte[length];
        System.arraycopy(this.byteValue, this.byteValue.length - length, v, 0, length);
        return v;
    }
    
    public boolean isStartWith(final byte[] start) {
        return this.byteValue != null && start != null && this.byteValue.length >= start.length && compare(this.byteValue, 0, start, 0, start.length) == 0;
    }
    
    public boolean isEndWith(final byte[] end) {
        return this.byteValue != null && end != null && this.byteValue.length >= end.length && compare(this.byteValue, this.byteValue.length - end.length, end, 0, end.length) == 0;
    }
    
    public int find(final ByteArray target) {
        return this.find(target, 0);
    }
    
    public int find(final ByteArray target, final int startIndex) {
        if (target == null) {
            return -1;
        }
        return this.find(target.byteValue, 0, target.length(), startIndex);
    }
    
    public int find(final byte[] target, final int startIndex) {
        return this.find(target, 0, target.length, startIndex);
    }
    
    public int find(final byte[] target, final int tarOff, final int tarLen, final int startIndex) {
        if (this.byteValue == null || target == null) {
            return -1;
        }
        final int targetLen = target.length;
        final int srcLen = this.byteValue.length;
        if (srcLen < targetLen) {
            return -1;
        }
        for (int offset = startIndex; offset + targetLen < srcLen; ++offset) {
            if (compare(this.byteValue, offset, target, 0, targetLen) == 0) {
                return offset;
            }
        }
        return -1;
    }
    
    public void append(final ByteArray append) {
        this.append(append.byteValue);
    }
    
    public void append(final byte a) {
        if (this.byteValue == null) {
            (this.byteValue = new byte[1])[0] = a;
            return;
        }
        final int aLen = this.byteValue.length;
        final byte[] newV = new byte[aLen + 1];
        System.arraycopy(this.byteValue, 0, newV, 0, aLen);
        newV[aLen] = a;
        this.byteValue = newV;
    }
    
    public void append(final int v, final int byteLen) {
        this.append(Util.intToBytes(v, byteLen));
    }
    
    public void append(final byte[] c) {
        if (c == null) {
            return;
        }
        final int aLen = c.length;
        int bLen = 0;
        if (this.byteValue != null) {
            bLen = this.byteValue.length;
            final byte[] newV = new byte[aLen + bLen];
            System.arraycopy(this.byteValue, 0, newV, 0, bLen);
            System.arraycopy(c, 0, newV, bLen, aLen);
            this.byteValue = newV;
            return;
        }
        this.byteValue = c.clone();
    }
    
    public void append(final byte[] c, final int offset, final int length) {
        if (c == null) {
            return;
        }
        final int aLen = length;
        int bLen = 0;
        if (this.byteValue != null) {
            bLen = this.byteValue.length;
        }
        final byte[] newV = new byte[aLen + bLen];
        if (this.byteValue != null) {
            System.arraycopy(this.byteValue, 0, newV, 0, bLen);
        }
        System.arraycopy(c, offset, newV, bLen, aLen);
        this.byteValue = newV;
    }
    
    public void append(final String hex) {
        this.append(convert(hex));
    }
    
    public void insert(final ByteArray value, final int insPos) {
        this.insert(value.byteValue, insPos);
    }
    
    public void insert(final byte[] value, int insPos) {
        if (this.byteValue == null) {
            this.byteValue = value.clone();
            return;
        }
        final int srcLen = this.byteValue.length;
        if (insPos > srcLen) {
            insPos = srcLen;
        }
        final int tarLen = value.length;
        final byte[] newV = new byte[srcLen + tarLen];
        if (insPos > 0) {
            System.arraycopy(this.byteValue, 0, newV, 0, insPos);
        }
        System.arraycopy(value, 0, newV, insPos, tarLen);
        if (srcLen - insPos > 0) {
            System.arraycopy(this.byteValue, insPos, newV, insPos + tarLen, srcLen - insPos);
        }
        this.byteValue = newV;
    }
    
    public boolean remove(final int startPos, final int length) {
        if (startPos < 0 || length < 0) {
            return false;
        }
        if (length == 0) {
            return true;
        }
        if (this.byteValue == null) {
            return false;
        }
        final int orgLen = this.byteValue.length;
        if (startPos + length > orgLen) {
            return false;
        }
        final byte[] newV = new byte[orgLen - length];
        System.arraycopy(this.byteValue, 0, newV, 0, startPos);
        if (startPos + length < orgLen) {
            System.arraycopy(this.byteValue, startPos + length, newV, startPos, orgLen - startPos - length);
        }
        this.byteValue = newV;
        return true;
    }
    
    public boolean remove(final int count) {
        if (count == 0) {
            return true;
        }
        if (this.byteValue == null) {
            return false;
        }
        final int orgLen = this.byteValue.length;
        if (count > orgLen) {
            return false;
        }
        if (orgLen == count) {
            this.byteValue = null;
            return true;
        }
        final byte[] newV = new byte[orgLen - count];
        System.arraycopy(this.byteValue, 0, newV, 0, orgLen - count);
        this.byteValue = newV;
        return true;
    }
    
    public int compare(final int index, final byte[] dst, final int dstOff, final int length) {
        final byte[] t = this.toBytes(index, length);
        return compare(t, 0, dst, dstOff, length);
    }
    
    public void reinit() {
        this.byteValue = null;
    }
    
    public void clearContent() {
        for (int totalLen = this.byteValue.length, i = 0; i < totalLen; ++i) {
            this.byteValue[i] = 0;
        }
    }
    
    public byte[][] split(final int len) {
        if (this.byteValue == null) {
            return null;
        }
        final int lenV = this.byteValue.length;
        int f = lenV / len;
        if (lenV % len != 0) {
            ++f;
        }
        final byte[][] a = new byte[f][];
        int leftLen = lenV;
        int offset = 0;
        for (int i = 0; i < f; ++i) {
            final int aLen = (leftLen > len) ? len : leftLen;
            a[i] = new byte[aLen];
            System.arraycopy(this.byteValue, offset, a[i], 0, aLen);
            leftLen -= aLen;
            offset += aLen;
        }
        return a;
    }
    
    public byte[][] split(final int firstItemLen, final int len) {
        if (this.byteValue == null) {
            return null;
        }
        int lenV = this.byteValue.length;
        final int itemLen = (firstItemLen > lenV) ? lenV : firstItemLen;
        final byte[] firstItem = new byte[itemLen];
        System.arraycopy(this.byteValue, 0, firstItem, 0, itemLen);
        lenV -= itemLen;
        int f = 0;
        if (lenV != 0) {
            f = lenV / len;
            if (lenV % len != 0) {
                ++f;
            }
        }
        final byte[][] a = new byte[++f][];
        a[0] = firstItem;
        int leftLen = lenV;
        int offset = itemLen;
        for (int i = 1; i < f; ++i) {
            final int aLen = (leftLen > len) ? len : leftLen;
            a[i] = new byte[aLen];
            System.arraycopy(this.byteValue, offset, a[i], 0, aLen);
            leftLen -= aLen;
            offset += aLen;
        }
        return a;
    }
    
    public boolean shiftLeft(final int bitCount) {
        if (this.byteValue == null) {
            return false;
        }
        if (bitCount < 1 || bitCount > 7) {
            return false;
        }
        final int vLen = this.byteValue.length;
        final int expByteLen = vLen + 1;
        final int shiftBitN = 8 - bitCount;
        final byte[] t = new byte[expByteLen];
        t[0] = (byte)(this.byteValue[0] >> shiftBitN & ~ByteArray.SHIFT_MASK[bitCount - 1]);
        for (int i = 1; i < expByteLen - 1; ++i) {
            t[i] = (byte)((this.byteValue[i - 1] << bitCount & ByteArray.SHIFT_MASK[bitCount - 1]) | (this.byteValue[i] >> shiftBitN & ~ByteArray.SHIFT_MASK[bitCount - 1]));
        }
        t[expByteLen - 1] = (byte)(this.byteValue[expByteLen - 1 - 1] << bitCount & ByteArray.SHIFT_MASK[bitCount - 1]);
        this.byteValue = t;
        return true;
    }
    
    public boolean shiftRight(final int bitCount) {
        if (this.byteValue == null) {
            return false;
        }
        if (bitCount < 1 || bitCount > 7) {
            return false;
        }
        final int vLen = this.byteValue.length;
        final int expByteLen = vLen + 1;
        final int shiftBitN = 8 - bitCount;
        final byte[] t = new byte[expByteLen];
        t[0] = (byte)(this.byteValue[0] >> bitCount & ByteArray.SHIFT_MASK_RIGHT[bitCount - 1]);
        for (int i = 1; i < expByteLen - 1; ++i) {
            t[i] = (byte)((this.byteValue[i] >> bitCount & ByteArray.SHIFT_MASK_RIGHT[bitCount - 1]) | (this.byteValue[i - 1] << shiftBitN & ~ByteArray.SHIFT_MASK_RIGHT[bitCount - 1]));
        }
        t[expByteLen - 1] = (byte)(this.byteValue[expByteLen - 1 - 1] << shiftBitN & ~ByteArray.SHIFT_MASK_RIGHT[bitCount - 1]);
        this.byteValue = t;
        return true;
    }
    
    @Override
    public boolean equals(final Object obj) {
        byte[] dst = null;
        if (obj instanceof ByteArray) {
            final ByteArray a = (ByteArray)obj;
            dst = a.byteValue;
        }
        else if (obj instanceof byte[]) {
            dst = (byte[])obj;
        }
        return dst != null && dst.length == this.byteValue.length && compare(this.byteValue, 0, dst, 0, this.byteValue.length) == 0;
    }
    
    @Override
    public String toString() {
        return this.toString(StringFormat.HEX);
    }
    
    public String toString(final StringFormat format) {
        if (this.byteValue == null) {
            return "";
        }
        return convert(this.byteValue, 0, this.byteValue.length, format);
    }
    
    public byte[] toLV(final boolean bBERlv) {
        if (bBERlv) {
            return this.toBERLV();
        }
        return this.toLV();
    }
    
    public byte[] toLV() {
        if (this.byteValue == null) {
            final byte[] a = { 0 };
            return a;
        }
        final int len = this.byteValue.length;
        if (len > 255) {
            throw new DataFormatException("toLV failed:length overflow! max length is 255");
        }
        final byte[] a2 = new byte[len + 1];
        a2[0] = (byte)len;
        System.arraycopy(this.byteValue, 0, a2, 1, len);
        return a2;
    }
    
    public byte[] toBERLV() {
        if (this.byteValue == null) {
            final byte[] a = { 0 };
            return a;
        }
        final LV lv = BERLVBuilder.buildLV(this.byteValue, 0, this.byteValue.length);
        return lv.toBytes();
    }
    
    public static boolean checkStrFormat(final String hex, final StringFormat format) {
        final int length = hex.length();
        if (format == StringFormat.HEX) {
            for (int i = 0; i < length; ++i) {
                final char c = hex.charAt(i);
                if ((c < '0' || c > '9') && (c < 'a' || c > 'F') && (c < 'a' || c > 'f') && c != ' ' && c != '\t' && c != '\n' && c != '\r') {
                    return false;
                }
            }
            return true;
        }
        if (format == StringFormat.UCS2) {
            return true;
        }
        if (format == StringFormat.ASCII) {
            for (int i = 0; i < length; ++i) {
                final char a = hex.charAt(i);
                if (a >= '\0' && a > '\u00ff') {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    public static byte[] convert(final String hex) {
        return convert(hex, StringFormat.HEX);
    }
    
    public static byte[] convert(String hex, final StringFormat format) {
        int length = hex.length();
        byte[] v = null;
        int validLen = 0;
        if (format == StringFormat.HEX) {
            hex = Util.stringTrimAll(hex);
            length = hex.length();
            if (length % 2 != 0) {
                DataFormatException.throwIt("hex string length error:" + hex);
            }
            v = new byte[(length + 1) / 2];
            for (int i = 0; i < length; i += 2) {
                final byte a = Util.hexCharToByte(hex.charAt(i));
                final byte b = Util.hexCharToByte(hex.charAt(i + 1));
                v[validLen++] = (byte)(a << 4 | b);
            }
        }
        else if (format == StringFormat.UCS2) {
            v = new byte[(length + 1) * 2];
            for (int i = 0; i < length; ++i) {
                final char a2 = hex.charAt(i);
                v[validLen++] = (byte)(a2 >> 8 & 0xFF);
                v[validLen++] = (byte)(a2 & '\u00ff');
            }
        }
        else if (format == StringFormat.ASCII) {
            v = new byte[length];
            for (int i = 0; i < length; ++i) {
                final char a2 = hex.charAt(i);
                if (a2 < '\0' || a2 > '\u00ff') {
                    throw new DataFormatException("convert from acsii to byte array failed with invalid char=" + a2 + "at index=" + i);
                }
                v[validLen++] = (byte)a2;
            }
        }
        final byte[] value = new byte[validLen];
        System.arraycopy(v, 0, value, 0, validLen);
        return value;
    }
    
    public static String convert(final byte[] data, final int offset, final int length) {
        return convert(data, offset, length, StringFormat.HEX);
    }
    
    public static char intToChar(final int v) {
        if (v > 15 || v < 0) {
            return '\uffff';
        }
        return ByteArray.charHex[v];
    }
    
    public static String convert(final byte[] data, final int offset, final int length, final StringFormat format) {
        final StringBuilder builder = new StringBuilder();
        if (format == StringFormat.HEX) {
            for (int end = offset + length, i = offset; i < end; ++i) {
                char a = ByteArray.charHex[data[i] >> 4 & 0xF];
                builder.append(a);
                a = ByteArray.charHex[data[i] & 0xF];
                builder.append(a);
            }
        }
        else if (format == StringFormat.UCS2) {
            for (int end = offset + length, i = offset; i < end; i += 2) {
                final char a = (char)((data[i] << 8 & 0xFF00) | (data[i + 1] & 0xFF));
                builder.append(a);
            }
        }
        else if (format == StringFormat.ASCII) {
            for (int end = offset + length, i = offset; i < end; ++i) {
                final char a = (char)data[i];
                if (a < '\0' || a > '\u00ff') {
                    throw new DataFormatException("convert from acsii to byte array failed with invalid char=" + a + "at index=" + i);
                }
                builder.append(a);
            }
        }
        else if (format == StringFormat.ASCII_7_BIT) {
            final byte[] d = Util.bit7ToBit8(data, offset, length);
            for (int end2 = offset + length, j = offset; j < end2; ++j) {
                final char a2 = (char)d[j];
                if (a2 < '\0' || a2 > '\u00ff') {
                    throw new DataFormatException("convert from acsii to byte array failed with invalid char=" + a2 + "at index=" + j);
                }
                builder.append(a2);
            }
        }
        return builder.toString();
    }
    
    public static String convert(final byte[] data, final StringFormat format) {
        if (data == null) {
            return "";
        }
        return convert(data, 0, data.length, format);
    }
    
    public static String convert(final byte[] data) {
        if (data == null) {
            return "";
        }
        return convert(data, 0, data.length, StringFormat.HEX);
    }
    
    public static boolean compare(final byte[] src, final byte[] dst) {
        if (src == null && dst == null) {
            return true;
        }
        if (src == null && dst != null) {
            return false;
        }
        if (dst == null && src != null) {
            return false;
        }
        if (dst.length != src.length) {
            return false;
        }
        for (int length = src.length, i = 0; i < length; ++i) {
            if (src[i] != dst[i]) {
                return false;
            }
        }
        return true;
    }
    
    public static int compare(final byte[] src, final int offset, final byte[] dst, final int doffset, final int length) {
        for (int i = 0; i < length; ++i) {
            if (src[offset + i] > dst[doffset + i]) {
                return 1;
            }
            if (src[offset + i] < dst[doffset + i]) {
                return -1;
            }
        }
        return 0;
    }
    
    public static boolean isAllNum(final byte[] src, final int off, final int length, final byte value) {
        for (int i = 0; i < length; ++i) {
            if (src[off + i] != value) {
                return false;
            }
        }
        return true;
    }
    
    public static void initContentWithInc(final byte[] src, final int off, final int length, byte initValue) {
        for (int i = 0; i < length; ++i) {
            src[off + i] = initValue;
            ++initValue;
        }
    }
    
    public static void xor(final byte[] src1, final int src1Off, final byte[] src2, final int src2Off, final byte[] dst, final int dstOff, final int length) {
        for (int i = 0; i < length; ++i) {
            dst[dstOff + i] = (byte)(src1[src1Off + i] ^ src2[src2Off + i]);
        }
    }
    
    public static void and(final byte[] src1, final int src1Off, final byte[] src2, final int src2Off, final byte[] dst, final int dstOff, final int length) {
        for (int i = 0; i < length; ++i) {
            dst[dstOff + i] = (byte)(src1[src1Off + i] & src2[dstOff + i]);
        }
    }
    
    public static void or(final byte[] src1, final int src1Off, final byte[] src2, final int src2Off, final byte[] dst, final int dstOff, final int length) {
        for (int i = 0; i < length; ++i) {
            dst[dstOff + i] = (byte)(src1[src1Off + i] | src2[dstOff + i]);
        }
    }
    
    public static void not(final byte[] src, final int src1Off, final byte[] dst, final int dstOff, final int length) {
        for (int i = 0; i < length; ++i) {
            dst[dstOff + i] = (byte)~src[src1Off + i];
        }
    }
    
    public static byte[] not(final byte[] src) {
        final int length = src.length;
        final byte[] dst = new byte[length];
        for (int i = 0; i < length; ++i) {
            dst[i] = (byte)~src[i];
        }
        return dst;
    }
    
    static {
        SHIFT_MASK = new int[] { 254, 252, 248, 240, 224, 192, 128 };
        SHIFT_MASK_RIGHT = new int[] { 127, 63, 31, 15, 7, 3, 1 };
        charHex = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    }
}
