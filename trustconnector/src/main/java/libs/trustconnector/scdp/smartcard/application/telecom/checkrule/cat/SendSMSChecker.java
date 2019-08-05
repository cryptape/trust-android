package libs.trustconnector.scdp.smartcard.application.telecom.checkrule.cat;

import libs.trustconnector.scdp.util.tlv.simpletlv.*;
import libs.trustconnector.scdp.smartcard.application.telecom.cat.*;

import libs.trustconnector.scdp.smartcard.application.telecom.cat.SendSMS;
import libs.trustconnector.scdp.util.tlv.simpletlv.TPDU;
import libs.trustconnector.scdp.util.tlv.simpletlv.TPDU_CDMA;

public class SendSMSChecker extends ProactiveCommandChecker
{
    public TPDU tpdu;
    
    public SendSMSChecker() {
        super(19, -127, -125);
    }
    
    @Override
    public boolean check() {
        final boolean bCheckRes = true;
        this.tpdu = this.getReturnTPDU();
        return bCheckRes;
    }
    
    public TPDU_CDMA getReturnTPDU_CDMA() {
        final SendSMS sendSMS = (SendSMS)this.command;
        return sendSMS.getTPDU_CDMA();
    }
    
    public TPDU getReturnTPDU() {
        final SendSMS sendSMS = (SendSMS)this.command;
        return sendSMS.getTPDU();
    }
}
