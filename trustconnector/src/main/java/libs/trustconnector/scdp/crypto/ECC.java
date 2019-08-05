package libs.trustconnector.scdp.crypto;

import libs.trustconnector.scdp.util.*;
import java.io.*;
import libs.trustconnector.scdp.*;
import java.util.*;

import libs.trustconnector.scdp.SCDP;
import libs.trustconnector.scdp.util.ByteArray;
import libs.trustconnector.scdp.util.Util;

public class ECC
{
    public static final int PARAM_INDEX_P = 0;
    public static final int PARAM_INDEX_A = 1;
    public static final int PARAM_INDEX_B = 2;
    public static final int PARAM_INDEX_GX = 3;
    public static final int PARAM_INDEX_GY = 4;
    public static final int PARAM_INDEX_N = 5;
    public static final int PARAM_COUNT = 6;
    public static byte[][] CurveParametersNISTP256;
    private byte[] p;
    private byte[] a;
    private byte[] b;
    private byte[] xG;
    private byte[] yG;
    private byte[] n;
    private int keyLen;
    
    public ECC() {
        this.loadP256Param(32);
    }
    
    public void setParameter(final int keyLen, final byte[] p, final byte[] a, final byte[] b, final byte[] xG, final byte[] yG, final byte[] n) {
        this.p = p;
        this.a = a;
        this.b = b;
        this.xG = xG;
        this.yG = yG;
        this.n = n;
        this.keyLen = keyLen;
    }
    
    public void loadP256Param(final int keyLen) {
        this.keyLen = keyLen;
        this.p = ECC.CurveParametersNISTP256[0];
        this.a = ECC.CurveParametersNISTP256[1];
        this.b = ECC.CurveParametersNISTP256[2];
        this.xG = ECC.CurveParametersNISTP256[3];
        this.yG = ECC.CurveParametersNISTP256[4];
        this.n = ECC.CurveParametersNISTP256[5];
    }
    
    public byte[] signHash(final byte[] hash, final byte[] privateKey) {
        if (hash.length != privateKey.length) {
            System.out.println("Hash Data Length not match Key Length!");
            return null;
        }
        if (privateKey.length != this.keyLen) {
            System.out.println("Hash Data Length not match Key Length!");
            return null;
        }
        final byte[] r = new byte[this.keyLen];
        final byte[] s = new byte[this.keyLen];
        _signature(this.p, this.a, this.b, this.xG, this.yG, this.n, privateKey, hash, r, s);
        final byte[] abySig = new byte[2 * this.keyLen];
        System.arraycopy(r, 0, abySig, 0, this.keyLen);
        System.arraycopy(s, 0, abySig, this.keyLen, this.keyLen);
        return abySig;
    }
    
    public byte[] sign(final byte[] data, final byte[] privateKey) {
        final byte[] hash = hashData(data, this.keyLen);
        if (hash == null) {
            return null;
        }
        return this.signHash(hash, privateKey);
    }
    
    public boolean verifyHash(final byte[] hash, final byte[] abyPubKey, final byte[] signature) {
        final byte[] abyX = new byte[this.keyLen];
        final byte[] abyY = new byte[this.keyLen];
        System.arraycopy(abyPubKey, 0, abyX, 0, this.keyLen);
        System.arraycopy(abyPubKey, this.keyLen, abyY, 0, this.keyLen);
        final byte[] r = new byte[this.keyLen];
        final byte[] s = new byte[this.keyLen];
        System.arraycopy(signature, 0, r, 0, this.keyLen);
        System.arraycopy(signature, this.keyLen, s, 0, this.keyLen);
        return _verify(this.p, this.a, this.b, this.xG, this.yG, this.n, abyX, abyY, hash, r, s);
    }
    
    public boolean verify(final byte[] data, final byte[] abyPubKey, final byte[] signature) {
        final byte[] hash = hashData(data, this.keyLen);
        return hash != null && this.verifyHash(hash, abyPubKey, signature);
    }
    
    public void generateKey(final byte[] abyPubKeyX, final byte[] abyPubKeyY, final byte[] abyPrivateKey) {
        _generateKey(this.p, this.a, this.b, this.xG, this.yG, this.n, abyPubKeyX, abyPubKeyY, abyPrivateKey);
    }
    
    public ECCKeyPair generateKey() {
        final byte[] abyPubKeyX = new byte[this.keyLen];
        final byte[] abyPubKeyY = new byte[this.keyLen];
        final byte[] abyPrivateKey = new byte[this.keyLen];
        _generateKey(this.p, this.a, this.b, this.xG, this.yG, this.n, abyPubKeyX, abyPubKeyY, abyPrivateKey);
        return new ECCKeyPair(abyPubKeyX, abyPubKeyY, abyPrivateKey);
    }
    
    public void ecka(final byte[] abyPubKeyX, final byte[] abyPubKeyY, final byte[] abyPrivateKey, final byte[] abySHS) {
        _ecka(this.p, this.a, this.b, this.xG, this.yG, this.n, abyPubKeyX, abyPubKeyY, abyPrivateKey, abySHS);
    }
    
    public static byte[] hashData(final byte[] data, final int keyLen) {
        if (keyLen == 32) {
            return SHA_256.calc(data);
        }
        if (keyLen == 48) {
            return SHA_384.calc(data);
        }
        if (keyLen == 64) {
            return SHA_512.calc(data);
        }
        return null;
    }
    
    public static ECCKeyPair generateKeyPair() {
        final byte[] abyPubKeyX = new byte[32];
        final byte[] abyPubKeyY = new byte[32];
        final byte[] abyPrivateKey = new byte[32];
        _generateKey(ECC.CurveParametersNISTP256[0], ECC.CurveParametersNISTP256[1], ECC.CurveParametersNISTP256[2], ECC.CurveParametersNISTP256[3], ECC.CurveParametersNISTP256[4], ECC.CurveParametersNISTP256[5], abyPubKeyX, abyPubKeyY, abyPrivateKey);
        return new ECCKeyPair(abyPubKeyX, abyPubKeyY, abyPrivateKey);
    }
    
    public static byte[] doSign(final byte[] data, final byte[] privateKey) {
        final byte[] r = new byte[32];
        final byte[] s = new byte[32];
        final byte[] hash = SHA_256.calc(data);
        _signature(ECC.CurveParametersNISTP256[0], ECC.CurveParametersNISTP256[1], ECC.CurveParametersNISTP256[2], ECC.CurveParametersNISTP256[3], ECC.CurveParametersNISTP256[4], ECC.CurveParametersNISTP256[5], privateKey, hash, r, s);
        final byte[] abySig = new byte[64];
        System.arraycopy(r, 0, abySig, 0, 32);
        System.arraycopy(s, 0, abySig, 32, 32);
        return abySig;
    }
    
    public static boolean doVerify(final byte[] data, final byte[] abyPubKey, final byte[] signature) {
        final byte[] abyX = new byte[32];
        final byte[] abyY = new byte[32];
        final byte[] hash = SHA_256.calc(data);
        System.arraycopy(abyPubKey, 0, abyX, 0, 32);
        System.arraycopy(abyPubKey, 32, abyY, 0, 32);
        final byte[] r = new byte[32];
        final byte[] s = new byte[32];
        System.arraycopy(signature, 0, r, 0, 32);
        System.arraycopy(signature, 32, s, 0, 32);
        return _verify(ECC.CurveParametersNISTP256[0], ECC.CurveParametersNISTP256[1], ECC.CurveParametersNISTP256[2], ECC.CurveParametersNISTP256[3], ECC.CurveParametersNISTP256[4], ECC.CurveParametersNISTP256[5], abyX, abyY, hash, r, s);
    }
    
    public static void ECKA(final byte[] abyPubKeyX, final byte[] abyPubKeyY, final byte[] abyPrivateKey, final byte[] abySHS) {
        _ecka(ECC.CurveParametersNISTP256[0], ECC.CurveParametersNISTP256[1], ECC.CurveParametersNISTP256[2], ECC.CurveParametersNISTP256[3], ECC.CurveParametersNISTP256[4], ECC.CurveParametersNISTP256[5], abyPubKeyX, abyPubKeyY, abyPrivateKey, abySHS);
    }
    
    private static native void _signature(final byte[] p0, final byte[] p1, final byte[] p2, final byte[] p3, final byte[] p4, final byte[] p5, final byte[] p6, final byte[] p7, final byte[] p8, final byte[] p9);
    
    private static native boolean _verify(final byte[] p0, final byte[] p1, final byte[] p2, final byte[] p3, final byte[] p4, final byte[] p5, final byte[] p6, final byte[] p7, final byte[] p8, final byte[] p9, final byte[] p10);
    
    private static native void _generateKey(final byte[] p0, final byte[] p1, final byte[] p2, final byte[] p3, final byte[] p4, final byte[] p5, final byte[] p6, final byte[] p7, final byte[] p8);
    
    private static native void _ecka(final byte[] p0, final byte[] p1, final byte[] p2, final byte[] p3, final byte[] p4, final byte[] p5, final byte[] p6, final byte[] p7, final byte[] p8, final byte[] p9);
    
    public static void main(final String[] args) throws Exception {
        final ECC ecc = new ECC();
        ecc.setParameter(32, ByteArray.convert("FFFFFFFF00000001000000000000000000000000FFFFFFFFFFFFFFFFFFFFFFFF"), ByteArray.convert("FFFFFFFF00000001000000000000000000000000FFFFFFFFFFFFFFFFFFFFFFFC"), ByteArray.convert("5AC635D8AA3A93E7B3EBBD55769886BC651D06B0CC53B0F63BCE3C3E27D2604B"), ByteArray.convert("6B17D1F2E12C4247F8BCE6E563A440F277037D812DEB33A0F4A13945D898C296"), ByteArray.convert("4FE342E2FE1A7F9B8EE7EB4A7C0F9E162BCE33576B315ECECBB6406837BF51F5"), ByteArray.convert("FFFFFFFF00000000FFFFFFFFFFFFFFFFBCE6FAADA7179E84F3B9CAC2FC632551"));
        final byte[] data = ByteArray.convert("0123456789ABCDEFFEDCBA98765432100123456789ABCDEFFEDCBA9876543210");
        final byte[] pubKeyX = new byte[32];
        final byte[] pubKeyY = new byte[32];
        final byte[] privateKey = new byte[32];
        ecc.generateKey(pubKeyX, pubKeyY, privateKey);
        final byte[] pubKey = new byte[64];
        System.arraycopy(pubKeyX, 0, pubKey, 0, 32);
        System.arraycopy(pubKeyY, 0, pubKey, 32, 32);
        System.out.println("private Key:" + ByteArray.convert(privateKey));
        System.out.println("public key:" + ByteArray.convert(pubKey));
        final byte[] abySig = ecc.sign(data, privateKey);
        System.out.println("signature:" + ByteArray.convert(abySig));
        if (ecc.verify(data, pubKey, abySig)) {
            System.out.println("verify success");
        }
        else {
            System.out.println("verify failed");
        }
        final ECCKeyPair k = generateKeyPair();
        System.out.println(k.toString());
        final byte[] dataT = Util.getRandom(16);
        final byte[] sign = doSign(dataT, k.getPrivateKey());
        if (!doVerify(dataT, k.getPublicKey(), sign)) {
            System.out.println("verify failed");
        }
        System.out.println("verify success");
    }
    
    static {
        final Properties prop = System.getProperties();
        final String os = prop.getProperty("os.name");
        final String arch = prop.getProperty("sun.arch.data.model");
        String dllName = "ECC";
        if ((os.startsWith("win") || os.startsWith("Win")) && arch.startsWith("64")) {
            dllName += "_x64";
        }
        String dllPath = System.getProperty("user.dir") + "\\lib\\" + dllName + ".dll";
        final File file = new File(dllPath);
        if (!file.exists()) {
            dllPath = SCDP.getProperty(1) + "\\" + dllName + ".dll";
        }
        System.load(dllPath);
        ECC.CurveParametersNISTP256 = new byte[][] { ByteArray.convert("FFFFFFFF00000001000000000000000000000000FFFFFFFFFFFFFFFFFFFFFFFF"), ByteArray.convert("FFFFFFFF00000001000000000000000000000000FFFFFFFFFFFFFFFFFFFFFFFC"), ByteArray.convert("5AC635D8AA3A93E7B3EBBD55769886BC651D06B0CC53B0F63BCE3C3E27D2604B"), ByteArray.convert("6B17D1F2E12C4247F8BCE6E563A440F277037D812DEB33A0F4A13945D898C296"), ByteArray.convert("4FE342E2FE1A7F9B8EE7EB4A7C0F9E162BCE33576B315ECECBB6406837BF51F5"), ByteArray.convert("FFFFFFFF00000000FFFFFFFFFFFFFFFFBCE6FAADA7179E84F3B9CAC2FC632551") };
    }
}
