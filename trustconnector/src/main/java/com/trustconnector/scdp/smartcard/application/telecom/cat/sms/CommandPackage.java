package com.trustconnector.scdp.smartcard.application.telecom.cat.sms;

import com.trustconnector.scdp.util.*;

public class CommandPackage
{
    protected int defChecksumAlg;
    protected int defChecksumKID;
    protected short SPI;
    protected byte KIc;
    protected byte KID;
    protected byte[] TAR;
    protected byte[] Count;
    protected int PadCount;
    protected byte[] RcCcDs;
    protected byte[] secData;
    protected byte[] EncCount;
    protected int EncPadCount;
    protected byte[] EncRcCcDs;
    protected byte[] EncSecData;
    protected int expChecksumLen;
    static final int CHECKSUM_TYPE_NON = 0;
    static final int CHECKSUM_TYPE_RC = 1;
    static final int CHECKSUM_TYPE_CC = 2;
    static final int CHECKSUM_TYPE_DS = 3;
    static final int CHECKSUM_TYPE_MASK = 3;
    public static final int DATA_CIPHER = 4;
    public static final int CHECKSUM_ALG_RC_CRC16 = 16;
    public static final int CHECKSUM_ALG_RC_CRC32 = 80;
    public static final int CHECKSUM_ALG_CC_DES = 1;
    public static final int CHECKSUM_ALG_CC_TDES_2KEY = 5;
    public static final int CHECKSUM_ALG_CC_TDES_3KEY = 9;
    public static final int CHECKSUM_ALG_CC_AES = 2;
    public static final int CHECKSUM_ALG_NON = 0;
    public static final int COUNT_NON = 0;
    public static final int COUNT_CHECK_NO_CHECK = 8;
    public static final int COUNT_CHECK_HIGH = 16;
    public static final int COUNT_CHECK_ONE_HIGH = 24;
    protected static final int COUNT_TYPE_MASK = 24;
    public static final int PoR_REQ_NON = 0;
    public static final int PoR_REQ_ALWAYS = 256;
    public static final int PoR_REQ_ON_ERR = 512;
    protected static final int PoR_REQ_MASK = 768;
    static final int PoR_CHECKSUM_NON = 0;
    static final int PoR_CHECKSUM_RC = 1024;
    static final int PoR_CHECKSUM_CC = 2048;
    static final int PoR_CHECKSUM_DS = 3072;
    static final int PoR_CHECKSUM_MASK = 3072;
    protected static final int PoR_CIPHER = 4096;
    public static final int PoR_TYPE_DELIVER_REPORT = 0;
    public static final int PoR_TYPE_SUBMIT = 8192;
    protected static final int PoR_TYPE_MASK = 8192;
    public static final int CIPHER_DES = 1;
    public static final int CIPHER_DES_ECB = 13;
    public static final int CIPHER_TDES_2KEY = 5;
    public static final int CIPHER_TDES_3KEY = 9;
    public static final int CIPHER_AES = 2;
    public static final int CIPHER_NULL = 240;
    public static final int CIPHER_MASK = 15;
    
    public CommandPackage() {
        this.TAR = new byte[3];
    }
    
    public CommandPackage(final int spi, final int kic, final int kid, final int tar) {
        this(spi, kic, kid, tar, null);
    }
    
    public CommandPackage(final int spi, final int kic, final int kid, final int tar, final String apdus) {
        final byte spi2 = (byte)(spi >> 8);
        final byte spi3 = (byte)spi;
        this.SPI = (short)((spi3 << 8 & 0xFF00) | (spi2 & 0xFF));
        this.KIc = (byte)kic;
        this.KID = (byte)kid;
        final byte[] tar_b = { (byte)(tar >> 16), (byte)(tar >> 8), (byte)tar };
        this.TAR = tar_b;
        if (apdus != null) {
            this.secData = ByteArray.convert(apdus);
        }
    }
    
    public void setChecksum(final int checksumAlg) {
        this.setChecksum(checksumAlg, 0);
    }
    
    public void setChecksumRCDefault(final int checksumAlg) {
        this.SPI &= 0xFFFFFFFC;
        this.SPI |= 0x1;
        this.KID = 0;
        this.defChecksumAlg = checksumAlg;
    }
    
    public void setChecksumCCDefault(final int checksumAlg, final int keyVer) {
        this.SPI &= 0xFFFFFFFC;
        this.SPI |= 0x2;
        this.KID = 0;
        this.defChecksumAlg = checksumAlg;
        this.defChecksumKID = keyVer;
    }
    
    public void setChecksum(int checksumAlg, final int keyVer) {
        this.SPI &= 0xFFFFFFFC;
        switch (checksumAlg) {
            case 16:
            case 80: {
                this.SPI |= 0x1;
                checksumAlg >>= 4;
                this.KID = (byte)(checksumAlg & 0xF);
                break;
            }
            case 0: {
                this.KID = (byte)(keyVer << 4);
                break;
            }
            default: {
                this.SPI |= 0x2;
                this.KID = (byte)(keyVer << 4 | (checksumAlg & 0xF));
                break;
            }
        }
    }
    
    public int getChecksumAlg() {
        if ((this.SPI & 0x1) == 0x1) {
            if (this.KID == 0) {
                return this.defChecksumAlg;
            }
            return (this.KID & 0xF) << 4 & 0xFF;
        }
        else {
            if ((this.SPI & 0x2) != 0x2) {
                return 0;
            }
            if (this.KID == 0) {
                return this.defChecksumAlg;
            }
            return this.KID & 0xF;
        }
    }
    
    public void setChecksum(final int checksumAlg, final int keyVer, final int expChecksumLen) {
        this.setChecksum(checksumAlg, keyVer);
        this.expChecksumLen = expChecksumLen;
    }
    
    public void setCipher(final int cipherType, final int keyVer) {
        if (cipherType == 240) {
            this.SPI &= 0xFFFFFFFB;
            this.KIc = (byte)(keyVer << 4);
        }
        else {
            this.SPI |= 0x4;
            this.KIc = (byte)(keyVer << 4 | (cipherType & 0xF));
        }
    }
    
    public void setPoRReq(final int PoRReqType) {
        this.SPI &= 0xFFFFFCFF;
        this.SPI |= (short)(PoRReqType & 0x300);
    }
    
    public void setPoRChecksum(final int checksumAlg) {
        this.SPI &= 0xFFFFF3FF;
        switch (checksumAlg) {
            case 16:
            case 80: {
                this.SPI |= 0x400;
                break;
            }
            case 0: {
                break;
            }
            default: {
                this.SPI |= 0x800;
                break;
            }
        }
    }
    
    public void setPoRChecksum(int checksumAlg, final int keyVer) {
        this.SPI &= 0xFFFFF3FF;
        switch (checksumAlg) {
            case 16:
            case 80: {
                this.SPI |= 0x400;
                checksumAlg >>= 4;
                this.KID = (byte)(checksumAlg & 0xF);
                break;
            }
            case 0: {
                this.KID = (byte)(keyVer << 4);
                break;
            }
            default: {
                this.SPI |= 0x800;
                this.KID = (byte)(keyVer << 4 | (checksumAlg & 0xF));
                break;
            }
        }
    }
    
    public void setPoRCipher(final boolean bPoRCipher) {
        if (bPoRCipher) {
            this.SPI |= 0x1000;
        }
        else {
            this.SPI &= 0xFFFFEFFF;
        }
    }
    
    public void setPoRType(final int PoRType) {
        this.SPI &= 0xFFFFDFFF;
        this.SPI |= (short)(PoRType & 0x2000);
    }
    
    public short getSPI() {
        return this.SPI;
    }
    
    public byte getKIc() {
        return this.KIc;
    }
    
    public byte getKID() {
        return this.KID;
    }
    
    public void setTAR(final String TAR) {
        this.TAR = ByteArray.convert(TAR);
    }
    
    public void setTAR(final byte[] TAR) {
        this.TAR = TAR.clone();
    }
    
    public byte[] getTAR() {
        return this.TAR;
    }
    
    public void setCount(final int value, final int type) {
        this.SPI &= 0xFFFFFFE7;
        this.SPI |= (short)(type & 0x18);
        this.Count = Util.intToBytes(value, 5);
    }
    
    public void setCount(final byte[] count, final int type) {
        this.SPI &= 0xFFFFFFE7;
        this.SPI |= (short)(type & 0x18);
        this.Count = count;
    }
    
    public void setCount(final int type) {
        this.SPI &= 0xFFFFFFE7;
        this.SPI |= (short)(type & 0x18);
        if (type == 0) {
            this.Count = new byte[5];
        }
        else {
            this.Count = null;
        }
    }
    
    public byte[] getCount() {
        return this.Count;
    }
    
    void setCount(final byte[] count) {
        this.Count = count.clone();
    }
    
    public void setChecksum(final byte[] checksum) {
        if (checksum != null) {
            this.RcCcDs = checksum.clone();
        }
        else {
            this.RcCcDs = null;
        }
    }
    
    public void setChecksumWrong() {
        if (this.EncRcCcDs != null) {
            ByteArray.not(this.EncRcCcDs, 0, this.EncRcCcDs, 0, this.EncRcCcDs.length);
        }
        else if (this.RcCcDs != null) {
            ByteArray.not(this.RcCcDs, 0, this.RcCcDs, 0, this.RcCcDs.length);
        }
    }
    
    public byte[] getChecksum() {
        return this.RcCcDs.clone();
    }
    
    public void setData(final byte[] data) {
        this.secData = data.clone();
        this.EncCount = null;
        this.EncPadCount = 0;
        this.EncRcCcDs = null;
        this.EncSecData = null;
    }
    
    public byte[] getData() {
        return this.secData.clone();
    }
    
    public int getDataLen() {
        return this.secData.length;
    }
    
    public byte[] toBytes() {
        final ByteArray b = new ByteArray();
        int chl = 13;
        if (this.RcCcDs != null) {
            chl += this.RcCcDs.length;
        }
        byte[] data = this.EncSecData;
        if (data == null) {
            data = this.secData;
        }
        final int cpl = 1 + chl + ((data == null) ? 0 : data.length);
        b.append(cpl, 2);
        b.append(chl, 1);
        b.append((byte)this.SPI);
        b.append((byte)(this.SPI >> 8));
        b.append(this.KIc);
        b.append(this.KID);
        b.append(this.TAR);
        if (this.EncCount != null) {
            b.append(this.EncCount);
        }
        else {
            b.append(this.Count);
        }
        if (this.EncCount != null) {
            b.append(this.EncPadCount, 1);
        }
        else {
            b.append(this.PadCount, 1);
        }
        if (this.EncRcCcDs != null) {
            b.append(this.EncRcCcDs);
        }
        else {
            b.append(this.RcCcDs);
        }
        if (this.EncSecData != null) {
            b.append(this.EncSecData);
        }
        else {
            b.append(this.secData);
        }
        return b.toBytes();
    }
    
    public byte[] toCATTPFormat() {
        return null;
    }
    
    int getExpChecksumLen() {
        return this.expChecksumLen;
    }
    
    void setPCount(final int paddingCount) {
        this.PadCount = paddingCount;
    }
    
    byte[] getChecksumData(final int checksumLen) {
        final ByteArray checksumData = new ByteArray();
        final int chl = 13 + checksumLen;
        final int cpl = 1 + chl + this.secData.length + this.PadCount;
        checksumData.append(cpl, 2);
        checksumData.append(chl, 1);
        checksumData.append((byte)this.SPI);
        checksumData.append((byte)(this.SPI >> 8));
        checksumData.append(this.KIc);
        checksumData.append(this.KID);
        checksumData.append(this.TAR);
        checksumData.append(this.Count);
        checksumData.append((byte)this.PadCount);
        checksumData.append(this.secData);
        if (this.PadCount > 0) {
            checksumData.append(new byte[this.PadCount]);
        }
        return checksumData.toBytes();
    }
    
    byte[] getEncOrgData() {
        final ByteArray cipherDataBytes = new ByteArray();
        cipherDataBytes.append(this.Count);
        cipherDataBytes.append((byte)this.PadCount);
        cipherDataBytes.append(this.RcCcDs);
        cipherDataBytes.append(this.secData);
        return cipherDataBytes.toBytes();
    }
    
    void setEncData(final byte[] encData) {
        int offset = 0;
        System.arraycopy(encData, offset, this.EncCount = new byte[5], 0, 5);
        offset += 5;
        this.EncPadCount = encData[offset];
        ++offset;
        if (this.RcCcDs != null) {
            System.arraycopy(encData, offset, this.EncRcCcDs = new byte[this.RcCcDs.length], 0, this.RcCcDs.length);
            offset += this.RcCcDs.length;
        }
        System.arraycopy(encData, offset, this.EncSecData = new byte[encData.length - offset], 0, this.EncSecData.length);
    }
    
    public int getDefaultChecksumAlg() {
        return this.defChecksumAlg;
    }
    
    public int getDefaultChecksumKID() {
        return this.defChecksumKID;
    }
    
    @Override
    public String toString() {
        final String res = "";
        return res;
    }
}
