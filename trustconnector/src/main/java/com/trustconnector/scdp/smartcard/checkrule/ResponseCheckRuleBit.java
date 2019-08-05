package com.trustconnector.scdp.smartcard.checkrule;

import java.util.*;

public class ResponseCheckRuleBit extends ResponseCheckRule
{
    protected boolean bRetValue;
    protected int bitOff;
    protected boolean bitMatch;
    public static final int BIT_0 = 0;
    public static final int BIT_1 = 1;
    public static final int BIT_2 = 2;
    public static final int BIT_3 = 3;
    public static final int BIT_4 = 4;
    public static final int BIT_5 = 5;
    public static final int BIT_6 = 6;
    public static final int BIT_7 = 7;
    
    public ResponseCheckRuleBit(final String name, final int byteOff, final int bitOff) {
        super(name, byteOff);
        this.bitOff = bitOff;
    }
    
    public ResponseCheckRuleBit(final String name, final int byteOff, final int bitOff, final boolean bitMatch) {
        super(name, byteOff);
        this.bitOff = bitOff;
        this.setMatch(bitMatch);
    }
    
    public ResponseCheckRuleBit(final String name, final int byteOff, final int bitOff, final String bitSetV, final String bitClrV) {
        super(name, byteOff);
        this.bitOff = bitOff;
        final Map<String, String> en = new HashMap<String, String>();
        en.put("1", bitSetV);
        en.put("0", bitClrV);
        this.valueInfoMap = en;
    }
    
    public ResponseCheckRuleBit(final String name, final int byteOff, final int bitOff, final boolean bitMatch, final String bitSetV, final String bitClrV) {
        super(name, byteOff);
        this.bitOff = bitOff;
        final Map<String, String> en = new HashMap<String, String>();
        en.put("1", bitSetV);
        en.put("0", bitClrV);
        this.valueInfoMap = en;
        this.setMatch(bitMatch);
    }
    
    public void setMatch(final boolean bitMatch) {
        this.matchSet = true;
        this.bitMatch = bitMatch;
        this.expValue = (bitMatch ? "1" : "0");
    }
    
    @Override
    public boolean checkRdata(final byte[] rdata) {
        final byte expMask = (byte)(1 << this.bitOff);
        final boolean bitRet = (rdata[this.byteOff] & expMask) == expMask;
        this.bRetValue = bitRet;
        this.retValue = (bitRet ? "1" : "0");
        return !this.matchSet || bitRet == this.bitMatch;
    }
    
    public boolean getReturnValue() {
        return this.bRetValue;
    }
}
