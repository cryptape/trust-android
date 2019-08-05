package libs.trustconnector.scdp.smartcard.application.edep.checker;

import libs.trustconnector.scdp.smartcard.checkrule.*;

import libs.trustconnector.scdp.smartcard.checkrule.APDURuleChecker;
import libs.trustconnector.scdp.smartcard.checkrule.ResponseCheckRuleBytes;

public class DebetForLoadResponseChecker extends APDURuleChecker
{
    public ResponseCheckRuleBytes TAC1;
    
    public DebetForLoadResponseChecker() {
        this.setCheckSW("9000");
        this.addCheckRule(this.TAC1 = new ResponseCheckRuleBytes("TAC1", 0, 4));
    }
}
