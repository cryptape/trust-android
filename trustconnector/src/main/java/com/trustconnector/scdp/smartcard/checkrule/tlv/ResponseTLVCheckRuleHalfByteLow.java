package com.trustconnector.scdp.smartcard.checkrule.tlv;

import com.trustconnector.scdp.util.tlv.*;
import java.util.*;

public class ResponseTLVCheckRuleHalfByteLow extends ResponseTLVCheckRule
{
    public ResponseTLVCheckRuleHalfByteLow(final String name, final TagList tagPath, final int valueOff) {
        super(name, tagPath, valueOff);
    }
    
    public ResponseTLVCheckRuleHalfByteLow(final String name, final TagList tagPath, final int valueOff, final Map<String, String> valueInfoMap) {
        super(name, tagPath, valueOff);
    }
    
    @Override
    public boolean checkTLVValue(final byte[] value) {
        return false;
    }
}
