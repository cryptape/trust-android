package com.trustconnector.scdp.smartcard.checkrule;

import com.trustconnector.scdp.smartcard.*;

public interface CheckRuleCondition
{
    boolean checkCondition(final APDU p0);
}
