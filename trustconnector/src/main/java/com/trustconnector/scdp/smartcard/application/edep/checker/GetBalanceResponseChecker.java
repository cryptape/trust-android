package com.trustconnector.scdp.smartcard.application.edep.checker;

import com.trustconnector.scdp.smartcard.checkrule.*;

public class GetBalanceResponseChecker extends APDURuleChecker
{
    public ResponseCheckRuleInt balance;
    
    public GetBalanceResponseChecker() {
        this.setCheckSW("9000");
        this.balance = new ResponseCheckRuleInt("\u4f59\u989d", 0);
    }
}
