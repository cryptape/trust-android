package com.trustconnector.scdp.smartcard.application.edep;

import com.trustconnector.scdp.smartcard.application.*;
import org.jdom2.*;
import com.trustconnector.scdp.util.*;

public class EDEPKey implements Key
{
    private int keyType;
    private int index;
    private byte[] keyValue;
    public static final int KEY_TYPE_MK = 1;
    public static final int KEY_TYPE_EK = 2;
    public static final int KEY_TYPE_IK = 3;
    public static final int KEY_TYPE_DPK = 4;
    public static final int KEY_TYPE_DLK = 5;
    public static final int KEY_TYPE_DULK = 6;
    public static final int KEY_TYPE_DTK = 7;
    public static final int KEY_TYPE_DUK = 8;
    public static final int KEY_TYPE_DPUK = 9;
    public static final int KEY_TYPE_DRPK = 10;
    public static final int KEY_TYPE_DAMK = 11;
    public static final int KEY_TYPE_PIN = 12;
    public static final String KEY_TYPE_STR_MK = "MK";
    public static final String KEY_TYPE_STR_EK = "EK";
    public static final String KEY_TYPE_STR_IK = "IK";
    public static final String KEY_TYPE_STR_DPK = "DPK";
    public static final String KEY_TYPE_STR_DLK = "DLK";
    public static final String KEY_TYPE_STR_DULK = "DULK";
    public static final String KEY_TYPE_STR_DTK = "DTK";
    public static final String KEY_TYPE_STR_DUK = "DUK";
    public static final String KEY_TYPE_STR_DPUK = "DPUK";
    public static final String KEY_TYPE_STR_DRPK = "DRPK";
    public static final String KEY_TYPE_STR_DAMK = "DAMK";
    public static final String KEY_TYPE_STR_PIN = "PIN";
    static String[] keyArray;
    
    public EDEPKey(final Element keyNode) {
        final String keyTypeStr = keyNode.getAttributeValue("type");
        if (keyTypeStr.startsWith("0x")) {
            this.keyType = XMLUtil.getNodeAttrHex(keyNode, "type");
        }
        else {
            this.keyType = getKeyTypeByStr(keyTypeStr);
        }
        this.index = XMLUtil.getNodeAttrHex(keyNode, "id");
        this.keyValue = XMLUtil.getNodeAttrBytes(keyNode, "value");
    }
    
    public EDEPKey(final int keyType, final int index, final byte[] keyValue) {
        this.keyType = keyType;
        this.index = index;
        this.keyValue = keyValue.clone();
    }
    
    @Override
    public int getLength() {
        if (this.keyValue == null) {
            return 0;
        }
        return this.keyValue.length;
    }
    
    @Override
    public byte[] getValue() {
        return this.keyValue;
    }
    
    @Override
    public int getType() {
        return this.keyType;
    }
    
    @Override
    public Object getCookie() {
        return new Integer(this.index);
    }
    
    public static int getKeyTypeByStr(final String keyTypeStr) {
        for (int i = 0; i < EDEPKey.keyArray.length; ++i) {
            if (keyTypeStr.compareTo(EDEPKey.keyArray[i]) == 0) {
                return i + 1;
            }
        }
        return -1;
    }
    
    static {
        EDEPKey.keyArray = new String[] { "MK", "EK", "IK", "DPK", "DLK", "DULK", "DTK", "DUK", "DPUK", "DRPK", "DAMK", "PIN" };
    }
}
