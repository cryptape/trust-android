package com.trustconnector.scdp.smartcard.checkrule;

import com.trustconnector.scdp.smartcard.*;

public class ResponseCheckConditionSW implements CheckRuleCondition
{
    protected int[] expSW;
    protected int[] expSWMask;
    
    public ResponseCheckConditionSW(final short sw) {
        (this.expSW = new int[1])[0] = sw;
        (this.expSWMask = new int[1])[1] = -1;
    }
    
    public ResponseCheckConditionSW(final int sw, final int swMask) {
        (this.expSW = new int[1])[0] = sw;
        (this.expSWMask = new int[1])[1] = (short)swMask;
    }
    
    public ResponseCheckConditionSW(final int[] sw) {
        this.expSW = sw.clone();
        this.expSWMask = new int[sw.length];
        for (int i = 0; i < sw.length; ++i) {
            this.expSWMask[i] = -1;
        }
    }
    
    public ResponseCheckConditionSW(final int[] sw, final int[] swMask) {
        this.expSW = sw.clone();
        this.expSWMask = swMask.clone();
    }
    
    @Override
    public boolean checkCondition(final APDU apdu) {
        if (this.expSW != null) {
            final int swRet = apdu.getSW();
            for (int i = 0; i < this.expSW.length; ++i) {
                if ((swRet & this.expSWMask[i]) == this.expSW[i]) {
                    return true;
                }
            }
        }
        return true;
    }
}
