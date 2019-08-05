package com.trustconnector.scdp.smartcard.checkrule;

import com.trustconnector.scdp.smartcard.*;

public class ResponseLengthCheckRule implements CheckRule
{
    protected int checkType;
    protected int expLen;
    protected int expLenMax;
    protected int[] expLens;
    protected String desc;
    
    public ResponseLengthCheckRule(final int expLen) {
        this.checkType = 1;
        this.expLen = expLen;
    }
    
    public ResponseLengthCheckRule(final int expMin, final int expMax) {
        this.checkType = 2;
        this.expLen = expMin;
        this.expLenMax = expMax;
    }
    
    public ResponseLengthCheckRule(final int[] expLen) {
        this.checkType = 3;
        this.expLens = expLen.clone();
    }
    
    @Override
    public boolean checkCondition(final APDU apdu) {
        return true;
    }
    
    @Override
    public boolean check(final APDU apdu) {
        final byte[] rdata = apdu.getRData();
        if (rdata == null) {
            return this.checkType == 1 && this.expLen == 0;
        }
        final int length = rdata.length;
        this.desc = String.format("Ret Lenght:0x%04X", length);
        if (this.checkType == 1) {
            if (length == this.expLen) {
                return true;
            }
            this.desc += String.format("Exp Length:0x%04X", this.expLen);
        }
        else if (this.checkType == 2) {
            if (length >= this.expLen && length <= this.expLenMax) {
                return true;
            }
            this.desc += String.format("Exp Min Length:0x%04X,Max Length:0x%04X", this.expLen, this.expLenMax);
        }
        else if (this.checkType == 3) {
            for (int c = this.expLens.length, i = 0; i < c; ++i) {
                if (this.expLens[i] == length) {
                    return true;
                }
            }
            this.desc += String.format("Exp Length check fail", new Object[0]);
        }
        return false;
    }
    
    @Override
    public String getRuleDescription() {
        return this.desc;
    }
    
    @Override
    public boolean hasExpect() {
        return true;
    }
}
