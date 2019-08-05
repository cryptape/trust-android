package com.trustconnector.scdp.smartcard.application.globalplatform;

import com.trustconnector.scdp.*;
import com.trustconnector.scdp.crypto.*;
import com.trustconnector.scdp.smartcard.application.globalplatform.checkrule.*;
import com.trustconnector.scdp.util.*;
import com.trustconnector.scdp.smartcard.*;
import com.trustconnector.scdp.smartcard.application.*;

public final class SCP02
{
    private APDU apdu;
    private AID aidSD;
    private SD sd;
    private int secLevel;
    private int i;
    protected int channelNo;
    private ByteArray icvCMAC;
    private ByteArray icvRMAC;
    private ByteArray skCMAC;
    private ByteArray skRMAC;
    private ByteArray skEnc;
    private ByteArray skDec;
    private static final int ICV_LEN = 8;
    private static final int SESSION_KEY_LEN = 16;
    private boolean showSessionData;
    private static final byte[][] skDerivationData;
    public static final int KEY_TYPE_CMAC = 0;
    public static final int KEY_TYPE_RMAC = 1;
    public static final int KEY_TYPE_DEC = 2;
    public static final int KEY_TYPE_ENC = 3;
    public static final byte I_KEY_TYPE_TRIPKEY = 1;
    public static final byte I_CMAC_ON_UNMODIFY = 2;
    public static final byte I_EXPLICIT_INIT = 4;
    public static final byte I_ICV_MAC_OVER_AID = 8;
    public static final byte I_ICV_ENC = 16;
    public static final byte I_RMAC_SUPPORT = 32;
    public static final byte I_RANDOM_ALG = 64;
    public static final int SECURITY_LEVEL_NOT_SETUP = 0;
    public static final int SECURITY_LEVEL_SETUP = 128;
    public static final int SECURITY_LEVEL_PLAIN = 0;
    public static final int SECURITY_LEVEL_CMAC = 1;
    public static final int SECURITY_LEVEL_CENC = 2;
    public static final int SECURITY_LEVEL_RMAC = 16;
    public static final int KEY_VERSION_DEFAULT = 0;
    public static final int KEY_ID_ENC = 1;
    public static final int KEY_ID_MAC = 2;
    public static final int KEY_ID_DEK = 3;
    public static final int KEY_ID_BASE = 1;
    public static final int MAC_LEN = 8;
    
    public SCP02(final SD sd, final int i) {
        this.icvCMAC = new ByteArray(8);
        this.icvRMAC = new ByteArray(8);
        this.skCMAC = new ByteArray(16);
        this.skRMAC = new ByteArray(16);
        this.skEnc = new ByteArray(16);
        this.skDec = new ByteArray(16);
        this.sd = sd;
        this.apdu = new APDU("0000000000");
        this.i = i;
        this.aidSD = sd.getAID();
    }
    
    protected void transmit() {
        this.apdu.setChannel(this.channelNo);
        this.sd.getReader().transmit(this.apdu);
    }
    
    protected KeySets getKeySet() {
        KeySets k = this.sd.getKeySets();
        if (k.size() == 0) {
            for (SD ass = this.sd.getAssociateSD(); ass != null; ass = this.sd.getAssociateSD()) {
                k = ass.getKeySets();
                if (k.size() > 0) {
                    return k;
                }
            }
        }
        return k;
    }
    
    public int getCurSecLevel() {
        return this.secLevel;
    }
    
    public void showSessionData(final boolean bShowSessionData) {
        this.showSessionData = bShowSessionData;
    }
    
    private void showSessionInfo(final String msg) {
        if (this.showSessionData) {
            SCDP.addLog(msg);
        }
    }
    
    public int getI() {
        return this.i;
    }
    
    public void setI(final int newI) {
        this.i = newI;
    }
    
    public void wrap(final APDU apdu) {
        byte[] cdata = apdu.getCData();
        if ((this.secLevel & 0x2) == 0x2) {
            if ((this.i & 0x1) == 0x1) {}
            this.showSessionInfo("org apdu=" + ByteArray.convert(cdata));
            cdata = DES.doCrypto(cdata, 0, cdata.length, this.skEnc.toBytes(), 0, this.skEnc.length(), null, 0, 801);
            this.showSessionInfo("ecn apdu=" + ByteArray.convert(cdata));
            apdu.setCData(cdata);
        }
        if ((this.secLevel & 0x1) == 0x1) {
            this.calcCMAC(apdu);
        }
    }
    
    public void unwrap(final APDU apdu) {
        if ((this.secLevel & 0x10) == 0x10) {
            this.calcRMAC(apdu);
        }
    }
    
    public byte[] encrypt(final byte[] data, final int offset, final int length) {
        final byte[] res = DES.doCrypto(data, offset, length, this.skDec.toBytes(), 0, this.skDec.length(), null, 0, 273);
        this.showSessionInfo("enc data=" + ByteArray.convert(data, offset, length));
        this.showSessionInfo("enc res=" + ByteArray.convert(data, offset, length));
        return res;
    }
    
    public void setChannel(final int chn) {
        this.channelNo = chn;
    }
    
    public int getChannel() {
        return this.channelNo;
    }
    
    public void initUpdate(final int keyVersion) {
        this.initUpdate(keyVersion, null, new InitUpdateResponseChecker());
    }
    
    public void initUpdate(final int keyVersion, final byte[] hostChallenge) {
        this.initUpdate(keyVersion, hostChallenge, new InitUpdateResponseChecker());
    }
    
    public void initUpdate(int keyVersion, byte[] hostChallenge, final APDUChecker c) {
        this.apdu.setName("Initial Update");
        this.apdu.setCAPDU("8050000000");
        this.apdu.setP1((byte)keyVersion);
        if (hostChallenge == null) {
            hostChallenge = Util.getRandom(8);
        }
        this.apdu.setCData(hostChallenge);
        this.apdu.setRAPDUChecker(c);
        this.transmit();
        final ByteArray rdata = new ByteArray(this.apdu.getRData());
        if (rdata.length() == 0) {
            System.out.println("init update response invalid");
            SCPException.throwIt("init update response invalid");
        }
        if (keyVersion == 0 || keyVersion == 255) {
            keyVersion = rdata.getUnsignedByte(10);
        }
        if (keyVersion == 255) {
            keyVersion = (int)this.getKeySet().getFirstCookie();
        }
        final int seqCount = rdata.getInt2(12);
        final SCPKeySet ks = (SCPKeySet)this.getKeySet().findKeySet(new Integer(keyVersion));
        if (ks == null) {
            SCDP.reportError("SCP02 Key not found! version=" + String.format("0x%02X", keyVersion));
        }
        final int expCount = (int)ks.getCounter();
        if (expCount != 0) {
            SCDP.addLog("Test card challenge");
            SCDP.addSublog("Evaluated: " + String.format("%04X", seqCount));
            SCDP.addSublog("Expected:  " + String.format("%04X", expCount));
            if (seqCount != expCount) {
                SCPException.throwIt("Card  Sequence Counter Error");
            }
        }
        else {
            ks.setCounter(seqCount);
        }
        this.skCMAC.setBytes(0, this.initSessionKey(keyVersion, 0, seqCount));
        this.skRMAC.setBytes(0, this.initSessionKey(keyVersion, 1, seqCount));
        this.skDec.setBytes(0, this.initSessionKey(keyVersion, 2, seqCount));
        this.skEnc.setBytes(0, this.initSessionKey(keyVersion, 3, seqCount));
        this.showSessionInfo("skCMAC:" + this.skCMAC);
        this.showSessionInfo("skRMAC:" + this.skRMAC);
        this.showSessionInfo("skDec:" + this.skDec);
        this.showSessionInfo("skEnc:" + this.skEnc);
        this.icvCMAC.clearContent();
        this.icvRMAC.clearContent();
        if ((this.i & 0x40) == 0x40) {
            final byte[] aid = this.aidSD.toBytes();
            final byte[] mac1 = DES.calcMAC(aid, 6, this.skCMAC.toBytes(), null, 13089);
            final byte[] retCAC1 = rdata.toBytes(14, 6);
            SCDP.addLog("Test card challenge");
            SCDP.addSublog("Evaluated: " + ByteArray.convert(mac1));
            SCDP.addSublog("Expected:  " + ByteArray.convert(retCAC1));
            if (ByteArray.compare(mac1, mac1.length - 6, retCAC1, 0, 6) != 0) {
                SCPException.throwIt("Card  challenge");
            }
        }
        final ByteArray a = new ByteArray(24);
        a.setBytes(0, hostChallenge);
        a.setInt2(8, seqCount);
        a.setBytes(10, rdata.toBytes(14, 6));
        a.setByte(16, 128);
        this.showSessionInfo("card auth crypoto org=" + a.toString());
        final byte[] cardAuthCryptogram = DES.doCrypto(a.toBytes(), this.skEnc.toBytes(), null, 289);
        this.showSessionInfo("card auth crypoto enc=" + ByteArray.convert(cardAuthCryptogram));
        final byte[] retCAC2 = rdata.toBytes(20, 8);
        this.showSessionInfo("card auth crypoto return=" + ByteArray.convert(retCAC2));
        if (ByteArray.compare(cardAuthCryptogram, cardAuthCryptogram.length - 8, retCAC2, 0, 8) != 0) {
            System.out.println("res:" + ByteArray.convert(cardAuthCryptogram));
            System.out.println("calc res:" + ByteArray.convert(cardAuthCryptogram, cardAuthCryptogram.length - 8, 8));
            System.out.println("recv res:" + ByteArray.convert(retCAC2));
            SCPException.throwIt("Card Authentication Cryptogram not valid");
        }
    }
    
    public void ExtAuth(final int secLevel) {
        final NormalAPDUChecker c = new NormalAPDUChecker(36864);
        this.ExtAuth(secLevel, c);
    }

    public void ExtAuth(int secLevel, APDUChecker c) {
        ByteArray rdata = new ByteArray(this.apdu.getRData());
        int seqCount = rdata.getInt2(12);
        byte[] hostChallenge = this.apdu.getCData();
        ByteArray a = new ByteArray(24);
        a.clearContent();
        a.setInt2(0, seqCount);
        a.setBytes(2, rdata.toBytes(14, 6));
        a.setBytes(8, hostChallenge);
        a.setByte(16, 128);
        this.showSessionInfo("host org data=" + a.toString());
        byte[] host = DES.doCrypto(a.toBytes(), this.skEnc.toBytes(), (byte[])null, 289);
        this.showSessionInfo("host auth crypto=" + ByteArray.convert(host));
        byte[] hostCryptogram = new byte[8];
        System.arraycopy(host, host.length - 8, hostCryptogram, 0, 8);
        this.apdu.setName("External Auth");
        this.apdu.setCAPDU("8482000000");
        this.apdu.setP1((byte)secLevel);
        this.apdu.setCData(hostCryptogram);
        byte[] apduHead = new byte[]{-124, -126, 1, 0, 16};
        ByteArray cmacData = new ByteArray();
        cmacData.append(apduHead);
        cmacData.append(hostCryptogram);
        this.showSessionInfo("ext auth mac data=" + ByteArray.convert(cmacData.toBytes()));
        byte[] mac = DES.calcMAC(cmacData.toBytes(), 8, this.skCMAC.toBytes(), (byte[])null, 13089);
        this.showSessionInfo("ext auth mac=" + ByteArray.convert(mac));
        this.apdu.appendCData(mac);
        this.apdu.setRAPDUChecker(c);
        this.transmit();
        this.icvCMAC.setBytes(0, mac);
        this.secLevel = secLevel;
        int keyVersion = rdata.getUnsignedByte(10);
        if (keyVersion == 255) {
            keyVersion = (Integer)this.getKeySet().getFirstCookie();
        }

        SCPKeySet ks = (SCPKeySet)this.getKeySet().get(keyVersion);
        ks.incCounter();
    }
    
    public void open(final int keyVersion, final int secLevel) {
        final byte[] hostChallenge = Util.getRandom(8);
        final InitUpdateResponseChecker initRspChecker = new InitUpdateResponseChecker();
        this.initUpdate(keyVersion, hostChallenge, initRspChecker);
        this.ExtAuth(secLevel);
    }
    
    public void openImplicit(final int keyVersion, final int seqCount, final byte[] aid, final APDU apdu) {
        this.skCMAC.setBytes(0, this.initSessionKey(keyVersion, 0, seqCount));
        this.skDec.setBytes(0, this.initSessionKey(keyVersion, 2, seqCount));
        this.icvCMAC.setBytes(0, DES.calcMAC(aid, 8, this.getKey(keyVersion, 2).getValue(), new byte[8], 13089));
        byte[] src = apdu.getCAPDU();
        if (apdu.getApduCase() == 4) {
            final byte[] tsrc = new byte[src.length - 1];
            System.arraycopy(src, 0, tsrc, 0, src.length - 1);
            src = tsrc;
        }
        else if (apdu.getApduCase() == 1) {
            final byte[] tsrc = new byte[5];
            System.arraycopy(src, 0, tsrc, 0, src.length);
            src = tsrc;
        }
        final byte[] array = src;
        final int n = 0;
        array[n] &= (byte)252;
        if ((this.i & 0x2) != 0x2) {
            final byte[] array2 = src;
            final int n2 = 0;
            array2[n2] |= 0x4;
            final byte[] array3 = src;
            final int n3 = 4;
            array3[n3] += 8;
        }
        final byte[] icv = this.icvCMAC.toBytes();
        final byte[] mac = DES.calcMAC(src, 8, this.skCMAC.toBytes(), icv, 13089);
        apdu.appendCData(mac);
        this.icvCMAC.setBytes(0, mac);
        apdu.setClass((byte)(apdu.getCls() | 0x4));
        this.secLevel = 1;
    }
    
    public void close() {
        this.secLevel = 0;
    }
    
    private Key getKey(final int version, final int keyid) {
        final KeySet keyset = this.getKeySet().findKeySet(new Integer(version));
        if (keyset == null) {
            return null;
        }
        return keyset.getKey(new Integer(keyid));
    }
    
    public byte[] initSessionKey(final int keyVersion, final int keytype, final int seqCounter) {
        int keyid = 0;
        if ((this.i & 0x1) == 0x1) {
            switch (keytype) {
                case 0:
                case 1: {
                    keyid = 2;
                    break;
                }
                case 2: {
                    keyid = 3;
                    break;
                }
                case 3: {
                    keyid = 1;
                    break;
                }
                default: {
                    keyid = 2;
                    SCPException.throwIt("SCPKey type not found,type=" + keytype);
                    break;
                }
            }
        }
        else {
            keyid = 1;
        }
        final Key key = this.getKey(keyVersion, keyid);
        if (key == null) {
            System.out.println("scp keys not found version=" + keyVersion);
            SCPException.throwIt("scp keys not found version=" + keyVersion);
        }
        final ByteArray sdata = new ByteArray(16);
        sdata.setBytes(0, SCP02.skDerivationData[keytype]);
        sdata.setInt2(2, seqCounter);
        final byte[] sk = DES.doCrypto(sdata.toBytes(), 0, 16, key.getValue(), 0, key.getLength(), null, 0, 289);
        this.showSessionInfo("def key=" + ByteArray.convert(key.getValue()));
        this.showSessionInfo("session data=" + sdata.toString());
        this.showSessionInfo("session key=" + ByteArray.convert(sk));
        return sk;
    }
    
    private void calcCMAC(final APDU apdu) {
        byte[] src = apdu.getCAPDU();
        if (apdu.getApduCase() == 4) {
            final byte[] tsrc = new byte[src.length - 1];
            System.arraycopy(src, 0, tsrc, 0, src.length - 1);
            src = tsrc;
        }
        else if (apdu.getApduCase() == 1) {
            final byte[] tsrc = new byte[5];
            System.arraycopy(src, 0, tsrc, 0, src.length);
            src = tsrc;
        }
        final byte[] array = src;
        final int n = 0;
        array[n] &= (byte)252;
        if ((this.i & 0x2) != 0x2) {
            final byte[] array2 = src;
            final int n2 = 0;
            array2[n2] |= 0x4;
            final byte[] array3 = src;
            final int n3 = 4;
            array3[n3] += 8;
        }
        this.showSessionInfo("calc Mac src:" + ByteArray.convert(src));
        this.showSessionInfo("calc Mac key:" + this.skCMAC);
        byte[] icv = this.icvCMAC.toBytes();
        if ((this.i & 0x10) == 0x10) {
            this.showSessionInfo("icv before enc:" + ByteArray.convert(icv));
            icv = DES.doCrypto(icv, this.skCMAC.toBytes(0, 8), null, 273);
            this.showSessionInfo("icv after enc:" + ByteArray.convert(icv));
        }
        final byte[] mac = DES.calcMAC(src, 8, this.skCMAC.toBytes(), icv, 13089);
        apdu.appendCData(mac);
        this.showSessionInfo("calc Mac res:" + ByteArray.convert(mac));
        this.icvCMAC.setBytes(0, mac);
        apdu.setClass((byte)(apdu.getCls() | 0x4));
    }
    
    private void calcRMAC(final APDU apdu) {
        final byte[] src = apdu.getRData();
        final byte[] sk = this.skRMAC.toBytes();
        final DES des = new DES();
        des.init(sk, 0, 8, 801, this.icvRMAC.toBytes(), 0);
        byte[] d = des.doFinal(src, 0, src.length);
        des.init(sk, 8, 8, 274, null, 0);
        d = des.doFinal(d, d.length - 8, 8);
        des.init(sk, 0, 8, 273, null, 0);
        d = des.doFinal(d, 0, 8);
        if (ByteArray.compare(src, src.length - 8, d, 0, 8) != 0) {
            SCPException.throwIt("RMAC Verify error");
        }
        this.icvRMAC.setBytes(0, d);
    }
    
    static {
        skDerivationData = new byte[][] { { 1, 1 }, { 1, 2 }, { 1, -127 }, { 1, -126 } };
    }
}
