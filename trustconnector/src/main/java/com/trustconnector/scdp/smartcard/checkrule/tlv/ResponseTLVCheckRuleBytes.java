package com.trustconnector.scdp.smartcard.checkrule.tlv;

import com.trustconnector.scdp.util.tlv.*;
import java.util.*;
import com.trustconnector.scdp.util.*;

public class ResponseTLVCheckRuleBytes extends ResponseTLVCheckRule
{
    protected byte[] retVlue;
    protected byte[] expValue;
    protected byte[] expValueM;
    
    public ResponseTLVCheckRuleBytes(final String name, final TagList tagPath) {
        super(name, tagPath, 0);
    }
    
    public ResponseTLVCheckRuleBytes(final String name, final TagList tagPath, final int valueOff) {
        super(name, tagPath, valueOff);
    }
    
    public ResponseTLVCheckRuleBytes(final String name, final TagList tagPath, final int valueOff, final Map<String, String> valueInfoMap) {
        super(name, tagPath, valueOff, valueInfoMap);
    }
    
    @Override
    public boolean checkTLVValue(final byte[] value) {
        final int retLen = value.length - this.valueOff;
        this.retVlue = new byte[value.length - this.valueOff];
        System.arraycopy(value, this.valueOff, this.retVlue, 0, retLen);
        this.retValue = ByteArray.convert(this.retVlue);
        return !this.matchSet || Util.arrayCompare(this.retVlue, 0, this.expValue, 0, retLen);
    }
    
    public void setMatch(final byte[] expValue) {
        this.matchSet = true;
        this.expValue = expValue.clone();
    }
    
    public void setMatch(final String expValueStr) {
        this.matchSet = true;
        this.expValue = ByteArray.convert(expValueStr);
    }
    
    public byte[] getReturnValue() {
        return this.retVlue;
    }
}
