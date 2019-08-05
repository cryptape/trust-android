package com.trustconnector.scdp.smartcard.checkrule.tlv;

import com.trustconnector.scdp.util.tlv.*;

public class ResponseTLVCheckRuleConditionBit extends ResponseTLVCheckRuleCondition
{
    public ResponseTLVCheckRuleConditionBit(final TagList tagPath, final int valueOff) {
        super(tagPath, valueOff);
    }
    
    @Override
    public boolean checkCondition(final byte[] value) {
        return false;
    }
}
