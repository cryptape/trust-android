package libs.trustconnector.scdp.smartcard.application.telecom.checkrule.cat;

import libs.trustconnector.scdp.smartcard.application.telecom.cat.*;
import libs.trustconnector.scdp.*;

import libs.trustconnector.scdp.SCDP;
import libs.trustconnector.scdp.smartcard.application.telecom.cat.GetInput;

public class GetInputChecker extends ProactiveCommandChecker
{
    protected String expText;
    protected int expTextDCS;
    protected int expRspMinLen;
    protected int expRspMaxLen;
    protected int expMask;
    protected static final int EXP_MASK_TEXT = 1;
    protected static final int EXP_MASK_DCS = 2;
    protected static final int EXP_MASK_LEN = 4;
    
    public GetInputChecker() {
        super(35, -127, -126, "00|01|02|04|05|06|07|08|09|0A|0B|0C|0D|0E|0F|80|81|82|84|85|86|87|88|89|8A|8B|8C|8D|8E|8F|");
    }
    
    public GetInputChecker(final String text) {
        super(35, -127, -126, "00|01|02|04|05|06|07|08|09|0A|0B|0C|0D|0E|0F|80|81|82|84|85|86|87|88|89|8A|8B|8C|8D|8E|8F|");
        this.setMatchText(text);
    }
    
    public GetInputChecker(final String text, final int responseLen) {
        super(35, -127, -126, "00|01|02|04|05|06|07|08|09|0A|0B|0C|0D|0E|0F|80|81|82|84|85|86|87|88|89|8A|8B|8C|8D|8E|8F|");
        this.setMatchText(text);
        this.setMatchResponseLen(responseLen);
    }
    
    public GetInputChecker(final String text, final int responseMinLen, final int responseMaxLen) {
        super(35, -127, -126, "00|01|02|04|05|06|07|08|09|0A|0B|0C|0D|0E|0F|80|81|82|84|85|86|87|88|89|8A|8B|8C|8D|8E|8F|");
        this.setMatchText(text);
        this.setMatchResponseLen(responseMinLen, responseMaxLen);
    }
    
    public GetInputChecker(final int qualifier, final String text, final int responseLen) {
        super(35, -127, -126, qualifier);
        this.setMatchText(text);
        this.setMatchResponseLen(responseLen);
    }
    
    public GetInputChecker(final int qualifier, final String text, final int dcs, final int responseMinLen, final int responseMaxLen) {
        super(35, -127, -126, qualifier);
        this.setMatchText(text, dcs);
        this.setMatchResponseLen(responseMinLen, responseMaxLen);
    }
    
    @Override
    public boolean check() {
        boolean bCheckRes = true;
        final GetInput getInput = (GetInput)this.command;
        if ((this.expMask & 0x2) == 0x2) {
            final int retValue = getInput.getTextDCS();
            if (retValue != this.expTextDCS) {
                bCheckRes = false;
                SCDP.reportAPDUExpErr("Text DCS Check Failed, ret=" + retValue + ",exp=" + this.expTextDCS);
            }
            else {
                SCDP.addAPDUExpInfo("Text DCS=" + this.expTextDCS);
            }
        }
        if ((this.expMask & 0x1) == 0x1) {
            final String ret = getInput.getText();
            if (ret.compareTo(this.expText) != 0) {
                bCheckRes = false;
                SCDP.reportAPDUExpErr("Text Check Failed, ret=" + ret + ",exp=" + this.expText);
            }
            else {
                SCDP.addAPDUExpInfo("Text =" + this.expTextDCS);
            }
        }
        if ((this.expMask & 0x4) == 0x4) {
            final int min = getInput.getMinLength();
            final int max = getInput.getMaxLength();
            if (min != this.expRspMinLen) {
                bCheckRes = false;
                SCDP.reportAPDUExpErr("Min Length Check Failed, ret=" + min + ",exp=" + this.expRspMinLen);
            }
            else {
                SCDP.addAPDUExpInfo("Min Length =" + this.expTextDCS);
            }
            if (max != this.expRspMaxLen) {
                bCheckRes = false;
                SCDP.reportAPDUExpErr("Max Length Check Failed, ret=" + max + ",exp=" + this.expRspMaxLen);
            }
            else {
                SCDP.addAPDUExpInfo("Max Length =" + this.expTextDCS);
            }
        }
        return bCheckRes;
    }
    
    public void setMatchText(final String text) {
        this.expText = text;
        this.expMask |= 0x1;
    }
    
    public void setMatchText(final String text, final int dcs) {
        this.expText = text;
        this.expMask |= 0x1;
        this.expTextDCS = dcs;
        this.expMask |= 0x2;
    }
    
    public void setMatchResponseLen(final int expLen) {
        this.expRspMinLen = expLen;
        this.expRspMaxLen = expLen;
        this.expMask |= 0x4;
    }
    
    public void setMatchResponseLen(final int expMin, final int expMax) {
        this.expRspMinLen = expMin;
        this.expRspMaxLen = expMax;
        this.expMask |= 0x4;
    }
}
