package com.trustconnector.scdp.smartcard.checkrule.tlv;

import java.util.*;
import com.trustconnector.scdp.util.tlv.*;

public abstract class ResponseTLVCheckRule implements TLVCheckRule
{
    protected byte[] retTLVValue;
    protected boolean matchSet;
    protected String name;
    protected TagList tagPath;
    protected String desc;
    protected String retValue;
    protected String expValue;
    protected String dataMask;
    protected int valueOff;
    protected boolean checkRes;
    protected Map<String, String> valueInfoMap;
    protected List<TLVCheckRuleCondition> checkConditionList;
    protected List<Boolean> checkConditionListType;
    
    public ResponseTLVCheckRule(final String name, final TagList tagPath, final int valueOff) {
        this.checkConditionList = new ArrayList<TLVCheckRuleCondition>();
        this.checkConditionListType = new ArrayList<Boolean>();
        this.name = name;
        this.tagPath = tagPath;
        this.valueOff = valueOff;
    }
    
    public ResponseTLVCheckRule(final String name, final TagList tagPath, final int valueOff, final Map<String, String> tlvValueInfoMap) {
        this.checkConditionList = new ArrayList<TLVCheckRuleCondition>();
        this.checkConditionListType = new ArrayList<Boolean>();
        this.name = name;
        this.tagPath = tagPath;
        this.valueOff = valueOff;
        this.valueInfoMap = tlvValueInfoMap;
    }
    
    @Override
    public String getRuleDescription() {
        return this.desc;
    }
    
    @Override
    public boolean hasExpect() {
        return this.matchSet;
    }
    
    @Override
    public boolean checkCondition(final TLVTree tlvTree) {
        final Iterator<TLVCheckRuleCondition> ite = this.checkConditionList.iterator();
        final Iterator<Boolean> iteType = this.checkConditionListType.iterator();
        while (ite.hasNext()) {
            final TLVCheckRuleCondition rule = ite.next();
            final Boolean b = iteType.next();
            if (b != rule.checkCondition(tlvTree)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean checkTLV(final TLVTree tlvTree) {
        final TLVTreeItem item = tlvTree.findTLV(this.tagPath);
        if (item == null) {
            if (this.matchSet) {
                this.desc = "Tag not found:name=" + this.name + ",tag path=" + this.tagPath;
            }
            return !this.matchSet;
        }
        final byte[] v = item.getValue();
        this.checkRes = this.checkTLVValue(v);
        this.desc = this.name + "=" + this.retValue;
        String valueDesc = null;
        if (this.valueInfoMap != null) {
            valueDesc = this.valueInfoMap.get(this.retValue);
        }
        if (valueDesc != null) {
            this.desc = this.desc + "[" + valueDesc + "]";
        }
        if (this.matchSet && !this.checkRes) {
            this.desc = this.desc + ",check fail,Expect=" + this.expValue;
            if (this.dataMask != null) {
                this.desc = this.desc + ",Mask=" + this.dataMask;
            }
            this.desc = this.desc + ",Offset=" + String.format("%04X", this.valueOff);
        }
        return this.checkRes;
    }
    
    public void addCondition(final TLVCheckRuleCondition condition) {
        this.checkConditionList.add(condition);
        this.checkConditionListType.add(new Boolean(true));
    }
    
    public void addFalseCondition(final TLVCheckRuleCondition condition) {
        this.checkConditionList.add(condition);
        this.checkConditionListType.add(new Boolean(false));
    }
    
    public abstract boolean checkTLVValue(final byte[] p0);
    
    public byte[] getValue() {
        return this.retTLVValue;
    }
}
