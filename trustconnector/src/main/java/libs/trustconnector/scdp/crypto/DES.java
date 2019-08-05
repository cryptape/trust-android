package libs.trustconnector.scdp.crypto;

import libs.trustconnector.scdp.util.*;
import javax.crypto.spec.*;

import java.security.spec.*;
import javax.crypto.*;

import libs.trustconnector.scdp.util.ByteArray;

public final class DES
{
    private byte[] key;
    private byte[] icv;
    private int mode;
    private ByteArray data;
    private static Cipher cipher;
    private static Cipher cipherTDES;
    private static final int DES_MODE_ALG_MASK = 15;
    private static final int DES_MODE_ALG_ENCRYPT = 1;
    private static final int DES_MODE_ALG_DECRYPT = 2;
    private static final int DES_MODE_BLOCK_MASK = 240;
    private static final int DES_MODE_BLOCK_ECB = 16;
    private static final int DES_MODE_BLOCK_CBC = 32;
    private static final int DES_MODE_PAD_MASK = 3840;
    private static final int DES_MODE_PAD_NOPAD = 256;
    private static final int DES_MODE_PAD_ISO9797_M1 = 512;
    private static final int DES_MODE_PAD_ISO9797_M2 = 768;
    private static final int DES_MODE_PAD_ISO9797_M3 = 1024;
    private static final int DES_MODE_PAD_PKCS5 = 1024;
    private static final int DES_MODE_MAC_MASK = 61440;
    public static final int DES_MODE_MAC_ISO9797_ALG_1 = 4096;
    public static final int DES_MODE_MAC_ISO9797_ALG_2 = 8192;
    public static final int DES_MODE_MAC_ISO9797_ALG_3 = 12288;
    public static final int DES_MODE_MAC_EMAC = 8192;
    public static final int DES_MAC_ISO9797_PAD_M1_ALG_1 = 4641;
    public static final int DES_MAC_ISO9797_PAD_M1_ALG_2 = 8737;
    public static final int DES_MAC_ISO9797_PAD_M1_ALG_3 = 12833;
    public static final int DES_MAC_ISO9797_PAD_M2_ALG_1 = 4897;
    public static final int DES_MAC_ISO9797_PAD_M2_ALG_2 = 8993;
    public static final int DES_MAC_ISO9797_PAD_M2_ALG_3 = 13089;
    private static final int DES_ICV_LEN = 8;
    public static final int DES_MODE_ENC_ECB_NOPAD = 273;
    public static final int DES_MODE_ENC_ECB_PAD_ISO9797_M1 = 529;
    public static final int DES_MODE_ENC_ECB_PAD_ISO9797_M2 = 785;
    public static final int DES_MODE_ENC_ECB_PAD_ISO9797_M3 = 1041;
    public static final int DES_MODE_ENC_ECB_PAD_PKCS5 = 1041;
    public static final int DES_MODE_ENC_CBC_NOPAD = 289;
    public static final int DES_MODE_ENC_CBC_PAD_ISO9797_M1 = 545;
    public static final int DES_MODE_ENC_CBC_PAD_ISO9797_M2 = 801;
    public static final int DES_MODE_ENC_CBC_PAD_ISO9797_M3 = 1057;
    public static final int DES_MODE_ENC_CBC_PAD_PKCS5 = 1057;
    public static final int DES_MODE_DEC_ECB_NOPAD = 274;
    public static final int DES_MODE_DEC_ECB_PAD_ISO9797_M1 = 530;
    public static final int DES_MODE_DEC_ECB_PAD_ISO9797_M2 = 786;
    public static final int DES_MODE_DEC_ECB_PAD_PKCS5 = 1042;
    public static final int DES_MODE_DEC_CBC_NOPAD = 290;
    public static final int DES_MODE_DEC_CBC_PAD_ISO9797_M1 = 546;
    public static final int DES_MODE_DEC_CBC_PAD_ISO9797_M2 = 802;
    public static final int DES_MODE_DEC_CBC_PAD_PKCS5 = 1058;
    
    public DES() {
        this.data = new ByteArray();
    }
    
    public void init(final byte[] key, final int keyLen, final int mode) {
        this.init(key, 0, keyLen, mode, null, 0);
    }
    
    public void init(final byte[] key, final int keyLen, final int mode, final byte[] icv) {
        this.init(key, 0, keyLen, mode, icv, 0);
    }
    
    public void init(final byte[] key, final int keyLen, final int mode, final byte[] icv, final int icvOff) {
        this.init(key, 0, keyLen, mode, icv, icvOff);
    }
    
    public void init(final byte[] key, final int offset, final int length, final int mode) {
        this.init(key, offset, length, mode, null, 0);
    }
    
    public void init(final byte[] key, final int offset, final int length, final int mode, final byte[] icv) {
        this.init(key, offset, length, mode, icv, 0);
    }
    
    public void init(final byte[] key, final int offset, final int length, final int mode, final byte[] icv, final int icvOff) {
        this.mode = 0;
        this.data.reinit();
        if (length != 8 && length != 16 && length != 24) {
            CryptoException.throwIt(CryptoException.INVALID_KEY_LENGTH);
        }
        int t = mode & 0xF;
        if (t != 1 && t != 2) {
            CryptoException.throwIt(CryptoException.INVALID_MODE);
        }
        t = (mode & 0xF0);
        if (t != 16 && t != 32) {
            CryptoException.throwIt(CryptoException.INVALID_MODE);
        }
        t = (mode & 0xF00);
        if (t < 256 || t > 1024) {
            CryptoException.throwIt(CryptoException.INVALID_MODE);
        }
        System.arraycopy(key, offset, this.key = new byte[length], 0, length);
        this.mode = mode;
        this.icv = new byte[8];
        if (icv != null) {
            System.arraycopy(icv, icvOff, this.icv, 0, 8);
        }
    }
    
    public int update(final byte[] content, final int offset, final int length) {
        if (this.mode == 0) {
            CryptoException.throwIt(CryptoException.ILLEGAL_USE);
        }
        this.data.append(content, offset, length);
        return this.data.length();
    }
    
    public byte[] doFinal() {
        return this.doFinal(null, 0, 0);
    }
    
    public byte[] doFinal(final byte[] lastData, final int dataOff, final int dataLen) {
        if (lastData != null) {
            this.data.append(lastData, dataOff, dataLen);
        }
        final boolean bEnc = (this.mode & 0xF) == 0x1;
        final boolean bCBC = (this.mode & 0x20) == 0x20;
        if (bEnc) {
            this.pad();
        }
        final int totalLen = this.data.length();
        try {
            KeySpec dks;
            SecretKeyFactory skf;
            Cipher cp;
            if (this.key.length == 8) {
                dks = new DESKeySpec(this.key);
                skf = SecretKeyFactory.getInstance("DES");
                cp = DES.cipher;
            }
            else {
                final ByteArray keyT = new ByteArray(this.key);
                if (this.key.length == 16) {
                    keyT.append(keyT.toBytes(0, 8));
                }
                dks = new DESedeKeySpec(keyT.toBytes());
                skf = SecretKeyFactory.getInstance("DESede");
                cp = DES.cipherTDES;
            }
            final SecretKey keySec = skf.generateSecret(dks);
            cp.init(bEnc ? 1 : 2, keySec);
            final byte[] icvT = this.icv.clone();
            for (int i = 0; i < totalLen; i += 8) {
                final byte[] tempData = this.data.toBytes(i, 8);
                if (bCBC && bEnc) {
                    ByteArray.xor(tempData, 0, icvT, 0, tempData, 0, 8);
                }
                byte[] res = null;
                res = cp.doFinal(tempData);
                if (bCBC) {
                    if (bEnc) {
                        System.arraycopy(res, 0, icvT, 0, 8);
                    }
                    else {
                        ByteArray.xor(res, 0, icvT, 0, res, 0, 8);
                        System.arraycopy(tempData, 0, icvT, 0, 8);
                    }
                }
                this.data.setBytes(i, res);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            CryptoException.throwIt(CryptoException.INTERNAL_ERROR);
        }
        if (!bEnc) {
            this.unpad();
        }
        return this.data.toBytes();
    }
    
    private void pad() {
        final int padMet = this.mode & 0xF00;
        final int totalLen = this.data.length();
        final int padLen = 8 - totalLen % 8;
        switch (padMet) {
            case 256: {
                if (totalLen == 0 || totalLen % 8 != 0) {
                    CryptoException.throwIt(CryptoException.ILLEGAL_USE);
                    break;
                }
                break;
            }
            case 512: {
                if (padLen % 8 != 0) {
                    this.data.append(new byte[padLen]);
                    break;
                }
                break;
            }
            case 768: {
                this.data.append((byte)(-128));
                if (padLen > 1) {
                    this.data.append(new byte[padLen - 1]);
                    break;
                }
                break;
            }
            case 1024: {
                for (int i = 0; i < padLen; ++i) {
                    this.data.append((byte)padLen);
                }
                break;
            }
            default: {
                CryptoException.throwIt(CryptoException.ILLEGAL_USE);
                break;
            }
        }
    }
    
    private void unpad() {
        final int padMet = this.mode & 0xF00;
        final int totalLen = this.data.length();
        int unpadLen = 0;
        int offset = totalLen - 1;
        switch (padMet) {
            case 256: {
                break;
            }
            case 512:
            case 768: {
                final byte endByte = (byte)((padMet == 512) ? 0 : -128);
                while (offset-- >= 0) {
                    if (this.data.getByte(offset) != endByte) {
                        break;
                    }
                    ++unpadLen;
                }
                break;
            }
            case 1024: {
                unpadLen = this.data.getByte(offset);
                break;
            }
            default: {
                CryptoException.throwIt(CryptoException.ILLEGAL_USE);
                break;
            }
        }
        this.data.remove(unpadLen);
    }
    
    public static byte[] doCrypto(final byte[] src, final int srcOff, final int srcLen, final byte[] key, final int keyOff, final int keyLen, final byte[] icv, final int icvOff, final int mode) {
        final DES des = new DES();
        des.init(key, keyOff, keyLen, mode, icv, icvOff);
        final byte[] res = des.doFinal(src, srcOff, srcLen);
        return res;
    }
    
    public static byte[] doCrypto(final byte[] src, final byte[] key, final byte[] icv, final int mode) {
        return doCrypto(src, 0, src.length, key, 0, key.length, icv, 0, mode);
    }
    
    public static byte[] doCrypto(final byte[] src, final byte[] key, final int mode) {
        return doCrypto(src, 0, src.length, key, 0, key.length, null, 0, mode);
    }
    
    public static byte[] calcMAC(final byte[] src, final int macLen, final byte[] key, final byte[] icv, final int mode) {
        return calcMAC(src, 0, src.length, macLen, key, 0, key.length, icv, 0, mode);
    }
    
    public static byte[] calcMAC(final byte[] src, final int srcOff, final int srcLen, final int macLen, final byte[] key, final int keyOff, final int keyLen, final byte[] icv, final int icvOff, final int macmode) {
        final DES des = new DES();
        final byte[] mac = new byte[macLen];
        final int macAlg = macmode & 0xF000;
        final int desAlg = macmode & 0xFFFF0FFF;
        switch (macAlg) {
            case 4096: {
                des.init(key, keyOff, 16, desAlg, icv, icvOff);
                final byte[] d = des.doFinal(src, srcOff, srcLen);
                System.arraycopy(d, d.length - 8, mac, 0, macLen);
                return mac;
            }
            case 8192: {
                des.init(key, keyOff, 8, desAlg, icv, icvOff);
                byte[] d = des.doFinal(src, srcOff, srcLen);
                des.init(key, keyOff + 8, 8, 273, null, 0);
                d = des.doFinal(d, d.length - 8, 8);
                System.arraycopy(d, d.length - 8, mac, 0, macLen);
                return mac;
            }
            case 12288: {
                des.init(key, keyOff, 8, desAlg, icv, icvOff);
                byte[] d = des.doFinal(src, srcOff, srcLen);
                if (keyLen > 8) {
                    des.init(key, keyOff + 8, 8, 274, null, 0);
                    d = des.doFinal(d, d.length - 8, 8);
                    if (keyLen == 16) {
                        des.init(key, keyOff, 8, 273, null, 0);
                    }
                    else {
                        des.init(key, keyOff + 16, 8, 273, null, 0);
                    }
                    d = des.doFinal(d, 0, 8);
                }
                System.arraycopy(d, d.length - 8, mac, 0, macLen);
                return mac;
            }
            default: {
                return null;
            }
        }
    }
    
    public static void main(final String[] param) {
        final byte[] mac = calcMAC(ByteArray.convert("2061F533AE8A2131333130353030393930313030303030303039344C61396C"), 8, ByteArray.convert("9D8A71D090AC3BDCCD47777445703107"), null, 13089);
        System.out.println(ByteArray.convert(mac));
    }
    
    static {
        DES.cipher = null;
        DES.cipherTDES = null;
        try {
            DES.cipher = Cipher.getInstance("DES/ECB/NoPadding");
            DES.cipherTDES = Cipher.getInstance("DESede/ECB/NoPadding");
        }
        catch (Exception e) {
            CryptoException.throwIt(CryptoException.INTERNAL_ERROR);
        }
    }
}
