package libs.trustconnector.scdp.smartcard.application.bl;

import libs.trustconnector.scdp.smartcard.application.*;
import libs.trustconnector.scdp.smartcard.*;
import libs.trustconnector.scdp.util.*;

import libs.trustconnector.scdp.smartcard.SmartCardReader;
import libs.trustconnector.scdp.smartcard.application.Application;
import libs.trustconnector.scdp.util.ByteArray;

public class BootLoader extends Application
{
    int curBank;
    public static final byte HAL_PARAM_LOCK = 0;
    public static final byte HAL_PARAM_T0_ATR = 1;
    public static final byte HAL_PARAM_SWP_TYPEA_UID = 2;
    public static final byte HAL_PARAM_SWP_TYPEA_SAK = 3;
    public static final byte HAL_PARAM_SWP_TYPEA_ATQA = 4;
    public static final byte HAL_PARAM_SWP_TYPEA_ATS_HIS = 5;
    public static final byte HAL_PARAM_SWP_TYPEA_FWI_SFGI = 6;
    public static final byte HAL_PARAM_SWP_TYPEA_CID = 7;
    public static final byte HAL_PARAM_SWP_TYPEA_DATARATE = 8;
    public static final byte HAL_PARAM_SWP_TYPEB_PUPI = 9;
    public static final byte HAL_PARAM_SWP_TYPEB_AFI = 10;
    public static final byte HAL_PARAM_SWP_TYPEB_ATQB = 11;
    public static final byte HAL_PARAM_SWP_TYPEB_HIGH_LAYER_RSP = 12;
    public static final byte HAL_PARAM_SWP_TYPEB_DATARATE = 13;
    public static final byte HAL_PARAM_TCLA_UID = 14;
    public static final byte HAL_PARAM_TCLA_SAK = 15;
    public static final byte HAL_PARAM_TCLA_ATQA = 16;
    public static final byte HAL_PARAM_TCLA_ATS = 17;
    public static final byte HAL_PARAM_TCLB_PUPI = 18;
    public static final byte HAL_PARAM_TCLB_ATQB = 19;
    public static final byte HAL_PARAM_CHIP_TYPE = 20;
    public static final byte HAL_PARAM_CHIP_ID = 21;
    public static final byte HAL_PARAM_CHIP_EXT_START = Byte.MIN_VALUE;
    public static final byte HAL_PARAM_CHIP_EXT_VER = Byte.MIN_VALUE;
    public static final byte HAL_PARAM_CHIP_EXT_FC = -127;
    public static final byte HAL_PARAM_CHIP_EXT_RZM = -126;
    public static final byte HAL_PARAM_CHIP_EXT_CLK = -125;
    
    public BootLoader(final SmartCardReader reader) {
        super(reader, null);
        this.curBank = -1;
    }
    
    public boolean enterBL(final byte[] rsKey) {
        this.reader.reset();
        this.apdu.setCAPDU("Enter BL", "00F60000083232323232323232");
        if (rsKey != null) {
            this.apdu.setCData(rsKey);
        }
        this.reader.transmit(this.apdu);
        return this.apdu.getSW() == 36864;
    }
    
    public boolean setBank(final int bankID) {
        this.apdu.setCAPDU("Set Bank " + String.format("0x%02X", bankID), "00F1000001FF");
        final byte[] bank = { (byte)bankID };
        this.apdu.setCData(bank);
        this.reader.transmit(this.apdu);
        if (this.apdu.getSW() == 36864) {
            this.curBank = bankID;
            return true;
        }
        return false;
    }
    
    public byte[] readFlash(int offset, int length) {
        final ByteArray rsp = new ByteArray();
        while (length > 0) {
            final int b = offset >> 16 & 0xFF;
            if (b != this.curBank && !this.setBank(offset >> 16 & 0xFF)) {
                return null;
            }
            final int readLen = (length > 256) ? 256 : length;
            this.apdu.setCAPDU("Read Flash Offset=" + String.format("0x%04X", offset), "00F3000000");
            this.apdu.setP1(offset >> 8);
            this.apdu.setP2(offset & 0xFF);
            this.apdu.setP3(readLen);
            this.reader.transmit(this.apdu);
            offset += readLen;
            length -= readLen;
            rsp.append(this.apdu.getRData());
        }
        return rsp.toBytes();
    }
    
    public boolean writeFlash(int offset, final byte[] content) {
        int off = 0;
        int leftLen = content.length;
        while (leftLen > 0) {
            final int b = offset >> 16 & 0xFF;
            if (b != this.curBank && !this.setBank(offset >> 16 & 0xFF)) {
                return false;
            }
            final int updateLen = (leftLen > 128) ? 128 : leftLen;
            this.apdu.setCAPDU("Write Flash Offset=" + String.format("0x%04X", offset), "00F7000000");
            this.apdu.setP1(offset >> 8);
            this.apdu.setP2(offset & 0xFF);
            this.apdu.setCData(content, off, updateLen);
            this.reader.transmit(this.apdu);
            leftLen -= updateLen;
            off += updateLen;
            offset += updateLen;
            if (this.apdu.getSW() != 36864) {
                return false;
            }
        }
        return true;
    }
    
    public boolean eraseFlash(final int offset, final int pageCount) {
        final int b = offset >> 16 & 0xFF;
        if (b != this.curBank && !this.setBank(offset >> 16 & 0xFF)) {
            return false;
        }
        this.apdu.setCAPDU("Erase Flash Offset=" + String.format("0x%04X", offset), "00F5000000");
        this.apdu.setP1(offset >> 8);
        this.apdu.setP2(offset & 0xFF);
        final byte[] bank = { (byte)pageCount };
        this.apdu.setCData(bank);
        this.reader.transmit(this.apdu);
        return this.apdu.getSW() == 36864;
    }
    
    byte[] getParam(final int paramType) {
        this.apdu.setCAPDU("Get Param", "0030000000");
        this.apdu.setP1(paramType);
        this.reader.transmit(this.apdu);
        if (this.apdu.getSW() == 36864) {
            return this.apdu.getRData();
        }
        return null;
    }
    
    boolean setParam(final int paramType, final byte[] newParam) {
        this.apdu.setCAPDU("Set Param", "8030000000");
        this.apdu.setP1(paramType);
        this.apdu.setCData(newParam);
        this.reader.transmit(this.apdu);
        return this.apdu.getSW() == 36864;
    }
}
