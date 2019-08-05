package libs.trustconnector.scdp.smartcard.checkrule.tlv;

import libs.trustconnector.scdp.util.tlv.*;

import libs.trustconnector.scdp.util.tlv.TagList;

public class ResponseTLVCheckRuleConditionByte extends ResponseTLVCheckRuleCondition
{
    protected int expValue;
    protected int expValueMask;
    
    public ResponseTLVCheckRuleConditionByte(final TagList tagPath, final int valueOff, final int expValue) {
        super(tagPath, valueOff);
        this.expValue = expValue;
        this.expValueMask = 255;
    }
    
    public ResponseTLVCheckRuleConditionByte(final TagList tagPath, final int valueOff, final int expValue, final int valueMask) {
        super(tagPath, valueOff);
        this.expValue = expValue;
        this.expValueMask = valueMask;
    }
    
    @Override
    public boolean checkCondition(final byte[] value) {
        return (value[this.valueOff] & this.expValueMask & 0xFF) == this.expValue;
    }
}
