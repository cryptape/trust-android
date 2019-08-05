package libs.trustconnector.scdp.util.tlv.simpletlv;

import libs.trustconnector.scdp.util.*;
import libs.trustconnector.scdp.util.tlv.*;
import java.util.*;
import libs.trustconnector.scdp.util.tlv.bertlv.*;
import libs.trustconnector.scdp.smartcard.application.telecom.cat.sms.*;

import libs.trustconnector.scdp.smartcard.application.telecom.cat.sms.CommandPackage;
import libs.trustconnector.scdp.smartcard.application.telecom.cat.sms.SCP80;
import libs.trustconnector.scdp.util.ByteArray;
import libs.trustconnector.scdp.util.tlv.TLV;
import libs.trustconnector.scdp.util.tlv.bertlv.BERTLV;
import libs.trustconnector.scdp.util.tlv.bertlv.BERTLVBuilder;

public class TPDU_CDMA_Deliver extends TPDU_CDMA
{
    static final byte[] defaultTele;
    private static int batNo;
    int messageID;
    boolean bHasHead;
    int displayMode;
    byte[] userData;
    ByteArray bearerData;
    String addr;
    protected Vector<TLV> IEIx;
    public static final int USER_DATA_MAX_LEN = 140;
    
    public TPDU_CDMA_Deliver() {
        this.addParam(new BERTLV(0, TPDU_CDMA_Deliver.defaultTele));
        this.IEIx = new Vector<TLV>();
        this.displayMode = 224;
    }
    
    public TPDU_CDMA_Deliver(final String addr) {
        super((byte)0);
        this.addParam(new BERTLV(0, TPDU_CDMA_Deliver.defaultTele));
        this.addr = addr;
        final TLV addrTLV = convertAddr(addr, (byte)2);
        this.addParam(addrTLV);
        this.IEIx = new Vector<TLV>();
        this.displayMode = 224;
    }
    
    public TPDU_CDMA_Deliver(final TPDU_CDMA_Deliver src) {
        super((byte)0);
        this.addParam(new BERTLV(0, TPDU_CDMA_Deliver.defaultTele));
        final TLV addrTLV = convertAddr(src.addr, (byte)2);
        this.addr = src.addr;
        this.addParam(addrTLV);
        this.messageID = src.messageID;
        this.bHasHead = src.bHasHead;
        this.displayMode = src.displayMode;
        this.IEIx = new Vector<TLV>();
        this.displayMode = 224;
    }
    
    public void setMessageID(final int msgID) {
        this.messageID = msgID;
    }
    
    public void setDisplayMode(final int displayMode) {
        this.displayMode = displayMode;
    }
    
    public void setUserData(final byte[] userData) {
        this.setUserData(userData, true);
    }
    
    public void setUserData(final byte[] userData, final boolean bHasHeader) {
        this.setUserData(userData, 0, userData.length, bHasHeader);
    }
    
    public void setUserData(final byte[] userData, final int offset, final int length) {
        this.setUserData(userData, offset, length, true);
    }
    
    public void setUserData(final byte[] userData, final int offset, final int length, final boolean bHasHeader) {
        System.arraycopy(userData, offset, this.userData = new byte[length], 0, length);
        this.bHasHead = bHasHeader;
    }
    
    public void addParam(final TLV param) {
        this.params.add(param);
    }
    
    @Override
    public byte[] toBytes() {
        (this.bearerData = new ByteArray()).append((byte)8);
        this.bearerData.append((byte)0);
        final byte[] msgID = { 0, 3, 16, 0, 0 };
        if (this.bHasHead) {
            final byte[] array = msgID;
            final int n = 4;
            array[n] |= 0x8;
            if (this.IEIx.size() == 0) {
                this.appendIEIx(TPDU.IEI_70_00);
            }
        }
        this.bearerData.append(msgID);
        final byte[] displayMode = { 15, 1, (byte)this.displayMode };
        this.bearerData.append(displayMode);
        final ByteArray udh = new ByteArray();
        for (final TLV tlv : this.IEIx) {
            udh.append(tlv.toBytes());
        }
        final ByteArray ud = new ByteArray();
        ud.append(udh.toLV());
        ud.append(this.userData);
        final ByteArray userDataLVBA = new ByteArray(ud.toLV());
        userDataLVBA.shiftRight(5);
        this.bearerData.append((byte)1);
        this.bearerData.append(userDataLVBA.toLV());
        this.bearerData.setByte(1, this.bearerData.length() - 2);
        this.updateValue(null);
        this.appendValue((byte)0);
        for (final TLV tlv2 : this.params) {
            this.appendValue(tlv2.toBytes());
        }
        this.appendValue(this.bearerData.toBytes());
        return super.toBytes();
    }
    
    public void setChainningInfo(final int batch, final int maxCount, final int curIndex) {
        final byte[] c = { (byte)batch, (byte)maxCount, (byte)curIndex };
        final TLV chainningIEIx = BERTLVBuilder.buildTLV(0, c);
        this.IEIx.addElement(chainningIEIx);
    }
    
    public void appendIEIx(final TLV tlv) {
        this.IEIx.addElement(tlv);
    }
    
    public static TPDU_CDMA buildTPDU(final CommandPackage pkg) {
        final byte[] userDataInCP = pkg.toBytes();
        final TPDU_CDMA_Deliver tpdu = new TPDU_CDMA_Deliver();
        tpdu.appendIEIx(TPDU.IEI_70_00);
        tpdu.setUserData(userDataInCP);
        return tpdu;
    }
    
    public Vector<TPDU_CDMA> buildTPDUs(final CommandPackage pkg, final int batchNo) {
        final Vector<TPDU_CDMA> tpdus = new Vector<TPDU_CDMA>();
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
            final TPDU_CDMA_Deliver tpdu = new TPDU_CDMA_Deliver(this);
            if (bCon) {
                tpdu.setChainningInfo(batchNo, maxCount, curIndex);
                maxLen -= 5;
            }
            if (leftLen == udl) {
                tpdu.appendIEIx(TPDU.IEI_70_00);
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
    
    public static TPDU_CDMA buildTPDU(final int spi, final int kic, final int kid, final int tar, final String apdus) {
        final CommandPackage cp = new CommandPackage(spi, kic, kid, tar, apdus);
        final SCP80 scp = SCP80.getService();
        scp.wrap(cp);
        return buildTPDU(cp);
    }
    
    public static Vector<TPDU_CDMA> buildTPDUs(final int spi, final int kic, final int kid, final int tar, final String apdus, final int batchNo) {
        final CommandPackage cp = new CommandPackage(spi, kic, kid, tar, apdus);
        final SCP80 scp = SCP80.getService();
        scp.wrap(cp);
        final TPDU_CDMA_Deliver tpdu = new TPDU_CDMA_Deliver();
        return tpdu.buildTPDUs(cp, batchNo);
    }
    
    public static Vector<TPDU_CDMA> buildTPDUs(final String dstAddr, final int spi, final int kic, final int kid, final int tar, final String apdus) {
        ++TPDU_CDMA_Deliver.batNo;
        return buildTPDUs(spi, kic, kid, tar, apdus, TPDU_CDMA_Deliver.batNo);
    }
    
    static {
        defaultTele = new byte[] { 16, 2 };
        TPDU_CDMA_Deliver.batNo = 0;
    }
}
