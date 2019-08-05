package libs.trustconnector.scdp.smartcard.checkrule;

import java.util.*;

public class ResponseCheckRuleHalfByteHigh extends ResponseCheckRule
{
    protected int rValue;
    protected int expValueI;
    
    public ResponseCheckRuleHalfByteHigh(final String name, final int byteOff) {
        super(name, byteOff);
    }
    
    public ResponseCheckRuleHalfByteHigh(final String name, final int byteOff, final Map<String, String> valueInfoMap) {
        super(name, byteOff, valueInfoMap);
    }
    
    public void setMatch(final int expValue) {
        this.expValueI = expValue;
        this.expValue = String.format("%01X", expValue & 0xF);
    }
    
    @Override
    public boolean checkRdata(final byte[] rdata) {
        final byte r = (byte)(rdata[this.byteOff] >> 4 & 0xF);
        this.rValue = r;
        this.retValue = String.format("%01X", r);
        return !this.matchSet || r == this.expValueI;
    }
    
    public int getReturnValue() {
        return this.rValue;
    }
}
