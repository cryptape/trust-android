package libs.trustconnector.scdp.smartcard.application.edep.checker;

import libs.trustconnector.scdp.smartcard.checkrule.*;

import libs.trustconnector.scdp.smartcard.checkrule.APDURuleChecker;
import libs.trustconnector.scdp.smartcard.checkrule.ResponseCheckRuleInt;

public class GetBalanceResponseChecker extends APDURuleChecker
{
    public ResponseCheckRuleInt balance;
    
    public GetBalanceResponseChecker() {
        this.setCheckSW("9000");
        this.balance = new ResponseCheckRuleInt("\u4f59\u989d", 0);
    }
}
