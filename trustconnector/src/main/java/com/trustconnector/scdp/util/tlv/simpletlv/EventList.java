//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.trustconnector.scdp.util.tlv.simpletlv;

import com.trustconnector.scdp.smartcard.checkrule.ValueInfoMap;
import com.trustconnector.scdp.util.ByteArray;
import com.trustconnector.scdp.util.tlv.Length;
import com.trustconnector.scdp.util.tlv.Tag;
import com.trustconnector.scdp.util.tlv.bertlv.BERLength;

public class EventList extends SimpleTLV {
    static ValueInfoMap devInfoMap = new ValueInfoMap("00|MT Call|01|Call Connected|02|Call Disconnected|03|Location Status|04|User Activity|05|Idle Screen Available|06|Card Reader Status|07|Language Selection|08|Browser Termination|09|Data Available|0A|Channel Status|0B|Access Technoledge Changed Status|0C|Display Param Changed|0D|Local Connection|0E|Network Search Mode Changed|0F|Browsing Status|10|Frames Info Change|11|IWLAN Access Status|12|Network Rejection|13|HCI Connectivity|14|Access Techonoledge Changed(Multi)|15|CGS Cell Selection|16|Contactless State Request|17|IMS Registration|18|IMS Incoming Data|19|Profile Container");
    public static final byte EVENT_MT_CALL = 0;
    public static final byte EVENT_CALL_CONNECTED = 1;
    public static final byte EVENT_CALL_DISCONNECTED = 2;
    public static final byte EVENT_LOCATION_STATUS = 3;
    public static final byte EVENT_USER_ACTIVITY = 4;
    public static final byte EVENT_IDLE_SCREEN_AVAILABLE = 5;
    public static final byte EVENT_CARD_READER_STATUS = 6;
    public static final byte EVENT_LANGUAGE_SELECTION = 7;
    public static final byte EVENT_BROWSER_TERMINATION = 8;
    public static final byte EVENT_DATA_AVAILABLE = 9;
    public static final byte EVENT_CHANNEL_STATSU = 10;
    public static final byte EVENT_ACC_TECH_CHANGED_SAT = 11;
    public static final byte EVENT_DISPALY_PARAM_CHANGED = 12;
    public static final byte EVENT_LOCAL_CONNECTION = 13;
    public static final byte EVENT_NETWORK_SEARCH_MODE_CHANGED = 14;
    public static final byte EVENT_BROWSING_STATUS = 15;
    public static final byte EVENT_FRAMES_INFO_CHANGE = 16;
    public static final byte EVENT_IWLAN_ACC_STATUS = 17;
    public static final byte EVENT_NETWORK_REJECTION = 18;
    public static final byte EVENT_HCI_CONNECTIVITY = 19;
    public static final byte EVENT_ACC_TECH_CHANGED_MAT = 20;
    public static final byte EVENT_CSG_CELL_SELECTION = 21;
    public static final byte EVENT_CONTACTLESS_STATE_REQ = 22;
    public static final byte EVENT_IMS_REGISTRATION = 23;
    public static final byte EVENT_IMS_INCOMING_DATA = 24;
    public static final byte EVENT_PROFILE_CONTAINER = 25;

    public EventList(Tag tag, Length len, byte[] v, int vOff) {
        super(tag, len, v, vOff);
    }

    public EventList(byte event) {
        this.tag = new SimpleTag(25);
        this.len = new BERLength(1);
        this.value = new ByteArray(1);
        this.value.setByte(0, event);
    }

    public EventList(byte[] eventList) {
        this.tag = new SimpleTag(25);
        this.len = new BERLength(eventList.length);
        this.value = new ByteArray(eventList);
    }

    public byte getEvent(int index) {
        return this.value.getByte(index);
    }

    public byte[] getEventList() {
        return this.value.toBytes();
    }

    public String toString() {
        String res = "Setup Event List:" + super.toString();
        byte[] event = this.getEventList();
        int count = event.length;

        for(int i = 0; i < count; ++i) {
            res = res + "\n    -";
            res = res + String.format("%02X", event[i]);
            res = res + "=";
            res = res + getEventName(event[i]);
        }

        return res;
    }

    public static String getEventName(int event) {
        String devName = (String)devInfoMap.get(String.format("%02X", (byte)event));
        if (devName == null) {
            devName = String.format("Unknown Event[%02X]", event);
        }

        return devName;
    }
}
