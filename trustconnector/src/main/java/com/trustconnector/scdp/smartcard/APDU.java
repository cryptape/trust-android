package com.trustconnector.scdp.smartcard;

import com.trustconnector.scdp.util.*;

public class APDU
{
    private ByteArray capdu;
    private ByteArray rdata;
    private int sw;
    private String cmdName;
    private int apduCase;
    private APDUChecker checker;
    private boolean checkerIsTemp;
    private int channel;
    private double excTime;
    public static final int APDU_OFF_CLASS = 0;
    public static final int APDU_OFF_INS = 1;
    public static final int APDU_OFF_P1 = 2;
    public static final int APDU_OFF_P2 = 3;
    public static final int APDU_OFF_P3 = 4;
    public static final int APDU_OFF_CDATA = 5;
    public static final int APDU_LEN_HEAD = 5;
    public static final int APDU_CASE_1 = 1;
    public static final int APDU_CASE_2 = 2;
    public static final int APDU_CASE_3 = 3;
    public static final int APDU_CASE_4 = 4;
    
    public APDU() {
        this.capdu = new ByteArray(4);
    }
    
    public APDU(final String name, final byte[] apdu) {
        this.cmdName = name;
        this.capdu = new ByteArray(apdu);
        this.updateAPDUCase();
    }
    
    public APDU(final String name, final byte[] apdu, final int offset, final int length) {
        this.cmdName = name;
        this.capdu = new ByteArray(apdu, offset, length);
        this.updateAPDUCase();
    }
    
    public APDU(final String name, final String apdu) {
        this.cmdName = name;
        this.capdu = new ByteArray(apdu);
        this.updateAPDUCase();
    }
    
    public APDU(final byte[] apdu) {
        this(ISO7816.getInsName(apdu[1]), apdu);
    }
    
    public APDU(final byte[] apdu, final int off, final int length) {
        this(ISO7816.getInsName(apdu[1]), apdu, off, length);
    }
    
    public APDU(final String apdu) {
        this.capdu = new ByteArray(apdu);
        this.cmdName = ISO7816.getInsName(this.capdu.getByte(1));
        this.updateAPDUCase();
    }
    
    public int getApduCase() {
        return this.apduCase;
    }
    
    public APDUChecker getRAPDUChecker() {
        final APDUChecker c = this.checker;
        if (this.checkerIsTemp) {
            this.checker = null;
        }
        return c;
    }
    
    public APDUChecker setRAPDUChecker(final APDUChecker newChecker) {
        final APDUChecker old = this.checker;
        this.checker = newChecker;
        this.checkerIsTemp = true;
        return old;
    }
    
    public APDUChecker setRAPDUChecker(final APDUChecker newChecker, final boolean bTemp) {
        final APDUChecker old = this.checker;
        this.checker = newChecker;
        this.checkerIsTemp = bTemp;
        return old;
    }
    
    private void updateAPDUCase() {
        final int length = this.capdu.length();
        if (length == 4) {
            this.apduCase = 1;
        }
        else if (length == 5) {
            this.apduCase = 2;
        }
        else if (length == 5 + (this.capdu.getByte(4) & 0xFF)) {
            this.apduCase = 3;
        }
        else {
            this.apduCase = 4;
        }
        this.channel = (byte)(this.capdu.getByte(0) & 0x3);
    }
    
    public void setCAPDU(final byte[] apdu) {
        this.capdu = new ByteArray(apdu);
        this.cmdName = ISO7816.getInsName(apdu[1]);
        this.updateAPDUCase();
    }
    
    public void setCAPDU(final byte[] apdu, final boolean bUpdateCmdName) {
        this.capdu = new ByteArray(apdu);
        if (bUpdateCmdName) {
            this.cmdName = ISO7816.getInsName(apdu[1]);
        }
        this.updateAPDUCase();
    }
    
    public void setCAPDU(final String apdu, final boolean bUpdateName) {
        this.capdu = new ByteArray(apdu);
        if (bUpdateName) {
            this.cmdName = ISO7816.getInsName(this.capdu.getByte(1));
        }
        this.updateAPDUCase();
    }
    
    public void setCAPDU(final String apdu) {
        this.capdu = new ByteArray(apdu);
        this.cmdName = ISO7816.getInsName(this.capdu.getByte(1));
        this.updateAPDUCase();
    }
    
    public void setCAPDU(final String apduName, final String apdu) {
        this.capdu = new ByteArray(apdu);
        this.cmdName = apduName;
        this.updateAPDUCase();
    }
    
    public byte[] getCAPDU() {
        if (this.capdu == null) {
            return null;
        }
        final byte[] a = this.capdu.toBytes();
        if (this.channel > 0 && this.channel <= 3) {
            final byte[] array = a;
            final int n = 0;
            array[n] &= (byte)252;
            final byte[] array2 = a;
            final int n2 = 0;
            array2[n2] |= (byte)this.channel;
        }
        return a;
    }
    
    public void setName(final String name) {
        this.cmdName = name;
    }
    
    public String getName() {
        return this.cmdName;
    }
    
    public byte getCls() {
        return this.capdu.getByte(0);
    }
    
    public void setClass(final int classValue) {
        this.capdu.setByte(0, classValue);
    }
    
    public void setChannel(final int chn) {
        if (chn >= 0 && chn < 4) {
            this.channel = chn;
        }
    }
    
    public int getChannel() {
        return this.channel;
    }
    
    public byte getIns() {
        return this.capdu.getByte(1);
    }
    
    public void setIns(final int ins) {
        this.capdu.setByte(1, ins);
    }
    
    public void setIns(final int ins, final boolean bUpdateCmdName) {
        this.capdu.setByte(1, ins);
        if (bUpdateCmdName) {
            this.cmdName = ISO7816.getInsName((byte)ins);
        }
    }
    
    public void setP1P2(final int p1p2) {
        this.capdu.setByte(2, p1p2 >> 8);
        this.capdu.setByte(3, p1p2);
    }
    
    public int getP1P2() {
        return this.capdu.getInt2(2);
    }
    
    public byte getP1() {
        return this.capdu.getByte(2);
    }
    
    public void setP1(final int p1) {
        this.capdu.setByte(2, p1);
    }
    
    public byte getP2() {
        return this.capdu.getByte(3);
    }
    
    public void setP2(final int p2) {
        this.capdu.setByte(3, p2);
    }
    
    public byte getP3() {
        return this.capdu.getByte(4);
    }
    
    public void setP3(final int p3) {
        if (this.capdu.length() == 4) {
            this.capdu.append((byte)p3);
            this.apduCase = 2;
        }
        else {
            final int p3l = p3 & 0xFF;
            this.capdu.setByte(4, p3);
            if (this.apduCase == 3) {
                if (this.capdu.length() - 5 > p3l) {
                    this.capdu.remove(this.capdu.length() - 5 - p3l);
                }
                else if (this.capdu.length() - 5 < p3l) {
                    final byte[] c = new byte[p3l - this.capdu.length() - 5];
                    this.capdu.append(c);
                }
                this.updateAPDUCase();
            }
            else if (this.apduCase == 4) {
                if (this.capdu.length() - 6 > p3l) {
                    this.capdu.remove(this.capdu.length() - 6 - p3l);
                }
                else if (this.capdu.length() - 6 < p3l) {
                    final byte[] c = new byte[p3l - this.capdu.length() - 6];
                    this.capdu.append(c);
                }
                this.updateAPDUCase();
            }
        }
    }
    
    public byte[] getCData() {
        if (this.apduCase == 1 || this.apduCase == 2) {
            return null;
        }
        final int cdataLen = this.capdu.getByte(4) & 0xFF;
        return this.capdu.toBytes(5, cdataLen);
    }
    
    public void clearCData() {
        if (this.apduCase == 3) {
            this.capdu.setByte(4, 0);
            this.capdu.remove(this.capdu.length() - 5);
            this.apduCase = 1;
        }
        else if (this.apduCase == 4) {
            this.capdu.setByte(4, this.capdu.getByte(this.capdu.length() - 1));
            this.capdu.remove(this.capdu.length() - 5);
            this.apduCase = 2;
        }
    }
    
    public void setCData(final byte[] cdata) {
        if (cdata == null) {
            this.clearCData();
            return;
        }
        if (this.apduCase == 1) {
            this.capdu.append((byte)cdata.length);
            this.capdu.append(cdata);
            this.apduCase = 3;
        }
        else if (this.apduCase == 2) {
            final byte[] p3 = { (byte)cdata.length };
            this.capdu.insert(p3, 4);
            this.capdu.insert(cdata, 5);
            this.apduCase = 4;
        }
        else if (this.apduCase == 3) {
            this.capdu.remove(5, this.capdu.getByte(4) & 0xFF);
            this.capdu.setByte(4, (byte)cdata.length);
            this.capdu.append(cdata);
        }
        else {
            this.capdu.remove(5, this.capdu.length() - 5 - 1);
            this.capdu.setByte(4, (byte)cdata.length);
            this.capdu.insert(cdata, 5);
        }
    }
    
    public void setCData(final byte[] cdata, final int offset, final int length) {
        if (length < 0 || length > 255) {
            return;
        }
        final byte[] ctdata = new byte[length];
        System.arraycopy(cdata, offset, ctdata, 0, length);
        this.setCData(ctdata);
    }
    
    public void appendCData(final byte[] cdata) {
        if (this.apduCase == 1 || this.apduCase == 2) {
            this.setCData(cdata);
        }
        else {
            if (this.apduCase == 3) {
                this.capdu.append(cdata);
            }
            else {
                this.capdu.insert(cdata, this.capdu.length() - 1);
            }
            this.capdu.setByte(4, (byte)((this.capdu.getByte(4) & 0xFF) + cdata.length));
        }
    }
    
    public void appendCData(final byte[] cdata, final int offset, final int length) {
        final byte[] ctdata = new byte[length];
        System.arraycopy(cdata, offset, ctdata, 0, length);
        this.appendCData(ctdata);
    }
    
    void setResData(final byte[] retRdata, final short retSW) {
        if (retRdata != null) {
            this.rdata = new ByteArray(retRdata);
        }
        this.sw = retSW;
    }
    
    void setRAPDU(final byte[] r, final double excTime) {
        this.excTime = excTime;
        final int rLen = r.length;
        this.sw = ((r[rLen - 2] << 8 & 0xFF00) | (r[rLen - 1] & 0xFF));
        if (rLen > 2) {
            this.rdata = new ByteArray(r, 0, rLen - 2);
        }
        else {
            this.rdata = null;
        }
    }
    
    public byte[] getRAPDU() {
        int length;
        byte[] rapdu;
        if (this.rdata == null) {
            length = 0;
            rapdu = new byte[2];
        }
        else {
            length = this.rdata.length();
            rapdu = new byte[length + 2];
            this.rdata.getBytes(0, rapdu, 0, length);
        }
        rapdu[length] = (byte)(this.sw >> 8);
        rapdu[length + 1] = (byte)this.sw;
        return rapdu;
    }
    
    public byte[] getRData() {
        if (this.rdata == null) {
            return null;
        }
        return this.rdata.toBytes();
    }
    
    public String getRDataString() {
        if (this.rdata == null) {
            return "";
        }
        return this.rdata.toString();
    }
    
    public int getSW() {
        return this.sw;
    }
    
    public String getSWString() {
        final byte[] swT = { (byte)(this.sw >> 8), (byte)(this.sw & 0xFF) };
        return ByteArray.convert(swT);
    }
    
    public byte getSW1() {
        return (byte)(this.sw >> 8 & 0xFF);
    }
    
    public byte getSW2() {
        return (byte)(this.sw & 0xFF);
    }
    
    public double getExcTime() {
        return this.excTime;
    }
    
    public void removeCMAC(final int macLen) {
        final byte cls = this.capdu.getByte(0);
        if ((byte)(cls & 0x4) == 4) {
            int p3 = this.capdu.getByte(4) & 0xFF;
            p3 -= macLen;
            this.capdu.remove(macLen);
            this.capdu.setByte(4, p3);
            this.capdu.setByte(0, cls & 0xFB);
        }
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this.capdu.equals(obj);
    }
    
    @Override
    public String toString() {
        return this.capdu.toString();
    }
}
