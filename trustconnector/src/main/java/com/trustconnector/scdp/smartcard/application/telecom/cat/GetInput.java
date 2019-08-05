package com.trustconnector.scdp.smartcard.application.telecom.cat;

import com.trustconnector.scdp.util.tlv.simpletlv.*;

public class GetInput extends ProactiveCommand
{
    protected TextString textString;
    protected ResponseLength responseLen;
    
    public GetInput(final byte[] cmd) {
        super(cmd);
        this.textString = (TextString)this.findTLV((byte)13);
        this.responseLen = (ResponseLength)this.findTLV((byte)17);
    }
    
    public String getText() {
        return this.textString.getText();
    }
    
    public byte getTextDCS() {
        return this.textString.getDCS();
    }
    
    public int getMinLength() {
        return this.responseLen.getMinLength();
    }
    
    public int getMaxLength() {
        return this.responseLen.getMaxLength();
    }
}
