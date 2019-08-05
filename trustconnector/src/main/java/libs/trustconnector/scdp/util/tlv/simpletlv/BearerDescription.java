package libs.trustconnector.scdp.util.tlv.simpletlv;

import libs.trustconnector.scdp.util.tlv.*;
import libs.trustconnector.scdp.util.*;

import libs.trustconnector.scdp.util.ByteArray;
import libs.trustconnector.scdp.util.tlv.Length;
import libs.trustconnector.scdp.util.tlv.Tag;

public class BearerDescription extends SimpleTLV
{
    public static final byte TYPE_SMS = 0;
    public static final byte TYPE_CIRCUIT_SWITCHED_DATA = 1;
    public static final byte TYPE_DEFAULT_BEARER = 3;
    public static final byte TYPE_LOCAL_LINK = 4;
    public static final byte TYPE_BLUETOOTH = 5;
    public static final byte TYPE_IrDA = 6;
    public static final byte TYPE_RS232 = 7;
    public static final byte TYPE_USB = 16;
    
    public BearerDescription(final Tag tag, final Length len, final byte[] v, final int vOff) {
        super(tag, len, v, vOff);
    }
    
    public BearerDescription(final byte bearerType) {
        this(bearerType, null);
    }
    
    public BearerDescription(final byte bearerType, final byte[] bearerParam) {
        super(53);
        int totalLen = 1;
        if (bearerParam != null) {
            totalLen += bearerParam.length;
        }
        final byte[] v = new byte[totalLen];
        System.arraycopy(bearerParam, 0, v, 1, totalLen - 1);
        v[0] = bearerType;
        this.appendValue(v);
    }
    
    public byte getBearerType() {
        return this.value.getByte(0);
    }
    
    public byte[] getBearerParam() {
        return this.value.toBytes(1, this.value.length() - 1);
    }
    
    @Override
    public String toString() {
        String res = "Bearer Type=";
        res += String.format("%02X", this.value.getByte(0));
        if (this.value.length() > 1) {
            res += "\r\nBearer Param=";
            res += ByteArray.convert(this.value.right(this.value.length() - 1));
        }
        return res;
    }
}
