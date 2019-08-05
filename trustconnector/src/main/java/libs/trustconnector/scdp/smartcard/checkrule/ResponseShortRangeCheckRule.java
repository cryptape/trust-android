package libs.trustconnector.scdp.smartcard.checkrule;

import java.util.*;

public class ResponseShortRangeCheckRule extends ResponseCheckRule
{
    protected int rValue;
    protected short[] expValues;
    
    public ResponseShortRangeCheckRule(final String name, final int byteOff) {
        super(name, byteOff);
    }
    
    public ResponseShortRangeCheckRule(final String name, final int byteOff, final Map<String, String> valueInfoMap) {
        super(name, byteOff, valueInfoMap);
    }
    
    @Override
    public boolean checkRdata(final byte[] rdata) {
        this.retValue = String.format("%02X", rdata[this.byteOff]);
        this.retValue += String.format("%02X", rdata[this.byteOff + 1]);
        this.rValue = (rdata[this.byteOff] << 8 | rdata[this.byteOff + 1]);
        if (this.matchSet) {
            for (int i = 0; i < this.expValues.length; ++i) {
                if (rdata[this.byteOff] == this.expValues[i]) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
    
    public void setMatch(final int min, final int max) {
        this.matchSet = true;
        this.expValues = new short[max - min + 1];
        for (int i = min; i < max; ++i) {
            this.expValues[i - min] = (short)i;
        }
        this.expValue = String.format("Min=%04X,Max=%04X", min, max);
    }
    
    public void setMatch(final short[] expValue) {
        this.matchSet = true;
        this.expValues = expValue.clone();
        this.expValue = String.format("|%04X", expValue[0]);
        for (int i = 1; i < expValue.length; ++i) {
            this.expValue += String.format("|%04X", expValue[i]);
        }
    }
    
    public int getReturnValue() {
        return this.rValue;
    }
}
