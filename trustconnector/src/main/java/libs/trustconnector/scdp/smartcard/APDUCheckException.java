package libs.trustconnector.scdp.smartcard;

import libs.trustconnector.scdp.*;

import libs.trustconnector.scdp.SCDPException;

public class APDUCheckException extends SCDPException
{
    private static final long serialVersionUID = 4745791931353843714L;
    private int failCode;
    public static final int FC_MASK_DATA_CHECK_FAILED = 1;
    public static final int FC_MASK_SW_CHECK_FAILED = 2;
    public static final int FC_MASK_RULE_CHECK_FAILED = 4;
    
    public APDUCheckException(final int failCode) {
        super("check faild");
        this.failCode = failCode;
        this.message = this.getFailDesc();
    }
    
    public String getFailDesc() {
        String failDesc = "";
        if ((this.failCode & 0x1) == 0x1) {
            failDesc += "data check failed";
        }
        if ((this.failCode & 0x2) == 0x2) {
            failDesc += "sw check failed";
        }
        if ((this.failCode & 0x4) == 0x4) {
            failDesc += "rule check failed";
        }
        return failDesc;
    }
    
    public int getFailCode() {
        return this.failCode;
    }
    
    public static void throwIt(final int failCode) {
        throw new APDUCheckException(failCode);
    }
}
