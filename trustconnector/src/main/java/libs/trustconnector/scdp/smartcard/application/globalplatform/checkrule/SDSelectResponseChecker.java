package libs.trustconnector.scdp.smartcard.application.globalplatform.checkrule;

import libs.trustconnector.scdp.util.tlv.bertlv.*;
import libs.trustconnector.scdp.smartcard.checkrule.tlv.*;

import libs.trustconnector.scdp.smartcard.checkrule.tlv.APDUTLVRuleChecker;
import libs.trustconnector.scdp.smartcard.checkrule.tlv.ResponseTLVCheckRuleBytes;
import libs.trustconnector.scdp.util.tlv.bertlv.BERTLVBuilder;

public class SDSelectResponseChecker extends APDUTLVRuleChecker
{
    public ResponseTLVCheckRuleBytes sdAID;
    public ResponseTLVCheckRuleBytes sdMngData;
    public ResponseTLVCheckRuleBytes appPdtLCData;
    public ResponseTLVCheckRuleBytes maxIncomingMsgLen;
    
    public SDSelectResponseChecker() {
        this.sdAID = new ResponseTLVCheckRuleBytes("SD AID", BERTLVBuilder.buildTagList("6F84"));
        this.sdMngData = new ResponseTLVCheckRuleBytes("SD Management Data", BERTLVBuilder.buildTagList("6FA573"));
        this.appPdtLCData = new ResponseTLVCheckRuleBytes("Application production Life Cycle data", BERTLVBuilder.buildTagList("6FA59F6E"));
        this.maxIncomingMsgLen = new ResponseTLVCheckRuleBytes("Maximum Length of LC", BERTLVBuilder.buildTagList("6FA59F65"));
        this.addCheckRule(this.sdAID);
        this.addCheckRule(this.sdMngData);
        this.addCheckRule(this.appPdtLCData);
        this.addCheckRule(this.maxIncomingMsgLen);
    }
}
