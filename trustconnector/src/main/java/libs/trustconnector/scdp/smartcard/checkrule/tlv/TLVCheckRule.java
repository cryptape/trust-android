package libs.trustconnector.scdp.smartcard.checkrule.tlv;

import libs.trustconnector.scdp.util.tlv.*;

import libs.trustconnector.scdp.util.tlv.TLVTree;

public interface TLVCheckRule
{
    boolean checkCondition(final TLVTree p0);
    
    boolean checkTLV(final TLVTree p0);
    
    String getRuleDescription();
    
    boolean hasExpect();
}
