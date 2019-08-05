package libs.trustconnector.scdp.smartcard.application.telecom.checkrule;

import libs.trustconnector.scdp.util.tlv.bertlv.*;
import libs.trustconnector.scdp.smartcard.checkrule.*;
import libs.trustconnector.scdp.smartcard.checkrule.tlv.*;
import libs.trustconnector.scdp.util.tlv.*;

import libs.trustconnector.scdp.smartcard.checkrule.ValueInfoMap;
import libs.trustconnector.scdp.smartcard.checkrule.tlv.APDUTLVRuleChecker;
import libs.trustconnector.scdp.smartcard.checkrule.tlv.ResponseTLVCheckRuleByte;
import libs.trustconnector.scdp.smartcard.checkrule.tlv.ResponseTLVCheckRuleBytes;
import libs.trustconnector.scdp.smartcard.checkrule.tlv.ResponseTLVCheckRuleConditionByte;
import libs.trustconnector.scdp.smartcard.checkrule.tlv.ResponseTLVCheckRuleShort;
import libs.trustconnector.scdp.util.tlv.TagList;
import libs.trustconnector.scdp.util.tlv.bertlv.BERTLVBuilder;

public class FCPRuleChecker extends APDUTLVRuleChecker
{
    public ResponseTLVCheckRuleByte fileDescByteShareable;
    public ResponseTLVCheckRuleByte fileDescByteFileType;
    public ResponseTLVCheckRuleByte fileDescByteEFStructure;
    public ResponseTLVCheckRuleByte dcs;
    public ResponseTLVCheckRuleShort recordLen;
    public ResponseTLVCheckRuleByte recordNum;
    public ResponseTLVCheckRuleShort fileID;
    public ResponseTLVCheckRuleBytes refExpForamt;
    public ResponseTLVCheckRuleShort fileSize;
    public ResponseTLVCheckRuleByte fileSFI;
    public static final int FILE_DESC_OFF_DESC_BYTE = 0;
    public static final int FILE_DESC_OFF_DCS = 1;
    public static final int FILE_DESC_OFF_REC_LEN = 2;
    public static final int FILE_DESC_OFF_REC_NUM = 4;
    public static final int DESC_BYTE_MASK_SHAREABLE = 192;
    public static final int DESC_BYTE_MASK_FILE_TYPE = 56;
    public static final int FILE_TYPE_WROKING_EF = 0;
    public static final int FILE_TYPE_INTERNAL_EF = 8;
    public static final int FILE_TYPE_DF_ADF = 56;
    public static final int DESC_BYTE_MASK_EF_STRUCTURE = 7;
    public static final int EF_STRCTURE_TRANSPRENT = 1;
    public static final int EF_STRCTURE_LINEAR_FIXED = 2;
    public static final int EF_STRCTURE_CYCLIC = 6;
    
    public FCPRuleChecker() {
        final TagList fileDescTagPath = BERTLVBuilder.buildTagList("6282");
        this.addCheckRule(this.fileDescByteShareable = new ResponseTLVCheckRuleByte("Shareable", fileDescTagPath, 0, 192, new ValueInfoMap("00|False|40|True")));
        this.addCheckRule(this.fileDescByteFileType = new ResponseTLVCheckRuleByte("File Type", fileDescTagPath, 0, 56, new ValueInfoMap("00|Working EF|08|Internal EF|38|DF/ADF")));
        this.fileDescByteEFStructure = new ResponseTLVCheckRuleByte("EF Structure", fileDescTagPath, 0, 7, new ValueInfoMap("01|Transparent|02|Linear Fixed|06|Cyclic"));
        final ResponseTLVCheckRuleConditionByte isDF = new ResponseTLVCheckRuleConditionByte(fileDescTagPath, 0, 56, 56);
        this.fileDescByteEFStructure.addFalseCondition(isDF);
        this.addCheckRule(this.fileDescByteEFStructure);
        (this.dcs = new ResponseTLVCheckRuleByte("Data coding byte", fileDescTagPath, 1)).setMatch(33);
        this.addCheckRule(this.dcs);
        (this.recordLen = new ResponseTLVCheckRuleShort("Record Length", fileDescTagPath, 2)).addFalseCondition(isDF);
        final ResponseTLVCheckRuleConditionByte isBinary = new ResponseTLVCheckRuleConditionByte(fileDescTagPath, 0, 1, 7);
        this.recordLen.addFalseCondition(isBinary);
        this.addCheckRule(this.recordLen);
        (this.recordNum = new ResponseTLVCheckRuleByte("Record Number", fileDescTagPath, 4)).addFalseCondition(isDF);
        this.recordNum.addFalseCondition(isBinary);
        this.addCheckRule(this.recordNum);
        final TagList fileIDTagPath = BERTLVBuilder.buildTagList("6283");
        this.addCheckRule(this.fileID = new ResponseTLVCheckRuleShort("File ID", fileIDTagPath, 0));
        final TagList fileRefExpForamtTagPath = BERTLVBuilder.buildTagList("628B");
        this.addCheckRule(this.refExpForamt = new ResponseTLVCheckRuleBytes("Security Attribute(8B)", fileRefExpForamtTagPath, 0));
        final TagList fileSzieTagPath = BERTLVBuilder.buildTagList("6280");
        this.addCheckRule(this.fileSize = new ResponseTLVCheckRuleShort("File Size", fileSzieTagPath, 0));
        final TagList efSFI = BERTLVBuilder.buildTagList("6288");
        (this.fileSFI = new ResponseTLVCheckRuleByte("SFI", efSFI, 0)).addFalseCondition(isBinary);
        this.addCheckRule(this.fileSFI);
    }
}
