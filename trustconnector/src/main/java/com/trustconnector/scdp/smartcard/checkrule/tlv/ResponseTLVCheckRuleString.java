package com.trustconnector.scdp.smartcard.checkrule.tlv;

import com.trustconnector.scdp.util.tlv.*;
import java.io.*;

public class ResponseTLVCheckRuleString extends ResponseTLVCheckRule
{
    public static final int DCS_OFF = 0;
    public static final byte ASCII_7Bit = 0;
    public static final byte ASCII = 4;
    public static final byte UCS2 = 8;
    
    public ResponseTLVCheckRuleString(final String name, final TagList tagPath) {
        super(name, tagPath, 0);
    }
    
    @Override
    public boolean checkTLVValue(final byte[] value) {
        this.retValue = "unknow encoding type";
        switch (value[0]) {
            case 0: {
                break;
            }
            case 4: {
                this.retValue = new String(value, 1, value.length - 1);
                break;
            }
            case Byte.MIN_VALUE:
            case 8: {
                try {
                    this.retValue = new String(value, 1, value.length - 1, "UnicodeBigUnmarked");
                }
                catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            }
            default: {
                this.retValue = "";
                break;
            }
        }
        return true;
    }
}
