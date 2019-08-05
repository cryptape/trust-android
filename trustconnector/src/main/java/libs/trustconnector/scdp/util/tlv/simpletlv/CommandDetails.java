package libs.trustconnector.scdp.util.tlv.simpletlv;

import libs.trustconnector.scdp.smartcard.checkrule.*;
import libs.trustconnector.scdp.util.tlv.*;
import libs.trustconnector.scdp.util.tlv.bertlv.*;
import libs.trustconnector.scdp.util.*;

import libs.trustconnector.scdp.smartcard.checkrule.ValueInfoMap;
import libs.trustconnector.scdp.util.ByteArray;
import libs.trustconnector.scdp.util.tlv.Length;
import libs.trustconnector.scdp.util.tlv.Tag;
import libs.trustconnector.scdp.util.tlv.bertlv.BERLength;

public class CommandDetails extends SimpleTLV
{
    private static final String valueInfoMapS = "01|Refresh|02|More Time|03|Poll Interval|04|Polling Off|05|Setup Event List|10|Setup Call|11|Send SS|12|Send USSD|13|Send SMS|14|Send DTMF|15|Launch Browser|16|Geo location Request|20|Play Tone|21|Display Text|22|Get Inkey|23|Get Input|24|Select Item|25|Setup Menu|26|Provide Local Information|27|Timer Management|28|Setup Idle mode text|30|Performa card APDU|31|Power On Card|32|Power Off Card|33|Get Reader Status|34|Run AT Command|35|Language Notification|40|Open Channel|41|Close Channel|42|Receive Data|43|Send Data|44|Get Channel Status|45|Service Search|46|Get Service Infomation|47|Declare Service|50|Set Frames|51|Get Frames Status|60|Retrieve Multimedia Message|61|Submit Multimedia Message|62|Display Multimedia Message|70|Activate|71|Contactless State Changed|72|Command Container|73|Encapsulated Session Control";
    protected static final ValueInfoMap cmdTypeMap;
    public static final int CMD_TYPE_REFRESH = 1;
    public static final int CMD_TYPE_MORE_TIME = 2;
    public static final int CMD_TYPE_POLL_INTERVAL = 3;
    public static final int CMD_TYPE_POLLING_OFF = 4;
    public static final int CMD_TYPE_SET_UP_EVENT_LIST = 5;
    public static final int CMD_TYPE_SET_UP_CALL = 16;
    public static final int CMD_TYPE_SEND_SS = 17;
    public static final int CMD_TYPE_SEND_USSD = 18;
    public static final int CMD_TYPE_SEND_SHORT_MESSAGE = 19;
    public static final int CMD_TYPE_SEND_DTMF = 20;
    public static final int CMD_TYPE_LAUNCH_BROWSER = 21;
    public static final int CMD_TYPE_GEOGRAPHICAL_LOCATION_REQUEST = 22;
    public static final int CMD_TYPE_PLAY_TONE = 32;
    public static final int CMD_TYPE_DISPLAY_TEXT = 33;
    public static final int CMD_TYPE_GET_INKEY = 34;
    public static final int CMD_TYPE_GET_INPUT = 35;
    public static final int CMD_TYPE_SELECT_ITEM = 36;
    public static final int CMD_TYPE_SET_UP_MENU = 37;
    public static final int CMD_TYPE_PROVIDE_LOCAL_INFORMATION = 38;
    public static final int CMD_TYPE_TIMER_MANAGEMENT = 39;
    public static final int CMD_TYPE_SET_UP_IDLE_MODE_TEXT = 40;
    public static final int CMD_TYPE_PERFORM_CARD_APDU = 48;
    public static final int CMD_TYPE_POWER_ON_CARD = 49;
    public static final int CMD_TYPE_POWER_OFF_CARD = 50;
    public static final int CMD_TYPE_GET_READER_STATUS = 51;
    public static final int CMD_TYPE_RUN_AT_COMMAND = 52;
    public static final int CMD_TYPE_LANGUAGE_NOTIFICATION = 53;
    public static final int CMD_TYPE_OPEN_CHANNEL = 64;
    public static final int CMD_TYPE_CLOSE_CHANNEL = 65;
    public static final int CMD_TYPE_RECEIVE_DATA = 66;
    public static final int CMD_TYPE_SEND_DATA = 67;
    public static final int CMD_TYPE_GET_CHANNEL_STATUS = 68;
    public static final int CMD_TYPE_SERVICE_SEARCH = 69;
    public static final int CMD_TYPE_GET_SERVICE_INFORMATION = 70;
    public static final int CMD_TYPE_DECLARE_SERVICE = 71;
    public static final int CMD_TYPE_SET_FRAMES = 80;
    public static final int CMD_TYPE_GET_FRAMES_STATUS = 81;
    public static final int CMD_TYPE_RETRIEVE_MULTIMEDIA_MESSAGE = 96;
    public static final int CMD_TYPE_SUBMIT_MULTIMEDIA_MESSAGE = 97;
    public static final int CMD_TYPE_DISPLAY_MULTIMEDIA_MESSAGE = 98;
    public static final int CMD_TYPE_ACTIVATE = 112;
    public static final int CMD_TYPE_CONTACTLESS_STATE_CHANGED = 113;
    public static final int CMD_TYPE_COMMAND_CONTAINER = 114;
    public static final int CMD_TYPE_ENCAPSULATED_SESSION_CONTROL = 115;
    
    public CommandDetails(final Tag tag, final Length len, final byte[] v, final int vOff) {
        super(tag, len, v, vOff);
    }
    
    public CommandDetails(final int cmdType) {
        this(cmdType, 0, 1);
    }
    
    public CommandDetails(final int cmdType, final int cmdQualifier, final int cmdNum) {
        this.tag = new SimpleTag(1);
        this.len = new BERLength(3);
        (this.value = new ByteArray(3)).setByte(0, cmdNum);
        this.value.setByte(1, cmdType);
        this.value.setByte(2, cmdQualifier);
    }
    
    public byte getCmdType() {
        return this.value.getByte(1);
    }
    
    public byte getCmdNumber() {
        return this.value.getByte(0);
    }
    
    public byte getCmdQualifier() {
        return this.value.getByte(2);
    }
    
    public String getCmdName() {
        return getCmdName(this.getCmdType());
    }

    public static String getCmdName(byte cmd) {
        return (String)cmdTypeMap.get(String.format("%02X", cmd));
    }

    public static String getQualifierDesc(final byte cmd, final byte qualifier) {
        return "";
    }
    
    @Override
    public String toString() {
        String res = "Command Details:" + super.toString();
        res += "\n    -Command Number:";
        res += String.format("%02X", this.getCmdNumber());
        res += "\n    -Command Type:";
        res += String.format("%02X(%s)", this.getCmdType(), this.getCmdName());
        res += "\n    -Command Qualifier:";
        res += String.format("%02X", this.getCmdQualifier());
        res += getQualifierDesc(this.getCmdType(), this.getCmdQualifier());
        return res;
    }
    
    static {
        cmdTypeMap = new ValueInfoMap("01|Refresh|02|More Time|03|Poll Interval|04|Polling Off|05|Setup Event List|10|Setup Call|11|Send SS|12|Send USSD|13|Send SMS|14|Send DTMF|15|Launch Browser|16|Geo location Request|20|Play Tone|21|Display Text|22|Get Inkey|23|Get Input|24|Select Item|25|Setup Menu|26|Provide Local Information|27|Timer Management|28|Setup Idle mode text|30|Performa card APDU|31|Power On Card|32|Power Off Card|33|Get Reader Status|34|Run AT Command|35|Language Notification|40|Open Channel|41|Close Channel|42|Receive Data|43|Send Data|44|Get Channel Status|45|Service Search|46|Get Service Infomation|47|Declare Service|50|Set Frames|51|Get Frames Status|60|Retrieve Multimedia Message|61|Submit Multimedia Message|62|Display Multimedia Message|70|Activate|71|Contactless State Changed|72|Command Container|73|Encapsulated Session Control");
    }
}
