package libs.trustconnector.scdp.smartcard.application.telecom.cat;

public class Refresh extends ProactiveCommand
{
    public static final byte CMD_QUALIFIER_NAA_INIT_AND_FULL_FILE_CHANGE_NOTIFICATION = 0;
    public static final byte CMD_QUALIFIER_FILE_CHANGE_NOTIFICATION = 1;
    public static final byte CMD_QUALIFIER_NAA_INIT_AND_FILE_CHANGE_NOTIFICATION = 2;
    public static final byte CMD_QUALIFIER_NAA_INIT = 3;
    public static final byte CMD_QUALIFIER_UICC_RESET = 4;
    public static final byte CMD_QUALIFIER_NAA_APP_RESET = 5;
    public static final byte CMD_QUALIFIER_NAA_SESSION_RESET = 6;
    public static final byte CMD_QUALIFIER_STEERING_OF_ROMIANG = 7;
    public static final byte CMD_QUALIFIER_STEERING_OF_ROMING_IWLAN = 8;
    
    public Refresh(final byte[] cmd) {
        super(cmd);
    }
}
