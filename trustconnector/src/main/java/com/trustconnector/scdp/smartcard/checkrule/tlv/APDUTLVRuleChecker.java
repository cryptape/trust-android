package com.trustconnector.scdp.smartcard.checkrule.tlv;

import com.trustconnector.scdp.smartcard.checkrule.*;
import com.trustconnector.scdp.*;
import com.trustconnector.scdp.util.tlv.bertlv.*;
import com.trustconnector.scdp.util.tlv.*;
import com.trustconnector.scdp.smartcard.*;
import java.util.*;

public class APDUTLVRuleChecker implements APDUChecker
{
    protected int sw;
    protected SWCheckRule swcheck;
    protected List<TLVCheckRule> ruleList;
    protected List<Boolean> ruleTypeList;
    
    public APDUTLVRuleChecker() {
        this.ruleList = new ArrayList<TLVCheckRule>();
        this.ruleTypeList = new ArrayList<Boolean>();
    }
    
    @Override
    public void check(final APDU apdu) throws APDUCheckException {
        this.sw = apdu.getSW();
        boolean bFinalRes = true;
        if (this.swcheck != null && this.swcheck.checkCondition(apdu)) {
            final boolean bCheckRes = this.swcheck.check(apdu);
            final String desc = this.swcheck.getRuleDescription();
            final boolean bhasExpect = this.swcheck.hasExpect();
            if (bCheckRes) {
                if (bhasExpect) {
                    SCDP.addAPDUExpInfo(desc);
                }
                else if (desc != null && desc.length() > 0) {
                    SCDP.addAPDUInfo(desc);
                }
            }
            else {
                bFinalRes = false;
                SCDP.reportAPDUExpErr(desc);
            }
        }
        final byte[] rdata = apdu.getRData();
        if (rdata != null) {
            final TLVTree tree = new TLVTree();
            if (tree.fromBytes(rdata, 0, rdata.length, new BERTLVParser()) == rdata.length) {
                final Iterator<TLVCheckRule> ite = this.ruleList.iterator();
                final Iterator<Boolean> iteType = this.ruleTypeList.iterator();
                while (ite.hasNext()) {
                    final TLVCheckRule rule = ite.next();
                    final Boolean type = iteType.next();
                    if (rule.checkCondition(tree)) {
                        final boolean bCheckRes2 = rule.checkTLV(tree);
                        final String desc2 = rule.getRuleDescription();
                        final boolean bhasExpect2 = rule.hasExpect();
                        if (bCheckRes2 == type) {
                            if (bhasExpect2) {
                                SCDP.addAPDUExpInfo(desc2);
                            }
                            else {
                                if (desc2 == null || desc2.length() <= 0) {
                                    continue;
                                }
                                SCDP.addAPDUInfo(desc2);
                            }
                        }
                        else {
                            bFinalRes = false;
                            SCDP.reportAPDUExpErr(desc2);
                        }
                    }
                }
            }
            else {
                bFinalRes = false;
                SCDP.reportAPDUExpErr("Expect TLV format Reponse but no Reponse return");
            }
        }
        else {
            bFinalRes = false;
            SCDP.reportAPDUExpErr("Expect TLV format Reponse but no Reponse return");
        }
        if (!bFinalRes) {
            APDUCheckException.throwIt(4);
        }
    }
    
    public void addCheckRule(final TLVCheckRule e) {
        this.ruleList.add(e);
        this.ruleTypeList.add(true);
    }
    
    public void addFalseCheckRule(final TLVCheckRule e) {
        this.ruleList.add(e);
        this.ruleTypeList.add(false);
    }
    
    public void clearAllRule() {
        this.ruleList.clear();
        this.ruleTypeList.clear();
    }
    
    protected void swCheckInit() {
        if (this.swcheck == null) {
            this.swcheck = new SWCheckRule();
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
