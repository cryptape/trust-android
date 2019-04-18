package com.cryptape.trust;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

/**
 * Created by ${User} on 2018/10/19
 */
public class Utils {
    protected static final String[] hexChars = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
    private static final byte[] SALT ={ /* deleted for security */    };
    public Utils() {
    }

    public static String dump(byte[] data) {
        return dump(data, 0, data.length);
    }

    public static String dump(byte[] data, int offset, int len) {
        if (data == null) {
            return "null";
        } else {
            char[] ascii = new char[16];
            StringBuffer out = new StringBuffer(256);
            if (offset + len > data.length) {
                len = data.length - offset;
            }

            int i = offset;

            while(i < offset + len) {
                out.append(hexify(i >>> 8 & 255));
                out.append(hexify(i & 255));
                out.append(":  ");

                for(int j = 0; j < 16; ++i) {
                    if (i >= offset + len) {
                        out.append("   ");
                        ascii[j] = ' ';
                    } else {
                        int b = data[i] & 255;
                        out.append(hexify(b)).append(' ');
                        ascii[j] = b >= 32 && b < 127 ? (char)b : 46;
                    }

                    ++j;
                }

                out.append(' ').append(ascii).append("\n");
            }

            return out.toString();
        }
    }

    public static String hexify(byte[] data) {
        return hexify(data, 0, data.length);
    }

    public static String hexify(byte[] data, int offset, int len) {
        if (data == null) {
            return "null";
        } else {
            if (offset + len > data.length) {
                len = data.length - offset;
            }

            StringBuffer out = new StringBuffer(256);
            int n = 0;

            for(int i = offset; i < len; ++i) {
                if (n > 0) {
                    out.append(' ');
                }

                out.append(hexChars[data[i] >> 4 & 15]);
                out.append(hexChars[data[i] & 15]);
                ++n;
                if (n == 16) {
                    out.append('\n');
                    n = 0;
                }
            }

            return out.toString();
        }
    }

    public static String toHexString(byte[] data) {
        return toHexString(data, 0, data.length);
    }

    public static String toHexString(byte[] data, int offset, int len) {
        if (data == null) {
            return "null";
        } else {
            if (offset + len > data.length) {
                len = data.length - offset;
            }

            StringBuffer out = new StringBuffer(256);

            for(int i = offset; i < len; ++i) {
                out.append(hexChars[data[i] >> 4 & 15]);
                out.append(hexChars[data[i] & 15]);
            }

            return out.toString();
        }
    }

    public static String hexify(int val) {
        return hexChars[(val & 255 & 240) >>> 4] + hexChars[val & 15];
    }

    public static String hexifyShort(byte a, byte b) {
        return hexifyShort(a & 255, b & 255);
    }

    public static String hexifyShort(int val) {
        return hexChars[(val & '\uffff' & '\uf000') >>> 12] + hexChars[(val & 4095 & 3840) >>> 8] + hexChars[(val & 255 & 240) >>> 4] + hexChars[val & 15];
    }

    public static String hexifyShort(int a, int b) {
        return hexifyShort(((a & 255) << 8) + (b & 255));
    }

    public static byte[] parseHexString(String byteString) {
        byte[] result = new byte[byteString.length() / 2];

        for(int i = 0; i < byteString.length(); i += 2) {
            String toParse = byteString.substring(i, i + 2);
            result[i / 2] = (byte)Integer.parseInt(toParse, 16);
        }

        return result;
    }

    public static byte[] parseLittleEndianHexString(String byteString) {
        byte[] result = new byte[byteString.length() / 2 + 1];

        for(int i = 0; i < byteString.length(); i += 2) {
            String toParse = byteString.substring(i, i + 2);
            result[(byteString.length() - i) / 2] = (byte)Integer.parseInt(toParse, 16);
        }

        result[0] = 0;
        return result;
    }

    /**
     * 返回puk
     * @param id 传入id
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String getPinAndPuk(String id) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] abyPin;
        long lPin;
        String strPuk;

        digest.reset();
        digest.update(SALT);
        digest.update(Util.stringToBytes(id));
        digest.update(Util.stringToBytes(id));
        abyPin = digest.digest();
        lPin = Util.bytesToInt(abyPin, 0, 4) & 0xFFFFFFFFL;
        lPin = lPin%100000000;
        String strPin = Util.bytesToString(String.valueOf(lPin).getBytes());
        System.out.print("PIN: ");
        System.out.println(strPin);

        digest.reset();
        digest.update(SALT);
        digest.update(Util.stringToBytes(id));
        abyPin = digest.digest();
        strPuk = Util.bytesToString(abyPin, 0, 8);
        System.out.print("PUK: ");
        System.out.println(strPuk);
        return strPin+" "+ strPuk;
    }

    public static boolean isValidHex(String value) {
        if (value == null) {
            return false;
        }
        if (value.startsWith("0x") || value.startsWith("0X")) {
            value = value.substring(2, value.length());
        }

        if (value.length() == 0 || value.length() % 2 != 0) {
            return false;
        }

        String pattern = "[0-9a-fA-F]+";
        return Pattern.matches(pattern, value);
    }
}
