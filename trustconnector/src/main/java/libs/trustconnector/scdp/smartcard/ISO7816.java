package libs.trustconnector.scdp.smartcard;

import libs.trustconnector.scdp.util.*;

import libs.trustconnector.scdp.util.Util;

public final class ISO7816
{
    public static final int SW_BYTES_REMAINING_00 = 24832;
    public static final int SW_WARNING_STATE_UNCHANGED = 25088;
    public static final int SW_DATA_CORRUPTED = 25217;
    public static final int SW_END_OF_FILE = 25218;
    public static final int SW_FILE_DEACTIVE = 25219;
    public static final int SW_WRONG_LENGTH = 26368;
    public static final int SW_LOGICAL_CHANNEL_NOT_SUPPORTED = 26753;
    public static final int SW_SECURE_MESSAGING_NOT_SUPPORTED = 26754;
    public static final int SW_LAST_COMMAND_EXPECTED = 26755;
    public static final int SW_COMMAND_CHAINING_NOT_SUPPORTED = 26756;
    public static final int SW_SECURITY_STATUS_NOT_SATISFIED = 27010;
    public static final int SW_FILE_INVALID = 27011;
    public static final int SW_DATA_INVALID = 27012;
    public static final int SW_CONDITIONS_NOT_SATISFIED = 27013;
    public static final int SW_COMMAND_NOT_ALLOWED = 27014;
    public static final int SW_APPLET_SELECT_FAILED = 27033;
    public static final int SW_WRONG_DATA = 27264;
    public static final int SW_FUNC_NOT_SUPPORTED = 27265;
    public static final int SW_FILE_NOT_FOUND = 27266;
    public static final int SW_RECORD_NOT_FOUND = 27267;
    public static final int SW_FILE_FULL = 27268;
    public static final int SW_INCORRECT_P1P2 = 27270;
    public static final int SW_REF_DATA_NOT_FOUND = 27272;
    public static final int SW_WRONG_P1P2 = 27392;
    public static final int SW_CORRECT_LENGTH_00 = 27648;
    public static final int SW_INS_NOT_SUPPORTED = 27904;
    public static final int SW_CLA_NOT_SUPPORTED = 28160;
    public static final int SW_UNKNOWN = 28416;
    public static final int SW_NO_ERROR = 36864;
    public static final byte ISO_INS_DEACTIVE_FILE = 4;
    public static final byte ISO_INS_ERASE_RECORD = 12;
    public static final byte ISO_INS_ERASE_BINARY = 14;
    public static final byte ISO_INS_PERFORM_SCQL_OPERATION = 16;
    public static final byte ISO_INS_FETCH = 18;
    public static final byte ISO_INS_TERMINAL_RESPONSE = 20;
    public static final byte ISO_INS_VERIFY = 32;
    public static final byte ISO_INS_MANAGE_SECURITY_ENVIRONMENT = 34;
    public static final byte ISO_INS_CHANGE_REFERENCE_DATA = 36;
    public static final byte ISO_INS_DISABLE_VERIFICATION_REQUIREMENT = 38;
    public static final byte ISO_INS_ENABLE_VERIFICATION_REQUIREMENT = 40;
    public static final byte ISO_INS_PERFORM_SECURITY_OPERATION = 42;
    public static final byte ISO_INS_RESET_RETRY_COUNTER = 44;
    public static final byte ISO_INS_ACTIVATE_FILE = 68;
    public static final byte ISO_INS_GENERATE_ASYMMETRIC_KEY_PAIR = 70;
    public static final byte ISO_INS_MANAGE_CHANNEL = 112;
    public static final byte ISO_INS_EXTERNAL_AUTHENTICATE = -126;
    public static final byte ISO_INS_GET_CHALLENGE = -124;
    public static final byte ISO_INS_GENERAL_AUTHENTICATE = -122;
    public static final byte ISO_INS_INTERNAL_AUTHENTICATE = -120;
    public static final byte ISO_INS_SEARCH_BINARY = -96;
    public static final byte ISO_INS_SEARCH_RECORD = -94;
    public static final byte ISO_INS_SELECT = -92;
    public static final byte ISO_INS_READ_BINARY = -80;
    public static final byte ISO_INS_READ_RECORD = -78;
    public static final byte ISO_INS_GET_RESPONSE = -64;
    public static final byte ISO_INS_ENVELOPE = -62;
    public static final byte ISO_INS_GET_DATA = -54;
    public static final byte ISO_INS_WRITE_BINARY = -48;
    public static final byte ISO_INS_WRITE_RECORD = -46;
    public static final byte ISO_INS_UPDATE_BINARY = -42;
    public static final byte ISO_INS_PUT_DATA = -38;
    public static final byte ISO_INS_UPDATE_RECORD = -36;
    public static final byte ISO_INS_CREATE_FILE = -32;
    public static final byte ISO_INS_APPEND_RECORD = -30;
    public static final byte ISO_INS_DELETE_FILE = -28;
    public static final byte ISO_INS_TERMINATE_DF = -26;
    public static final byte ISO_INS_TERMINATE_EF = -24;
    public static final byte ISO_INS_TERMINATE_CARD_USAGE = -2;
    public static final byte ISO_INS_GP_INIT_UPDATE = 80;
    public static final byte ISO_INS_PBOC_GPO = -88;
    public static final byte ISO_INS_GET_STATUS = -14;
    public static final byte ISO_INS_PUT_KEY = -40;
    
    private ISO7816() {
    }
    
    public static String getInsName(final byte instruction) {
        switch (instruction) {
            case 4: {
                return "Deactive File";
            }
            case 12: {
                return "Erase Record";
            }
            case 14: {
                return "Erase Binary";
            }
            case 16: {
                return "Terminal Profile";
            }
            case 18: {
                return "Fetch";
            }
            case 20: {
                return "Terminal Response";
            }
            case 32: {
                return "Verify PIN";
            }
            case 34: {
                return "Manage Security Environment";
            }
            case 36: {
                return "Change PIN";
            }
            case 38: {
                return "Disable PIN";
            }
            case 40: {
                return "Enable PIN";
            }
            case 42: {
                return "Perform Security Operation";
            }
            case 44: {
                return "Unblock PIN";
            }
            case 68: {
                return "Activate File";
            }
            case 70: {
                return "Generate Asymmetric Key Pair";
            }
            case 112: {
                return "Manage Channel";
            }
            case -126: {
                return "External Authenticate";
            }
            case -124: {
                return "Get Challenge";
            }
            case -122: {
                return "General Authenticate";
            }
            case -120: {
                return "Internal Authenticate";
            }
            case -96: {
                return "Search Binary";
            }
            case -94: {
                return "Search Record";
            }
            case -92: {
                return "Select";
            }
            case -80: {
                return "Read Binary";
            }
            case -78: {
                return "Read Record";
            }
            case -64: {
                return "Get Response";
            }
            case -62: {
                return "Envelope";
            }
            case -54: {
                return "Get Data";
            }
            case -48: {
                return "Write Binary";
            }
            case -46: {
                return "Write Record";
            }
            case -42: {
                return "Update Binary";
            }
            case -38: {
                return "Put Data";
            }
            case -36: {
                return "Update Record";
            }
            case -32: {
                return "Create File";
            }
            case -30: {
                return "Append Record";
            }
            case -28: {
                return "Delete File";
            }
            case -26: {
                return "Install|Terminate DF";
            }
            case -24: {
                return "Load|Terminate EF";
            }
            case -2: {
                return "Terminate Card Usage";
            }
            case 80: {
                return "Initial Update";
            }
            case -88: {
                return "PBOC GPO";
            }
            case -14: {
                return "Get Status";
            }
            case -40: {
                return "Put Key";
            }
            default: {
                return "Unknown Instruction(" + Util.intToString(instruction & 0xFF, false) + ")";
            }
        }
    }

    public static String getStatusWordDesc(short sw) {
        byte sw1 = (byte)(sw >> 8);
        byte sw2 = (byte)sw;
        switch(sw1) {
            case -112:
                switch(sw2) {
                    case 0:
                        return "No Error";
                    default:
                        return "Not specific status word";
                }
            case 97:
                return "Response bytes remaining length=" + sw2;
            case 98:
                switch(sw2) {
                    case -127:
                        return "Part of returned data may be corrupted";
                    case -126:
                        return "End of file or record reached before reading Nebytes";
                    case -125:
                        return "Selected file deactivated";
                    case -124:
                        return "File control information not formatted";
                    case -123:
                        return "Selected file in termination state";
                    case -122:
                        return "No input data available from a sensor on the card";
                    case 0:
                        return "Warning, card state unchanged";
                    default:
                        return "Not specific status word";
                }
            case 99:
                switch(sw2) {
                    case 0:
                        return "Warning no given formation";
                    default:
                        return "Not specific status word";
                }
            case 100:
                if (sw2 == 0) {
                    return "Execution error";
                }
                break;
            case 101:
                if (sw2 == 129) {
                    return "Memory failure";
                }
                break;
            case 103:
                if (sw2 == 0) {
                    return "Wrong Length";
                }
                break;
            case 104:
                switch(sw2) {
                    case -127:
                        return "Logical channel not supported";
                    case -126:
                        return "Secure messaging not supported";
                    case -125:
                        return "";
                }
        }

        return "Not specific status word";
    }
}
