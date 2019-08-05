package libs.trustconnector.scdp.util;

import java.util.regex.*;
import java.security.*;
import java.util.*;
import java.text.*;

public final class Util
{
    private Util() {
    }
    
    public static short getShort(final byte[] bts, final int off) {
        short v = bts[off];
        v <<= 8;
        v |= (short)(bts[off + 1] & 0xFF);
        return v;
    }
    
    public static void setShort(final byte[] bts, final int off, final short value) {
        bts[off] = (byte)(value >> 8);
        bts[off + 1] = (byte)value;
    }
    
    public static int getInt(final byte[] bts, final int off) {
        int v = bts[off] & 0xFF;
        v <<= 8;
        v |= (bts[off + 1] & 0xFF);
        v <<= 8;
        v |= (bts[off + 2] & 0xFF);
        v <<= 8;
        v |= (bts[off + 3] & 0xFF);
        return v;
    }
    
    public static void setInt(final byte[] bts, final int off, final short value) {
        bts[off + 0] = (byte)(value >> 24);
        bts[off + 1] = (byte)(value >> 16);
        bts[off + 2] = (byte)(value >> 8);
        bts[off + 3] = (byte)value;
    }
    
    public static int bytesToInt(final byte[] bts) {
        return bytesToInt(bts, 0, (bts.length > 4) ? 4 : bts.length);
    }
    
    public static int bytesToInt(final byte[] bts, final int offset) {
        return bytesToInt(bts, offset, 4);
    }
    
    public static int bytesToInt(final byte[] bts, final int offset, final int evlLen) {
        int rt = 0;
        for (int i = offset; i < evlLen; ++i) {
            rt <<= 8;
            rt |= (bts[i] & 0xFF);
        }
        return rt;
    }
    
    public static byte[] intToBytes(final int v) {
        int byteLen = 0;
        int vTepm = v;
        if (v < 0) {
            byteLen = 4;
        }
        else {
            do {
                vTepm >>= 8;
                ++byteLen;
            } while (vTepm > 0);
        }
        return intToBytes(v, byteLen);
    }
    
    public static byte[] intToBytes(int v, final int byteLen) {
        final byte[] b = new byte[byteLen];
        for (int i = byteLen - 1; i >= 0; b[i--] = (byte)v, v >>= 8) {}
        return b;
    }
    
    public static int intToBytes(final byte[] buf, int iOff, final int iValue, final int iExpLen) {
        for (int iLen = iExpLen; iLen > 0; --iLen) {
            int iTmp = iValue;
            for (int i = 0; i < iLen - 1; ++i) {
                iTmp >>= 8;
            }
            buf[iOff++] = (byte)(iTmp & 0xFF);
        }
        return iExpLen;
    }
    
    public static String intToHexStr(int v, final int byteLen) {
        final byte[] b = new byte[byteLen];
        for (int i = byteLen - 1; v > 0 && i >= 0; b[i--] = (byte)v, v >>= 8) {}
        return ByteArray.convert(b);
    }
    
    public static String stringTrimAll(final String str) {
        String dest = "";
        if (str != null) {
            final Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            final Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }
    
    public static String intToString(final int value, final boolean add0x) {
        final byte[] va = intToBytes(value);
        final StringBuilder d = new StringBuilder();
        if (add0x) {
            d.append("0x");
        }
        d.append(ByteArray.convert(va));
        return d.toString();
    }
    
    public static boolean isHexChar(final char ch) {
        return (ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'f') || (ch >= 'A' && ch <= 'F');
    }
    
    public static boolean isSpaceChar(final char ch) {
        return ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n';
    }
    
    public static int HexStringToInt(final String hex) {
        return bytesToInt(ByteArray.convert(hex));
    }
    
    public static byte hexCharToByte(final char c) {
        if (c >= '0' && c <= '9') {
            return (byte)(c - '0');
        }
        if (c >= 'a' && c <= 'f') {
            return (byte)(c - 'a' + 10);
        }
        if (c >= 'A' && c <= 'F') {
            return (byte)(c - 'A' + 10);
        }
        throw new DataFormatException("hex char invalid:" + c);
    }
    
    public static boolean isAsciiStr(final String acsii) {
        return acsii.length() == acsii.getBytes().length;
    }
    
    public static int getRandom() {
        final SecureRandom rs = new SecureRandom();
        return rs.nextInt();
    }
    
    public static byte[] getRandom(final int length) {
        final SecureRandom rs = new SecureRandom();
        final byte[] random = new byte[length];
        rs.nextBytes(random);
        return random;
    }
    
    public static int getRandom(final byte[] random, final int offset, final int length) {
        final SecureRandom rs = new SecureRandom();
        final byte[] trand = new byte[length];
        rs.nextBytes(trand);
        System.arraycopy(trand, 0, random, offset, length);
        return offset + length;
    }
    
    public static boolean compareHexStrWithX(final byte[] result, final int offset, final int length, final String expWithX) {
        final String resultS = ByteArray.convert(result, offset, length);
        return compareHexStrWithX(resultS, expWithX);
    }
    
    public static boolean compareHexStrWithX(final byte[] data, final String expWithX) {
        final String result = ByteArray.convert(data);
        return compareHexStrWithX(result, expWithX);
    }
    
    public static boolean compareHexStrWithX(String result, String expWithX) {
        result = stringTrimAll(result);
        expWithX = stringTrimAll(expWithX);
        if (result.length() == expWithX.length()) {
            for (int length = expWithX.length(), i = 0; i < length; ++i) {
                final char ch = Character.toUpperCase(expWithX.charAt(i));
                final char ex = Character.toUpperCase(result.charAt(i));
                if (ch != ex && ch != 'X') {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    public static void arrayFill(final byte[] src, final int off, final int length, final byte value) {
        for (int i = 0; i < length; ++i) {
            src[off + i] = value;
        }
    }
    
    public static byte[] newArrayAndFill(final int length, final byte value) {
        final byte[] v = new byte[length];
        arrayFill(v, 0, length, value);
        return v;
    }
    
    public static boolean arrayCompare(final byte[] src1, final int offset1, final byte[] src2, final int offset2, final int length) {
        if (src1 == null && src2 == null) {
            return true;
        }
        if ((src1 == null && src2 != null) || (src1 != null && src2 == null)) {
            return false;
        }
        for (int i = 0; i < length; ++i) {
            if (src1[offset1 + i] != src2[offset2 + i]) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean arrayCompare(final byte[] src, final byte[] dst) {
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
    
    public static boolean arrayIsAllNum(final byte[] src, final int off, final int length, final byte value) {
        for (int i = 0; i < length; ++i) {
            if (src[off + i] != value) {
                return false;
            }
        }
        return true;
    }
    
    public static void arrayInitContentWithInc(final byte[] src, final int off, final int length, byte initValue) {
        for (int i = 0; i < length; ++i) {
            src[off + i] = initValue;
            ++initValue;
        }
    }
    
    public static String getCurrentDate() {
        return getDate(new Date());
    }
    
    public static String getCurrentTime() {
        return getTime(new Date());
    }
    
    public static String getCurrentDateTime() {
        return getDateTime(new Date());
    }
    
    public static String getDate(final Date d) {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(d);
    }
    
    public static String getTime(final Date d) {
        final SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
        return sdf.format(d);
    }
    
    public static String getDateTime(final Date d) {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(d);
    }
    
    public static String strHighLowRevert(final String str) {
        final StringBuilder addrC = new StringBuilder();
        for (int c = str.length(), i = 0; i < c; i += 2) {
            addrC.append(str.charAt(i + 1));
            addrC.append(str.charAt(i));
        }
        return addrC.toString();
    }
    
    public static void bytesAddHex(final byte[] augend, int augOff, final byte[] addend, int addOff, final byte[] out, int oOff, final int sDataLen) {
        byte c = 0;
        int i = sDataLen - 1;
        augOff += i;
        addOff += i;
        oOff += i;
        while (i >= 0) {
            final short sf = (short)(augend[augOff--] & 0xFF);
            short st = (short)(addend[addOff--] & 0xFF);
            st += c;
            c = 0;
            st += sf;
            if (st > 255) {
                st -= 256;
                c = 1;
            }
            out[oOff--] = (byte)st;
            --i;
        }
    }
    
    public static byte[] bit7ToBit8(final byte[] abySrc, final int iSrcOff, final int iSrcLen) {
        final StringBuilder strBuilder = new StringBuilder();
        final byte[] abyBitMask = { 1, 3, 7, 15, 31, 63, 127 };
        byte byH = 0;
        byte byL = 0;
        for (int i = 0; i < iSrcLen; ++i) {
            final int iShift = i % 7;
            final byte by7Bit = abySrc[iSrcOff + i];
            byH = (byte)(by7Bit << iShift);
            final byte by8Bit = (byte)((byH | byL) & 0x7F);
            strBuilder.append(String.format("%1$02X", by8Bit));
            byL = (byte)(by7Bit >> 7 - iShift & abyBitMask[iShift]);
            if ((i + 1) % 7 == 0) {
                strBuilder.append(String.format("%1$02X", byL));
                byL = 0;
            }
        }
        return ByteArray.convert(strBuilder.toString());
    }
}
