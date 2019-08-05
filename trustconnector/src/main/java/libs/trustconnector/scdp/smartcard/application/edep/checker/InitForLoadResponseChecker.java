package libs.trustconnector.scdp.smartcard.application.edep.checker;

import libs.trustconnector.scdp.smartcard.checkrule.*;

import libs.trustconnector.scdp.smartcard.checkrule.APDURuleChecker;
import libs.trustconnector.scdp.smartcard.checkrule.ResponseCheckRuleByte;
import libs.trustconnector.scdp.smartcard.checkrule.ResponseCheckRuleBytes;
import libs.trustconnector.scdp.smartcard.checkrule.ResponseCheckRuleInt;
import libs.trustconnector.scdp.smartcard.checkrule.ResponseCheckRuleShort;

public class InitForLoadResponseChecker extends APDURuleChecker
{
    public ResponseCheckRuleInt balance;
    public ResponseCheckRuleShort onLineSN;
    public ResponseCheckRuleByte keyVer;
    public ResponseCheckRuleByte algID;
    public ResponseCheckRuleBytes random;
    public ResponseCheckRuleBytes MAC1;
    
    public InitForLoadResponseChecker() {
        this.setCheckSW("9000");
        this.balance = new ResponseCheckRuleInt("\u4f59\u989d", 0);
        this.onLineSN = new ResponseCheckRuleShort("\u8054\u673a\u4ea4\u6613\u5e8f\u53f7", 4);
        this.keyVer = new ResponseCheckRuleByte("\u79d8\u94a5\u7248\u672c\u53f7", 6);
        this.algID = new ResponseCheckRuleByte("\u7b97\u6cd5\u6807\u8bc6", 7);
        this.random = new ResponseCheckRuleBytes("\u4f2a\u968f\u673a\u6570", 8, 4);
        this.MAC1 = new ResponseCheckRuleBytes("\u4f2a\u968f\u673a\u6570", 12, 4);
        this.addCheckRule(this.balance);
        this.addCheckRule(this.onLineSN);
        this.addCheckRule(this.keyVer);
        this.addCheckRule(this.algID);
        this.addCheckRule(this.random);
        this.addCheckRule(this.MAC1);
    }
}
