package libs.trustconnector.scdp.smartcard.checkrule.tlv;

import libs.trustconnector.scdp.util.tlv.*;
import java.util.*;
import libs.trustconnector.scdp.util.*;

import libs.trustconnector.scdp.util.ByteArray;
import libs.trustconnector.scdp.util.tlv.TagList;

public class ResponseTLVCheckRuleShort extends ResponseTLVCheckRule
{
    protected int retVlue;
    protected int expValue;
    protected int expValueM;
    
    public ResponseTLVCheckRuleShort(final String name, final TagList tagPath, final int valueOff) {
        super(name, tagPath, valueOff);
        this.expValueM = -1;
    }
    
    public ResponseTLVCheckRuleShort(final String name, final TagList tagPath, final int valueOff, final Map<String, String> valueInfo) {
        super(name, tagPath, valueOff);
        this.expValueM = -1;
    }
    
    @Override
    public boolean checkTLVValue(final byte[] value) {
        this.retVlue = ((value[this.valueOff] & 0xFF) << 8 | (value[this.valueOff + 1] & 0xFF));
        this.retValue = ByteArray.convert(value, this.valueOff, 2);
        return !this.matchSet || (this.retVlue & this.expValueM & 0xFFFF) == this.expValue;
    }
    
    public int getReturnValue() {
        return this.retVlue & 0xFFFF;
    }
    
    public void setMatch(final int expValue) {
        this.matchSet = true;
        this.expValue = expValue;
        this.expValueM = 65535;
        super.expValue = String.format("%04X", expValue);
    }
    
    public void setMatch(final int expValue, final int expMask) {
        this.matchSet = true;
        this.expValue = expValue;
        this.expValueM = expMask;
        super.expValue = String.format("%04X", expValue);
        super.dataMask = String.format("%04X", expMask);
    }
}
