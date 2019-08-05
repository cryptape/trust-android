package com.trustconnector.scdp.smartcard.checkrule.tlv;

import com.trustconnector.scdp.util.tlv.*;

public abstract class ResponseTLVCheckRuleCondition implements TLVCheckRuleCondition
{
    protected TagList tagPath;
    protected int valueOff;
    
    public ResponseTLVCheckRuleCondition(final TagList tagPath, final int valueOff) {
        this.tagPath = tagPath;
        this.valueOff = valueOff;
    }
    
    @Override
    public boolean checkCondition(final TLVTree tlvTree) {
        final TLVTreeItem item = tlvTree.findTLV(this.tagPath);
        if (item != null) {
            final byte[] value = item.getValue();
            if (value != null && value.length > this.valueOff) {
                return this.checkCondition(value);
            }
        }
        return false;
    }
    
    public abstract boolean checkCondition(final byte[] p0);
}
