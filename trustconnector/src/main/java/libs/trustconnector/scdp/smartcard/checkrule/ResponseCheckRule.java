package libs.trustconnector.scdp.smartcard.checkrule;

import libs.trustconnector.scdp.smartcard.*;
import java.util.*;

import libs.trustconnector.scdp.smartcard.APDU;

public abstract class ResponseCheckRule implements CheckRule
{
    protected String name;
    protected String ruleDesc;
    protected String retValue;
    protected String expValue;
    protected String dataMask;
    protected int byteOff;
    protected boolean checkRes;
    protected Map<String, String> valueInfoMap;
    protected boolean matchSet;
    protected List<CheckRuleCondition> checkConditionList;
    protected List<Boolean> checkConditionListType;
    
    public ResponseCheckRule(final String name, final int byteOff) {
        this.checkConditionList = new ArrayList<CheckRuleCondition>();
        this.checkConditionListType = new ArrayList<Boolean>();
        this.name = name;
        this.byteOff = byteOff;
    }
    
    public ResponseCheckRule(final String name, final int byteOff, final Map<String, String> valueInfoMap) {
        this.checkConditionList = new ArrayList<CheckRuleCondition>();
        this.checkConditionListType = new ArrayList<Boolean>();
        this.name = name;
        this.byteOff = byteOff;
        this.valueInfoMap = valueInfoMap;
    }
    
    @Override
    public boolean check(final APDU apdu) {
        final byte[] rdata = apdu.getRData();
        this.checkRes = false;
        if (rdata != null && rdata.length >= this.byteOff) {
            this.checkRes = this.checkRdata(rdata);
            this.ruleDesc = this.name + "=" + this.retValue;
            String valueDesc = null;
            if (this.valueInfoMap != null) {
                valueDesc = this.valueInfoMap.get(this.retValue);
            }
            if (valueDesc != null) {
                this.ruleDesc = this.ruleDesc + "[" + valueDesc + "]";
            }
            if (this.matchSet && !this.checkRes) {
                this.ruleDesc = this.ruleDesc + ",check fail,Expect=" + this.expValue;
                if (this.dataMask != null) {
                    this.ruleDesc = this.ruleDesc + ",Mask=" + this.dataMask;
                }
                this.ruleDesc = this.ruleDesc + ",Offset=" + String.format("%04X", this.byteOff);
            }
            return this.checkRes;
        }
        if (this.matchSet) {
            this.ruleDesc = this.name + " not found!check failed";
            return this.checkRes;
        }
        return true;
    }
    
    public abstract boolean checkRdata(final byte[] p0);
    
    @Override
    public boolean checkCondition(final APDU apdu) {
        final Iterator<CheckRuleCondition> ite = this.checkConditionList.iterator();
        final Iterator<Boolean> iteType = this.checkConditionListType.iterator();
        while (ite.hasNext()) {
            final CheckRuleCondition rule = ite.next();
            final Boolean b = iteType.next();
            if (b != rule.checkCondition(apdu)) {
                return false;
            }
        }
        return true;
    }
    
    public void addCondition(final CheckRuleCondition condition) {
        this.checkConditionList.add(condition);
        this.checkConditionListType.add(new Boolean(true));
    }
    
    public void addFalseCondition(final CheckRuleCondition condition) {
        this.checkConditionList.add(condition);
        this.checkConditionListType.add(new Boolean(false));
    }
    
    @Override
    public String getRuleDescription() {
        return this.ruleDesc;
    }
    
    @Override
    public boolean hasExpect() {
        return this.matchSet;
    }
}
