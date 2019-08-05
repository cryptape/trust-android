package com.trustconnector.scdp.util.tlv.simpletlv;

import com.trustconnector.scdp.util.tlv.*;
import com.trustconnector.scdp.util.tlv.bertlv.*;
import com.trustconnector.scdp.util.*;

public class TextString extends SimpleTLV
{
    public static final int DCS_GSM_7_BIT = 0;
    public static final int DCS_GSM_8_BIT = 4;
    public static final int DCS_UCS2 = 8;
    
    public TextString(final Tag tag, final Length len, final byte[] v, final int vOff) {
        super(tag, len, v, vOff);
    }
    
    public TextString() {
        this.tag = new SimpleTag(13);
        this.len = new BERLength(0);
    }
    
    public TextString(final String text) {
        this.tag = new SimpleTag(13);
        (this.value = new ByteArray()).append((byte)8);
        this.value.append(text.getBytes());
        this.len = new BERLength(this.value.length());
    }
    
    public TextString(final byte[] value, final int dcs) {
        this.tag = new SimpleTag(13);
        (this.value = new ByteArray()).append((byte)dcs);
        this.value.append(value);
        this.len = new BERLength(this.value.length());
    }
    
    public byte getDCS() {
        return this.value.getByte(0);
    }
    
    public String getText() {
        final byte retDCS = this.getDCS();
        String retValue = null;
        final byte[] bV = this.value.toBytes(1, this.value.length() - 1);
        if (retDCS == 0) {
            retValue = ByteArray.convert(bV, StringFormat.ASCII_7_BIT);
        }
        else if (retDCS == 4) {
            retValue = ByteArray.convert(bV, StringFormat.ASCII);
        }
        else if (retDCS == 8) {
            retValue = ByteArray.convert(bV, StringFormat.UCS2);
        }
        return retValue;
    }
    
    @Override
    public String toString() {
        String res = "Text String:" + super.toString();
        res = res + "\n    -Text=" + this.getText();
        return res;
    }
}
