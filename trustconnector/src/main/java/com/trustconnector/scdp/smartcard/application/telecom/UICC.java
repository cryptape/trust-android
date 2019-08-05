package com.trustconnector.scdp.smartcard.application.telecom;

import com.trustconnector.scdp.SCDP;
import com.trustconnector.scdp.smartcard.AID;
import com.trustconnector.scdp.smartcard.APDUChecker;
import com.trustconnector.scdp.smartcard.NormalAPDUChecker;
import com.trustconnector.scdp.smartcard.SmartCardReader;
import com.trustconnector.scdp.smartcard.application.Application;
import com.trustconnector.scdp.smartcard.application.telecom.cat.ProactiveCommand;
import com.trustconnector.scdp.smartcard.application.telecom.cat.SendSMS;
import com.trustconnector.scdp.smartcard.application.telecom.cat.SetupEventList;
import com.trustconnector.scdp.smartcard.application.telecom.cat.SetupMenu;
import com.trustconnector.scdp.smartcard.application.telecom.cat.sms.ResponsePackage;
import com.trustconnector.scdp.smartcard.application.telecom.checkrule.FCIRuleChecker;
import com.trustconnector.scdp.smartcard.application.telecom.checkrule.FCPRuleChecker;
import com.trustconnector.scdp.smartcard.application.telecom.checkrule.cat.SendSMSChecker;
import com.trustconnector.scdp.util.ByteArray;
import com.trustconnector.scdp.util.Util;
import com.trustconnector.scdp.util.tlv.TLV;
import com.trustconnector.scdp.util.tlv.TLVList;
import com.trustconnector.scdp.util.tlv.TLVTree;
import com.trustconnector.scdp.util.tlv.TLVTreeItem;
import com.trustconnector.scdp.util.tlv.bertlv.BERTLV;
import com.trustconnector.scdp.util.tlv.bertlv.BERTLVBuilder;
import com.trustconnector.scdp.util.tlv.bertlv.BERTLVParser;
import com.trustconnector.scdp.util.tlv.simpletlv.AccessTechnology;
import com.trustconnector.scdp.util.tlv.simpletlv.Address;
import com.trustconnector.scdp.util.tlv.simpletlv.BrowserTerminationCause;
import com.trustconnector.scdp.util.tlv.simpletlv.BrowsingStatus;
import com.trustconnector.scdp.util.tlv.simpletlv.CellBroadcastPage;
import com.trustconnector.scdp.util.tlv.simpletlv.ChannelDataLength;
import com.trustconnector.scdp.util.tlv.simpletlv.ChannelStatus;
import com.trustconnector.scdp.util.tlv.simpletlv.CommandDetails;
import com.trustconnector.scdp.util.tlv.simpletlv.DeviceIdentities;
import com.trustconnector.scdp.util.tlv.simpletlv.DisplayParameters;
import com.trustconnector.scdp.util.tlv.simpletlv.EventList;
import com.trustconnector.scdp.util.tlv.simpletlv.HelpRequest;
import com.trustconnector.scdp.util.tlv.simpletlv.ItemIdentifier;
import com.trustconnector.scdp.util.tlv.simpletlv.Language;
import com.trustconnector.scdp.util.tlv.simpletlv.LocationInfo;
import com.trustconnector.scdp.util.tlv.simpletlv.LocationStatus;
import com.trustconnector.scdp.util.tlv.simpletlv.NetworkSearchMode;
import com.trustconnector.scdp.util.tlv.simpletlv.Result;
import com.trustconnector.scdp.util.tlv.simpletlv.ServiceRecord;
import com.trustconnector.scdp.util.tlv.simpletlv.SimpleTLV;
import com.trustconnector.scdp.util.tlv.simpletlv.TPDU;
import com.trustconnector.scdp.util.tlv.simpletlv.TPDU_CDMA;
import com.trustconnector.scdp.util.tlv.simpletlv.TextString;
import com.trustconnector.scdp.util.tlv.simpletlv.TimerIdentifier;
import com.trustconnector.scdp.util.tlv.simpletlv.TimerValue;
import com.trustconnector.scdp.util.tlv.simpletlv.TransactionIdentifier;
import java.util.Vector;

public class UICC extends Application {
    protected boolean bGSMMode;
    protected SetupMenu setupMenu;
    protected SetupEventList setupEventList;
    protected ProactiveCommand lastProactiveCmd;
    boolean bAIDInit;
    protected AID USIM;
    protected AID CSIM;
    protected AID ISIM;
    protected String DefaultCheckRule = "9000|91XX";
    public static final int SELECT_P1_SELECT_BY_FID = 0;
    public static final int SELECT_P1_SELECT_CHILD_DF = 1;
    public static final int SELECT_P1_SELECT_PARENT_DF = 3;
    public static final int SELECT_P1_SELECT_BY_AID = 4;
    public static final int SELECT_P1_SELECT_BY_PATH_MF = 8;
    public static final int SELECT_P1_SELECT_BY_PATH_CUR_DF = 9;
    public static final int SELECT_P2_NO_FCP = 12;
    public static final int SELECT_P2_RETURN_FCP = 4;
    static final byte[] USIM_AID_Magic = new byte[]{-96, 0, 0, 0, -121, 16, 2};
    static final byte[] CSIM_AID_Magic = new byte[]{-96, 0, 0, 3, 67, 16, 2};
    static final byte[] ISIM_AID_Magic = new byte[]{-96, 0, 0, 0, -121, 16, 4};
    public static int STATUS_P2_RETURN_FCP = 0;
    public static int STATUS_P2_RETURN_AID = 1;
    public static int STATUS_P2_NO_DATA_RETURN = 12;
    public static int RECORD_MODE_NEXT = 2;
    public static int RECORD_MODE_PREVIOUS = 3;
    public static int RECORD_MODE_ABS_CUR = 4;
    public static final int UICC_PIN_REF_UPIN = 17;
    public static final int UICC_PIN_REF_LPIN1 = 1;
    public static final int UICC_PIN_REF_LPIN2 = 129;
    public static final int UICC_PIN_REF_ADM1 = 10;
    public static final int UICC_PIN_REF_ADM2 = 11;
    public static final int SIM_PIN_REF_CHV1 = 1;
    public static final int SIM_PIN_REF_CHV2 = 2;
    public static final int SIM_PIN_REF_CHV4 = 4;
    public static final int SIM_PIN_REF_CHV5 = 5;
    public static final int PIN_TO_CONVERT_CHV1 = 1;
    public static final int PIN_TO_CONVERT_CHV2 = 2;
    public static final int PIN_TO_CONVERT_ADM = 4;
    protected String defult_Profile = "FFFFFFFFFFFFFFFFFFFFFFFF";

    public SetupMenu getSetupMenu() {
        return this.setupMenu;
    }

    public SetupEventList getSetupEventList() {
        return this.setupEventList;
    }

    public UICC(SmartCardReader reader) {
        super(reader, (AID)null);
    }

    protected void transmit() {
        if (this.bGSMMode) {
            this.apdu.setClass(160);
        }

        super.transmit();
    }

    public void reset() {
        super.reset();
        this.bGSMMode = false;
        this.bDisable616C = false;
        this.bDisable616CTemp = true;
    }

    public void setDefaultCheckRule(String simpleRule) {
        this.DefaultCheckRule = simpleRule;
    }

    public void autoMode() {
        this.selectFile(16128, (String)"6E00|9000");
        if (this.apdu.getSW() == 28160) {
            this.bGSMMode = true;
        } else {
            this.bGSMMode = false;
        }

    }

    public void setGSMMode(boolean bGSMMode) {
        this.bGSMMode = bGSMMode;
    }

    public boolean getGSMMode() {
        return this.bGSMMode;
    }

    public SelectFileResponse selectFile(String fid) {
        int fidV = Util.HexStringToInt(fid);
        return this.selectFile(fidV);
    }

    public SelectFileResponse selectFile(String fid, String simpleRule) {
        int fidV = Util.HexStringToInt(fid);
        return this.selectFile(fidV, simpleRule);
    }

    public SelectFileResponse selectFile(String fid, APDUChecker c) {
        int fidV = Util.HexStringToInt(fid);
        return this.selectFile(fidV, c);
    }

    public SelectFileResponse selectFile(int fid) {
        if (this.bGSMMode) {
            APDUChecker c = new FCIRuleChecker();
            return this.selectFile(0, 0, fid, (APDUChecker)c);
        } else {
            APDUChecker c = new FCPRuleChecker();
            return this.selectFile(0, 4, fid, (APDUChecker)c);
        }
    }

    public SelectFileResponse selectFile(int fid, String simpleRule) {
        return this.bGSMMode ? this.selectFile(0, 0, fid, (APDUChecker)(new NormalAPDUChecker(simpleRule))) : this.selectFile(0, 4, fid, (APDUChecker)(new NormalAPDUChecker(simpleRule)));
    }

    public SelectFileResponse selectFile(int fid, APDUChecker c) {
        return this.bGSMMode ? this.selectFile(0, 0, fid, (APDUChecker)c) : this.selectFile(0, 4, fid, (APDUChecker)c);
    }

    public void selectFileNoFCP(int fid) {
        byte[] fidB = new byte[]{(byte)(fid >> 8), (byte)fid};
        if (this.bGSMMode) {
            this.selectFile(0, 0, fidB, (String)this.DefaultCheckRule);
        } else {
            this.selectFile(0, 12, fidB, (String)this.DefaultCheckRule);
        }

    }

    public void selectFileNoFCP(int fid, String simpleRule) {
        byte[] fidB = new byte[]{(byte)(fid >> 8), (byte)fid};
        this.selectFile(0, 12, fidB, (APDUChecker)(new NormalAPDUChecker(simpleRule)));
    }

    public SelectFileResponse selectADF(String aid) {
        FCPRuleChecker fcp = new FCPRuleChecker();
        return this.selectFile(4, 4, ByteArray.convert(aid), (APDUChecker)fcp);
    }

    public SelectFileResponse selectADF(String aid, String simpleRule) {
        return this.selectFile(4, 4, ByteArray.convert(aid), (APDUChecker)(new NormalAPDUChecker(simpleRule)));
    }

    public SelectFileResponse selectADF(AID aid) {
        FCPRuleChecker fcp = new FCPRuleChecker();
        return this.selectFile(4, 4, aid.toBytes(), (APDUChecker)fcp);
    }

    public SelectFileResponse selectADF(AID aid, String simpleRule) {
        return this.selectFile(4, 4, aid.toBytes(), (APDUChecker)(new NormalAPDUChecker(simpleRule)));
    }

    public SelectFileResponse selectFileByPathFromMF(String filePath) {
        Object c;
        if (this.bGSMMode) {
            c = new FCIRuleChecker();
        } else {
            c = new FCPRuleChecker();
        }

        return this.selectFileByPathFromMF(filePath, (APDUChecker)c);
    }

    public SelectFileResponse selectFileByPathFromMF(String filePath, String simpleRule) {
        return this.selectFileByPathFromMF(filePath, (APDUChecker)(new NormalAPDUChecker(simpleRule)));
    }

    public SelectFileResponse selectFileByPathFromMF(final String filePath, final APDUChecker c) {
        final byte[] fidB = ByteArray.convert(filePath);
        if (this.bGSMMode) {
            this.selectFile(0, 0, 16128, this.DefaultCheckRule);
            final int fidLen = fidB.length;
            for (int i = 0; i < fidLen - 2; i += 2) {
                final int fid = (fidB[i] << 8 & 0xFF00) | (fidB[i + 1] & 0xFF);
                this.selectFile(0, 0, fid, this.DefaultCheckRule);
            }
            final int fid2 = (fidB[fidLen - 2] << 8 & 0xFF00) | (fidB[fidLen - 1] & 0xFF);
            return this.selectFile(0, 0, fid2, c);
        }
        return this.selectFile(8, 4, fidB, c);
    }

    public SelectFileResponse selectFileByPathFromCurDF(String filePath) {
        FCPRuleChecker fcp = new FCPRuleChecker();
        return this.selectFileByPathFromCurDF(filePath, (APDUChecker)fcp);
    }

    public SelectFileResponse selectFileByPathFromCurDF(String filePath, String simpleRule) {
        return this.selectFileByPathFromCurDF(filePath, (APDUChecker)(new NormalAPDUChecker(simpleRule)));
    }

    public SelectFileResponse selectFileByPathFromCurDF(String filePath, APDUChecker c) {
        byte[] fidB = ByteArray.convert(filePath);
        if (!this.bGSMMode) {
            return this.selectFile(9, 4, fidB, (APDUChecker)c);
        } else {
            SelectFileResponse res = null;

            for(int i = 0; i < fidB.length; i += 2) {
                int fid = fidB[i] << 8 & '\uff00' | fidB[i + 1] & 255;
                res = this.selectFile(0, 0, fid, (String)this.DefaultCheckRule);
            }

            return res;
        }
    }

    public SelectFileResponse selectFile(int p1, int p2, int fid, String simpleRule) {
        return this.selectFile(p1, p2, fid, (APDUChecker)(new NormalAPDUChecker(simpleRule)));
    }

    public SelectFileResponse selectFile(int p1, int p2, byte[] fid, String simpleRule) {
        return this.selectFile(p1, p2, fid, (APDUChecker)(new NormalAPDUChecker(simpleRule)));
    }

    public SelectFileResponse selectFile(int p1, int p2, int fid, APDUChecker c) {
        byte[] fidB = new byte[]{(byte)(fid >> 8), (byte)fid};
        return this.selectFile(p1, p2, fidB, c);
    }

    public SelectFileResponse selectFile(int p1, int p2, byte[] fid, APDUChecker c) {
        this.apdu.setCAPDU("Select File " + ByteArray.convert(fid), "00A4000000");
        this.apdu.setP1(p1);
        this.apdu.setP2(p2);
        this.apdu.setCData(fid);
        if (!this.bGSMMode) {
            this.apdu.setRAPDUChecker(c);
        }

        this.transmit();
        if (this.bGSMMode) {
            if (this.apdu.getSW1() == -97) {
                this.apdu.setName("Get Response");
                this.apdu.setCAPDU("A0C00000");
                this.apdu.setP3(this.apdu.getSW2());
                this.apdu.setRAPDUChecker(c);
                this.transmit();
            } else {
                c.check(this.apdu);
            }
        }

        byte[] res = this.apdu.getRData();
        if (res != null) {
            if (this.bGSMMode) {
                SCDP.addAPDUInfo((new FCI(res)).toString());
                return new FCI(res);
            } else {
                SCDP.addAPDUInfo((new FCP(res)).toString());
                return new FCP(res);
            }
        } else {
            return null;
        }
    }

    public ARR getARR(String filePath, FCP fcp) {
        String fidARR = String.format("%04X", fcp.getARRFileID());
        int c = SCDP.beginGroup("Get ARR of File=" + filePath + " ArrFileID=" + fidARR);
        if (filePath.startsWith("3F00")) {
            filePath = filePath.substring(4);
        }

        int length = filePath.length();
        ARR arr = null;

        while(true) {
            String arrPath = filePath.substring(0, length - 4);
            arrPath = arrPath + fidARR;
            if (arrPath.length() < 4) {
                break;
            }

            SelectFileResponse fcpT = this.selectFileByPathFromMF(arrPath, "6A82|9000");
            if (fcpT != null) {
                arr = new ARR(this.readRecord(fcp.getARRRecID(), fcpT.getRecordLength()));
                break;
            }

            length -= 4;
        }

        SCDP.endGroup(c);
        return arr;
    }

    public ARR getARRUnderADF(String filePath, FCP fcp) {
        String fidARR = String.format("%04X", fcp.getARRFileID());
        int c = SCDP.beginGroup("Get ARR of File=" + filePath + " ArrFileID=" + fidARR);
        if (filePath.startsWith("7FFF")) {
            filePath = filePath.substring(4);
        }

        int length = filePath.length();
        ARR arr = null;
        this.selectFile(32767);

        while(true) {
            String arrPath = filePath.substring(0, length - 4);
            arrPath = arrPath + fidARR;
            if (arrPath.length() < 4) {
                break;
            }

            SelectFileResponse f = this.selectFileByPathFromCurDF(arrPath, "6A82|9000");
            if (f != null) {
                arr = new ARR(this.readRecord(fcp.getARRRecID(), f.getRecordLength()));
                break;
            }

            length -= 4;
        }

        SCDP.endGroup(c);
        return arr;
    }

    public void getAllADFAid() {
        if (!this.bAIDInit) {
            int ck = SCDP.beginGroup("Get All ADF AID");
            this.selectFile(16128);
            FCPRuleChecker f = new FCPRuleChecker();
            this.selectFile(0, 4, 12032, (APDUChecker)f);
            int recNum = f.recordNum.getReturnValue();
            int recLen = f.recordLen.getReturnValue();

            for(int i = 1; i <= recNum; ++i) {
                byte[] c = this.readRecord(i, recLen);
                if (c[0] == 97) {
                    int valueLen = (c[1] & 255) + 2;
                    TLVTree tlv = new TLVTree(c, 0, valueLen, new BERTLVParser());
                    TLVTreeItem item = tlv.findTLV(BERTLVBuilder.buildTagList("614F"));
                    byte[] aid = item.getValue();
                    if (Util.arrayCompare(aid, 0, USIM_AID_Magic, 0, USIM_AID_Magic.length)) {
                        this.USIM = new AID(aid);
                    } else if (Util.arrayCompare(aid, 0, CSIM_AID_Magic, 0, CSIM_AID_Magic.length)) {
                        this.CSIM = new AID(aid);
                    } else if (Util.arrayCompare(aid, 0, ISIM_AID_Magic, 0, ISIM_AID_Magic.length)) {
                        this.ISIM = new AID(aid);
                    }
                }
            }

            SCDP.endGroup(ck);
            this.bAIDInit = true;
        }
    }

    public AID getUSIMADFAid() {
        this.getAllADFAid();
        return this.USIM;
    }

    public AID getCSIMADFAid() {
        this.getAllADFAid();
        return this.CSIM;
    }

    public AID getISIMADFAid() {
        this.getAllADFAid();
        return this.ISIM;
    }

    public void deactivateFile() {
        this.deactivateFile(this.DefaultCheckRule);
    }

    public void deactivateFile(String simpleRule) {
        this.deactivateFile((APDUChecker)(new NormalAPDUChecker(simpleRule)));
    }

    public void deactivateFile(APDUChecker c) {
        this.apdu.setName("Deactivate File");
        this.apdu.setCAPDU("0004000000");
        this.apdu.setRAPDUChecker(c);
        this.transmit();
    }

    public void activateFile() {
        this.activateFile(this.DefaultCheckRule);
    }

    public void activateFile(String simpleRule) {
        this.activateFile((APDUChecker)(new NormalAPDUChecker(simpleRule)));
    }

    public void activateFile(APDUChecker c) {
        this.apdu.setName("Activate File");
        this.apdu.setCAPDU("0044000000");
        this.apdu.setRAPDUChecker(c);
        this.transmit();
    }

    public byte openChannel() {
        this.openChannel(this.DefaultCheckRule);
        return this.apdu.getRData()[0];
    }

    public void openChannel(String simpleRule) {
        this.openChannel((APDUChecker)(new NormalAPDUChecker(simpleRule)));
    }

    public void openChannel(APDUChecker c) {
        this.apdu.setName("Open Channel");
        this.apdu.setCAPDU("0070000001");
        this.apdu.setRAPDUChecker(c);
        this.transmit();
    }

    public void closeChannel(byte channelID) {
        this.closeChannel(channelID, this.DefaultCheckRule);
    }

    public void closeChannel(byte channelID, String simpleRule) {
        this.closeChannel(channelID, (APDUChecker)(new NormalAPDUChecker(simpleRule)));
    }

    public void closeChannel(byte channelID, APDUChecker c) {
        this.apdu.setName("Close Channel");
        this.apdu.setCAPDU("0070800000");
        this.apdu.setP2(channelID);
        this.apdu.setRAPDUChecker(c);
        this.transmit();
    }

    public SelectFileResponse status() {
        Object c;
        if (this.bGSMMode) {
            c = new FCIRuleChecker();
        } else {
            c = new FCPRuleChecker();
        }

        return this.status(STATUS_P2_RETURN_FCP, 0, (APDUChecker)c);
    }

    public SelectFileResponse status(int p2, int le) {
        Object c;
        if (this.bGSMMode) {
            c = new FCIRuleChecker();
        } else {
            c = new FCPRuleChecker();
        }

        return this.status(p2, le, (APDUChecker)c);
    }

    public SelectFileResponse status(int p2, int le, String simpleRule) {
        return this.status(p2, le, (APDUChecker)(new NormalAPDUChecker(simpleRule)));
    }

    public SelectFileResponse status(int p2, int le, APDUChecker c) {
        this.apdu.setName("Status");
        this.apdu.setCAPDU("80F2000000");
        this.apdu.setP2(p2);
        this.apdu.setP3(le);
        this.apdu.setRAPDUChecker(c);
        this.transmit();
        byte[] res = this.apdu.getRData();
        if (res != null) {
            return (SelectFileResponse)(this.bGSMMode ? new FCI(res) : new FCP(res));
        } else {
            return null;
        }
    }

    public byte[] readBinary(int offset, int length) {
        return this.readBinary(offset, length, "9000|91XX");
    }

    public byte[] readBinary(int offset, int length, String simpleRule) {
        return this.readBinary(offset, length, (APDUChecker)(new NormalAPDUChecker(simpleRule)));
    }

    public byte[] readBinary(int offset, int length, APDUChecker c) {
        this.apdu.setName("Read Binary");
        this.apdu.setCAPDU("00B0000000");
        this.apdu.setP1((byte)(offset >> 8));
        this.apdu.setP2((byte)offset);
        this.apdu.setP3((byte)length);
        this.apdu.setRAPDUChecker(c);
        this.transmit();
        return this.apdu.getRData();
    }

    public byte[] readBinaryBySFI(int sfi, int offset, int length) {
        return this.readBinaryBySFI(sfi, offset, length, (APDUChecker)(new NormalAPDUChecker("9000|91XX")));
    }

    public byte[] readBinaryBySFI(int sfi, int offset, int length, String simpleRule) {
        return this.readBinaryBySFI(sfi, offset, length, (APDUChecker)(new NormalAPDUChecker(simpleRule)));
    }

    public byte[] readBinaryBySFI(int sfi, int offset, int length, APDUChecker c) {
        this.apdu.setName("Read Binary");
        this.apdu.setCAPDU("00B0000000");
        this.apdu.setP1((byte)(sfi | 128));
        this.apdu.setP2((byte)offset);
        this.apdu.setP3((byte)length);
        this.apdu.setRAPDUChecker(c);
        this.transmit();
        return this.apdu.getRData();
    }

    public void updateBinary(int offset, byte[] binary) {
        this.updateBinary(offset, binary, "9000|91XX");
    }

    public void updateBinary(int offset, String binary) {
        this.updateBinary(offset, ByteArray.convert(binary), "9000|91XX");
    }

    public void updateBinary(int offset, byte[] binary, String simpleRule) {
        this.updateBinary(offset, (byte[])binary, (APDUChecker)(new NormalAPDUChecker(simpleRule)));
    }

    public void updateBinaryBySFI(int sfi, int offset, byte[] binary) {
        this.updateBinaryBySFI(sfi, offset, binary, (APDUChecker)(new NormalAPDUChecker("9000|91XX")));
    }

    public void updateBinaryBySFI(int sfi, int offset, byte[] binary, String simpleRule) {
        this.updateBinaryBySFI(sfi, offset, binary, (APDUChecker)(new NormalAPDUChecker(simpleRule)));
    }

    public void updateBinaryBySFI(int sfi, int offset, byte[] binary, APDUChecker c) {
        this.apdu.setName("Update Binary");
        this.apdu.setCAPDU("00D60000");
        this.apdu.setP1((byte)(sfi | 128));
        this.apdu.setP2((byte)offset);
        this.apdu.setCData(binary);
        this.apdu.setRAPDUChecker(c);
        this.transmit();
    }

    public void updateBinary(int offset, String binary, String rule) {
        this.updateBinary(offset, ByteArray.convert(binary), rule);
    }

    public void updateBinary(int offset, byte[] binary, APDUChecker c) {
        this.apdu.setName("Update Binary");
        this.apdu.setCAPDU("00D60000");
        this.apdu.setP1((byte)(offset >> 8));
        this.apdu.setP2((byte)offset);
        this.apdu.setCData(binary);
        this.apdu.setRAPDUChecker(c);
        this.transmit();
    }

    public void updateBinaryAndCheck(int offset, byte[] binary) {
        int c = SCDP.beginGroup("Update Binary And Check");
        this.updateBinary(offset, binary);
        String expData = ByteArray.convert(binary);
        String rule = expData + "9000|" + expData + "91XX";
        NormalAPDUChecker ck = new NormalAPDUChecker(rule);
        this.readBinary(offset, binary.length, (APDUChecker)ck);
        SCDP.endGroup(c);
    }

    public void checkBinary(int offset, byte[] binary) {
        String expData = ByteArray.convert(binary);
        String rule = expData + "9000|" + expData + "91XX";
        NormalAPDUChecker c = new NormalAPDUChecker(rule);
        this.readBinary(offset, binary.length, (APDUChecker)c);
    }

    public void checkBinary(int offset, String binaryExpect) {
        this.checkBinary(offset, ByteArray.convert(binaryExpect));
    }

    public byte[] readRecord(int recNo) {
        return this.readRecord(recNo, RECORD_MODE_ABS_CUR, 0, (String)this.DefaultCheckRule);
    }

    public byte[] readRecord(int recNo, String simpleRule) {
        return this.readRecord(recNo, RECORD_MODE_ABS_CUR, 0, (String)simpleRule);
    }

    public byte[] readRecord(int recNo, int recLen) {
        return this.readRecord(recNo, RECORD_MODE_ABS_CUR, recLen, this.DefaultCheckRule);
    }

    public byte[] readRecord(int recNo, int recLen, String simpleRule) {
        return this.readRecord(recNo, RECORD_MODE_ABS_CUR, recLen, simpleRule);
    }

    public byte[] readRecord(int recNo, int mode, int recLen) {
        return this.readRecord(recNo, mode, recLen, this.DefaultCheckRule);
    }

    public byte[] readRecord(int recNo, int mode, int recLen, String c) {
        return this.readRecord(recNo, mode, recLen, (APDUChecker)(new NormalAPDUChecker(c)));
    }

    public byte[] readRecord(int recNo, int mode, int recLen, APDUChecker c) {
        this.apdu.setName("Read Record");
        this.apdu.setCAPDU("00B2000000");
        this.apdu.setP1(recNo);
        this.apdu.setP2(mode);
        this.apdu.setP3(recLen);
        this.apdu.setRAPDUChecker(c);
        this.transmit();
        return this.apdu.getRData();
    }

    public byte[] readRecordBySFI(int SFI, int recNo, int mode, int recLen) {
        return this.readRecordBySFI(SFI, recNo, mode, recLen, (APDUChecker)(new NormalAPDUChecker("9000|91XX")));
    }

    public byte[] readRecordBySFI(int SFI, int recNo, int mode, int recLen, String simpleRule) {
        return this.readRecordBySFI(SFI, recNo, mode, recLen, (APDUChecker)(new NormalAPDUChecker(simpleRule)));
    }

    public byte[] readRecordBySFI(int SFI, int recNo, int mode, int recLen, APDUChecker c) {
        this.apdu.setName("Read Record");
        this.apdu.setCAPDU("00B2000000");
        this.apdu.setP1(recNo);
        this.apdu.setP2(mode | SFI << 3);
        this.apdu.setP3(recLen);
        this.apdu.setRAPDUChecker(c);
        this.transmit();
        return this.apdu.getRData();
    }

    public void updateRecord(int recNo, byte[] data) {
        this.updateRecord(recNo, RECORD_MODE_ABS_CUR, data, this.DefaultCheckRule);
    }

    public void updateRecord(int recNo, String data) {
        this.updateRecord(recNo, RECORD_MODE_ABS_CUR, ByteArray.convert(data), this.DefaultCheckRule);
    }

    public void updateRecord(int recNo, int mode, byte[] data) {
        this.updateRecord(recNo, mode, data, this.DefaultCheckRule);
    }

    public void updateRecord(int recNo, int mode, String data) {
        this.updateRecord(recNo, mode, ByteArray.convert(data), this.DefaultCheckRule);
    }

    public void updateRecord(int recNo, byte[] data, String simpleRule) {
        this.updateRecord(recNo, RECORD_MODE_ABS_CUR, (byte[])data, (APDUChecker)(new NormalAPDUChecker(simpleRule)));
    }

    public void updateRecord(int recNo, String data, String simpleRule) {
        this.updateRecord(recNo, RECORD_MODE_ABS_CUR, (byte[])ByteArray.convert(data), (APDUChecker)(new NormalAPDUChecker(simpleRule)));
    }

    public void updateRecord(int recNo, int mode, byte[] data, String simpleRule) {
        this.updateRecord(recNo, mode, (byte[])data, (APDUChecker)(new NormalAPDUChecker(simpleRule)));
    }

    public void updateRecord(int recNo, int mode, String data, String simpleRule) {
        this.updateRecord(recNo, mode, (byte[])ByteArray.convert(data), (APDUChecker)(new NormalAPDUChecker(simpleRule)));
    }

    public void updateRecord(int recNo, int mode, byte[] data, APDUChecker c) {
        this.apdu.setName("Update Record");
        this.apdu.setCAPDU("00DC0000");
        this.apdu.setP1(recNo);
        this.apdu.setP2(mode);
        this.apdu.setCData(data);
        this.apdu.setRAPDUChecker(c);
        this.transmit();
    }

    public void updateRecordBySFI(int SFI, int recNo, int mode, byte[] data) {
        this.updateRecordBySFI(SFI, recNo, mode, data, (APDUChecker)(new NormalAPDUChecker("9000|91XX")));
    }

    public void updateRecordBySFI(int SFI, int recNo, int mode, byte[] data, String simpleRule) {
        this.updateRecordBySFI(SFI, recNo, mode, data, (APDUChecker)(new NormalAPDUChecker(simpleRule)));
    }

    public void updateRecordBySFI(int SFI, int recNo, int mode, byte[] data, APDUChecker c) {
        this.apdu.setName("Update Record");
        this.apdu.setCAPDU("00DC0000");
        this.apdu.setP1(recNo);
        this.apdu.setP2(mode | SFI << 3);
        this.apdu.setCData(data);
        this.apdu.setRAPDUChecker(c);
        this.transmit();
    }

    public void checkRecord(int recNo, int mode, byte[] data) {
        NormalAPDUChecker ck = new NormalAPDUChecker(data, 36864);
        this.readRecord(recNo, mode, data.length, (APDUChecker)ck);
    }

    public void checkRecord(int recNo, int mode, String data) {
        this.checkRecord(recNo, mode, ByteArray.convert(data));
    }

    public void increase(int p1, byte[] increaseValue) {
        this.increase(p1, increaseValue, this.DefaultCheckRule);
    }

    public void increase(int p1, String increaseValue) {
        this.increase(p1, ByteArray.convert(increaseValue), this.DefaultCheckRule);
    }

    public void increase(int p1, byte[] increaseValue, String simpleRule) {
        this.increase(p1, (byte[])increaseValue, (APDUChecker)(new NormalAPDUChecker(simpleRule)));
    }

    public void increase(int p1, String increasevalue, String rule) {
        this.increase(p1, (byte[])ByteArray.convert(increasevalue), (APDUChecker)(new NormalAPDUChecker(rule)));
    }

    public void increase(int p1, byte[] increaseValue, APDUChecker checker) {
        this.apdu.setCAPDU("Increase", "80320000");
        this.apdu.setP1(p1);
        this.apdu.setCData(increaseValue);
        this.apdu.setRAPDUChecker(checker);
        this.transmit();
    }

    public void verifyPIN(int pinRef, byte[] pin) {
        this.verifyPIN(pinRef, pin, this.DefaultCheckRule);
    }

    public void verifyPIN(int pinRef, String pin) {
        this.verifyPIN(pinRef, ByteArray.convert(pin), this.DefaultCheckRule);
    }

    public void verifyPIN(int pinRef, byte[] pin, String simpleRule) {
        this.verifyPIN(pinRef, pin, (APDUChecker)(new NormalAPDUChecker(simpleRule)));
    }

    public void verifyPIN(int pinRef, byte[] pin, APDUChecker checker) {
        this.apdu.setName("Verify PIN");
        this.apdu.setCAPDU("00200000");
        this.apdu.setP2(pinRef);
        this.apdu.setCData(pin);
        this.apdu.setRAPDUChecker(checker);
        this.transmit();
    }

    public void changePIN(int pinRef, byte[] oldPIN, byte[] newPIN) {
        this.changePIN(pinRef, oldPIN, newPIN, this.DefaultCheckRule);
    }

    public void changePIN(int pinRef, byte[] oldPIN, byte[] newPIN, String simpleRule) {
        this.changePIN(pinRef, oldPIN, newPIN, (APDUChecker)(new NormalAPDUChecker(simpleRule)));
    }

    public void changePIN(int pinRef, byte[] oldPIN, byte[] newPIN, APDUChecker c) {
        this.apdu.setName("Change PIN");
        this.apdu.setCAPDU("00240000");
        this.apdu.setP2(pinRef);
        this.apdu.setCData(oldPIN);
        this.apdu.appendCData(newPIN);
        this.apdu.setRAPDUChecker(c);
        this.transmit();
    }

    public void disablePIN(int pinRef, byte[] PIN) {
        this.disablePIN(pinRef, PIN, this.DefaultCheckRule);
    }

    public void disablePIN(int pinRef, byte[] PIN, String simpleRule) {
        this.disablePIN(pinRef, PIN, (APDUChecker)(new NormalAPDUChecker(simpleRule)));
    }

    public void disablePIN(int pinRef, byte[] PIN, APDUChecker c) {
        this.apdu.setName("Disable PIN");
        this.apdu.setCAPDU("00260000");
        this.apdu.setP2(pinRef);
        this.apdu.setCData(PIN);
        this.apdu.setRAPDUChecker(c);
        this.transmit();
    }

    public void enablePIN(byte pinRef, byte[] PIN) {
        this.enablePIN(pinRef, PIN, this.DefaultCheckRule);
    }

    public void enablePIN(byte pinRef, byte[] PIN, String simpleRule) {
        this.enablePIN(pinRef, PIN, (APDUChecker)(new NormalAPDUChecker(simpleRule)));
    }

    public void enablePIN(byte pinRef, byte[] PIN, APDUChecker c) {
        this.apdu.setName("Enable PIN");
        this.apdu.setCAPDU("00280000");
        this.apdu.setP2(pinRef);
        this.apdu.setCData(PIN);
        this.apdu.setRAPDUChecker(c);
        this.transmit();
    }

    public void unblockPIN(int pinRef, byte[] PUK, byte[] newPIN) {
        this.unblockPIN(pinRef, PUK, newPIN, this.DefaultCheckRule);
    }

    public void unblockPIN(int pinRef, byte[] PUK, byte[] newPIN, String simpleRule) {
        this.unblockPIN(pinRef, PUK, newPIN, (APDUChecker)(new NormalAPDUChecker(simpleRule)));
    }

    public void unblockPIN(int pinRef, byte[] PUK, byte[] newPIN, APDUChecker c) {
        this.apdu.setName("Unblock PIN");
        this.apdu.setCAPDU("002C0000");
        this.apdu.setP2(pinRef);
        this.apdu.setCData(PUK);
        this.apdu.appendCData(newPIN);
        this.apdu.setRAPDUChecker(c);
        this.transmit();
    }

    public int PINRefConvert(int intPIN) {
        if (this.bGSMMode) {
            return intPIN;
        } else {
            switch(intPIN) {
                case 1:
                    return 1;
                case 2:
                    return 129;
                case 3:
                default:
                    return 0;
                case 4:
                    return 10;
            }
        }
    }

    public byte[] authenticate(byte[] authData) {
        return this.authenticate((byte[])authData, (APDUChecker)(new NormalAPDUChecker("9000|91XX")));
    }

    public byte[] authenticate(byte[] authData, APDUChecker c) {
        return this.bGSMMode ? this.authenticate(0, authData, (APDUChecker)c) : this.authenticate(129, authData, (APDUChecker)c);
    }

    public byte[] authenticate(String authData) {
        return this.authenticate(ByteArray.convert(authData));
    }

    public byte[] authenticate(String authData, String simpleRule) {
        return this.authenticate((byte[])ByteArray.convert(authData), (APDUChecker)(new NormalAPDUChecker(simpleRule)));
    }

    public byte[] authenticate(int p2, byte[] authData, String simpleRule) {
        return this.authenticate(p2, authData, (APDUChecker)(new NormalAPDUChecker(simpleRule)));
    }

    public byte[] authenticate(int p2, byte[] authData, APDUChecker c) {
        this.apdu.setName("Authentication");
        this.apdu.setCAPDU("00880000");
        this.apdu.setP2(p2);
        this.apdu.setCData(authData);
        if (!this.bGSMMode) {
            this.apdu.setRAPDUChecker(c);
        }

        this.transmit();
        if (this.bGSMMode && this.apdu.getSW1() == -97) {
            this.apdu.setName("Get Response");
            this.apdu.setCAPDU("A0C00000");
            this.apdu.setP3(this.apdu.getSW2());
            this.apdu.setRAPDUChecker(c);
            this.transmit();
        }

        return this.apdu.getRData();
    }

    public void createFile(byte[] fcp) {
        this.createFile(fcp, this.DefaultCheckRule);
    }

    public void createFile(byte[] fcp, String simpleRule) {
        this.createFile(fcp, (APDUChecker)(new NormalAPDUChecker(simpleRule)));
    }

    public void createFile(byte[] fcp, APDUChecker c) {
        this.apdu.setName("Create File");
        this.apdu.setCAPDU("00E0000000");
        this.apdu.setCData(fcp);
        this.apdu.setRAPDUChecker(c);
        this.transmit();
    }

    public void deleteFile(int fid) {
        this.deleteFile(fid, this.DefaultCheckRule);
    }

    public void deleteFile(int fid, String simpleRule) {
        this.deleteFile(fid, (APDUChecker)(new NormalAPDUChecker(simpleRule)));
    }

    public void deleteFile(int fid, APDUChecker c) {
        this.apdu.setName("Delete File");
        this.apdu.setCAPDU("00E4000000");
        byte[] fidB = new byte[]{(byte)(fid >> 8), (byte)fid};
        this.apdu.setCData(fidB);
        this.apdu.setRAPDUChecker(c);
        this.transmit();
    }

    public void resizeFile(byte[] FCP) {
        this.resizeFile(FCP, this.DefaultCheckRule);
    }

    public void resizeFile(byte[] FCP, String simpleRule) {
        this.resizeFile(FCP, (APDUChecker)(new NormalAPDUChecker(simpleRule)));
    }

    public void resizeFile(byte[] FCP, APDUChecker c) {
        this.apdu.setName("Resize File");
        this.apdu.setCAPDU("80D4000000");
        this.apdu.setCData(FCP);
        this.apdu.setRAPDUChecker(c);
        this.transmit();
    }

    public void terminalProfile(String profile) {
        this.terminalProfile(ByteArray.convert(profile), "9000|91XX");
    }

    public void terminalProfile(String profile, String rule) {
        this.terminalProfile((byte[])ByteArray.convert(profile), (APDUChecker)(new NormalAPDUChecker(rule)));
    }

    public void terminalProfile(byte[] TProfile) {
        this.terminalProfile(TProfile, "9000|91XX");
    }

    public void terminalProfile(byte[] TProfile, String simpleRule) {
        this.terminalProfile((byte[])TProfile, (APDUChecker)(new NormalAPDUChecker(simpleRule)));
    }

    public void terminalProfile(byte[] TProfile, APDUChecker c) {
        this.apdu.setName("Terminal Profile");
        this.apdu.setCAPDU("8010000000");
        this.apdu.setCData(TProfile);
        this.apdu.setRAPDUChecker(c);
        this.transmit();
    }

    public void setDefaultProfile(String profile) {
        this.defult_Profile = profile;
    }

    public void terminalProfileSession() {
        this.terminalProfileSession(this.defult_Profile);
    }

    public void terminalProfileSession(byte[] TProfile) {
        int cd = SCDP.beginGroup("UICC Terminal Profile Session");
        this.terminalProfile(TProfile);
        boolean bResOK = false;

        do {
            if (this.apdu.getSW1() == -111) {
                this.fetch(this.apdu.getSW2() & 255);
                if (this.apdu.getSW() == 36864) {
                    this.terminalResponse();
                }
            } else {
                bResOK = true;
            }
        } while(!bResOK);

        SCDP.endGroup(cd);
    }

    public void terminalProfileSession(String profile) {
        this.terminalProfileSession(ByteArray.convert(profile));
    }

    public boolean simuPowerOn(String profile) {
        int c = SCDP.beginGroup("UICC Power On");
        this.reset();
        this.selectFile(16128);
        this.selectFile(12258);
        this.selectFile(12032, (String)"9000|6283|9404");
        if (this.apdu.getSW() != 36864) {
            this.bGSMMode = true;
        } else {
            this.bGSMMode = false;
        }

        if (!this.bGSMMode) {
            this.getAllADFAid();
        }

        this.terminalProfileSession(profile);
        SelectFileResponse rsp;
        if (this.bGSMMode) {
            this.selectFile(32544);
            rsp = this.selectFile(28542);
        } else {
            this.selectADF(this.getUSIMADFAid());
            rsp = this.selectFile(28542);
        }

        int fileSize = rsp.getFileSize();
        byte[] data = Util.getRandom(fileSize);
        data[fileSize - 1] = 0;
        this.updateBinary(0, (byte[])data);

        while(this.apdu.getSW1() == -111) {
            this.fetch();
            this.terminalResponse();
        }

        rsp = this.status();

        while(this.apdu.getSW1() == -111) {
            this.fetch();
            this.terminalResponse();
        }

        for(int i = 0; i < 10; ++i) {
            this.status(0, rsp.getResponseLength());

            while(this.apdu.getSW1() == -111) {
                this.fetch();
                this.terminalResponse();
            }
        }

        SCDP.endGroup(c);
        return true;
    }

    public ProactiveCommand fetch() {
        return this.fetch(this.apdu.getSW2() & 255);
    }

    public ProactiveCommand fetch(String simpleRule) {
        return this.fetch((APDUChecker)(new NormalAPDUChecker(simpleRule)));
    }

    public ProactiveCommand fetch(APDUChecker checker) {
        return this.fetch(this.apdu.getSW2() & 255, checker);
    }

    public ProactiveCommand fetch(int fetchLen) {
        return this.fetch(fetchLen, (APDUChecker)null);
    }

    public ProactiveCommand fetch(int fetchLen, String simpleRule) {
        return this.fetch(fetchLen, (APDUChecker)(new NormalAPDUChecker(simpleRule)));
    }

    public ProactiveCommand fetch(int fetchLen, APDUChecker checker) {
        this.apdu.setCAPDU("Fetch", "8012000000");
        this.apdu.setP3(fetchLen);
        this.apdu.setRAPDUChecker(checker);
        this.transmit();
        byte[] rdata = this.apdu.getRData();
        if (rdata != null) {
            this.lastProactiveCmd = ProactiveCommand.buildCommand(rdata);
            if (this.lastProactiveCmd instanceof SetupMenu) {
                this.setupMenu = (SetupMenu)this.lastProactiveCmd;
            } else if (this.lastProactiveCmd instanceof SetupEventList) {
                this.setupEventList = (SetupEventList)this.lastProactiveCmd;
            }

            if (this.lastProactiveCmd != null) {
                SCDP.addAPDUInfo(this.lastProactiveCmd.toString());
            }

            return this.lastProactiveCmd;
        } else {
            this.lastProactiveCmd = null;
            return null;
        }
    }

    public void terminalResponse() {
        this.terminalResponse(this.lastProactiveCmd.getCmdType(), this.lastProactiveCmd.getCmdQualifier(), this.lastProactiveCmd.getCmdNumber(), this.lastProactiveCmd.getSrcDevID(), this.lastProactiveCmd.getDstDevID(), 0, (SimpleTLV[])null, new NormalAPDUChecker("9000|91XX"));
    }

    public void terminalResponse(String simpleRule) {
        this.terminalResponse(this.lastProactiveCmd.getCmdType(), this.lastProactiveCmd.getCmdQualifier(), this.lastProactiveCmd.getCmdNumber(), this.lastProactiveCmd.getSrcDevID(), this.lastProactiveCmd.getDstDevID(), 0, (SimpleTLV[])null, new NormalAPDUChecker(simpleRule));
    }

    public void terminalResponse(APDUChecker c) {
        this.terminalResponse(this.lastProactiveCmd.getCmdType(), this.lastProactiveCmd.getCmdQualifier(), this.lastProactiveCmd.getCmdNumber(), this.lastProactiveCmd.getSrcDevID(), this.lastProactiveCmd.getDstDevID(), 0, (SimpleTLV[])null, c);
    }

    public void terminalResponse(int cmdResult) {
        this.terminalResponse(this.lastProactiveCmd.getCmdType(), this.lastProactiveCmd.getCmdQualifier(), this.lastProactiveCmd.getCmdNumber(), this.lastProactiveCmd.getSrcDevID(), this.lastProactiveCmd.getDstDevID(), cmdResult, (SimpleTLV[])null, new NormalAPDUChecker("9000|91XX"));
    }

    public void terminalResponse(int cmdResult, String simpleRule) {
        this.terminalResponse(this.lastProactiveCmd.getCmdType(), this.lastProactiveCmd.getCmdQualifier(), this.lastProactiveCmd.getCmdNumber(), this.lastProactiveCmd.getSrcDevID(), this.lastProactiveCmd.getDstDevID(), cmdResult, (SimpleTLV[])null, new NormalAPDUChecker(simpleRule));
    }

    public void terminalResponse(int cmdType, int cmdQulifier, int cmdNumber, int srcDevID, int dstDevID, int cmdResult) {
        this.terminalResponse(cmdType, cmdQulifier, cmdNumber, srcDevID, dstDevID, 0, (SimpleTLV[])null, new NormalAPDUChecker("9000|91XX"));
    }

    public void terminalResponse(int cmdType, int cmdQulifier, int cmdNumber, int srcDevID, int dstDevID, int cmdResult, String simpleRule) {
        this.terminalResponse(cmdType, cmdQulifier, cmdNumber, cmdResult, srcDevID, dstDevID, (SimpleTLV[])null, new NormalAPDUChecker(simpleRule));
    }

    public void terminalResponse(int cmdType, int cmdQulifier, int cmdNumber, int srcDevID, int dstDevID, int cmdResult, APDUChecker checker) {
        this.terminalResponse(cmdType, cmdQulifier, cmdNumber, cmdResult, srcDevID, dstDevID, (SimpleTLV[])null, checker);
    }

    public void terminalResponse(SimpleTLV tlv) {
        SimpleTLV[] tlvs = null;
        if (tlv != null) {
            tlvs = new SimpleTLV[]{tlv};
        }

        this.terminalResponse(this.lastProactiveCmd.getCmdType(), this.lastProactiveCmd.getCmdQualifier(), this.lastProactiveCmd.getCmdNumber(), this.lastProactiveCmd.getSrcDevID(), this.lastProactiveCmd.getDstDevID(), 0, tlvs, new NormalAPDUChecker("9000|91XX"));
    }

    public void terminalResponse(SimpleTLV tlv, String simpleRule) {
        SimpleTLV[] tlvs = null;
        if (tlv != null) {
            tlvs = new SimpleTLV[]{tlv};
        }

        this.terminalResponse(this.lastProactiveCmd.getCmdType(), this.lastProactiveCmd.getCmdQualifier(), this.lastProactiveCmd.getCmdNumber(), this.lastProactiveCmd.getSrcDevID(), this.lastProactiveCmd.getDstDevID(), 0, tlvs, new NormalAPDUChecker(simpleRule));
    }

    public void terminalResponse(SimpleTLV tlv, APDUChecker checkRule) {
        SimpleTLV[] tlvs = null;
        if (tlv != null) {
            tlvs = new SimpleTLV[]{tlv};
        }

        this.terminalResponse(this.lastProactiveCmd.getCmdType(), this.lastProactiveCmd.getCmdQualifier(), this.lastProactiveCmd.getCmdNumber(), this.lastProactiveCmd.getSrcDevID(), this.lastProactiveCmd.getDstDevID(), 0, tlvs, checkRule);
    }

    public void terminalResponse(SimpleTLV[] tlvs, String simpleRule) {
        this.terminalResponse(this.lastProactiveCmd.getCmdType(), this.lastProactiveCmd.getCmdQualifier(), this.lastProactiveCmd.getCmdNumber(), this.lastProactiveCmd.getSrcDevID(), this.lastProactiveCmd.getDstDevID(), 0, tlvs, new NormalAPDUChecker(simpleRule));
    }

    public void terminalResponse(SimpleTLV[] tlvs, APDUChecker checkRule) {
        this.terminalResponse(this.lastProactiveCmd.getCmdType(), this.lastProactiveCmd.getCmdQualifier(), this.lastProactiveCmd.getCmdNumber(), this.lastProactiveCmd.getSrcDevID(), this.lastProactiveCmd.getDstDevID(), 0, tlvs, checkRule);
    }

    public void terminalResponseWithItemID(byte itemID) {
        this.terminalResponse((SimpleTLV)(new ItemIdentifier(itemID)), (String)"9000|91XX");
    }

    public void terminalResponseWithItemID(byte itemID, String simpleRule) {
        this.terminalResponse((SimpleTLV)(new ItemIdentifier(itemID)), (String)simpleRule);
    }

    public void terminalResponseWithText(String text) {
        this.terminalResponse((SimpleTLV)(new TextString(text)), (String)"9000|91XX");
    }

    public void terminalResponseWithText(byte[] text, byte dcs) {
        this.terminalResponse((SimpleTLV)(new TextString(text, dcs)), (String)"9000|91XX");
    }

    public void terminalResponseWithText(String text, String simpleRule) {
        this.terminalResponse((SimpleTLV)(new TextString(text)), (String)simpleRule);
    }

    public void terminalResponseWithText(byte[] text, byte dcs, String simpleRule) {
        this.terminalResponse((SimpleTLV)(new TextString(text, dcs)), (String)simpleRule);
    }

    public void terminalResponse(int cmdType, int cmdQualifier, int cmdNumber, int srcDeviceID, int dstDeviceID, int result, SimpleTLV[] tlv, APDUChecker checker) {
        TLVList list = new TLVList();
        CommandDetails cdt = new CommandDetails(cmdType, cmdQualifier, cmdNumber);
        list.add(cdt);
        DeviceIdentities di = new DeviceIdentities((byte)srcDeviceID, (byte)dstDeviceID);
        list.add(di);
        Result res = new Result(result);
        list.add(res);
        if (tlv != null) {
            int c = tlv.length;

            for(int i = 0; i < c; ++i) {
                list.add(tlv[i]);
            }
        }

        this.apdu.setName("Terminal Response");
        this.apdu.setCAPDU("80140000");
        this.apdu.setCData(list.toBytes());
        this.apdu.setRAPDUChecker(checker);
        this.transmit();
    }

    public ResponsePackage envelopeAndFetchPoR(Vector<TPDU> tpdus, SendSMSChecker cmdChecker) {
        this.envelope(tpdus, "91XX");
        if (cmdChecker == null) {
            cmdChecker = new SendSMSChecker();
        }

        boolean bPoRFinished = false;
        ResponsePackage rspPkg = new ResponsePackage();

        do {
            ProactiveCommand cmd = this.fetch((APDUChecker)cmdChecker);
            this.terminalResponse();
            if (cmd instanceof SendSMS) {
                SendSMS sendSMS = (SendSMS)cmd;
                TPDU por = sendSMS.getTPDU();
                byte[] userData = por.getUserDataContent();
                bPoRFinished = rspPkg.procUserData(por.getConcatenateCount(), por.getConcatenateIndex(), userData);
            }
        } while(this.apdu.getSW1() == -111 && !bPoRFinished);

        return rspPkg;
    }

    public ResponsePackage envelopeAndFetchPoR(TPDU tpdu) {
        this.envelope(tpdu, "91XX");
        ResponsePackage rspPkg = new ResponsePackage();
        boolean bPoRFinished = false;

        do {
            ProactiveCommand proCmd = this.fetch();
            this.terminalResponse();
            if (proCmd instanceof SendSMS) {
                SendSMS sendSMS = (SendSMS)proCmd;
                TPDU por = sendSMS.getTPDU();
                byte[] userData = por.getUserDataContent();
                bPoRFinished = rspPkg.procUserData(por.getConcatenateCount(), por.getConcatenateIndex(), userData);
            }
        } while(this.apdu.getSW1() == -111 && !bPoRFinished);

        return rspPkg;
    }

    public ResponsePackage envelopeAndFetchPoR(TPDU tpdu, SendSMSChecker cmdChecker) {
        this.envelope(tpdu, "91XX");
        if (cmdChecker == null) {
            cmdChecker = new SendSMSChecker();
        }

        ResponsePackage rspPkg = new ResponsePackage();
        boolean bPoRFinished = false;

        do {
            this.fetch((APDUChecker)cmdChecker);
            this.terminalResponse();
            TPDU por = cmdChecker.getReturnTPDU();
            byte[] userData = por.getUserDataContent();
            bPoRFinished = rspPkg.procUserData(por.getConcatenateCount(), por.getConcatenateIndex(), userData);
        } while(this.apdu.getSW1() == -111 && !bPoRFinished);

        return rspPkg;
    }

    public ResponsePackage envelopeAndGetPoR(Vector<TPDU> tpdus, boolean bExpOK) {
        if (this.bGSMMode) {
            this.envelope(tpdus, "9FXX|9EXX");
        } else {
            this.envelope(tpdus, "9000|6200|61XX");
        }

        ResponsePackage rspPkg = new ResponsePackage();
        byte[] por = this.apdu.getRData();
        byte[] pkg = new byte[por.length - 3];
        System.arraycopy(por, 3, pkg, 0, por.length - 3);
        rspPkg.procUserData(pkg);
        if (rspPkg.getResponseStatusCode() == 11 && this.apdu.getSW1() == -111) {
            rspPkg = new ResponsePackage();
            boolean bPoRFinished = false;

            do {
                ProactiveCommand cmd = this.fetch();
                this.terminalResponse();
                if (cmd instanceof SendSMS) {
                    SendSMS sendSMS = (SendSMS)cmd;
                    TPDU Tpor = sendSMS.getTPDU();
                    byte[] userData = Tpor.getUserDataContent();
                    bPoRFinished = rspPkg.procUserData(Tpor.getConcatenateCount(), Tpor.getConcatenateIndex(), userData);
                }
            } while(this.apdu.getSW1() == -111 && !bPoRFinished);
        }

        return rspPkg;
    }

    public ResponsePackage envelopeAndGetPoR(TPDU tpdu, boolean bExpOK) {
        if (this.bGSMMode) {
            this.envelope(tpdu, bExpOK ? "9FXX" : "9EXX");
            this.apdu.setCAPDU("Get Response", "00C0000000");
            this.apdu.setP3(this.apdu.getSW2());
            this.transmit();
        } else {
            this.envelope(tpdu, bExpOK ? "9000|91XX" : "6200");
            if (!bExpOK && this.apdu.getSW() == 25088) {
                byte[] res = this.apdu.getRData();
                if (res == null) {
                    this.apdu.setCAPDU("Get Response", "00C0000000");
                    this.transmit();
                }
            }
        }

        ResponsePackage rspPkg = new ResponsePackage();
        byte[] por = this.apdu.getRData();
        byte[] pkg = new byte[por.length - 3];
        System.arraycopy(por, 3, pkg, 0, por.length - 3);
        rspPkg.procUserData(pkg);
        if (rspPkg.getResponseStatusCode() == 11 && this.apdu.getSW1() == -111) {
            rspPkg = new ResponsePackage();
            boolean bPoRFinished = false;

            do {
                ProactiveCommand cmd = this.fetch();
                this.terminalResponse();
                if (cmd instanceof SendSMS) {
                    SendSMS sendSMS = (SendSMS)cmd;
                    TPDU Tpor = sendSMS.getTPDU();
                    byte[] userData = Tpor.getUserDataContent();
                    bPoRFinished = rspPkg.procUserData(Tpor.getConcatenateCount(), Tpor.getConcatenateIndex(), userData);
                }
            } while(this.apdu.getSW1() == -111 && !bPoRFinished);
        }

        return rspPkg;
    }

    public void envelope(Vector<TPDU> tpdus) {
        this.envelope(tpdus, "9000|91XX");
    }

    public void envelope(Vector<TPDU> tpdus, String simpleRule) {
        this.envelope((Vector)tpdus, (APDUChecker)(new NormalAPDUChecker(simpleRule)));
    }

    public void envelope(Vector<TPDU> tpdus, APDUChecker checker) {
        int c = tpdus.size() - 1;

        for(int i = 0; i < c; ++i) {
            this.envelope((TPDU)tpdus.get(i), "9000");
        }

        this.envelope((TPDU)tpdus.get(c), checker);
    }

    public void envelope(TPDU tpdu) {
        this.envelope(tpdu, "9000|91XX");
    }

    public void envelope(TPDU tpdu, String simpleRule) {
        this.envelope((TPDU)tpdu, (APDUChecker)(new NormalAPDUChecker(simpleRule)));
    }

    public void envelope(TPDU tpdu, APDUChecker checker) {
        this.apdu.setName("Envelope SMS download");
        this.apdu.setCAPDU("80C20000");
        TLV d1 = new BERTLV(-47);
        TLV devID = new DeviceIdentities((byte)-125, (byte)-127);
        d1.appendValue(devID.toBytes());
        d1.appendValue(tpdu.toBytes());
        this.apdu.setCData(d1.toBytes());
        this.apdu.setRAPDUChecker(checker);
        this.transmit();
    }

    public ResponsePackage envelopeAndGetPoR(TPDU_CDMA tpdu, boolean bExpOK) {
        if (this.bGSMMode) {
            this.envelope(tpdu, bExpOK ? "9FXX" : "9EXX");
            this.apdu.setCAPDU("Get Response", "00C0000000");
            this.apdu.setP3(this.apdu.getSW2());
            this.transmit();
        } else {
            this.envelope(tpdu, bExpOK ? "9000|91XXS" : "6200");
            if (!bExpOK && this.apdu.getSW() == 25088) {
                byte[] res = this.apdu.getRData();
                if (res == null) {
                    this.apdu.setCAPDU("Get Response", "00C0000000");
                    this.transmit();
                }
            }
        }

        ResponsePackage rspPkg = new ResponsePackage();
        byte[] por = this.apdu.getRData();
        byte[] pkg = new byte[por.length - 3];
        System.arraycopy(por, 3, pkg, 0, por.length - 3);
        rspPkg.procUserData(pkg);
        if (rspPkg.getResponseStatusCode() == 11 && this.apdu.getSW1() == -111) {
            rspPkg = new ResponsePackage();
            boolean bPoRFinished = false;

            do {
                ProactiveCommand cmd = this.fetch();
                this.terminalResponse();
                if (cmd instanceof SendSMS) {
                    SendSMS sendSMS = (SendSMS)cmd;
                    TPDU Tpor = sendSMS.getTPDU();
                    byte[] userData = Tpor.getUserDataContent();
                    bPoRFinished = rspPkg.procUserData(Tpor.getConcatenateCount(), Tpor.getConcatenateIndex(), userData);
                }
            } while(this.apdu.getSW1() == -111 && !bPoRFinished);
        }

        return rspPkg;
    }

    public ResponsePackage envelopeAndFetchPoR(TPDU_CDMA tpdu, SendSMSChecker cmdChecker) {
        this.envelope(tpdu, "91XX");
        if (cmdChecker == null) {
            cmdChecker = new SendSMSChecker();
        }

        ResponsePackage rspPkg = new ResponsePackage();

        do {
            this.fetch((APDUChecker)cmdChecker);
            this.terminalResponse();
            TPDU_CDMA por = cmdChecker.getReturnTPDU_CDMA();
            byte[] userData = por.getUserData();
            rspPkg.procUserData(por.getConcatenateCount(), por.getConcatenateIndex(), userData);
        } while(this.apdu.getSW1() == -111);

        return rspPkg;
    }

    public ResponsePackage envelopeAndFetchPoR(Vector<TPDU_CDMA> tpdus) {
        int c = tpdus.size() - 1;

        for(int i = 0; i < c; ++i) {
            this.envelope((TPDU_CDMA)tpdus.get(i), "9000");
        }

        SendSMSChecker cmdChecker = new SendSMSChecker();
        this.envelope((TPDU_CDMA)tpdus.get(c), "91XX");
        ResponsePackage rspPkg = new ResponsePackage();

        do {
            this.fetch((APDUChecker)cmdChecker);
            this.terminalResponse();
            TPDU_CDMA por = cmdChecker.getReturnTPDU_CDMA();
            byte[] userData = por.getUserData();
            rspPkg.procUserData(por.getConcatenateCount(), por.getConcatenateIndex(), userData);
        } while(this.apdu.getSW1() == -111);

        return rspPkg;
    }

    public void envelope(TPDU_CDMA tpdu) {
        this.envelope(tpdu, "9000|91XX");
    }

    public void envelope(TPDU_CDMA tpdu, String simpleRule) {
        this.envelope((TPDU_CDMA)tpdu, (APDUChecker)(new NormalAPDUChecker(simpleRule)));
    }

    public void envelope(TPDU_CDMA tpdu, APDUChecker checker) {
        this.apdu.setName("Envelope SMS download");
        this.apdu.setCAPDU("80C20000");
        TLV d1 = new BERTLV(-47);
        TLV devID = new DeviceIdentities((byte)-125, (byte)-127);
        d1.appendValue(devID.toBytes());
        d1.appendValue(tpdu.toBytes());
        this.apdu.setCData(d1.toBytes());
        this.apdu.setRAPDUChecker(checker);
        this.transmit();
    }

    public void envelope(CellBroadcastPage cbp) {
        this.envelope((CellBroadcastPage)cbp, (APDUChecker)(new NormalAPDUChecker("9000")));
    }

    public void envelope(CellBroadcastPage cbp, String simpleRule) {
        this.envelope((CellBroadcastPage)cbp, (APDUChecker)(new NormalAPDUChecker(simpleRule)));
    }

    public void envelope(CellBroadcastPage cbp, APDUChecker c) {
        this.apdu.setName("Envelope Cell Broadcast download");
        this.apdu.setCAPDU("80C20000");
        TLV d1 = new BERTLV(-46);
        TLV devID = new DeviceIdentities((byte)-125, (byte)-127);
        d1.appendValue(devID.toBytes());
        d1.appendValue(cbp.toBytes());
        this.apdu.setCData(d1.toBytes());
        this.apdu.setRAPDUChecker(c);
        this.transmit();
    }

    public void envelope(String envelopeName, int tag, byte srcDevID, byte dstDevID, TLVList additionTLV) {
        this.envelope(envelopeName, tag, srcDevID, dstDevID, additionTLV, "9000|91XX");
    }

    public void envelope(String envelopeName, int tag, byte srcDevID, byte dstDevID, TLVList additionTLV, String simpleRule) {
        this.envelopse(envelopeName, tag, srcDevID, dstDevID, additionTLV, new NormalAPDUChecker(simpleRule));
    }

    public void envelopse(String envelopeName, int tag, byte srcDevID, byte dstDevID, TLVList additionTLV, APDUChecker checker) {
        TLV tlv = BERTLVBuilder.buildTLV(tag);
        DeviceIdentities did = new DeviceIdentities(srcDevID, dstDevID);
        tlv.appendValue(did.toBytes());
        if (additionTLV != null) {
            tlv.appendValue(additionTLV.toBytes());
        }

        this.apdu.setCAPDU(envelopeName, "80C20000");
        this.apdu.setCData(tlv.toBytes());
        this.apdu.setRAPDUChecker(checker);
        this.transmit();
    }

    public void envelopeMenuSelection(byte menuID, boolean bHelpReq) {
        this.envelopeMenuSelection(menuID, bHelpReq, "9000|91XX");
    }

    public void envelopeMenuSelection(byte menuID, boolean bHelpReq, String simpleRule) {
        TLVList tls = new TLVList();
        ItemIdentifier iid = new ItemIdentifier(menuID);
        tls.add(iid);
        if (bHelpReq) {
            HelpRequest hlpReq = new HelpRequest();
            tls.add(hlpReq);
        }

        this.envelope("Envelope Menu Selection", -45, (byte)1, (byte)-127, tls, simpleRule);
    }

    public void envelopeCallControl(byte[] addr, byte[] locInfo) {
        TLVList tls = new TLVList();
        tls.add(new Address(addr));
        tls.add(new LocationInfo(locInfo));
        this.envelope("Envelope Call Control", -44, (byte)-126, (byte)-127, tls);
    }

    public void envelopeTimerExpiration(int timerID, int timerValue) {
        TLVList tls = new TLVList();
        TimerIdentifier ti = new TimerIdentifier(timerID);
        tls.add(ti);
        TimerValue tv = new TimerValue(timerValue);
        tls.add(tv);
        this.envelope("Envelope Timer Expiration", -41, (byte)-126, (byte)-127, tls);
    }

    public void envelopeUnrecognized(byte[] data) {
        TLV tlv = BERTLVBuilder.buildTLV(-47);
        tlv.appendValue(data);
        this.apdu.setName("Envelope Unrecognized");
        this.apdu.setCAPDU("80C20000");
        this.apdu.setCData(tlv.toBytes());
        this.transmit();
    }

    public void envelopeEventDownloadMTCall(byte[] transID) {
        TLVList tls = new TLVList();
        tls.add(new EventList((byte)0));
        tls.add(new TransactionIdentifier(transID));
        this.envelope("Envelope Event Download(Call Control)", -42, (byte)-126, (byte)-127, tls);
    }

    public void envelopeEventDownloadCallConnected(boolean bMTCall, byte[] transID) {
        TLVList tls = new TLVList();
        tls.add(new EventList((byte)1));
        tls.add(new TransactionIdentifier(transID));
        this.envelope("Envelope Event Download(Call Connected)", -42, (byte)(bMTCall ? -126 : -125), (byte)-127, tls);
    }

    public void envelopeEventDownloadCallDisconnected(boolean bNearEnd, byte[] transID) {
        TLVList tls = new TLVList();
        tls.add(new EventList((byte)2));
        tls.add(new TransactionIdentifier(transID));
        this.envelope("Envelope Event Download(Call Disconnected)", -42, (byte)(bNearEnd ? -126 : -125), (byte)-127, tls);
    }

    public void envelopeEventDownloadLocationStatus(byte status, byte[] locationInfo) {
        TLVList tls = new TLVList();
        tls.add(new EventList((byte)3));
        tls.add(new LocationStatus(status));
        if (locationInfo != null) {
            tls.add(new LocationInfo(locationInfo));
        }

        this.envelope("Envelope Event Download(Location Status)", -42, (byte)-126, (byte)-127, tls);
    }

    public void envelopeEventDownloadLocationStatus(byte status) {
        this.envelopeEventDownloadLocationStatus(status, (byte[])null);
    }

    public void envelopeEventDownloadUserActivity() {
        TLVList tls = new TLVList();
        tls.add(new EventList((byte)4));
        this.envelope("Envelope Event Download(User Activity)", -42, (byte)-126, (byte)-127, tls);
    }

    public void envelopeEventDownloadIdleScreenAvailable() {
        TLVList tls = new TLVList();
        tls.add(new EventList((byte)5));
        this.envelope("Envelope Event Download(Idle Screen Available)", -42, (byte)2, (byte)-127, tls);
    }

    public void envelopeEventDownloadCardReaderStatus() {
        TLVList tls = new TLVList();
        tls.add(new EventList((byte)6));
        this.envelope("Envelope Event Download(Card Reader Status)", -42, (byte)-126, (byte)-127, tls);
    }

    public void envelopeEventDownloadLanguageSelection(String language) {
        TLVList tls = new TLVList();
        tls.add(new EventList((byte)7));
        tls.add(new Language(language));
        this.envelope("Envelope Event Download(Language Selection)", -42, (byte)-126, (byte)-127, tls);
    }

    public void envelopeEventDownloadBrowserTermination(byte cause) {
        TLVList tls = new TLVList();
        tls.add(new EventList((byte)8));
        tls.add(new BrowserTerminationCause(cause));
        this.envelope("Envelope Event Download(Browser Termination)", -42, (byte)-126, (byte)-127, tls);
    }

    public void envelopeEventDownloadDataAvailable(byte channelID, boolean bLinkEstablished, int dataLen) {
        TLVList tls = new TLVList();
        tls.add(new EventList((byte)9));
        tls.add(new ChannelStatus(channelID, bLinkEstablished));
        tls.add(new ChannelDataLength(dataLen));
        this.envelope("Envelope Event Download(Data Available)", -42, (byte)-126, (byte)-127, tls);
    }

    public void envelopeEventDownloadChannelStatus(byte channelID, boolean bLinkEstablished) {
        TLVList tls = new TLVList();
        tls.add(new EventList((byte)10));
        tls.add(new ChannelStatus(channelID, bLinkEstablished));
        this.envelope("Envelope Event Download(Channel Status)", -42, (byte)-126, (byte)-127, tls);
    }

    public void envelopeEventDownloadAccessTechnologyChange(byte event, byte accTech) {
        TLVList tls = new TLVList();
        tls.add(new EventList(event));
        tls.add(new AccessTechnology(accTech));
        this.envelope("Envelope Event Download(Access Technology Change)", -42, (byte)-126, (byte)-127, tls);
    }

    public void envelopeEventDownloadDisplayParametersChanged(byte[] param) {
        TLVList tls = new TLVList();
        tls.add(new EventList((byte)12));
        tls.add(new DisplayParameters(param));
        this.envelope("Envelope Event Download(Display Parameters Changed)", -42, (byte)-126, (byte)-127, tls);
    }

    public void envelopeEventDownloadLocalConnection(byte[] serviceRec) {
        TLVList tls = new TLVList();
        tls.add(new EventList((byte)13));
        tls.add(new ServiceRecord(serviceRec));
        this.envelope("Envelope Event Download(Local Connection)", -42, (byte)-126, (byte)-127, tls);
    }

    public void envelopeEventDownloadNetworkSearchModeChange(byte searchMode) {
        TLVList tls = new TLVList();
        tls.add(new EventList((byte)14));
        tls.add(new NetworkSearchMode(searchMode));
        this.envelope("Envelope Event Download(Network Search Mode Change)", -42, (byte)-126, (byte)-127, tls);
    }

    public void envelopeEventDownloadBrowsingStatus(byte[] browsingStatus) {
        TLVList tls = new TLVList();
        tls.add(new EventList((byte)15));
        tls.add(new BrowsingStatus(browsingStatus));
        this.envelope("Envelope Event Download(Browsing Status)", -42, (byte)-126, (byte)-127, tls);
    }
}
