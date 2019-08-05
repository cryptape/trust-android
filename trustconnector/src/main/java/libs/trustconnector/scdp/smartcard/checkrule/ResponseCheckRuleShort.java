package libs.trustconnector.scdp.smartcard.checkrule;

import java.util.*;
import libs.trustconnector.scdp.util.*;

import libs.trustconnector.scdp.util.ByteArray;

public class ResponseCheckRuleShort extends ResponseCheckRule
{
    protected int rValue;
    protected int expValueT;
    protected int expValueM;
    
    public ResponseCheckRuleShort(final String name, final int byteOff) {
        super(name, byteOff);
    }
    
    public ResponseCheckRuleShort(final String name, final int byteOff, final Map<String, String> valueInfoMap) {
        super(name, byteOff, valueInfoMap);
    }
    
    public void setMatch(final int expValue) {
        this.matchSet = true;
        this.expValueT = expValue;
        this.expValueM = 65535;
        this.expValue = String.format("%04X", expValue);
    }
    
    public void setMatch(final int expValue, final int dataMask) {
        this.matchSet = true;
        this.expValueT = expValue;
        this.expValue = String.format("%04X", expValue);
        this.dataMask = String.format("%04X", dataMask);
    }
    
    @Override
    public boolean checkRdata(final byte[] rdata) {
        this.rValue = ((rdata[this.byteOff] & 0xFF) << 8 | (rdata[this.byteOff + 1] & 0xFF));
        this.retValue = ByteArray.convert(rdata, this.byteOff, 2);
        return !this.matchSet || (this.rValue & this.expValueM & 0xFFFF) == this.expValueT;
    }
    
    public int getReturnValue() {
        return this.rValue;
    }
}
