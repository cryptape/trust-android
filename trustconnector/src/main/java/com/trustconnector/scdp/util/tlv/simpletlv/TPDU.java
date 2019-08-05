package com.trustconnector.scdp.util.tlv.simpletlv;

import com.trustconnector.scdp.util.tlv.*;
import com.trustconnector.scdp.util.*;
import com.trustconnector.scdp.util.tlv.bertlv.*;

public class TPDU extends SimpleTLV
{
    boolean bRes;
    TLVList IEIx;
    int udlOffset;
    public static final Tag TAG;
    public static final int TPDU_TYPE_SMS_DELIVER = 0;
    public static final int TPDU_TYPE_SMS_DELIVER_REPORT = 0;
    public static final int TPDU_TYPE_SMS_SUBMIT = 1;
    public static final int TPDU_TYPE_SMS_STATUS = 2;
    public static final TLV IEI_70_00;
    public static final TLV IEI_71_00;
    
    public TPDU() {
        super(11);
        this.IEIx = new TLVList();
    }
    
    public TPDU(final Tag tag, final Length len, final byte[] v, final int vOff) {
        super(tag, len, v, vOff);
        this.IEIx = new TLVList();
        this.bRes = true;
        int offset = 0;
        ++offset;
        offset = ++offset + (2 + (this.value.getByte(offset) + 1) / 2);
        ++offset;
        ++offset;
        final int tp_vpf = this.value.getByte(0) & 0x18;
        if (tp_vpf != 0) {
            if (tp_vpf == 16) {
                ++offset;
            }
            else {
                offset += 7;
            }
        }
        final int udl = this.value.getByte(offset) & 0xFF;
        if (udl + offset + 1 != this.value.length()) {
            this.bRes = false;
        }
        this.udlOffset = offset;
        ++offset;
        int udhl = 0;
        if ((this.value.getByte(0) & 0x40) == 0x40) {
            udhl = this.IEIx.fromBytes(this.value.toBytes(), offset + 1, this.value.getByte(offset), new BERTLVParser());
            if (udhl != this.value.getByte(offset)) {
                this.bRes = false;
            }
            offset += udhl + 1;
        }
    }
    
    protected int getPIDOffset() {
        int offset = 0;
        ++offset;
        offset = ++offset + (2 + (this.value.getByte(offset) + 1) / 2);
        return offset;
    }
    
    public int getPID() {
        return this.value.getByte(this.getPIDOffset());
    }
    
    public int getDCS() {
        return this.value.getByte(this.getPIDOffset() + 1);
    }
    
    public byte[] getUserData() {
        final int udl = this.value.getByte(this.udlOffset) & 0xFF;
        return this.value.toBytes(this.udlOffset + 1, udl);
    }
    
    public byte[] getUserDataHead() {
        final int udhl = this.value.getByte(this.udlOffset + 1);
        return this.value.toBytes(this.udlOffset + 1, udhl);
    }
    
    public byte[] getUserDataContent() {
        final int udl = this.value.getByte(this.udlOffset) & 0xFF;
        final int udhl = this.value.getByte(this.udlOffset + 1);
        return this.value.toBytes(this.udlOffset + 1 + udhl + 1, udl - udhl - 1);
    }
    
    public int getConcatenateCount() {
        final TLV tlv = this.IEIx.findTLV(new BERTag(0));
        if (tlv != null) {
            final byte[] v = tlv.getValue();
            if (v.length == 3) {
                int c = v[1];
                if (v[2] > c) {
                    c = v[2];
                }
                return c;
            }
        }
        return 1;
    }
    
    public int getConcatenateIndex() {
        final TLV tlv = this.IEIx.findTLV(new BERTag(0));
        if (tlv != null) {
            final byte[] v = tlv.getValue();
            if (v.length == 3) {
                int c = v[1];
                if (v[2] < c) {
                    c = v[2];
                }
                return c;
            }
        }
        return 1;
    }
    
    @Override
    public String toString() {
        String res = "TPDU:" + super.toString();
        int offset = 0;
        res = res + "\n    -TP_TMI=0x" + String.format("%02X", this.value.getByte(offset));
        ++offset;
        res = res + "\n    -TP_MR=0x" + String.format("%02X", this.value.getByte(offset));
        ++offset;
        final int tpduLen = 2 + (this.value.getByte(offset) + 1) / 2;
        res = res + "\n    -TP_DA_D=" + ByteArray.convert(this.value.toBytes(offset, tpduLen));
        res = res + "\n    -TP_DA_Addr=" + Address.decodingAddrLV(this.value.toBytes(), offset);
        offset += tpduLen;
        res = res + "\n    -TP_PID=0x" + String.format("%02X", this.value.getByte(offset));
        ++offset;
        res = res + "\n    -TP_DCS=0x" + String.format("%02X", this.value.getByte(offset));
        ++offset;
        final int tp_vpf = this.value.getByte(0) & 0x18;
        if (tp_vpf != 0) {
            res += "\n    -TP_VPF Exist";
            if (tp_vpf == 16) {
                ++offset;
            }
            else {
                offset += 7;
            }
        }
        final int udl = this.value.getByte(offset) & 0xFF;
        res = res + "\n    -TP_UDL=0x" + String.format("%02X", udl);
        if (udl + offset + 1 != this.value.length()) {
            this.bRes = false;
        }
        this.udlOffset = offset;
        ++offset;
        int udhl = 0;
        if ((this.value.getByte(0) & 0x40) == 0x40) {
            udhl = this.IEIx.fromBytes(this.value.toBytes(), offset + 1, this.value.getByte(offset), new BERTLVParser());
            if (udhl != this.value.getByte(offset)) {
                this.bRes = false;
            }
            res = res + "\n    -TP_UDHL=0x" + String.format("%02X", udhl);
            res = res + "\n    -TP_UDH=" + ByteArray.convert(this.value.toBytes(offset + 1, udhl));
            offset += udhl + 1;
            res = res + "\n    -TP_UD_DATA=" + ByteArray.convert(this.value.toBytes(offset, udl - udhl - 1));
        }
        else {
            res = res + "\n    -TP_UD=" + ByteArray.convert(this.value.toBytes(offset, udl));
        }
        return res;
    }
    
    static {
        TAG = new SimpleTag(11);
        IEI_70_00 = new BERTLV(112);
        IEI_71_00 = new BERTLV(113);
    }
}
