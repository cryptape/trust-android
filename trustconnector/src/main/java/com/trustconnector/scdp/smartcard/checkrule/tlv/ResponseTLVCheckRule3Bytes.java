package com.trustconnector.scdp.smartcard.checkrule.tlv;

import com.trustconnector.scdp.util.tlv.*;
import java.util.*;

public class ResponseTLVCheckRule3Bytes extends ResponseTLVCheckRule
{
    public ResponseTLVCheckRule3Bytes(final String name, final TagList tagPath, final int valueOff) {
        super(name, tagPath, valueOff);
    }
    
    public ResponseTLVCheckRule3Bytes(final String name, final TagList tagPath, final int valueOff, final Map<String, String> valueInfoMap) {
        super(name, tagPath, valueOff, valueInfoMap);
    }
    
    @Override
    public boolean checkTLVValue(final byte[] value) {
        return false;
    }
}
