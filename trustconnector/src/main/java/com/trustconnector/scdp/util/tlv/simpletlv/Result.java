package com.trustconnector.scdp.util.tlv.simpletlv;

import com.trustconnector.scdp.util.tlv.*;
import com.trustconnector.scdp.util.tlv.bertlv.*;
import com.trustconnector.scdp.util.*;

public class Result extends SimpleTLV
{
    public static final byte PERFORMED_SUCCESS = 0;
    public static final byte TERMINATE = 16;
    
    public Result(final Tag tag, final Length len, final byte[] v, final int vOff) {
        super(tag, len, v, vOff);
    }
    
    public Result() {
        this.tag = new SimpleTag(3);
        this.len = new BERLength(1);
        (this.value = new ByteArray(1)).setByte(0, 0);
    }
    
    public Result(final int result) {
        this.tag = new SimpleTag(3);
        this.len = new BERLength(1);
        (this.value = new ByteArray(1)).setByte(0, result);
    }
    
    public Result(final int result, final byte[] additionalInfo, final int offset, final int length) {
        this.tag = new SimpleTag(3);
        int totalLen = 1;
        if (additionalInfo != null) {
            totalLen += length;
        }
        this.len = new BERLength(totalLen);
        (this.value = new ByteArray(totalLen)).setByte(0, result);
        this.value.setBytes(1, additionalInfo, offset, length);
    }
    
    public byte getResult() {
        return this.value.getByte(0);
    }
    
    public byte[] getAdditionalInfo() {
        if (this.value.length() > 1) {
            return this.value.toBytes(1, this.value.length() - 1);
        }
        return null;
    }
    
    public static String getResultDesc(final int result) {
        switch (result) {
            case 0: {
                return "Command performed successfully";
            }
            default: {
                return null;
            }
        }
    }
}
