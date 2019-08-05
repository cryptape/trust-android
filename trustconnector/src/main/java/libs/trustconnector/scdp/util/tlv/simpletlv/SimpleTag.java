package libs.trustconnector.scdp.util.tlv.simpletlv;

import libs.trustconnector.scdp.util.tlv.*;
import libs.trustconnector.scdp.util.*;

import libs.trustconnector.scdp.util.ByteArray;
import libs.trustconnector.scdp.util.Util;
import libs.trustconnector.scdp.util.tlv.Tag;

public class SimpleTag implements Tag
{
    private byte[] v;
    public static final byte TAG_VALUE_COMMAND_DETAILS = 1;
    public static final SimpleTag TAG_COMMAND_DETAILS;
    public static final byte TAG_VALUE_DEVICE_IDENTITIES = 2;
    public static final SimpleTag TAG_DEVICE_IDENTITIES;
    public static final byte TAG_VALUE_RESULT = 3;
    public static final SimpleTag TAG_RESULT;
    public static final byte TAG_VALUE_DURATION = 4;
    public static final SimpleTag TAG_DURATION;
    public static final byte TAG_VALUE_ALPHA_IDENTIFIER = 5;
    public static final SimpleTag TAG_ALPHA_IDENTIFIER;
    public static final byte TAG_VALUE_ADDRESS = 6;
    public static final SimpleTag TAG_ADDRESS;
    public static final byte TAG_VALUE_SMS_TPDU = 11;
    public static final SimpleTag TAG_SMS_TPDU;
    public static final byte TAG_VALUE_CAPABILITY_CONFIGURATION_PARAMETERS = 7;
    public static final SimpleTag TAG_CAPABILITY_CONFIGURATION_PARAMETERS;
    public static final byte TAG_VALUE_CALLED_PARTY_SUBADDRESS = 8;
    public static final SimpleTag TAG_CALLED_PARTY_SUBADDRESS;
    public static final byte TAG_VALUE_CELL_BROADCAST_PAGE = 12;
    public static final SimpleTag TAG_CELL_BROADCAST_PAGE;
    public static final byte TAG_VALUE_TEXT_STRING = 13;
    public static final SimpleTag TAG_TEXT_STRING;
    public static final byte TAG_VALUE_TONE = 14;
    public static final SimpleTag TAG_TONE;
    public static final byte TAG_VALUE_ITEM = 15;
    public static final SimpleTag TAG_ITEM;
    public static final byte TAG_VALUE_ITEM_IDENTIFIER = 16;
    public static final SimpleTag TAG_ITEM_IDENTIFIER;
    public static final byte TAG_VALUE_RESPONSE_LENGTH = 17;
    public static final SimpleTag TAG_RESPONSE_LENGTH;
    public static final byte TAG_VALUE_FILE_LIST = 18;
    public static final SimpleTag TAG_FILE_LIST;
    public static final byte TAG_VALUE_LOCATION_INFORMATION = 19;
    public static final SimpleTag TAG_LOCATION_INFORMATION;
    public static final byte TAG_VALUE_IMEI = 20;
    public static final SimpleTag TAG_IMEI;
    public static final byte TAG_VALUE_HELP_REQUEST = 21;
    public static final SimpleTag TAG_HELP_REQUEST;
    public static final byte TAG_VALUE_NETWORK_MEASUREMENT_RESULTS = 22;
    public static final SimpleTag TAG_NETWORK_MEASUREMENT_RESULTS;
    public static final byte TAG_VALUE_DEFAULT_TEXT = 23;
    public static final SimpleTag TAG_DEFAULT_TEXT;
    public static final byte TAG_VALUE_ITEMS_NEXT_ACTION_INDICATOR = 24;
    public static final SimpleTag TAG_ITEMS_NEXT_ACTION_INDICATOR;
    public static final byte TAG_VALUE_EVENT_LIST = 25;
    public static final SimpleTag TAG_EVENT_LIST;
    public static final byte TAG_VALUE_LOCATION_STATUS = 27;
    public static final SimpleTag TAG_LOCATION_STATUS;
    public static final byte TAG_VALUE_TRANSACTION_IDENTIFIER = 28;
    public static final SimpleTag TAG_TRANSACTION_IDENTIFIER;
    public static final byte TAG_VALUE_ICON_IDENTIFIER = 30;
    public static final SimpleTag TAG_ICON_IDENTIFIER;
    public static final byte TAG_VALUE_ITEM_ICON_IDENTIFIER_LIST = 31;
    public static final SimpleTag TAG_ITEM_ICON_IDENTIFIER_LIST;
    public static final byte TAG_VALUE_CARD_READER_STATUS = 32;
    public static final SimpleTag TAG_CARD_READER_STATUS;
    public static final byte TAG_VALUE_CARD_ATR = 33;
    public static final SimpleTag TAG_CARD_ATR;
    public static final byte TAG_VALUE_C_APDU = 34;
    public static final SimpleTag TAG_C_APDU;
    public static final byte TAG_VALUE_R_APDU = 35;
    public static final SimpleTag TAG_R_APDU;
    public static final byte TAG_VALUE_TIMER_IDENTIFIER = 36;
    public static final SimpleTag TAG_TIMER_IDENTIFIER;
    public static final byte TAG_VALUE_TIMER_VALUE = 37;
    public static final SimpleTag TAG_TIMER_VALUE;
    public static final byte TAG_VALUE_DATE_TIME_AND_TIME_ZONE = 38;
    public static final SimpleTag TAG_DATE_TIME_AND_TIME_ZONE;
    public static final byte TAG_VALUE_CALL_CONTROL_REQUESTED_ACTION = 39;
    public static final SimpleTag TAG_CALL_CONTROL_REQUESTED_ACTION;
    public static final byte TAG_VALUE_AT_COMMAND = 40;
    public static final SimpleTag TAG_AT_COMMAND;
    public static final byte TAG_VALUE_AT_RESPONSE = 41;
    public static final SimpleTag TAG_AT_RESPONSE;
    public static final byte TAG_VALUE_IMMEDIATE_RESPONSE = 43;
    public static final SimpleTag TAG_IMMEDIATE_RESPONSE;
    public static final byte TAG_VALUE_DTMF_STRING = 44;
    public static final SimpleTag TAG_DTMF_STRING;
    public static final byte TAG_VALUE_LANGUAGE = 45;
    public static final SimpleTag TAG_LANGUAGE;
    public static final byte TAG_VALUE_AID = 47;
    public static final SimpleTag TAG_AID;
    public static final byte TAG_VALUE_BROWSER_IDENTITY = 48;
    public static final SimpleTag TAG_BROWSER_IDENTITY;
    public static final byte TAG_VALUE_URL = 49;
    public static final SimpleTag TAG_URL;
    public static final byte TAG_VALUE_BEARER = 50;
    public static final SimpleTag TAG_BEARER;
    public static final byte TAG_VALUE_PROVISION_REFERENCE_FILE = 51;
    public static final SimpleTag TAG_PROVISION_REFERENCE_FILE;
    public static final byte TAG_VALUE_BROWSER_TERMINATION_CAUSE = 52;
    public static final SimpleTag TAG_BROWSER_TERMINATION_CAUSE;
    public static final byte TAG_VALUE_BEARER_DESCRIPTION = 53;
    public static final SimpleTag TAG_BEARER_DESCRIPTION;
    public static final byte TAG_VALUE_CHANNEL_DATA = 54;
    public static final SimpleTag TAG_CHANNEL_DATA;
    public static final byte TAG_VALUE_CHANNEL_DATA_LENGTH = 55;
    public static final SimpleTag TAG_CHANNEL_DATA_LENGTH;
    public static final byte TAG_VALUE_CHANNEL_STATUS = 56;
    public static final SimpleTag TAG_CHANNEL_STATUS;
    public static final byte TAG_VALUE_BUFFER_SIZE = 57;
    public static final SimpleTag TAG_BUFFER_SIZE;
    public static final byte TAG_VALUE_CARD_READER_ID = 58;
    public static final SimpleTag TAG_CARD_READER_ID;
    public static final byte TAG_VALUE_UICC_TERMINAL_TRANSPORT_LEVEL = 60;
    public static final SimpleTag TAG_UICC_TERMINAL_TRANSPORT_LEVEL;
    public static final byte TAG_VALUE_OTHER_DATA_DESTINATION_ADDRESS = 62;
    public static final SimpleTag TAG_OTHER_DATA_DESTINATION_ADDRESS;
    public static final byte TAG_VALUE_ACCESS_TECHNOLOGY = 63;
    public static final SimpleTag TAG_ACCESS_TECHNOLOGY;
    public static final byte TAG_VALUE_DISPLAY_PARAMETERS = 64;
    public static final SimpleTag TAG_DISPLAY_PARAMETERS;
    public static final byte TAG_VALUE_SERVICE_RECORD = 65;
    public static final SimpleTag TAG_SERVICE_RECORD;
    public static final byte TAG_VALUE_DEVICE_FILTER = 66;
    public static final SimpleTag TAG_DEVICE_FILTER;
    public static final byte TAG_VALUE_SERVICE_SEARCH = 67;
    public static final SimpleTag TAG_SERVICE_SEARCH;
    public static final byte TAG_VALUE_ATTRIBUTE_INFORMATION = 68;
    public static final SimpleTag TAG_ATTRIBUTE_INFORMATION;
    public static final byte TAG_VALUE_SERVICE_AVAILABILITY = 69;
    public static final SimpleTag TAG_SERVICE_AVAILABILITY;
    public static final byte TAG_VALUE_ESN = 70;
    public static final SimpleTag TAG_ESN;
    public static final byte TAG_VALUE_NETWORK_ACCESS_NAME = 71;
    public static final SimpleTag TAG_NETWORK_ACCESS_NAME;
    public static final byte TAG_VALUE_CDMA_TPDU = 72;
    public static final SimpleTag TAG_CDMA_TPDU;
    public static final byte TAG_VALUE_REMOTE_ENTITY_ADDRESS = 73;
    public static final SimpleTag TAG_REMOTE_ENTITY_ADDRESS;
    public static final byte TAG_VALUE_TEXT_ATTRIBUTE_TAG = 80;
    public static final SimpleTag TAG_TEXT_ATTRIBUTE_TAG;
    public static final byte TAG_VALUE_ITEM_TEXT_ATTRIBUTE_LIST = 81;
    public static final SimpleTag TAG_ITEM_TEXT_ATTRIBUTE_LIST;
    public static final byte TAG_VALUE_IMEISV = 98;
    public static final SimpleTag TAG_IMEISV;
    public static final byte TAG_VALUE_BATTERY_STATE = 99;
    public static final SimpleTag TAG_BATTERY_STATE;
    public static final byte TAG_VALUE_BROWSING_STATUS = 100;
    public static final SimpleTag TAG_BROWSING_STATUS;
    public static final byte TAG_VALUE_NETWORK_SEARCH_MODE = 101;
    public static final SimpleTag TAG_NETWORK_SEARCH_MODE;
    public static final byte TAG_MASK_SET_CR = Byte.MIN_VALUE;
    public static final byte TAG_MASK_CLEAR_CR = Byte.MAX_VALUE;
    
    public SimpleTag() {
    }
    
    public SimpleTag(final int tag) {
        if ((tag & 0xFF0000) == 0x7F0000) {
            this.v = Util.intToBytes(tag, 3);
            final byte[] v = this.v;
            final int n = 1;
            v[n] |= 0xFFFFFF80;
        }
        else {
            this.v = Util.intToBytes(tag, 1);
            final byte[] v2 = this.v;
            final int n2 = 0;
            v2[n2] |= 0xFFFFFF80;
        }
    }
    
    public SimpleTag(final int tag, final boolean bCRSet) {
        if ((tag & 0xFF0000) == 0x7F0000) {
            this.v = Util.intToBytes(tag, 3);
            if (bCRSet) {
                final byte[] v = this.v;
                final int n = 1;
                v[n] |= 0xFFFFFF80;
            }
        }
        else {
            this.v = Util.intToBytes(tag, 1);
            if (bCRSet) {
                final byte[] v2 = this.v;
                final int n2 = 0;
                v2[n2] |= 0xFFFFFF80;
            }
        }
    }
    
    @Override
    public byte[] toBytes() {
        return this.v.clone();
    }
    
    @Override
    public int fromBytes(final byte[] bts, final int offset, final int maxLength) {
        this.v = null;
        if (bts[offset] == 127) {
            if (maxLength >= 3) {
                System.arraycopy(bts, offset, this.v = new byte[3], 0, 3);
                return 3;
            }
        }
        else if (maxLength > 0) {
            (this.v = new byte[1])[0] = bts[offset];
            return 1;
        }
        return -1;
    }
    
    @Override
    public int length() {
        if (this.v == null) {
            return 0;
        }
        return this.v.length;
    }
    
    @Override
    public String getDescription() {
        return "Simplet TLV(Comprehension TLV):" + ByteArray.convert(this.v);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Tag) {
            final Tag t = (Tag)obj;
            final byte[] tb = t.toBytes();
            return this.v.length == tb.length && (this.v[0] & 0x7F) == (tb[0] & 0x7F) && (this.v.length != 3 || (this.v[1] == tb[1] && this.v[2] == tb[2]));
        }
        return false;
    }
    
    public boolean isTRSet() {
        return (this.v[0] & 0xFFFFFF80) == 0xFFFFFF80;
    }
    
    public void setTR() {
        final byte[] v = this.v;
        final int n = 0;
        v[n] |= 0xFFFFFF80;
    }
    
    public void clearTR() {
        final byte[] v = this.v;
        final int n = 0;
        v[n] &= 0x7F;
    }
    
    @Override
    public String toString() {
        return ByteArray.convert(this.v);
    }
    
    static {
        TAG_COMMAND_DETAILS = new SimpleTag(1);
        TAG_DEVICE_IDENTITIES = new SimpleTag(2);
        TAG_RESULT = new SimpleTag(3);
        TAG_DURATION = new SimpleTag(4);
        TAG_ALPHA_IDENTIFIER = new SimpleTag(5);
        TAG_ADDRESS = new SimpleTag(6);
        TAG_SMS_TPDU = new SimpleTag(11);
        TAG_CAPABILITY_CONFIGURATION_PARAMETERS = new SimpleTag(7);
        TAG_CALLED_PARTY_SUBADDRESS = new SimpleTag(8);
        TAG_CELL_BROADCAST_PAGE = new SimpleTag(12);
        TAG_TEXT_STRING = new SimpleTag(13);
        TAG_TONE = new SimpleTag(14);
        TAG_ITEM = new SimpleTag(15);
        TAG_ITEM_IDENTIFIER = new SimpleTag(16);
        TAG_RESPONSE_LENGTH = new SimpleTag(17);
        TAG_FILE_LIST = new SimpleTag(18);
        TAG_LOCATION_INFORMATION = new SimpleTag(19);
        TAG_IMEI = new SimpleTag(20);
        TAG_HELP_REQUEST = new SimpleTag(21);
        TAG_NETWORK_MEASUREMENT_RESULTS = new SimpleTag(22);
        TAG_DEFAULT_TEXT = new SimpleTag(23);
        TAG_ITEMS_NEXT_ACTION_INDICATOR = new SimpleTag(24);
        TAG_EVENT_LIST = new SimpleTag(25);
        TAG_LOCATION_STATUS = new SimpleTag(27);
        TAG_TRANSACTION_IDENTIFIER = new SimpleTag(28);
        TAG_ICON_IDENTIFIER = new SimpleTag(30);
        TAG_ITEM_ICON_IDENTIFIER_LIST = new SimpleTag(31);
        TAG_CARD_READER_STATUS = new SimpleTag(32);
        TAG_CARD_ATR = new SimpleTag(33);
        TAG_C_APDU = new SimpleTag(34);
        TAG_R_APDU = new SimpleTag(35);
        TAG_TIMER_IDENTIFIER = new SimpleTag(36);
        TAG_TIMER_VALUE = new SimpleTag(37);
        TAG_DATE_TIME_AND_TIME_ZONE = new SimpleTag(38);
        TAG_CALL_CONTROL_REQUESTED_ACTION = new SimpleTag(39);
        TAG_AT_COMMAND = new SimpleTag(40);
        TAG_AT_RESPONSE = new SimpleTag(41);
        TAG_IMMEDIATE_RESPONSE = new SimpleTag(43);
        TAG_DTMF_STRING = new SimpleTag(44);
        TAG_LANGUAGE = new SimpleTag(45);
        TAG_AID = new SimpleTag(47);
        TAG_BROWSER_IDENTITY = new SimpleTag(48);
        TAG_URL = new SimpleTag(49);
        TAG_BEARER = new SimpleTag(50);
        TAG_PROVISION_REFERENCE_FILE = new SimpleTag(51);
        TAG_BROWSER_TERMINATION_CAUSE = new SimpleTag(52);
        TAG_BEARER_DESCRIPTION = new SimpleTag(53);
        TAG_CHANNEL_DATA = new SimpleTag(54);
        TAG_CHANNEL_DATA_LENGTH = new SimpleTag(55);
        TAG_CHANNEL_STATUS = new SimpleTag(56);
        TAG_BUFFER_SIZE = new SimpleTag(57);
        TAG_CARD_READER_ID = new SimpleTag(58);
        TAG_UICC_TERMINAL_TRANSPORT_LEVEL = new SimpleTag(60);
        TAG_OTHER_DATA_DESTINATION_ADDRESS = new SimpleTag(62);
        TAG_ACCESS_TECHNOLOGY = new SimpleTag(63);
        TAG_DISPLAY_PARAMETERS = new SimpleTag(64);
        TAG_SERVICE_RECORD = new SimpleTag(65);
        TAG_DEVICE_FILTER = new SimpleTag(66);
        TAG_SERVICE_SEARCH = new SimpleTag(67);
        TAG_ATTRIBUTE_INFORMATION = new SimpleTag(68);
        TAG_SERVICE_AVAILABILITY = new SimpleTag(69);
        TAG_ESN = new SimpleTag(70);
        TAG_NETWORK_ACCESS_NAME = new SimpleTag(71);
        TAG_CDMA_TPDU = new SimpleTag(72);
        TAG_REMOTE_ENTITY_ADDRESS = new SimpleTag(73);
        TAG_TEXT_ATTRIBUTE_TAG = new SimpleTag(80);
        TAG_ITEM_TEXT_ATTRIBUTE_LIST = new SimpleTag(81);
        TAG_IMEISV = new SimpleTag(98);
        TAG_BATTERY_STATE = new SimpleTag(99);
        TAG_BROWSING_STATUS = new SimpleTag(100);
        TAG_NETWORK_SEARCH_MODE = new SimpleTag(101);
    }
}
