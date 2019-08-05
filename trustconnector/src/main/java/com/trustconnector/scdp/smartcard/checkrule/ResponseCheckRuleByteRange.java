package com.trustconnector.scdp.smartcard.checkrule;

import java.util.*;

public class ResponseCheckRuleByteRange extends ResponseCheckRule
{
    protected int rValue;
    protected byte[] expValues;
    
    public ResponseCheckRuleByteRange(final String name, final int byteOff) {
        super(name, byteOff);
    }
    
    public ResponseCheckRuleByteRange(final String name, final int byteOff, final Map<String, String> valueInfoMap) {
        super(name, byteOff, valueInfoMap);
    }
    
    @Override
    public boolean checkRdata(final byte[] rdata) {
        this.rValue = rdata[this.byteOff];
        this.retValue = String.format("%02X", rdata[this.byteOff]);
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
        this.expValues = new byte[max - min + 1];
        for (int i = min; i < max; ++i) {
            this.expValues[i - min] = (byte)i;
        }
        this.expValue = String.format("Min=%02X,Max=%02X", min, max);
    }
    
    public void setMatch(final byte[] expValue) {
        this.matchSet = true;
        this.expValues = expValue.clone();
        this.expValue = String.format("|%02X", expValue[0]);
        for (int i = 1; i < expValue.length; ++i) {
            this.expValue += String.format("|%02X", expValue[i]);
        }
    }
    
    public int getReturnValue() {
        return this.rValue;
    }
}
