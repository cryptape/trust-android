package libs.trustconnector.scdp.smartcard.checkrule;

import libs.trustconnector.scdp.smartcard.*;

import libs.trustconnector.scdp.smartcard.APDU;

public interface CheckRuleCondition
{
    boolean checkCondition(final APDU p0);
}
