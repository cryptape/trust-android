package com.trustconnector.scdp.crypto;

import javax.crypto.*;
import javax.crypto.spec.*;

import com.trustconnector.scdp.util.*;
import java.util.*;

public class AES
{
    static final byte[] const_Zero;
    static final byte[] const_Rb;
    
    public static byte[] encrypt(byte[] data, final byte[] key) {
        try {
            final int kenLen = key.length;
            final SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            final Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            final IvParameterSpec iv = new IvParameterSpec(new byte[key.length]);
            cipher.init(1, keySpec, iv);
            if (data.length % 16 != 0) {
                final int padLen = kenLen - data.length % 16;
                final byte[] t = new byte[data.length + padLen];
                System.arraycopy(data, 0, t, 0, data.length);
                data = t;
            }
            return cipher.doFinal(data);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static byte[] decrypt(final byte[] data, final byte[] key) {
        try {
            final SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            final Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            final IvParameterSpec iv = new IvParameterSpec(new byte[key.length]);
            cipher.init(2, keySpec, iv);
            return cipher.doFinal(data);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static byte[] calcCMAC(final byte[] data, final byte[] key) {
        try {
            final SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            final Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            final IvParameterSpec iv = new IvParameterSpec(new byte[key.length]);
            cipher.init(1, keySpec, iv);
            System.out.println("Key=" + ByteArray.convert(key));
            int padLen = key.length - data.length % key.length;
            if (padLen == key.length) {
                padLen = 0;
            }
            if (data == null || data.length == 0) {
                padLen = 16;
            }
            final byte[] tdata = new byte[data.length + padLen];
            System.arraycopy(data, 0, tdata, 0, data.length);
            final byte[] L = cipher.doFinal(AES.const_Zero);
            System.out.println("L=" + ByteArray.convert(L));
            final ByteArray K1 = new ByteArray(L);
            K1.shiftLeft(1);
            K1.remove(0, 1);
            System.out.println("L shift,L=" + K1.toString());
            if ((byte)(L[0] & 0x80) == -128) {
                K1.xor(AES.const_Rb, 0, AES.const_Rb.length);
            }
            System.out.println("K1=" + K1.toString());
            if (padLen != 0) {
                final byte t = K1.getByte(0);
                K1.shiftLeft(1);
                K1.remove(0, 1);
                if ((byte)(t & 0x80) == -128) {
                    K1.xor(AES.const_Rb, 0, AES.const_Rb.length);
                }
                System.out.println("K2=" + K1.toString());
                tdata[data.length] = -128;
            }
            final byte[] Kt = K1.toBytes();
            System.out.println("D=" + ByteArray.convert(tdata));
            ByteArray.xor(Kt, 0, tdata, tdata.length - key.length, tdata, tdata.length - key.length, key.length);
            System.out.println("D xor=" + ByteArray.convert(tdata));
            cipher.init(1, keySpec, iv);
            final byte[] res = cipher.doFinal(tdata);
            System.out.println("cbc res=" + ByteArray.convert(res));
            final byte[] mac = new byte[key.length];
            System.arraycopy(res, res.length - key.length, mac, 0, key.length);
            return mac;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private static void leftShift(final byte[] array) {
        final int n = 0;
        array[n] <<= 1;
        for (int i = 1; i < array.length; ++i) {
            if (array[i] < 0) {
                final int n2 = i - 1;
                array[n2] |= 0x1;
            }
            final int n3 = i;
            array[n3] <<= 1;
        }
    }
    
    public static byte[] PRF(final byte[] key, byte[] message, final int outputLen) {
        final int blockSize = 16;
        final byte[] L = encrypt(key, new byte[16]);
        final byte[][] K = new byte[2][];
        for (int i = 0; i < 2; ++i) {
            final boolean leftMostBit = L[0] < 0;
            leftShift(L);
            if (leftMostBit) {
                final byte[] array = L;
                final int n = L.length - 1;
                array[n] ^= 0xFFFFFF87;
            }
            K[i] = L.clone();
        }
        byte[] xor = K[0];
        int iPad = message.length % blockSize;
        if (message.length == 0 || iPad != 0) {
            xor = K[1];
            iPad = blockSize - iPad;
            final byte[] abyTmp = new byte[message.length + iPad];
            System.arraycopy(message, 0, abyTmp, 0, message.length);
            abyTmp[message.length] = -128;
            message = abyTmp.clone();
        }
        final int offset = message.length - blockSize;
        for (int j = 0; j < blockSize; ++j) {
            final byte[] array2 = message;
            final int n2 = offset + j;
            array2[n2] ^= xor[j];
        }
        final byte[] C = encrypt(message, key);
        final byte[] T = Arrays.copyOfRange(C, C.length - blockSize, C.length - blockSize + outputLen);
        return T;
    }
    
    public static void main(final String[] param) {
        final byte[] msg0 = ByteArray.convert("");
        final byte[] msg2 = ByteArray.convert("0305004D9FA775");
        final byte[] msg3 = ByteArray.convert("6BC1BEE22E409F96E93D7E117393172A");
        final byte[] msg4 = ByteArray.convert("6BC1BEE22E409F96E93D7E117393172AAE2D8A571E03AC9C9EB76FAC45AF8E5130C81C46A35CE411");
        final byte[] msg5 = ByteArray.convert("6BC1BEE22E409F96E93D7E117393172AAE2D8A571E03AC9C9EB76FAC45AF8E5130C81C46A35CE411E5FBC1191A0A52EFF69F2445DF4F9B17AD2B417BE66C3710");
        final byte[] key = ByteArray.convert("D77F5CD4EB1FDA227C37251BC67FC8A2");
        byte[] t = calcCMAC(msg2, key);
        System.out.println(ByteArray.convert(t));
        t = calcCMAC(msg3, key);
        System.out.println(ByteArray.convert(t));
        t = calcCMAC(msg4, key);
        System.out.println(ByteArray.convert(t));
        t = calcCMAC(msg5, key);
        System.out.println(ByteArray.convert(t));
    }
    
    static {
        const_Zero = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        const_Rb = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -121 };
    }
}
