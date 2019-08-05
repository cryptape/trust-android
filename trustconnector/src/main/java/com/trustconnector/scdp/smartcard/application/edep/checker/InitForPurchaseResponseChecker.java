package com.trustconnector.scdp.smartcard.application.edep.checker;

import com.trustconnector.scdp.smartcard.checkrule.*;

public class InitForPurchaseResponseChecker extends APDURuleChecker
{
    public ResponseCheckRuleInt balance;
    public ResponseCheckRuleShort offLineSN;
    public ResponseCheckRule3Bytes overdrawLimit;
    public ResponseCheckRuleByte keyVer;
    public ResponseCheckRuleByte algID;
    public ResponseCheckRuleBytes random;
    
    public InitForPurchaseResponseChecker() {
        this.setCheckSW("9000");
        this.balance = new ResponseCheckRuleInt("\u4f59\u989d", 0);
        this.offLineSN = new ResponseCheckRuleShort("\u8131\u673a\u4ea4\u6613\u5e8f\u53f7", 4);
        this.overdrawLimit = new ResponseCheckRule3Bytes("\u900f\u652f\u9650\u989d", 6);
        this.keyVer = new ResponseCheckRuleByte("\u79d8\u94a5\u7248\u672c\u53f7", 9);
        this.algID = new ResponseCheckRuleByte("\u7b97\u6cd5\u6807\u8bc6", 10);
        this.random = new ResponseCheckRuleBytes("\u4f2a\u968f\u673a\u6570", 11, 4);
        this.addCheckRule(this.balance);
        this.addCheckRule(this.offLineSN);
        this.addCheckRule(this.overdrawLimit);
        this.addCheckRule(this.keyVer);
        this.addCheckRule(this.algID);
        this.addCheckRule(this.random);
    }
}
