package libs.trustconnector.scdp.smartcard.checkrule;

import libs.trustconnector.scdp.smartcard.*;

import libs.trustconnector.scdp.smartcard.APDU;

public interface CheckRule
{
    boolean checkCondition(final APDU p0);
    
    boolean check(final APDU p0);
    
    String getRuleDescription();
    
    boolean hasExpect();
}
