package libs.trustconnector.scdp.smartcard.checkrule.tlv;

import libs.trustconnector.scdp.util.tlv.*;
import java.util.*;

import libs.trustconnector.scdp.util.tlv.TagList;

public class ResponseTLVCheckRuleByte extends ResponseTLVCheckRule
{
    protected int retVlue;
    protected int valueMask;
    protected int expValue;
    protected int expValueM;
    
    public ResponseTLVCheckRuleByte(final String name, final TagList tagPath, final int valueOff) {
        super(name, tagPath, valueOff);
        this.expValueM = 255;
    }
    
    public ResponseTLVCheckRuleByte(final String name, final TagList tagPath, final int valueOff, final Map<String, String> valueInfoMap) {
        super(name, tagPath, valueOff, valueInfoMap);
        this.expValueM = 255;
    }
    
    public ResponseTLVCheckRuleByte(final String name, final TagList tagPath, final int valueOff, final int valueMask) {
        super(name, tagPath, valueOff);
        this.expValueM = valueMask;
    }
    
    public ResponseTLVCheckRuleByte(final String name, final TagList tagPath, final int valueOff, final int valueMask, final Map<String, String> valueInfoMap) {
        super(name, tagPath, valueOff, valueInfoMap);
        this.expValueM = valueMask;
    }
    
    @Override
    public boolean checkTLVValue(final byte[] value) {
        if (value != null && this.valueOff < value.length) {
            this.retVlue = (value[this.valueOff] & this.expValueM);
            this.retValue = String.format("%02X", this.retVlue);
            if (this.matchSet) {
                return (this.retVlue & 0xFF) == this.expValue;
            }
        }
        else if (this.matchSet) {
            return false;
        }
        return true;
    }
    
    public void setMatch(final int byteValue) {
        this.matchSet = true;
        this.expValue = byteValue;
        super.expValue = String.format("%02X", byteValue);
    }
    
    public void setMatch(final int byteValue, final int expMask) {
        this.matchSet = true;
        this.expValue = byteValue;
        this.expValueM = expMask;
        super.expValue = String.format("%02X", byteValue);
        super.dataMask = String.format("%02X", expMask);
    }
    
    public int getReturnValue() {
        return this.retVlue & 0xFF;
    }
}
