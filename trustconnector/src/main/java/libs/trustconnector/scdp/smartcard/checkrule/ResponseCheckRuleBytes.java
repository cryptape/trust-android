package libs.trustconnector.scdp.smartcard.checkrule;

import java.util.*;
import libs.trustconnector.scdp.util.*;

import libs.trustconnector.scdp.util.ByteArray;
import libs.trustconnector.scdp.util.Util;

public class ResponseCheckRuleBytes extends ResponseCheckRule
{
    protected int rspLen;
    protected byte[] rValue;
    protected byte[] expValueB;
    protected byte[] dataValueM;
    
    public ResponseCheckRuleBytes(final String name, final int byteOff, final int bytesLen) {
        super(name, byteOff);
        this.rspLen = bytesLen;
    }
    
    public ResponseCheckRuleBytes(final String name, final int byteOff, final int bytesLen, final Map<String, String> valueInfoMap) {
        super(name, byteOff, valueInfoMap);
        this.rspLen = bytesLen;
    }
    
    public void setMatch(final byte[] expRsp) {
        this.matchSet = true;
        this.expValue = ByteArray.convert(expRsp);
        this.expValueB = expRsp.clone();
        this.dataValueM = null;
    }
    
    public void setMatch(final byte[] expRsp, final byte[] dataMask) {
        this.matchSet = true;
        this.expValue = ByteArray.convert(expRsp);
        this.dataMask = ByteArray.convert(dataMask);
        this.expValueB = expRsp.clone();
        this.dataValueM = dataMask.clone();
    }
    
    public void setMatch(final String expRsp) {
        this.matchSet = true;
        this.expValue = expRsp;
        if (expRsp.indexOf(88) == -1) {
            this.expValueB = ByteArray.convert(expRsp);
        }
        else {
            String t = expRsp.replace('X', '0');
            this.expValueB = ByteArray.convert(t);
            t = this.buildMask(t);
            this.dataValueM = ByteArray.convert(t);
        }
    }
    
    @Override
    public boolean checkRdata(final byte[] rdata) {
        this.rValue = new byte[this.rspLen];
        System.arraycopy(rdata, this.byteOff, this.rValue, 0, this.rspLen);
        this.retValue = ByteArray.convert(rdata, this.byteOff, this.rspLen);
        if (this.matchSet) {
            if (this.dataValueM == null) {
                return Util.arrayCompare(rdata, this.byteOff, this.expValueB, 0, this.rspLen);
            }
            for (int expLength = this.rspLen, i = 0; i < expLength; ++i) {
                if ((rdata[this.byteOff + i] & this.dataValueM[i]) != this.expValueB[i]) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public byte[] getReturnValue() {
        return this.rValue;
    }
    
    private String buildMask(final String maskWithX) {
        final StringBuilder tBuilder = new StringBuilder();
        for (int l = maskWithX.length(), i = 0; i < l; ++i) {
            if (maskWithX.charAt(i) == 'X') {
                tBuilder.append('0');
            }
            else {
                tBuilder.append('F');
            }
        }
        return tBuilder.toString();
    }
}
