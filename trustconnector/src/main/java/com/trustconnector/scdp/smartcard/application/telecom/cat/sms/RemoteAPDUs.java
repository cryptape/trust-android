package com.trustconnector.scdp.smartcard.application.telecom.cat.sms;

import com.trustconnector.scdp.util.*;
import com.trustconnector.scdp.smartcard.*;
import java.util.*;
import com.trustconnector.scdp.util.tlv.bertlv.*;
import com.trustconnector.scdp.util.tlv.*;

public class RemoteAPDUs
{
    protected Vector<byte[]> apduList;
    protected int cls;
    public static final int TAG_COMMAND_SCRIPTING_TEMPLATE = 170;
    public static final int TAG_COMMAND_SCRIPTING_TEMPLATE_INDEFINDED_LENGTH = 174;
    public static final int TAG_C_APDU = 34;
    public static final int TAG_IMM_ACTION = 129;
    public static final int TAG_ERROR_ACTION = 130;
    public static final int TAG_SCRIPT_CHAINING = 131;
    
    public RemoteAPDUs() {
        this.apduList = new Vector<byte[]>();
        this.cls = -1;
    }
    
    public RemoteAPDUs(final String apdus) {
        this.apduList = new Vector<byte[]>();
        this.cls = -1;
        this.appendAPDUs(apdus);
    }
    
    public int getAPDUCount() {
        return this.apduList.size();
    }
    
    public void appendAPDU(final byte[] apdu) {
        this.apduList.add(apdu);
    }
    
    public void appendAPDU(final byte[] apdu, final int offset, final int length) {
        final byte[] apduT = new byte[length];
        System.arraycopy(apdu, offset, apduT, 0, length);
        this.apduList.add(apduT);
    }
    
    public void appendAPDU(final String apdu) {
        this.apduList.add(ByteArray.convert(apdu));
    }
    
    public void appendAPDU(final APDU apdu) {
        final byte[] apduBuf = apdu.getCAPDU();
        if (apdu.getApduCase() == 4) {
            this.appendAPDU(apduBuf, 0, apduBuf.length - 1);
        }
        else {
            this.apduList.add(apduBuf);
        }
    }
    
    public void appendAPDUs(final String apdus) {
        final String[] apdu = apdus.split("\\|");
        for (int count = apdu.length, i = 0; i < count; ++i) {
            if (apdu[i].length() > 0) {
                this.appendAPDU(apdu[i]);
            }
        }
    }
    
    public int appendSelect(final String filePath, final int P1, final int P2, final boolean bGSM) {
        int c = 0;
        final byte[] fidB = ByteArray.convert(filePath);
        final APDU apdu = new APDU("Select", "00A40000");
        if (bGSM) {
            apdu.setClass(160);
        }
        apdu.setP1(P1);
        apdu.setP2(P2);
        if (!bGSM && P1 != 0) {
            apdu.clearCData();
            apdu.setCAPDU(fidB);
            this.appendAPDU(apdu);
            c = 1;
        }
        else {
            for (int fidLen = fidB.length, i = 0; i < fidLen; i += 2) {
                apdu.setCData(fidB, i, 2);
                this.appendAPDU(apdu);
                ++c;
            }
        }
        return c;
    }
    
    public void appendReadBinary(final int offset, final int length, final boolean bGSM) {
        final APDU apdu = new APDU("read binary", "00B0000000");
        if (bGSM) {
            apdu.setClass(160);
        }
        apdu.setP1P2(offset);
        apdu.setP3(length);
        this.appendAPDU(apdu);
    }
    
    public void appendUpdateBinary(final int offset, final byte[] data, final boolean bGSM) {
        final APDU apdu = new APDU("update binary", "00D6000000");
        if (bGSM) {
            apdu.setClass(160);
        }
        apdu.setP1P2(offset);
        apdu.appendCData(data);
        this.appendAPDU(apdu);
    }
    
    public void appendReadRecord(final int mode, final int recNum, final int recLen, final boolean bGSM) {
        final APDU apdu = new APDU("Read Record", "00B2000000");
        if (bGSM) {
            apdu.setClass(160);
        }
        apdu.setP1(recNum);
        apdu.setP2(mode);
        apdu.setP3(recLen);
        this.appendAPDU(apdu);
    }
    
    public void appendUpdateRecord(final int mode, final int recNum, final byte[] data, final boolean bGSM) {
        final APDU apdu = new APDU("Update Record", "00DC000000");
        if (bGSM) {
            apdu.setClass(160);
        }
        apdu.setP1(recNum);
        apdu.setP2(mode);
        apdu.appendCData(data);
        this.appendAPDU(apdu);
    }
    
    public void appendSearch(final int mode, final byte[] searchData, final boolean bGSM) {
    }
    
    public void changeAPDUClass(final int cls) {
        this.cls = cls;
    }
    
    public void clearAllAPDU() {
        this.apduList.removeAllElements();
        this.cls = -1;
    }
    
    public byte[] toCompactFormat() {
        final ByteArray apduListT = new ByteArray();
        for (final byte[] a : this.apduList) {
            if (this.cls != -1) {
                a[0] = (byte)this.cls;
            }
            apduListT.append(a);
        }
        return apduListT.toBytes();
    }
    
    public Vector<byte[]> toExpandedFormat(final int maxBatchLen, final boolean bChainning, final boolean bIsIndefinite) {
        final Vector<byte[]> pkgs = new Vector<byte[]>();
        final TLV scriptChaining = BERTLVBuilder.buildTLV(131);
        if (bIsIndefinite) {
            final ByteArray indefinded = new ByteArray();
            for (final byte[] v : this.apduList) {
                if (this.cls != -1) {
                    v[0] = (byte)this.cls;
                }
                final TLV apdu = BERTLVBuilder.buildTLV(34, v);
                if (indefinded.length() == 0) {
                    indefinded.append((byte)(-82));
                    indefinded.append((byte)(-128));
                }
                if (bChainning && indefinded.length() == 0) {
                    if (pkgs.size() == 0) {
                        scriptChaining.updateValue(new byte[] { 1 });
                    }
                    indefinded.append(scriptChaining.toBytes());
                }
                if (indefinded.length() + 2 <= maxBatchLen) {
                    indefinded.append(apdu.toBytes());
                }
                else {
                    indefinded.append(0, 2);
                    pkgs.add(indefinded.toBytes());
                    indefinded.clearContent();
                }
            }
            if (indefinded.length() > 0) {
                indefinded.append(0, 2);
                pkgs.add(indefinded.toBytes());
            }
        }
        else {
            final TLV cmdScriptTempl = BERTLVBuilder.buildTLV(170);
            for (final byte[] v : this.apduList) {
                final TLV apdu = BERTLVBuilder.buildTLV(34, v);
                if (bChainning && cmdScriptTempl.getValueLen() == 0) {
                    if (pkgs.size() == 0) {
                        scriptChaining.updateValue(new byte[] { 1 });
                    }
                    cmdScriptTempl.appendValue(scriptChaining.toBytes());
                }
                if (cmdScriptTempl.length() + apdu.length() < maxBatchLen) {
                    cmdScriptTempl.appendValue(apdu.toBytes());
                }
                else {
                    pkgs.add(cmdScriptTempl.toBytes());
                    cmdScriptTempl.updateValue(null);
                    scriptChaining.updateValue(new byte[] { 2 });
                }
            }
            if (cmdScriptTempl.length() > 0) {
                scriptChaining.updateValue(new byte[] { 2 });
                pkgs.add(cmdScriptTempl.toBytes());
            }
        }
        return pkgs;
    }
    
    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder();
        for (final byte[] a : this.apduList) {
            if (this.cls != -1) {
                a[0] = (byte)this.cls;
            }
            s.append(ByteArray.convert(a));
        }
        return s.toString();
    }
}
