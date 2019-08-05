package com.trustconnector.scdp.smartcard.application.telecom.cat;

import com.trustconnector.scdp.util.tlv.simpletlv.*;

public class SendSMS extends ProactiveCommand
{
    protected TPDU tpdu;
    protected TPDU_CDMA tpdu_cdma;
    
    public SendSMS(final byte[] cmd) {
        super(cmd);
        this.tpdu = (TPDU)this.findTLV((byte)11);
        this.tpdu_cdma = (TPDU_CDMA)this.findTLV((byte)72);
    }
    
    public TPDU getTPDU() {
        return this.tpdu;
    }
    
    public TPDU_CDMA getTPDU_CDMA() {
        return this.tpdu_cdma;
    }
}
