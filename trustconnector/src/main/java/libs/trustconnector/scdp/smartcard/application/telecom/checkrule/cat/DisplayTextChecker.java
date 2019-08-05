package libs.trustconnector.scdp.smartcard.application.telecom.checkrule.cat;

import libs.trustconnector.scdp.smartcard.application.telecom.cat.*;
import libs.trustconnector.scdp.*;

import libs.trustconnector.scdp.SCDP;
import libs.trustconnector.scdp.smartcard.application.telecom.cat.DisplayText;

public class DisplayTextChecker extends ProactiveCommandChecker
{
    protected int expMask;
    protected static final int EXP_MASK_TEXT = 1;
    protected static final int EXP_MASK_DCS = 2;
    protected String retValue;
    protected int retDCS;
    protected String expValue;
    protected int expDCS;
    
    public DisplayTextChecker() {
        super(33, -127, 2, "00|01|80|81");
    }
    
    public DisplayTextChecker(final int qualifilier) {
        super(33, -127, 2, qualifilier);
    }
    
    public DisplayTextChecker(final String text) {
        super(33, -127, 2, "00|01|80|81");
        this.setMatchTextString(text);
    }
    
    public DisplayTextChecker(final int qualifilier, final String text) {
        super(33, -127, 2, qualifilier);
        this.setMatchTextString(text);
    }
    
    public DisplayTextChecker(final int qualifilier, final String text, final int dcs) {
        super(33, -127, 2, qualifilier);
        this.setMatchTextString(text, dcs);
    }
    
    @Override
    public boolean check() {
        boolean bCheckRes = true;
        final DisplayText dp = (DisplayText)this.command;
        final byte dcs = dp.getDCS();
        this.retValue = dp.getText();
        if (dcs != 4 && dcs != 4 && dcs != 8) {
            bCheckRes = false;
            SCDP.reportAPDUExpErr("DCS Check Failed, ret=0x" + String.format("%02x", dcs) + ",exp=00|04|08");
        }
        if ((this.expMask & 0x1) == 0x1) {
            if (this.retValue.compareTo(this.expValue) != 0) {
                bCheckRes = false;
                SCDP.reportAPDUExpErr("Text Check Failed, ret=" + this.retValue + ",exp=" + this.expValue);
            }
            else {
                SCDP.addAPDUExpInfo("Text=" + dp.getText());
            }
        }
        if ((this.expMask & 0x2) == 0x2) {
            if (dcs != this.expDCS) {
                bCheckRes = false;
                SCDP.reportAPDUExpErr("DCS Check Failed, ret=0x" + String.format("%02x", dcs) + ",exp=0x" + String.format("%02x", dcs));
            }
            else {
                SCDP.addAPDUExpInfo("Text=" + dp.getText());
            }
        }
        return bCheckRes;
    }
    
    public void setMatchTextString(final String text) {
        this.expValue = text;
        this.expMask |= 0x1;
    }
    
    public void setMatchTextString(final String text, final int dcs) {
        this.expValue = text;
        this.expMask |= 0x1;
        this.expDCS = dcs;
        this.expMask |= 0x2;
    }
}
