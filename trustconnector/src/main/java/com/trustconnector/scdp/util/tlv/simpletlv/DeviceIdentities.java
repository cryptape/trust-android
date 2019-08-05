package com.trustconnector.scdp.util.tlv.simpletlv;

import com.trustconnector.scdp.smartcard.checkrule.*;
import com.trustconnector.scdp.util.tlv.*;
import com.trustconnector.scdp.util.tlv.bertlv.*;
import com.trustconnector.scdp.util.*;

public class DeviceIdentities extends SimpleTLV
{
    static ValueInfoMap devInfoMap;
    public static final byte DEVICE_ID_KEYPAD = 1;
    public static final byte DEVICE_ID_DISPLAY = 2;
    public static final byte DEVICE_ID_EARPIECE = 3;
    public static final byte DEVICE_ID_CARD_READER_0 = 16;
    public static final byte DEVICE_ID_CARD_READER_1 = 17;
    public static final byte DEVICE_ID_CARD_READER_2 = 18;
    public static final byte DEVICE_ID_CARD_READER_3 = 19;
    public static final byte DEVICE_ID_CARD_READER_4 = 20;
    public static final byte DEVICE_ID_CARD_READER_5 = 21;
    public static final byte DEVICE_ID_CARD_READER_6 = 22;
    public static final byte DEVICE_ID_CARD_READER_7 = 23;
    public static final byte DEVICE_ID_CHN_1 = 33;
    public static final byte DEVICE_ID_CHN_2 = 34;
    public static final byte DEVICE_ID_CHN_3 = 35;
    public static final byte DEVICE_ID_CHN_4 = 36;
    public static final byte DEVICE_ID_CHN_5 = 37;
    public static final byte DEVICE_ID_CHN_6 = 38;
    public static final byte DEVICE_ID_CHN_7 = 39;
    public static final byte DEVICE_ID_ECAT_CLIENT_1 = 49;
    public static final byte DEVICE_ID_ECAT_CLIENT_2 = 50;
    public static final byte DEVICE_ID_ECAT_CLIENT_3 = 51;
    public static final byte DEVICE_ID_ECAT_CLIENT_4 = 52;
    public static final byte DEVICE_ID_ECAT_CLIENT_5 = 53;
    public static final byte DEVICE_ID_ECAT_CLIENT_6 = 54;
    public static final byte DEVICE_ID_ECAT_CLIENT_7 = 55;
    public static final byte DEVICE_ID_ECAT_CLIENT_8 = 56;
    public static final byte DEVICE_ID_ECAT_CLIENT_9 = 57;
    public static final byte DEVICE_ID_ECAT_CLIENT_10 = 58;
    public static final byte DEVICE_ID_ECAT_CLIENT_11 = 59;
    public static final byte DEVICE_ID_ECAT_CLIENT_12 = 60;
    public static final byte DEVICE_ID_ECAT_CLIENT_13 = 61;
    public static final byte DEVICE_ID_ECAT_CLIENT_14 = 62;
    public static final byte DEVICE_ID_ECAT_CLIENT_15 = 63;
    public static final byte DEVICE_ID_UICC = -127;
    public static final byte DEVICE_ID_TERMINAL = -126;
    public static final byte DEVICE_ID_NETWORK = -125;
    
    public DeviceIdentities(final Tag tag, final Length len, final byte[] v, final int vOff) {
        super(tag, len, v, vOff);
    }
    
    public DeviceIdentities(final byte srcDevID, final byte dstDevID) {
        this.tag = new SimpleTag(2);
        this.len = new BERLength(2);
        final byte[] v = { srcDevID, dstDevID };
        this.value = new ByteArray(v);
    }
    
    public DeviceIdentities(final byte[] srcDstDevID) {
        super(new SimpleTag(2), new BERLength(2), srcDstDevID);
    }
    
    public byte getSrcDevID() {
        return this.value.getByte(0);
    }
    
    public String getSrcDevName() {
        return getDeviceName(this.getSrcDevID());
    }
    
    public byte getDstDevID() {
        return this.value.getByte(1);
    }
    
    public String getDstDevName() {
        return getDeviceName(this.getDstDevID());
    }
    
    public void setSrcDevID(final byte srcDevID) {
        this.value.setByte(0, srcDevID);
    }
    
    public void setDstDevID(final byte srcDstID) {
        this.value.setByte(1, srcDstID);
    }
    
    @Override
    public String toString() {
        String res = "Device Identity:" + super.toString();
        res += "\n    -Device Identity Source:";
        res += this.getSrcDevName();
        res += "\n    -Device Identity Destination:";
        res += this.getDstDevName();
        return res;
    }

    public static String getDeviceName(int devID) {
        String devName = (String)devInfoMap.get(String.format("%02X", (byte)devID));
        if (devName == null) {
            devName = String.format("Unknown Device[%02X]", devID);
        }

        return devName;
    }
    
    static {
        DeviceIdentities.devInfoMap = new ValueInfoMap("01|KeyPad|02|Dispaly|03|Earpiece|81|UICC|82|Terminal|83|Network");
        for (int i = 0; i < 8; ++i) {
            DeviceIdentities.devInfoMap.put(String.format("1%1X", i), String.format("CardRead %02X", i));
        }
        for (int i = 0; i < 8; ++i) {
            DeviceIdentities.devInfoMap.put(String.format("2%1X", i), String.format("Channel %02X", i));
        }
    }
}
