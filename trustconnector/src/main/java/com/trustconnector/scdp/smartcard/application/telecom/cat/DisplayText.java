package com.trustconnector.scdp.smartcard.application.telecom.cat;

import com.trustconnector.scdp.util.tlv.simpletlv.*;

public class DisplayText extends ProactiveCommand
{
    protected TextString textString;
    
    public DisplayText(final byte[] cmd) {
        super(cmd);
        this.textString = (TextString)this.findTLV((byte)13);
    }
    
    public String getText() {
        return this.textString.getText();
    }
    
    public byte getDCS() {
        return this.textString.getDCS();
    }
}
