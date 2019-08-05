package com.trustconnector.scdp.smartcard.checkrule.tlv;

import com.trustconnector.scdp.util.tlv.*;

public interface TLVCheckRule
{
    boolean checkCondition(final TLVTree p0);
    
    boolean checkTLV(final TLVTree p0);
    
    String getRuleDescription();
    
    boolean hasExpect();
}
