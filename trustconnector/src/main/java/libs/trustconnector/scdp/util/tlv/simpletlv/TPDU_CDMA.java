package libs.trustconnector.scdp.util.tlv.simpletlv;

import libs.trustconnector.scdp.util.tlv.*;
import libs.trustconnector.scdp.util.*;
import libs.trustconnector.scdp.util.tlv.bertlv.*;

import libs.trustconnector.scdp.util.ByteArray;
import libs.trustconnector.scdp.util.tlv.Length;
import libs.trustconnector.scdp.util.tlv.TLV;
import libs.trustconnector.scdp.util.tlv.TLVList;
import libs.trustconnector.scdp.util.tlv.Tag;
import libs.trustconnector.scdp.util.tlv.bertlv.BERTLV;
import libs.trustconnector.scdp.util.tlv.bertlv.BERTag;

public class TPDU_CDMA extends SimpleTLV
{
    protected byte type;
    protected TLVList params;
    public static final byte SMS_MSG_TYPE_P2P = 0;
    public static final byte SMS_MSG_TYPE_BROADCAST = 1;
    public static final byte SMS_MSG_TYPE_ACK = 2;
    public static final byte PARAM_TELESERVICE_IDENTIFIER = 0;
    public static final byte PARAM_SERVICE_CATEGORY = 1;
    public static final byte PARAM_ORGIGINATING_ADDRESS = 2;
    public static final byte PARAM_ORGIGINATING_SUBADDRESS = 3;
    public static final byte PARAM_DESTINATION_ADDRESS = 4;
    public static final byte PARAM_DESTINATION_SUBADDRESS = 5;
    public static final byte PARAM_BEARER_REPLY_OPTION = 6;
    public static final byte PARAM_CAUSE_CODE = 7;
    public static final byte PARAM_BEARER_DATA = 8;
    public static final byte BEARER_DATA_PARAM_MESSAGE_IDENTIFIER = 0;
    public static final byte BEARER_DATA_PARAM_USER_DATA = 1;
    public static final byte BEARER_DATA_PARAM_USER_RESPONSE_CODE = 2;
    public static final byte BEARER_DATA_PARAM_MESSAGE_CENTER_TIME_STAMP = 3;
    public static final byte BEARER_DATA_PARAM_VALIDITY_PERIOD_ABSOLUTE = 4;
    public static final byte BEARER_DATA_PARAM_VALIDITY_PERIOD_RELATIVE = 5;
    public static final byte BEARER_DATA_PARAM_DEFERRED_DELIVERY_TIME_ABSOLUTE = 6;
    public static final byte BEARER_DATA_PARAM_DEFERRED_DELIVERY_TIME_RELATIVE = 7;
    public static final byte BEARER_DATA_PARAM_PRIORITY_INDICATOR = 8;
    public static final byte BEARER_DATA_PARAM_PRIVACY_INDICATOR = 9;
    public static final byte BEARER_DATA_PARAM_REPLY_OPTION = 10;
    public static final byte BEARER_DATA_PARAM_NUMBER_OF_MESSAGES = 11;
    public static final byte BEARER_DATA_PARAM_ALERT_ON_MESSAGE_DELIVERY = 12;
    public static final byte BEARER_DATA_PARAM_LANGUAGE_INDICATOR = 13;
    public static final byte BEARER_DATA_PARAM_CALL_BACK_NUMBER = 14;
    public static final byte BEARER_DATA_PARAM_MESSAGE_DISPLAY_MODE = 15;
    public static final byte BEARER_DATA_PARAM_MULTIPLE_ENCODING_USER_DATA = 16;
    public static final byte BEARER_DATA_PARAM_MESSAGE_DEPOSIT_INDEX = 17;
    public static final byte BEARER_DATA_PARAM_SERVICE_CATEGORY_PROGRAM_DATA = 18;
    public static final byte BEARER_DATA_PARAM_SERVICE_CATEGORY_PROGRAM_RESULTS = 19;
    public static final byte BEARER_DATA_PARAM_MESSAGE_STATUS = 20;
    public static final byte BEARER_DATA_PARAM_TP_FAILURE_CAUSE = 21;
    public static final byte BEARER_DATA_PARAM_ENHANCED_VMN = 22;
    public static final byte BEARER_DATA_PARAM_ENHANCED_VMN_ACK = 23;
    public static final byte MESSAGE_TYPE_DELIVER = 1;
    public static final byte MESSAGE_TYPE_SUBMIT = 2;
    public static final byte MESSAGE_TYPE_CANCELLATION = 3;
    public static final byte MESSAGE_TYPE_DELIVERY_ACKNOWLEDGMENT = 4;
    public static final byte MESSAGE_TYPE_USER_ACKNOWLEDGMENT = 5;
    public static final byte MESSAGE_TYPE_READ_ACKNOWLEDGMENT = 6;
    public static final byte MESSAGE_TYPE_DELIVER_REPORT = 7;
    public static final byte MESSAGE_TYPE_SUBMIT_REPORT = 8;
    
    public TPDU_CDMA() {
        super(72);
        this.updateValue(new byte[1]);
        this.params = new TLVList();
    }
    
    public TPDU_CDMA(final byte type) {
        super(72);
        final byte[] a = { type };
        this.updateValue(a);
        this.params = new TLVList();
    }
    
    public TPDU_CDMA(final byte[] tpdu) {
        super(tpdu[0]);
        this.updateValue(tpdu, 1, tpdu.length - 2);
        this.type = tpdu[1];
        final byte[] paramTLV = this.value.toBytes(1, this.value.length() - 1);
        this.params = new TLVList(paramTLV);
    }
    
    public TPDU_CDMA(final Tag tag, final Length len, final byte[] v, final int vOff) {
        super(tag, len, v, vOff);
        this.type = this.value.getByte(0);
        final byte[] paramTLV = this.value.toBytes(1, this.value.length() - 1);
        this.params = new TLVList(paramTLV);
    }
    
    public byte getType() {
        return this.value.getByte(0);
    }
    
    public TLV getParam(final byte tag) {
        final int index = this.params.find(new BERTag(tag));
        if (index != -1) {
            return this.params.get(index);
        }
        return null;
    }
    
    public TLV getSubParamValue(final byte tag, final byte subTag) {
        final TLV tlv = this.getParam(tag);
        if (tlv != null) {
            final byte[] v = tlv.getValue();
            final TLVList subTLVlist = new TLVList(v);
            final int index = subTLVlist.find(new BERTag(subTag));
            if (index != -1) {
                return subTLVlist.get(index);
            }
        }
        return null;
    }
    
    public byte[] getBearerData() {
        final TLV bearerData = this.getParam((byte)8);
        if (bearerData != null) {
            return bearerData.getValue();
        }
        return null;
    }
    
    public byte[] getUserDataRaw() {
        final TLV tlv = this.getSubParamValue((byte)8, (byte)1);
        if (tlv != null) {
            return tlv.getValue();
        }
        return null;
    }
    
    public byte[] getUserData() {
        final byte[] userdata = this.getUserDataRaw();
        if (userdata != null) {
            final ByteArray a = new ByteArray(userdata);
            a.shiftLeft(5);
            int len = a.getByte(1);
            final int udh = a.getByte(2) + 1;
            len -= udh;
            return a.toBytes(2 + udh, len);
        }
        return null;
    }
    
    public int getConcatenateCount() {
        return 1;
    }
    
    public int getConcatenateIndex() {
        return 1;
    }
    
    public String getOrgAddr() {
        final TLV bearerData = this.getParam((byte)2);
        if (bearerData != null) {
            return getAddr(bearerData);
        }
        return null;
    }
    
    public String getDstAddr() {
        final TLV bearerData = this.getParam((byte)4);
        if (bearerData != null) {
            return getAddr(bearerData);
        }
        return null;
    }
    
    public static TLV convertAddr(String addr, final byte addrTag) {
        final BERTLV addrTLV = new BERTLV(addrTag);
        addr = addr.replace('0', 'A');
        final int digCount = addr.length();
        if (digCount % 2 == 1) {
            addr += '0';
        }
        final byte[] addrB = ByteArray.convert(addr);
        final ByteArray addrLV = new ByteArray();
        addrLV.append((byte)digCount);
        addrLV.append(addrB);
        addrLV.shiftRight(2);
        final byte[] a = addrLV.toBytes();
        int len = a.length;
        if (digCount % 2 == 1) {
            --len;
        }
        addrTLV.updateValue(a, 0, len);
        return addrTLV;
    }
    
    public static String getAddr(final TLV addrTLV) {
        final byte[] addrV = addrTLV.getValue();
        final ByteArray addrLV = new ByteArray(addrV);
        addrLV.shiftLeft(2);
        String addrLVStr = addrLV.toString();
        addrLVStr = addrLVStr.replace('A', '0');
        final int digLen = addrLV.getByte(1);
        return addrLVStr.substring(4, 4 + digLen);
    }
    
    @Override
    public String toString() {
        final String res = "CDMA TPDU:" + super.toString();
        return res;
    }
}
