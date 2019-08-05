package com.trustconnector.scdp.smartcard.application.edep.checker;

import com.trustconnector.scdp.smartcard.checkrule.*;

public class DebetForLoadResponseChecker extends APDURuleChecker
{
    public ResponseCheckRuleBytes TAC1;
    
    public DebetForLoadResponseChecker() {
        this.setCheckSW("9000");
        this.addCheckRule(this.TAC1 = new ResponseCheckRuleBytes("TAC1", 0, 4));
    }
}
