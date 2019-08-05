package com.trustconnector.scdp.smartcard.checkrule;

import java.util.*;
import com.trustconnector.scdp.util.*;

public class ResponseCheckRuleInt extends ResponseCheckRule
{
    protected int rValue;
    protected int expValueT;
    protected int expValueM;
    
    public ResponseCheckRuleInt(final String name, final int byteOff) {
        super(name, byteOff);
    }
    
    public ResponseCheckRuleInt(final String name, final int byteOff, final Map<String, String> infoValueMap) {
        super(name, byteOff, infoValueMap);
    }
    
    @Override
    public boolean checkRdata(final byte[] rdata) {
        this.rValue = Util.bytesToInt(rdata, this.byteOff);
        this.retValue = ByteArray.convert(rdata, this.byteOff, 4);
        return !this.matchSet || (this.rValue & this.expValueM) == this.expValueT;
    }
    
    public void setMatch(final int expValue) {
        this.matchSet = true;
        this.expValueT = expValue;
        this.expValueM = -1;
        super.expValue = String.format("%08X", expValue);
    }
    
    public void setMatch(final int expValue, final int mask) {
        this.matchSet = true;
        this.expValueT = expValue;
        this.expValueM = mask;
        super.expValue = String.format("%08X", expValue);
        super.dataMask = String.format("%08X", mask);
    }
    
    public int getReturnValue() {
        return this.rValue;
    }
}
