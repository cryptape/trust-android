package libs.trustconnector.scdp.smartcard.checkrule.tlv;

import libs.trustconnector.scdp.util.tlv.*;

import libs.trustconnector.scdp.util.tlv.TagList;

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
