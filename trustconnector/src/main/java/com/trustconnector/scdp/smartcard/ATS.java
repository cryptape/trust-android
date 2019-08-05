package com.trustconnector.scdp.smartcard;

import com.trustconnector.scdp.util.*;

public class ATS
{
    int[] FSC_C;
    private byte[] ats;
    
    public ATS(final byte[] ATS) {
        this.FSC_C = new int[] { 16, 24, 32, 40, 48, 64, 96, 128, 256 };
        this.setATS(ATS);
    }
    
    public ATS(final byte[] ATS, final int offset, final int length) {
        this.FSC_C = new int[] { 16, 24, 32, 40, 48, 64, 96, 128, 256 };
        this.setATS(ATS, offset, length);
    }
    
    public ATS(final String ATS) {
        this.FSC_C = new int[] { 16, 24, 32, 40, 48, 64, 96, 128, 256 };
        this.setATS(ATS);
    }
    
    public void setATS(final byte[] ats) {
        if (ats != null) {
            this.setATS(ats, 0, ats.length);
        }
        else {
            this.ats = null;
        }
    }
    
    public void setATS(final String ats) {
        this.ats = ByteArray.convert(ats);
    }
    
    public void setATS(final byte[] ats, final int offset, final int length) {
        if (ats == null) {
            this.ats = null;
        }
        else {
            System.arraycopy(ats, offset, this.ats = new byte[length], 0, length);
        }
    }
    
    public int getFSCI() {
        if (this.ats == null) {
            return -1;
        }
        return this.ats[1] & 0xF;
    }
    
    public int getFSC() {
        final int FSCI = this.getFSCI();
        if (FSCI != -1 && FSCI <= 8) {
            return this.FSC_C[FSCI];
        }
        return -1;
    }
    
    public byte getTA1() {
        if ((this.ats[1] & 0x10) == 0x10) {
            return this.ats[2];
        }
        return -1;
    }
    
    public byte getTB1() {
        int offset = 2;
        if ((this.ats[1] & 0x10) == 0x10) {
            ++offset;
        }
        if ((this.ats[1] & 0x20) == 0x20) {
            return this.ats[offset];
        }
        return -1;
    }
    
    public int getFWI() {
        final int TB1 = this.getTB1();
        if (TB1 == -1) {
            return -1;
        }
        return TB1 >> 8 & 0xF;
    }
    
    public int getSFGI() {
        final int TB1 = this.getTB1();
        if (TB1 == -1) {
            return -1;
        }
        return TB1 & 0xF;
    }
    
    public byte getTC1() {
        int offset = 2;
        if ((this.ats[1] & 0x10) == 0x10) {
            ++offset;
        }
        if ((this.ats[1] & 0x20) == 0x20) {
            ++offset;
        }
        if ((this.ats[1] & 0x40) == 0x40) {
            return this.ats[offset];
        }
        return -1;
    }
    
    public boolean isSupportCID() {
        final int TC1 = this.getTC1();
        return TC1 != -1 && (TC1 & 0x2) == 0x2;
    }
    
    public boolean isSupportNAD() {
        final int TC1 = this.getTC1();
        return TC1 != -1 && (TC1 & 0x1) == 0x1;
    }
    
    public byte[] getHistoryBytes() {
        int offset = 2;
        if ((this.ats[1] & 0x10) == 0x10) {
            ++offset;
        }
        if ((this.ats[1] & 0x20) == 0x20) {
            ++offset;
        }
        if ((this.ats[1] & 0x40) == 0x40) {
            ++offset;
        }
        if (this.ats.length < offset) {
            final int hisLen = this.ats.length - offset;
            final byte[] historybytes = new byte[hisLen];
            System.arraycopy(this.ats, offset + 1, historybytes, 0, hisLen);
            return historybytes;
        }
        return null;
    }
    
    public byte[] toBytes() {
        return this.ats.clone();
    }
}
