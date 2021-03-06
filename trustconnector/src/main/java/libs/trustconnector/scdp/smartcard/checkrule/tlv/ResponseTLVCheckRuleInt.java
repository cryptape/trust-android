package libs.trustconnector.scdp.smartcard.checkrule.tlv;

import libs.trustconnector.scdp.util.tlv.*;
import java.util.*;

import libs.trustconnector.scdp.util.tlv.TagList;

public class ResponseTLVCheckRuleInt extends ResponseTLVCheckRule
{
    public ResponseTLVCheckRuleInt(final String name, final TagList tagPath, final int valueOff) {
        super(name, tagPath, valueOff);
    }
    
    public ResponseTLVCheckRuleInt(final String name, final TagList tagPath, final int valueOff, final Map<String, String> valueInfoMap) {
        super(name, tagPath, valueOff, valueInfoMap);
    }
    
    @Override
    public boolean checkTLVValue(final byte[] value) {
        return false;
    }
}
