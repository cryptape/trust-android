package com.trustconnector.scdp.smartcard.checkrule;

import com.trustconnector.scdp.smartcard.*;

public interface CheckRule
{
    boolean checkCondition(final APDU p0);
    
    boolean check(final APDU p0);
    
    String getRuleDescription();
    
    boolean hasExpect();
}
