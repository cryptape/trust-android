package com.trustconnector.scdp.smartcard.application.bl;

import com.trustconnector.scdp.smartcard.application.*;
import com.trustconnector.scdp.smartcard.*;

public class CIU98M25 extends Application
{
    public static final int CHIP_SIZE = 1171456;
    public static final int CHIP_BPS = 512;
    
    public CIU98M25(final SmartCardReader reader) {
        super(reader, null);
    }
    
    public boolean eraseChip() {
        this.apdu.setCAPDU("Erase Full Chip", "BF44010000");
        this.reader.transmit(this.apdu);
        return this.apdu.getSW() == 36864;
    }
    
    public boolean erasePage(final int startAddr, final int endAddr) {
        this.apdu.setCAPDU(String.format("Erase Full Chip(%08X-%08X)", startAddr, endAddr), "BF44030000");
        final byte[] cdata = new byte[4];
        final int sIndex = startAddr / 512;
        final int eIndex = endAddr / 512;
        cdata[0] = (byte)(sIndex >> 8);
        cdata[1] = (byte)sIndex;
        cdata[2] = (byte)(eIndex >> 8);
        cdata[3] = (byte)eIndex;
        this.apdu.setCData(cdata);
        this.reader.transmit(this.apdu);
        return this.apdu.getSW() == 36864;
    }
    
    public boolean isChipEmpty() {
        this.apdu.setCAPDU("check Full Chip", "BF42000006000011BF83EC");
        this.reader.transmit(this.apdu);
        return this.apdu.getSW() == 36864;
    }
    
    public boolean writeFlashPage(final int addr, final byte[] flash) {
        this.apdu.setCAPDU(String.format("Write Flash Chip,addr=0x%08X", addr), "B070000000");
        this.apdu.setClass(0xB0 | (flash[255] >> 4 & 0xF));
        this.apdu.setIns(0x70 | (flash[255] & 0xF));
        this.apdu.setP1P2(addr / 256);
        this.apdu.setCData(flash, 0, 255);
        this.reader.transmit(this.apdu);
        if (this.apdu.getSW() == 36864) {
            this.apdu.setCAPDU(String.format("Write Flash Chip,addr=0x%08X", addr + 256), "B070000000");
            this.apdu.setClass(0xB0 | (flash[511] >> 4 & 0xF));
            this.apdu.setIns(0x70 | (flash[511] & 0xF));
            this.apdu.setP1P2(addr / 256 + 1);
            this.apdu.setCData(flash, 256, 511);
            this.reader.transmit(this.apdu);
        }
        return this.apdu.getSW() == 36864;
    }
    
    public boolean switchToCOS() {
        this.apdu.setCAPDU("switchToCOS", "BF46000000");
        this.reader.transmit(this.apdu);
        return this.apdu.getSW() == 36864;
    }
    
    public boolean checkFlash(final int startAddr, final int endAddr, final int checkCRC) {
        this.apdu.setCAPDU("switchToCOS", "BF42000000");
        final byte[] cdata = new byte[6];
        final int sIndex = startAddr / 512;
        final int eIndex = endAddr / 512;
        cdata[0] = (byte)(sIndex >> 8);
        cdata[1] = (byte)sIndex;
        cdata[2] = (byte)(eIndex >> 8);
        cdata[3] = (byte)eIndex;
        cdata[4] = (byte)(checkCRC >> 8);
        cdata[5] = (byte)checkCRC;
        this.apdu.setCData(cdata);
        this.reader.transmit(this.apdu);
        return this.apdu.getSW() == 36864;
    }
}
