package com.trustconnector.scdp.smartcard.application.edep.checker;

import com.trustconnector.scdp.smartcard.checkrule.*;

public class DebitForPurchaseResponseChecker extends APDURuleChecker
{
    public ResponseCheckRuleBytes TAC;
    public ResponseCheckRuleBytes MAC2;
    
    public DebitForPurchaseResponseChecker() {
        this.setCheckSW("9000");
        this.addCheckRule(this.TAC = new ResponseCheckRuleBytes("TAC", 0, 4));
        this.addCheckRule(this.MAC2 = new ResponseCheckRuleBytes("MAC2", 4, 4));
    }
}
