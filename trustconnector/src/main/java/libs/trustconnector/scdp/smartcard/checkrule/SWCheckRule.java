package libs.trustconnector.scdp.smartcard.checkrule;

import libs.trustconnector.scdp.smartcard.*;

import libs.trustconnector.scdp.smartcard.APDU;

public class SWCheckRule implements CheckRule
{
    protected String ruleDesc;
    protected String expDesc;
    protected boolean bCheckRes;
    protected int retSW;
    protected int checkType;
    protected int[] swExp;
    protected int[] swExpMask;
    protected static final int CHECK_TYPE_SW_MATCH_SPEC = 0;
    protected static final int CHECK_TYPE_SW_MATCH_LIST = 1;
    protected static final int CHECK_TYPE_SW_MATCH_SPEC_WITH_MASK = 2;
    protected static final int CHECK_TYPE_SW_MATCH_LIST_WITH_MASK = 3;
    
    @Override
    public String getRuleDescription() {
        return this.ruleDesc;
    }
    
    @Override
    public boolean checkCondition(final APDU apdu) {
        return true;
    }
    
    public int getReturnValue() {
        return this.retSW;
    }
    
    public String getRetureValue() {
        return String.format("%04X", this.retSW);
    }
    
    @Override
    public boolean check(final APDU apdu) {
        this.retSW = apdu.getSW();
        this.bCheckRes = false;
        switch (this.checkType) {
            case 0: {
                this.bCheckRes = (this.retSW == this.swExp[0]);
                break;
            }
            case 1: {
                for (int i = 0; i < this.swExp.length; ++i) {
                    if (this.retSW == this.swExp[i]) {
                        this.bCheckRes = true;
                        break;
                    }
                }
                break;
            }
            case 2: {
                this.bCheckRes = ((this.retSW & this.swExpMask[0]) == this.swExp[0]);
                break;
            }
            case 3: {
                for (int i = 0; i < this.swExp.length; ++i) {
                    if ((this.retSW & this.swExpMask[i]) == this.swExp[i]) {
                        this.bCheckRes = true;
                        break;
                    }
                }
                break;
            }
        }
        final String retSWS = String.format("%04X", this.retSW);
        if (!this.bCheckRes) {
            this.ruleDesc = "SW Ret=" + retSWS + ",Exp=" + this.expDesc + ",Check Failed";
        }
        else {
            this.ruleDesc = "SW Ret=" + retSWS + ",Exp=" + this.expDesc + ",Check Pass";
        }
        return this.bCheckRes;
    }
    
    public void setMatch(final String expSW) {
        this.expDesc = expSW;
        if (expSW.indexOf(124) == -1) {
            if (expSW.indexOf(88) == -1) {
                this.checkType = 0;
                (this.swExp = new int[1])[0] = Integer.valueOf(expSW, 16);
            }
            else {
                this.checkType = 2;
                this.swExp = new int[1];
                this.swExpMask = new int[1];
                String expSWStr = expSW.replace('X', '0');
                this.swExp[0] = Integer.valueOf(expSWStr, 16);
                expSWStr = this.buildMask(expSW);
                this.swExpMask[0] = Integer.valueOf(expSWStr, 16);
            }
        }
        else if (expSW.indexOf(88) == -1) {
            this.checkType = 1;
            final String[] expSWs = expSW.split("\\|");
            this.swExp = new int[expSWs.length];
            for (int i = 0; i < this.swExp.length; ++i) {
                this.swExp[i] = Integer.valueOf(expSW, 16);
            }
        }
        else {
            this.checkType = 3;
            final String[] expSWs = expSW.split("\\|");
            this.swExp = new int[expSWs.length];
            this.swExpMask = new int[expSWs.length];
            for (int i = 0; i < this.swExp.length; ++i) {
                String expSWStr2 = expSWs[i].replace('X', '0');
                this.swExp[i] = Integer.valueOf(expSWStr2, 16);
                expSWStr2 = this.buildMask(expSWs[i]);
                this.swExpMask[i] = Integer.valueOf(expSWStr2, 16);
            }
        }
    }
    
    public void setMatch(final int sw) {
        this.checkType = 0;
        this.expDesc = String.format("%04X", sw);
        (this.swExp = new int[1])[0] = sw;
    }
    
    public void setMatch(final int sw, final int swMask) {
        this.checkType = 2;
        this.expDesc = String.format("%04X,Mask=%04X", sw, swMask);
        (this.swExp = new int[1])[0] = sw;
        (this.swExpMask = new int[1])[0] = swMask;
    }
    
    public void setMatch(final int[] sw) {
        this.checkType = 1;
        this.expDesc = String.format("%04X", sw[0]);
        for (int i = 1; i < sw.length; ++i) {
            this.expDesc = String.format("|%04X", sw[i]);
        }
        this.swExp = sw.clone();
    }
    
    public void setMatch(final int[] sw, final int[] swMask) {
        this.checkType = 2;
        this.expDesc = String.format("%04X,Mask=%04X", sw);
        this.swExp = sw.clone();
        this.swExpMask = swMask.clone();
    }
    
    String buildMask(final String maskWithX) {
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
    
    @Override
    public boolean hasExpect() {
        return true;
    }
}
