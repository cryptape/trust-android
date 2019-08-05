package com.trustconnector.scdp.smartcard.checkrule;

import com.trustconnector.scdp.smartcard.*;

public class ResponseCheckConditionBit implements CheckRuleCondition
{
    protected int byteOff;
    protected int bitOff;
    protected boolean cmpValue;
    public static final int BIT_0 = 0;
    public static final int BIT_1 = 1;
    public static final int BIT_2 = 2;
    public static final int BIT_3 = 3;
    public static final int BIT_4 = 4;
    public static final int BIT_5 = 5;
    public static final int BIT_6 = 6;
    public static final int BIT_7 = 7;
    
    public ResponseCheckConditionBit(final int byteOff, final int bitOff, final boolean bitMatch) {
        this.byteOff = byteOff;
        this.bitOff = bitOff;
        this.cmpValue = bitMatch;
    }
    
    @Override
    public boolean checkCondition(final APDU apdu) {
        final byte[] rdata = apdu.getRData();
        boolean conditionRes = false;
        if (rdata != null && rdata.length > this.byteOff) {
            final byte expMask = (byte)(1 << this.bitOff);
            conditionRes = (this.cmpValue == ((rdata[this.byteOff] & expMask) == expMask));
        }
        return conditionRes;
    }
    
    public void setMatch(final boolean bitMatch) {
        this.cmpValue = bitMatch;
    }
}
