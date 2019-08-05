package libs.trustconnector.scdp.smartcard.checkrule;

import libs.trustconnector.scdp.*;
import libs.trustconnector.scdp.smartcard.*;
import java.util.*;

import libs.trustconnector.scdp.SCDP;
import libs.trustconnector.scdp.smartcard.APDU;
import libs.trustconnector.scdp.smartcard.APDUCheckException;
import libs.trustconnector.scdp.smartcard.APDUChecker;

public class APDURuleChecker implements APDUChecker
{
    protected List<CheckRule> ruleList;
    protected List<Boolean> ruleTypeList;
    protected int sw;
    protected SWCheckRule swcheck;
    
    public APDURuleChecker() {
        this.ruleList = new ArrayList<CheckRule>();
        this.ruleTypeList = new ArrayList<Boolean>();
    }
    
    @Override
    public void check(final APDU apdu) throws APDUCheckException {
        this.sw = apdu.getSW();
        boolean bFinalRes = true;
        final Iterator<CheckRule> ite = this.ruleList.iterator();
        final Iterator<Boolean> iteType = this.ruleTypeList.iterator();
        while (ite.hasNext()) {
            final CheckRule rule = ite.next();
            final Boolean type = iteType.next();
            if (rule.checkCondition(apdu)) {
                final boolean bCheckRes = rule.check(apdu);
                final String desc = rule.getRuleDescription();
                final boolean bhasExpect = rule.hasExpect();
                if (bCheckRes == type) {
                    if (bhasExpect) {
                        SCDP.addAPDUExpInfo(desc);
                    }
                    else {
                        if (desc == null || desc.length() <= 0) {
                            continue;
                        }
                        SCDP.addAPDUInfo(desc);
                    }
                }
                else {
                    SCDP.reportAPDUExpErr(desc);
                    bFinalRes = false;
                }
            }
        }
        if (!bFinalRes) {
            APDUCheckException.throwIt(4);
        }
    }
    
    public void addCheckRule(final CheckRule e) {
        this.ruleList.add(e);
        this.ruleTypeList.add(true);
    }
    
    public void addFalseCheckRule(final CheckRule e) {
        this.ruleList.add(e);
        this.ruleTypeList.add(false);
    }
    
    public void clearAllRule() {
        this.ruleList.clear();
        this.ruleTypeList.clear();
    }
    
    protected void swCheckInit() {
        if (this.swcheck == null) {
            this.addCheckRule(this.swcheck = new SWCheckRule());
        }
    }
    
    public void setCheckSW(final int sw) {
        this.swCheckInit();
        this.swcheck.setMatch(sw);
    }
    
    public void setCheckSW(final int sw, final int swMask) {
        this.swCheckInit();
        this.swcheck.setMatch(sw, swMask);
    }
    
    public void setCheckSW(final int[] sw) {
        this.swCheckInit();
        this.swcheck.setMatch(sw);
    }
    
    public void setCheckSW(final int[] sw, final int[] swMask) {
        this.swCheckInit();
        this.swcheck.setMatch(sw, swMask);
    }
    
    public void setCheckSW(final String sw) {
        this.swCheckInit();
        this.swcheck.setMatch(sw);
    }
    
    public int getSW() {
        return this.sw;
    }
}
