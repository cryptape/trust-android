package libs.trustconnector.scdp.smartcard.checkrule.tlv;

import libs.trustconnector.scdp.util.tlv.*;

import libs.trustconnector.scdp.util.tlv.TLVTree;

public interface TLVCheckRuleCondition
{
    boolean checkCondition(final TLVTree p0);
}
