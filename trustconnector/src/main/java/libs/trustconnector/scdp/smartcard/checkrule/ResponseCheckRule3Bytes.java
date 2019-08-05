package libs.trustconnector.scdp.smartcard.checkrule;

import java.util.*;
import libs.trustconnector.scdp.util.*;

import libs.trustconnector.scdp.util.ByteArray;

public class ResponseCheckRule3Bytes extends ResponseCheckRule
{
    protected int retVlue;
    protected int expValueT;
    protected int expValueM;
    
    public ResponseCheckRule3Bytes(final String name, final int byteOff) {
        super(name, byteOff);
    }
    
    public ResponseCheckRule3Bytes(final String name, final int byteOff, final Map<String, String> infoValueMap) {
        super(name, byteOff, infoValueMap);
    }
    
    @Override
    public boolean checkRdata(final byte[] rdata) {
        this.retVlue = ((rdata[this.byteOff] & 0xFF) << 16 | (rdata[this.byteOff + 1] & 0xFF) << 8 | (rdata[this.byteOff + 3] & 0xFF));
        super.retValue = ByteArray.convert(rdata, this.byteOff, 3);
        return !this.matchSet || (this.expValueT & this.expValueM) == this.retVlue;
    }
    
    public void setMatch(final int valueExp) {
        this.expValueT = valueExp;
        this.expValueM = 16777215;
        this.matchSet = true;
        this.expValue = String.format("%06X", valueExp);
    }
    
    public void setMatch(final int valueExp, final int mask) {
        this.expValueT = valueExp;
        this.expValueM = mask;
        this.matchSet = true;
        this.dataMask = String.format("%06X", mask);
        this.expValue = String.format("%06X", valueExp);
    }
    
    public int getReturnValue() {
        return this.retVlue;
    }
}
