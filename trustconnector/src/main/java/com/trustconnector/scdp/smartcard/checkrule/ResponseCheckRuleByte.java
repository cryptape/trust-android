package com.trustconnector.scdp.smartcard.checkrule;

import java.util.*;

public class ResponseCheckRuleByte extends ResponseCheckRule
{
    protected int retVlue;
    protected int expValueT;
    protected int expValueM;
    
    public ResponseCheckRuleByte(final String name, final int byteOff) {
        super(name, byteOff);
    }
    
    public ResponseCheckRuleByte(final String name, final int byteOff, final Map<String, String> valueInfoMap) {
        super(name, byteOff);
        this.valueInfoMap = valueInfoMap;
    }
    
    public void setMatch(final int expValue) {
        this.matchSet = true;
        this.expValueT = expValue;
        this.expValueM = 255;
        this.expValue = String.format("%02X", expValue);
    }
    
    public void setMatch(final int expValue, final int dataMask) {
        this.matchSet = true;
        this.expValueT = expValue;
        this.expValue = String.format("%02X", expValue);
        this.dataMask = String.format("%02X", dataMask);
    }
    
    @Override
    public boolean checkRdata(final byte[] rdata) {
        this.retVlue = rdata[this.byteOff];
        this.retValue = String.format("%02X", rdata[this.byteOff]);
        return !this.matchSet || (rdata[this.byteOff] & this.expValueM & 0xFF) == this.expValueT;
    }
    
    public int getReturnValue() {
        return this.retVlue;
    }
}
