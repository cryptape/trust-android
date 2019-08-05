package com.trustconnector.scdp.crypto;

import java.math.*;
import java.security.interfaces.*;
import java.security.spec.*;
import javax.crypto.*;
import java.security.*;

public class RSA
{
    static KeyFactory keyFac;
    public static final byte[] e_10001;
    
    private static byte[] checkKey(final byte[] n) {
        if (n[0] < 0) {
            final byte[] kn = new byte[n.length + 1];
            System.arraycopy(n, 0, kn, 1, n.length);
            return kn;
        }
        return n;
    }
    
    public static RSAPublicKey generateRSAPublicKey(final byte[] n, final byte[] e) {
        try {
            if (RSA.keyFac == null) {
                RSA.keyFac = KeyFactory.getInstance("RSA");
            }
            final RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(new BigInteger(checkKey(n)), new BigInteger(e));
            return (RSAPublicKey)RSA.keyFac.generatePublic(pubKeySpec);
        }
        catch (Exception ex) {
            return null;
        }
    }
    
    public static RSAPublicKey generateRSAPublicKey(final byte[] n) {
        return generateRSAPublicKey(n, RSA.e_10001);
    }
    
    public static RSAPrivateKey generateRSAPrivateKey(final byte[] n, final byte[] d) {
        try {
            if (RSA.keyFac == null) {
                RSA.keyFac = KeyFactory.getInstance("RSA");
            }
            final RSAPrivateKeySpec priKeySpec = new RSAPrivateKeySpec(new BigInteger(checkKey(n)), new BigInteger(checkKey(d)));
            return (RSAPrivateKey)RSA.keyFac.generatePrivate(priKeySpec);
        }
        catch (Exception ex) {
            return null;
        }
    }
    
    public static RSAPrivateKey generateRSACRTPrivateKey(final byte[] n, final byte[] e, final byte[] d, final byte[] p, final byte[] q, final byte[] dp, final byte[] dq, final byte[] pq) throws Exception {
        try {
            if (RSA.keyFac == null) {
                RSA.keyFac = KeyFactory.getInstance("RSA");
            }
            final RSAPrivateCrtKeySpec priKeySpec = new RSAPrivateCrtKeySpec(new BigInteger(checkKey(n)), new BigInteger(e), new BigInteger(checkKey(d)), new BigInteger(checkKey(p)), new BigInteger(checkKey(q)), new BigInteger(checkKey(dp)), new BigInteger(checkKey(dq)), new BigInteger(checkKey(pq)));
            return (RSAPrivateKey)RSA.keyFac.generatePrivate(priKeySpec);
        }
        catch (Exception ex) {
            return null;
        }
    }
    
    public static byte[] decrypt(final byte[] data, final RSAPublicKey puk) {
        try {
            final Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(2, puk);
            return cipher.doFinal(data);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static byte[] encrypt(final byte[] data, final RSAPrivateKey prk) {
        try {
            final Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(1, prk);
            return cipher.doFinal(data);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    static {
        e_10001 = new byte[] { 0, 1, 0, 1 };
    }
}
