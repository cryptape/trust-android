package com.trustconnector.scdp.smartcard.checkrule.tlv;

import com.trustconnector.scdp.util.tlv.*;

public interface TLVCheckRuleCondition
{
    boolean checkCondition(final TLVTree p0);
}
