package com.trustconnector.scdp.smartcard.application.telecom.checkrule;

import java.util.*;
import com.trustconnector.scdp.smartcard.checkrule.*;

public class FCIRuleChecker extends APDURuleChecker
{
    public ResponseCheckRuleShort fid;
    public ResponseCheckRuleByte fileType;
    public static Map<String, String> fileTypMap;
    public ResponseCheckRuleBit chv1State;
    public ResponseCheckRuleByte DFNumber;
    public ResponseCheckRuleByte EFNumber;
    public ResponseCheckRuleHalfByteLow chv1Left;
    public ResponseCheckRuleHalfByteLow unblockchv1Left;
    public ResponseCheckRuleHalfByteLow chv2Left;
    public ResponseCheckRuleHalfByteLow unblockchv2Left;
    public ResponseCheckRuleShort fileSize;
    public ResponseCheckRuleBit increaseEnable;
    public ResponseCheckRuleHalfByteLow accUpdate;
    public ResponseCheckRuleHalfByteHigh accReadSeek;
    public ResponseCheckRuleHalfByteLow accIncrease;
    public ResponseCheckRuleHalfByteLow accInvalidate;
    public ResponseCheckRuleHalfByteHigh accRehabilitate;
    public ResponseCheckRuleBit fileStatus;
    public ResponseCheckRuleBit fileCanAccIfInv;
    public ResponseCheckRuleByte fileStructure;
    public ResponseCheckRuleByte recordLen;
    public SWCheckRule swcheck;
    protected static Map<String, String> accMap;
    protected static Map<String, String> efTypeMap;
    public static final int FCI_OFF_FILE_ID = 4;
    public static final int FCI_OFF_FILE_TYPE = 6;
    public static final int FCI_OFF_DF_FILE_Characteristic = 13;
    public static final int FCI_OFF_DF_DF_NUM = 14;
    public static final int FCI_OFF_DF_EF_NUM = 15;
    public static final int FCI_OFF_DF_CHV1_STATUS = 18;
    public static final int FCI_OFF_DF_UNBLOCK_CHV1_STATUS = 19;
    public static final int FCI_OFF_DF_CHV2_STATUS = 20;
    public static final int FCI_OFF_DF_UNBLOCK_CHV2_STATUS = 21;
    public static final int FCI_OFF_EF_FILE_SIZE = 2;
    public static final int FCI_OFF_EF_INC_ENABLE = 7;
    public static final int FCI_OFF_EF_ACC_UPDATE_READSEEK = 8;
    public static final int FCI_OFF_EF_ACC_INC = 9;
    public static final int FCI_OFF_EF_ACC_INV_REH = 10;
    public static final int FCI_OFF_EF_FILE_STATUS = 11;
    public static final int FCI_OFF_EF_FILE_STRUCTURE = 13;
    public static final int FCI_OFF_EF_RECORD_LEN = 14;
    
    public FCIRuleChecker() {
        this.setCheckSW("9000|91XX");
        this.addCheckRule(this.fid = new ResponseCheckRuleShort("FID", 4));
        if (FCIRuleChecker.fileTypMap == null) {
            (FCIRuleChecker.fileTypMap = new HashMap<String, String>()).put("01", "MF");
            FCIRuleChecker.fileTypMap.put("02", "DF");
            FCIRuleChecker.fileTypMap.put("04", "EF");
        }
        this.addCheckRule(this.fileType = new ResponseCheckRuleByte("File Type", 6, FCIRuleChecker.fileTypMap));
        final ResponseCheckConditionByte isEF = new ResponseCheckConditionByte(6, 4);
        (this.chv1State = new ResponseCheckRuleBit("CHV1 State", 13, 7, "Disable", "Enable")).addFalseCondition(isEF);
        this.addCheckRule(this.chv1State);
        (this.DFNumber = new ResponseCheckRuleByte("DF Number", 14)).addFalseCondition(isEF);
        this.addCheckRule(this.DFNumber);
        (this.EFNumber = new ResponseCheckRuleByte("EF Number", 15)).addFalseCondition(isEF);
        this.addCheckRule(this.EFNumber);
        final ResponseCheckConditionBit chv1Init = new ResponseCheckConditionBit(18, 7, true);
        (this.chv1Left = new ResponseCheckRuleHalfByteLow("CHV1 try left count", 18)).addFalseCondition(isEF);
        this.chv1Left.addCondition(chv1Init);
        this.addCheckRule(this.chv1Left);
        final ResponseCheckConditionBit unblockchv1Init = new ResponseCheckConditionBit(19, 7, true);
        (this.unblockchv1Left = new ResponseCheckRuleHalfByteLow("Unblock CHV1 try left count", 19)).addFalseCondition(isEF);
        this.unblockchv1Left.addCondition(unblockchv1Init);
        this.addCheckRule(this.unblockchv1Left);
        final ResponseCheckConditionBit chv2Init = new ResponseCheckConditionBit(20, 7, true);
        (this.chv2Left = new ResponseCheckRuleHalfByteLow("CHV2 try left count", 20)).addFalseCondition(isEF);
        this.chv2Left.addCondition(chv2Init);
        this.addCheckRule(this.chv2Left);
        final ResponseCheckConditionBit unblockchv2Init = new ResponseCheckConditionBit(21, 7, true);
        (this.unblockchv2Left = new ResponseCheckRuleHalfByteLow("Unblock CHV2 try left count", 21)).addFalseCondition(isEF);
        this.unblockchv2Left.addCondition(unblockchv2Init);
        this.addCheckRule(this.unblockchv2Left);
        (this.fileSize = new ResponseCheckRuleShort("FileSize", 2)).addCondition(isEF);
        this.addCheckRule(this.fileSize);
        (this.increaseEnable = new ResponseCheckRuleBit("Increate", 7, 7, "Enable", "Disalbe")).addCondition(isEF);
        this.addCheckRule(this.increaseEnable);
        if (FCIRuleChecker.accMap == null) {
            (FCIRuleChecker.accMap = new HashMap<String, String>()).put("0", "Always");
            FCIRuleChecker.accMap.put("1", "CHV1");
            FCIRuleChecker.accMap.put("2", "CHV2");
            FCIRuleChecker.accMap.put("4", "ADM");
            FCIRuleChecker.accMap.put("5", "ADM");
            FCIRuleChecker.accMap.put("F", "Never");
        }
        (this.accUpdate = new ResponseCheckRuleHalfByteLow("Update ACC", 8, FCIRuleChecker.accMap)).addCondition(isEF);
        this.addCheckRule(this.accUpdate);
        (this.accReadSeek = new ResponseCheckRuleHalfByteHigh("Read|Seek ACC", 8, FCIRuleChecker.accMap)).addCondition(isEF);
        this.addCheckRule(this.accReadSeek);
        (this.accIncrease = new ResponseCheckRuleHalfByteLow("Increase ACC", 9, FCIRuleChecker.accMap)).addCondition(isEF);
        this.addCheckRule(this.accIncrease);
        (this.accInvalidate = new ResponseCheckRuleHalfByteLow("Invalidate ACC", 10, FCIRuleChecker.accMap)).addCondition(isEF);
        this.addCheckRule(this.accInvalidate);
        (this.accRehabilitate = new ResponseCheckRuleHalfByteHigh("Rehabilitate ACC", 10, FCIRuleChecker.accMap)).addCondition(isEF);
        this.addCheckRule(this.accRehabilitate);
        (this.fileStatus = new ResponseCheckRuleBit("File State", 11, 0, "Not Invalidate", "Invalidate")).addCondition(isEF);
        this.addCheckRule(this.fileStatus);
        (this.fileCanAccIfInv = new ResponseCheckRuleBit("File can Read/Update When Invalidate", 11, 2, "YES", "No")).addCondition(isEF);
        this.addCheckRule(this.fileCanAccIfInv);
        if (FCIRuleChecker.efTypeMap == null) {
            (FCIRuleChecker.efTypeMap = new HashMap<String, String>()).put("00", "Transparent");
            FCIRuleChecker.efTypeMap.put("01", "Linear Fixed");
            FCIRuleChecker.efTypeMap.put("03", "Cyclic");
        }
        (this.fileStructure = new ResponseCheckRuleByte("File Structure", 13, FCIRuleChecker.efTypeMap)).addCondition(isEF);
        this.addCheckRule(this.fileStructure);
        final ResponseCheckConditionByte isBianryFile = new ResponseCheckConditionByte(13, 0);
        (this.recordLen = new ResponseCheckRuleByte("Record Length", 14)).addCondition(isEF);
        this.recordLen.addFalseCondition(isBianryFile);
        this.addCheckRule(this.recordLen);
    }
    
    public void setCheckFID(final int fid) {
        this.fid.setMatch(fid);
    }
    
    public void setCheckFileType(final int fileType) {
        this.fileType.setMatch(fileType);
    }
    
    public void setCheckCHV1State(final boolean bDisalbe) {
        this.chv1State.setMatch(bDisalbe);
    }
    
    public void setCheckDFNumber(final int DFNumber) {
        this.DFNumber.setMatch(DFNumber);
    }
    
    public void setCheckEFNumber(final int EFNumber) {
        this.EFNumber.setMatch(EFNumber);
    }
    
    public void setCheckCHV1LeftCount(final int leftCount) {
        this.chv1Left.setMatch(leftCount);
    }
    
    public void setCheckUnblockCHV1LeftCount(final int leftCount) {
        this.unblockchv1Left.setMatch(leftCount);
    }
    
    public void setCheckCHV2LeftCount(final int leftCount) {
        this.chv2Left.setMatch(leftCount);
    }
    
    public void setCheckUnblockCHV2LeftCount(final int leftCount) {
        this.unblockchv2Left.setMatch(leftCount);
    }
    
    public void setCheckFileStructure(final int fileStructure) {
        this.fileStructure.setMatch(fileStructure);
    }
    
    public void setCheckFileSize(final int fileSize) {
        this.fileSize.setMatch(fileSize);
    }
    
    public void setCheckIncreaseEnable(final boolean enable) {
        this.increaseEnable.setMatch(enable);
    }
    
    public void setCheckAccUpdate(final int EFACC) {
        this.accUpdate.setMatch(EFACC);
    }
    
    public void setCheckAccReadSeek(final int EFACC) {
        this.accReadSeek.setMatch(EFACC);
    }
    
    public void setCheckAccIncrease(final int EFACC) {
        this.accIncrease.setMatch(EFACC);
    }
    
    public void setCheckAccInvalidate(final int EFACC) {
        this.accInvalidate.setMatch(EFACC);
    }
    
    public void setCheckACCRehabilitate(final int EFACC) {
        this.accRehabilitate.setMatch(EFACC);
    }
    
    public void setCheckFileInvalidate(final boolean bInvalidate) {
        this.fileStatus.setMatch(!bInvalidate);
    }
}
