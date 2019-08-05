package com.trustconnector.scdp.smartcard.application.telecom.cat.sms;

import com.trustconnector.scdp.smartcard.*;
import com.trustconnector.scdp.smartcard.application.*;
import com.trustconnector.scdp.*;
import com.trustconnector.scdp.smartcard.application.globalplatform.*;
import com.trustconnector.scdp.util.*;
import com.trustconnector.scdp.crypto.*;
import java.util.*;

public class SCP80
{
    protected short SPI;
    protected byte KIc;
    protected byte KID;
    protected Key keyForChecksum;
    protected Key keyForCipher;
    protected KeySets keySets;
    protected boolean bShowCiperDetail;
    protected static Map<String, SCP80> serviceMap;
    public static final int SCP80_COUNTER_LEN = 5;
    
    public static SCP80 getService() {
        SCP80 scp80 = SCP80.serviceMap.get("ISD");
        if (scp80 == null) {
            final ISD isd = SD.getISD();
            scp80 = new SCP80(isd.getKeySets());
            SCP80.serviceMap.put("ISD", scp80);
        }
        return scp80;
    }
    
    public static SCP80 getService(final AID aid) {
        SCP80 scp80 = SCP80.serviceMap.get(aid.toString());
        if (scp80 == null) {
            final SD sd = SD.getSD(aid);
            scp80 = new SCP80(sd.getKeySets());
            SCP80.serviceMap.put(aid.toString(), scp80);
        }
        return scp80;
    }
    
    SCP80(final KeySets keysets) {
        this.updateKeyset(keysets);
    }

    protected void updateKeyset(KeySets keysets) {
        this.keySets = new KeySets();
        Iterator iter = keysets.entrySet().iterator();

        while(true) {
            Integer version;
            KeySet keyset;
            do {
                if (!iter.hasNext()) {
                    return;
                }

                Map.Entry<Object, KeySet> entry = (Map.Entry)iter.next();
                version = (Integer)entry.getKey();
                keyset = (KeySet)entry.getValue();
            } while(version >= 16);

            Iterator<Map.Entry<Object, Key>> k = keyset.entrySet().iterator();
            Key kic = null;
            Key kid = null;

            while(k.hasNext()) {
                Map.Entry<Object, Key> keyEntry = (Map.Entry)k.next();
                Integer id = (Integer)keyEntry.getKey();
                Key key = (Key)keyEntry.getValue();
                if (id == 1) {
                    kic = key;
                } else if (id == 2) {
                    kid = key;
                }
            }

            KeySet set = new SCPKeySet(version);
            if (kic != null) {
                set.addKey(kic);
            }

            if (kid != null) {
                set.addKey(kid);
            }

            if (set.size() > 0) {
                this.keySets.addKeySet(set);
            }
        }
    }
    
    public byte[] getKeyCount(final int version) {
        final SCPKeySet set = (SCPKeySet)this.keySets.findKeySet(new Integer(version));
        if (set != null) {
            return set.getCounterBytes(5);
        }
        return null;
    }
    
    public void setKeyCount(final int version, final byte[] count) {
        final SCPKeySet set = (SCPKeySet)this.keySets.findKeySet(new Integer(version));
        if (set != null) {
            long c = 0L;
            for (int i = 0; i < 5; ++i) {
                c <<= 8;
                c |= count[i];
            }
            set.setCounter(c);
        }
    }

    public void resetCount() {
        Iterator iter = this.keySets.entrySet().iterator();

        while(iter.hasNext()) {
            Map.Entry<Object, KeySet> entry = (Map.Entry)iter.next();
            Integer version = (Integer)entry.getKey();
            if (version < 16) {
                SCPKeySet set = (SCPKeySet)this.keySets.findKeySet(version);
                if (set != null) {
                    set.setCounter(1L);
                }
            }
        }

    }
    
    public KeySets getKeysets() {
        return this.keySets;
    }
    
    public void enableShowCipherDetail() {
        this.bShowCiperDetail = true;
    }
    
    public void disableShowCipherDetail() {
        this.bShowCiperDetail = false;
    }
    
    void showSessionDetail(final String msg, final byte[] data) {
        if (this.bShowCiperDetail) {
            final String log = msg + ":" + ByteArray.convert(data);
            SCDP.addLog(log);
        }
    }
    
    public void setKey(final int version, final byte[] kic, final byte[] kid) {
        KeySet set = this.keySets.findKeySet(new Integer(version));
        if (set == null) {
            set = new SCPKeySet(version);
            this.keySets.addKeySet(set);
        }
        set.addKey(new GPKey(version, 128, 1, kic));
        set.addKey(new GPKey(version, 128, 2, kid));
    }
    
    public void wrap(final CommandPackage cp) {
        this.SPI = cp.getSPI();
        this.KIc = cp.getKIc();
        this.KID = cp.getKID();
        int checksumLen = 0;
        final int checksumAlgType = cp.getChecksumAlg();
        switch (checksumAlgType) {
            case 16: {
                checksumLen = 2;
                break;
            }
            case 80: {
                checksumLen = 4;
                break;
            }
            default: {
                checksumLen = 8;
                break;
            }
        }
        final int expChecksumLen = cp.getExpChecksumLen();
        if (expChecksumLen > 0) {
            checksumLen = expChecksumLen;
        }
        final int dataLen = cp.getDataLen();
        int padLen = 0;
        if ((this.SPI & 0x4) == 0x4) {
            final int cipherDataLen = 6 + checksumLen + dataLen;
            if ((this.KIc & 0xF) == 0x2) {
                padLen = 16 - cipherDataLen % 16;
                if (padLen == 16) {
                    padLen = 0;
                }
            }
            else {
                padLen = 8 - cipherDataLen % 8;
                if (padLen == 8) {
                    padLen = 0;
                }
            }
        }
        cp.setPCount(padLen);
        SCPKeySet set = null;
        int keyVer = (this.KIc & 0xFF) >> 4;
        if (keyVer == 0) {
            keyVer = (this.KID & 0xFF) >> 4;
        }
        set = (SCPKeySet)this.keySets.findKeySet(new Integer(keyVer));
        if (set != null) {
            this.keyForCipher = set.getKey(new Integer(1));
            this.keyForChecksum = set.getKey(new Integer(2));
        }
        byte[] count = cp.getCount();
        if (count == null) {
            if (set != null) {
                set.incCounter();
                count = set.getCounterBytes(5);
                cp.setCount(count);
            }
            else {
                count = new byte[] { 0, 0, 0, 0, 1 };
                cp.setCount(count);
            }
            this.showSessionDetail("count", count);
        }
        if (checksumAlgType != 0) {
            final byte[] checksumDataBytes = cp.getChecksumData(checksumLen);
            byte[] checksumres = this.calcChecksum(checksumDataBytes, checksumAlgType);
            if (checksumLen < checksumres.length) {
                final byte[] t = checksumres;
                checksumres = new byte[checksumLen];
                System.arraycopy(t, 0, checksumres, 0, checksumLen);
            }
            cp.setChecksum(checksumres);
        }
        if ((this.SPI & 0x4) == 0x4) {
            byte[] cipherData = cp.getEncOrgData();
            cipherData = this.cipherData(cipherData, true);
            cp.setEncData(cipherData);
        }
    }
    
    public void counterRecover() {
        int keyVer = this.KIc >> 4;
        if (keyVer == 0) {
            keyVer = this.KID >> 4;
        }
        final SCPKeySet set = (SCPKeySet)this.keySets.findKeySet(new Integer(keyVer));
        if ((this.SPI & 0x18) > 0 && set != null) {
            final long c = set.getCounter();
            set.setCounter(c - 1L);
        }
    }
    
    public void counterRecover(final int dec) {
        int keyVer = this.KIc >> 4;
        if (keyVer == 0) {
            keyVer = this.KID >> 4;
        }
        final SCPKeySet set = (SCPKeySet)this.keySets.findKeySet(new Integer(keyVer));
        if ((this.SPI & 0x18) > 0 && set != null) {
            final long c = set.getCounter();
            set.setCounter(c - dec);
        }
    }
    
    public boolean unwrap(final ResponsePackage rp) {
        if ((this.SPI & 0x1000) == 0x1000) {
            byte[] cipherData = rp.getEncData();
            if (cipherData.length % 8 != 0) {
                return false;
            }
            cipherData = this.cipherData(cipherData, false);
            rp.setDecData(cipherData);
        }
        int checksumType = (this.SPI & 0xC00) >> 10;
        if (checksumType != 0) {
            if (rp.data[2] <= 10) {
                return false;
            }
            int checksumLen = 0;
            switch (checksumType) {
                case 1: {
                    if ((this.KID & 0xF) << 4 == 16) {
                        checksumLen = 2;
                        checksumType = 16;
                        break;
                    }
                    checksumLen = 4;
                    checksumType = 80;
                    break;
                }
                case 2: {
                    checksumLen = 8;
                    checksumType = (this.KID & 0xF);
                    break;
                }
                default: {
                    SCDP.reportError("Unsppport checksum alg type DS");
                    break;
                }
            }
            final byte[] checksumData = rp.getChecksumData(checksumLen);
            final byte[] checksumRes = this.calcChecksum(checksumData, checksumType);
            final byte[] retChecksum = rp.getChecksum();
            if (checksumRes.length != retChecksum.length || !Util.arrayCompare(checksumRes, 0, retChecksum, 0, checksumRes.length)) {
                return false;
            }
        }
        return true;
    }
    
    byte[] cipherData(byte[] cipherData, final boolean bEnc) {
        this.showSessionDetail("cipher data", cipherData);
        final byte[] cipherKey = this.keyForCipher.getValue();
        this.showSessionDetail("cipher key", cipherKey);
        final int cipherType = this.KIc & 0xF;
        if (cipherType == 2) {
            if (bEnc) {
                cipherData = AES.encrypt(cipherData, cipherKey);
            }
            else {
                cipherData = AES.decrypt(cipherData, cipherKey);
            }
        }
        else {
            final int mode = bEnc ? 545 : 290;
            if (cipherType == 1) {
                cipherData = DES.doCrypto(cipherData, 0, cipherData.length, cipherKey, 0, 8, null, 0, mode);
            }
            else if (cipherType == 5) {
                cipherData = DES.doCrypto(cipherData, 0, cipherData.length, cipherKey, 0, 16, null, 0, mode);
            }
            else if (cipherType == 9) {
                cipherData = DES.doCrypto(cipherData, 0, cipherData.length, cipherKey, 0, 24, null, 0, mode);
            }
            else if (cipherType == 13) {
                cipherData = DES.doCrypto(cipherData, 0, cipherData.length, cipherKey, 0, 8, null, 0, mode);
            }
        }
        this.showSessionDetail("cipher res", cipherData);
        return cipherData;
    }
    
    void calcCRC(final byte[] data, final byte[] IniVal, final int u8CrcLength) {
        int u8NewVal = data[0] & 0xFF;
        long u32TmpVal = (IniVal[u8CrcLength - 1] ^ u8NewVal) & 0xFF;
        long u32Poly;
        if (2 == u8CrcLength) {
            u32Poly = 33800L;
        }
        else {
            u32Poly = 3988292384L;
        }
        for (u8NewVal = 0; u8NewVal < 8; ++u8NewVal) {
            if ((u32TmpVal & 0x1L) == 0x1L) {
                u32TmpVal = (u32TmpVal >> 1 ^ u32Poly);
            }
            else {
                u32TmpVal >>= 1;
            }
        }
        for (u8NewVal = (byte)(u8CrcLength - 1); u8NewVal > 0; --u8NewVal) {
            IniVal[u8NewVal] = (byte)((long)IniVal[u8NewVal - 1] ^ u32TmpVal);
            u32TmpVal >>= 8;
        }
        IniVal[0] = (byte)u32TmpVal;
    }
    
    byte[] calcChecksum(final byte[] checksumDataBytes, final int checksumAlgType) {
        byte[] checksumres = null;
        this.showSessionDetail("checksum data:", checksumDataBytes);
        switch (checksumAlgType) {
            case 16: {
                checksumres = new byte[] { -1, -1 };
                final byte[] data = { 0 };
                for (int i = 0; i < checksumDataBytes.length; ++i) {
                    data[0] = checksumDataBytes[i];
                    this.calcCRC(data, checksumres, 2);
                }
                for (int i = 0; i < checksumres.length; ++i) {
                    checksumres[i] ^= -1;
                }
                break;
            }
            case 80: {
                checksumres = new byte[] { -1, -1, -1, -1 };
                final byte[] datat = { 0 };
                for (int j = 0; j < checksumDataBytes.length; ++j) {
                    datat[0] = checksumDataBytes[j];
                    this.calcCRC(datat, checksumres, 4);
                }
                for (int j = 0; j < checksumres.length; ++j) {
                    checksumres[j] ^= -1;
                }
                break;
            }
            case 1: {
                final byte[] cipherKey = this.keyForChecksum.getValue();
                this.showSessionDetail("checksum key", cipherKey);
                final byte[] res = DES.doCrypto(checksumDataBytes, 0, checksumDataBytes.length, cipherKey, 0, 8, null, 0, 545);
                checksumres = new byte[8];
                System.arraycopy(res, res.length - 8, checksumres, 0, 8);
                break;
            }
            case 5: {
                final byte[] cipherKey = this.keyForChecksum.getValue();
                this.showSessionDetail("checksum key", cipherKey);
                final byte[] res = DES.doCrypto(checksumDataBytes, 0, checksumDataBytes.length, cipherKey, 0, 16, null, 0, 545);
                checksumres = new byte[8];
                System.arraycopy(res, res.length - 8, checksumres, 0, 8);
                break;
            }
            case 9: {
                final byte[] cipherKey = this.keyForChecksum.getValue();
                this.showSessionDetail("checksum key", cipherKey);
                final byte[] res = DES.doCrypto(checksumDataBytes, 0, checksumDataBytes.length, cipherKey, 0, 24, null, 0, 545);
                checksumres = new byte[8];
                System.arraycopy(res, res.length - 8, checksumres, 0, 8);
                break;
            }
            case 2: {
                final byte[] cipherKey = this.keyForChecksum.getValue();
                this.showSessionDetail("checksum key", cipherKey);
                final byte[] res = AES.calcCMAC(checksumDataBytes, cipherKey);
                checksumres = new byte[8];
                System.arraycopy(res, res.length - 16, checksumres, 0, 8);
                break;
            }
            default: {
                SCDP.reportError("Checksum NOT Support DS for now!");
                break;
            }
        }
        this.showSessionDetail("checksum res", checksumres);
        return checksumres;
    }
    
    static {
        SCP80.serviceMap = new HashMap<String, SCP80>();
    }
}
