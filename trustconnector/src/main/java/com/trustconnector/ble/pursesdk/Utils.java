package com.trustconnector.ble.pursesdk;

import com.trustconnector.scdp.util.*;

public class Utils
{
    public static byte[] addBytes(final byte[] data1, final byte[] data2) {
        final byte[] data3 = new byte[data1.length + data2.length];
        System.arraycopy(data1, 0, data3, 0, data1.length);
        System.arraycopy(data2, 0, data3, data1.length, data2.length);
        return data3;
    }
    
    public static byte[] addBytes(final byte[] data1, final byte[] data2, final byte[] data3) {
        final byte[] bytes = addBytes(data1, data2);
        final byte[] addBytes = addBytes(bytes, data3);
        return addBytes;
    }
    
    public static byte[] addBytes(final byte[] data1, final int len) {
        final byte[] data2 = new byte[len];
        System.arraycopy(data1, 0, data2, 0, len);
        return data2;
    }
    
    public static byte[] addBytes(final byte[] data1, final int start, final int len) {
        final byte[] data2 = new byte[len];
        System.arraycopy(data1, start, data2, 0, len);
        return data2;
    }
    
    public static byte[] getRandomValue() {
        String str = "";
        for (int i = 0; i < 16; ++i) {
            char temp = '\0';
            final int key = (int)(Math.random() * 2.0);
            switch (key) {
                case 0: {
                    temp = (char)(Math.random() * 10.0 + 48.0);
                    break;
                }
                case 1: {
                    temp = (char)(Math.random() * 6.0 + 97.0);
                    break;
                }
            }
            str += temp;
        }
        return HexString.parseHexString(str);
    }
    
    public static byte[] addDesEnd(final byte[] bytes) {
        final int len = bytes.length % 8;
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 8 - len; ++i) {
            if (i == 0) {
                sb.append("80");
            }
            else {
                sb.append("00");
            }
        }
        return addBytes(bytes, HexString.parseHexString(sb.toString()));
    }
    
    public static byte[] strToBytes(final String hex) {
        final int length = hex.length();
        final byte[] v = new byte[(length + 1) / 2];
        int validLen = 0;
        for (int i = 0; i < length; i += 2) {
            final byte a = Util.hexCharToByte(hex.charAt(i));
            final byte b = Util.hexCharToByte(hex.charAt(i + 1));
            v[validLen++] = (byte)(a << 4 | b);
        }
        return v;
    }
}
