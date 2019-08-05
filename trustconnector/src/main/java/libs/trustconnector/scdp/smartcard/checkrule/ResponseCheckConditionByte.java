package libs.trustconnector.scdp.smartcard.checkrule;

import libs.trustconnector.scdp.smartcard.*;

import libs.trustconnector.scdp.smartcard.APDU;

public class ResponseCheckConditionByte implements CheckRuleCondition
{
    protected int byteOff;
    protected int expValue;
    protected int dataMask;
    
    public ResponseCheckConditionByte(final int byteOff, final int exp) {
        this.byteOff = byteOff;
        this.expValue = exp;
        this.dataMask = 255;
    }
    
    public ResponseCheckConditionByte(final int byteOff, final int exp, final int mask) {
        this.byteOff = byteOff;
        this.expValue = exp;
        this.dataMask = mask;
    }
    
    @Override
    public boolean checkCondition(final APDU apdu) {
        final byte[] rdata = apdu.getRData();
        boolean conditionRes = false;
        if (rdata != null && rdata.length > this.byteOff) {
            conditionRes = (this.expValue == (rdata[this.byteOff] & this.dataMask));
        }
        return conditionRes;
    }
}
