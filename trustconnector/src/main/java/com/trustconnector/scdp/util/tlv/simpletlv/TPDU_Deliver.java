package com.trustconnector.scdp.util.tlv.simpletlv;

import com.trustconnector.scdp.util.tlv.*;
import java.text.*;
import com.trustconnector.scdp.util.*;
import com.trustconnector.scdp.util.tlv.bertlv.*;
import java.util.*;
import com.trustconnector.scdp.smartcard.application.telecom.cat.sms.*;

public class TPDU_Deliver extends TPDU
{
    private static int batNo;
    protected byte TPMTI;
    protected byte[] TP_OA;
    protected byte TP_PID;
    protected byte TP_DCS;
    protected byte[] TP_SCTS;
    protected Vector<TLV> IEIx;
    protected byte[] userData;
    public static final int USER_DATA_MAX_LEN = 140;
    
    public TPDU_Deliver() {
        this.TPMTI |= 0x0;
        this.TPMTI |= 0x4;
        this.TP_PID = 127;
        this.TP_DCS = -10;
        final SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
        String date = sdf.format(new Date());
        date = Util.strHighLowRevert(date);
        date += "23";
        this.TP_SCTS = ByteArray.convert(date);
        this.IEIx = new Vector<TLV>();
    }
    
    public TPDU_Deliver(final String dstAddr) {
        this.TPMTI |= 0x0;
        this.TPMTI |= 0x4;
        this.TP_PID = 127;
        this.TP_DCS = -10;
        final SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
        String date = sdf.format(new Date());
        date = Util.strHighLowRevert(date);
        date += "23";
        this.TP_SCTS = ByteArray.convert(date);
        this.IEIx = new Vector<TLV>();
        this.setOriginaAddr(dstAddr);
    }
    
    TPDU_Deliver(final TPDU_Deliver tpdu) {
        this.TPMTI = tpdu.TPMTI;
        this.TP_OA = tpdu.TP_OA.clone();
        this.TP_PID = tpdu.TP_PID;
        this.TP_DCS = tpdu.TP_DCS;
        this.TP_SCTS = tpdu.TP_SCTS.clone();
        this.IEIx = new Vector<TLV>();
    }
    
    public void setOriginaAddr(final String addr) {
        this.TP_OA = Address.encodingAddrLV(addr);
    }
    
    public void setPID(final int PID) {
        this.TP_PID = (byte)PID;
    }
    
    public void setDCS(final int DCS) {
        this.TP_DCS = (byte)DCS;
    }
    
    public void setSCTS(final byte[] scts) {
        this.TP_SCTS = scts;
    }
    
    public void appendIEIx(final TLV tlv) {
        this.IEIx.addElement(tlv);
        this.TPMTI |= 0x40;
    }
    
    public void setChainningInfo(final int batch, final int maxCount, final int curIndex) {
        this.TPMTI |= 0x40;
        if (curIndex == maxCount) {
            this.TPMTI |= 0x4;
        }
        else {
            this.TPMTI &= (byte)251;
        }
        final byte[] c = { (byte)batch, (byte)maxCount, (byte)curIndex };
        final TLV chainningIEIx = BERTLVBuilder.buildTLV(0, c);
        this.IEIx.addElement(chainningIEIx);
    }
    
    public void setUserData(final byte[] ud) {
        this.userData = ud.clone();
    }
    
    public void setUserData(final byte[] ud, final int offset, final int length) {
        System.arraycopy(ud, offset, this.userData = new byte[length], 0, length);
    }
    
    @Override
    public byte[] toBytes() {
        final ByteArray tpdu = new ByteArray();
        tpdu.append(this.TPMTI);
        tpdu.append(this.TP_OA);
        tpdu.append(this.TP_PID);
        tpdu.append(this.TP_DCS);
        tpdu.append(this.TP_SCTS);
        final ByteArray udh = new ByteArray();
        for (final TLV tlv : this.IEIx) {
            udh.append(tlv.toBytes());
        }
        int udLen = this.userData.length;
        final int udhLen = udh.length();
        if (this.IEIx.size() != 0) {
            udLen += 1 + udhLen;
        }
        tpdu.append((byte)udLen);
        if (this.IEIx.size() != 0) {
            tpdu.append((byte)udhLen);
            tpdu.append(udh);
        }
        tpdu.append(this.userData);
        this.updateValue(tpdu.toBytes());
        return super.toBytes();
    }
    
    public TPDU buildTPDU(final CommandPackage pkg) {
        final byte[] userDataInCP = pkg.toBytes();
        final TPDU_Deliver tpdu = new TPDU_Deliver(this);
        tpdu.appendIEIx(TPDU_Deliver.IEI_70_00);
        tpdu.setUserData(userDataInCP);
        return tpdu;
    }
    
    public Vector<TPDU> buildTPDUs(final CommandPackage pkg, final int batchNo) {
        final Vector<TPDU> tpdus = new Vector<TPDU>();
        final byte[] userDataInCP = pkg.toBytes();
        final int udl = userDataInCP.length;
        final boolean bCon = udl + 3 > 140;
        int maxCount = 0;
        int curIndex = 1;
        if (bCon) {
            maxCount = (udl + 2) / 134;
            if ((udl + 2) % 134 != 0) {
                ++maxCount;
            }
        }
        int leftLen = udl;
        int offset = 0;
        while (leftLen > 0) {
            int maxLen = 140;
            final TPDU_Deliver tpdu = new TPDU_Deliver(this);
            if (bCon) {
                tpdu.setChainningInfo(batchNo, maxCount, curIndex);
                maxLen -= 5;
            }
            if (leftLen == udl) {
                tpdu.appendIEIx(TPDU_Deliver.IEI_70_00);
                maxLen -= 2;
            }
            --maxLen;
            final int userDataLen = (leftLen > maxLen) ? maxLen : leftLen;
            tpdu.setUserData(userDataInCP, offset, userDataLen);
            leftLen -= userDataLen;
            offset += userDataLen;
            ++curIndex;
            tpdus.addElement(tpdu);
        }
        return tpdus;
    }
    
    public static TPDU buildTPDU(final String dstAddr, final CommandPackage pkg) {
        final SCP80 scp = SCP80.getService();
        scp.wrap(pkg);
        final TPDU_Deliver tpdu = new TPDU_Deliver(dstAddr);
        return tpdu.buildTPDU(pkg);
    }
    
    public static TPDU buildTPDU(final String dstAddr, final int spi, final int kic, final int kid, final int tar, final String apdus) {
        final CommandPackage cp = new CommandPackage(spi, kic, kid, tar, apdus);
        final SCP80 scp = SCP80.getService();
        scp.wrap(cp);
        final TPDU_Deliver tpdu = new TPDU_Deliver(dstAddr);
        return tpdu.buildTPDU(cp);
    }
    
    public static Vector<TPDU> buildTPDUs(final String dstAddr, final int spi, final int kic, final int kid, final int tar, final String apdus, final int batchNo) {
        final CommandPackage cp = new CommandPackage(spi, kic, kid, tar, apdus);
        final SCP80 scp = SCP80.getService();
        scp.wrap(cp);
        final TPDU_Deliver tpdu = new TPDU_Deliver(dstAddr);
        return tpdu.buildTPDUs(cp, batchNo);
    }
    
    public static Vector<TPDU> buildTPDUs(final String dstAddr, final int spi, final int kic, final int kid, final int tar, final String apdus) {
        ++TPDU_Deliver.batNo;
        return buildTPDUs(dstAddr, spi, kic, kid, tar, apdus, TPDU_Deliver.batNo);
    }
}
