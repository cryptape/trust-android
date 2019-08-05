package libs.trustconnector.scdp.smartcard;

import libs.trustconnector.scdp.*;
import libs.trustconnector.scdp.util.*;

import libs.trustconnector.scdp.SCDP;
import libs.trustconnector.scdp.util.ByteArray;

public class SDI01XReader extends PCSCReader
{
    boolean bSDI;
    int rawProtocol;
    public static final int PROTOCOL_NON = 0;
    public static final int PROTOCOL_TYPE_A = 1;
    public static final int PROTOCOL_TYPE_B = 2;
    public static final int BIT_COUNT_ALL = 0;
    public static final int CASCADE_LEVEL_1 = 1;
    public static final int CASCADE_LEVEL_2 = 2;
    public static final int CASCADE_LEVEL_3 = 3;
    
    SDI01XReader(final int index, final boolean bSDI) {
        super(index);
        this.bSDI = bSDI;
    }
    
    static SDI01XReader checkReader(final String name, final int index) {
        if (name.startsWith("SCM Microsystems Inc. SDI011G Contactless Reader")) {
            return new SDI01XReader(index, true);
        }
        if (name.startsWith("Identive CLOUD 4700 F Contactless Reader")) {
            return new SDI01XReader(index, false);
        }
        return null;
    }
    
    public static SDI01XReader getReader() {
        final int c = SCDP.readerCount();
        SDI01XReader r = null;
        for (int i = 0; i < c; ++i) {
            final String name = SCDP.readerGetName(i);
            r = checkReader(name, i);
            if (r != null) {
                break;
            }
        }
        return r;
    }
    
    public int getProtocolType() {
        final APDU apdu = new APDU("Get Protocol Type", "FFCC00000111");
        this.transmit(apdu);
        if (apdu.getSW() != 36864) {
            return 0;
        }
        final byte[] cardInfo = apdu.getRData();
        if (this.bSDI) {
            if ((cardInfo[2] & 0xF) == 0x1) {
                return 1;
            }
            return 2;
        }
        else {
            if ((cardInfo[2] & 0xF) == 0x0) {
                return 1;
            }
            return 2;
        }
    }
    
    public byte[] getUID() {
        final APDU apdu = new APDU("Get UID", "FFCA000000");
        this.transmit(apdu);
        if (apdu.getSW() == 36864) {
            return apdu.getRData();
        }
        return null;
    }
    
    public byte[] getATS() {
        final APDU apdu = new APDU("Get ATS", "FFCC00000193");
        this.transmit(apdu);
        if (apdu.getSW() == 36864) {
            return apdu.getRData();
        }
        return null;
    }
    
    public boolean mifareLoadKey(final boolean bKeyA, final byte[] keyValue) {
        final APDU apdu = new APDU("Mifare Load key", "FF82006000");
        if (!bKeyA) {
            apdu.setP2(97);
        }
        apdu.setCData(keyValue);
        this.transmit(apdu, "9000");
        return apdu.getSW() == 36864;
    }
    
    public boolean mifareLoadKeyA(final byte[] keyA) {
        return this.mifareLoadKey(true, keyA);
    }
    
    public boolean mifareLoadKeyB(final byte[] keyB) {
        return this.mifareLoadKey(false, keyB);
    }
    
    public boolean mifareLoadKeyA(final String keyA) {
        return this.mifareLoadKey(true, ByteArray.convert(keyA));
    }
    
    public boolean mifareLoadKeyB(final String keyB) {
        return this.mifareLoadKey(false, ByteArray.convert(keyB));
    }
    
    public boolean mifareAuth(final int blockNum, final boolean bKeyA) {
        String name = String.format("Mifare Auth Block:%02X with ", blockNum);
        name += (bKeyA ? "KeyA" : "KeyB");
        final APDU apdu = new APDU(name, "FF86000000");
        final byte[] data = { 1, 0, (byte)blockNum, (byte)(bKeyA ? 96 : 97), 1 };
        apdu.setCData(data);
        this.transmit(apdu, "9000");
        return apdu.getSW() == 36864;
    }
    
    public boolean mifareAuthKeyA(final int blockNum) {
        return this.mifareAuth(blockNum, true);
    }
    
    public boolean mifareAuthKeyB(final int blockNum) {
        return this.mifareAuth(blockNum, false);
    }
    
    public byte[] mifareReadBinary(final int blockNum) {
        final APDU apdu = new APDU(String.format("Mifare Read Block:%02X", blockNum), "FFB0000000");
        apdu.setP2(blockNum);
        this.transmit(apdu, "9000");
        if (apdu.getSW() == 36864) {
            return apdu.getRData();
        }
        return null;
    }
    
    public boolean mifareWriteBinary(final int blockNum, final byte[] data) {
        final APDU apdu = new APDU(String.format("Mifare Write Block:%02X", blockNum), "FFD6000000");
        apdu.setP2(blockNum);
        apdu.setCData(data);
        this.transmit(apdu, "9000");
        return apdu.getSW() == 36864;
    }
    
    public boolean mifareValueBlock(final int blockNum, final boolean bInc, final int value) {
        final APDU apdu = new APDU("Mifare Value Block", "FFF0000000");
        apdu.setP2(blockNum);
        final byte[] data = { (byte)(bInc ? -64 : -63), (byte)blockNum, (byte)value, (byte)(value >> 8), (byte)(value >> 16), (byte)(value >> 24) };
        apdu.setCData(data);
        this.transmit(apdu, "9000");
        return apdu.getSW() == 36864;
    }
    
    public byte[] getPUPI() {
        return this.getUID();
    }
    
    public byte[] getATQB() {
        return this.getATS();
    }
    
    public boolean setRawCommandControl(final int protocolType) {
        final APDU apdu = new APDU("Switch to Raw Command", "FFCC000000");
        final byte[] cdata = { -105, 0 };
        this.rawProtocol = protocolType;
        if (protocolType == 1) {
            cdata[1] = 0;
        }
        else {
            if (protocolType != 2) {
                return false;
            }
            cdata[1] = 1;
        }
        apdu.setCData(cdata);
        this.transmit(apdu, "9000");
        return apdu.getSW() == 36864;
    }
    
    public byte[] transimitRawCommand(final String cmdName, final byte[] rawCommand, final int bitCountToSend, final int FWI, final boolean bAutoCRC, final int protocol) {
        final APDU apdu = new APDU(cmdName, "FFCC000001AE");
        final byte[] cfg = { (byte)FWI, (byte)(bAutoCRC ? 1 : 0), (byte)bitCountToSend, 0, 0 };
        if (protocol == 1) {
            cfg[3] = 0;
        }
        else {
            if (protocol != 2) {
                return null;
            }
            cfg[3] = 1;
        }
        cfg[4] = (byte)rawCommand.length;
        apdu.appendCData(cfg);
        apdu.appendCData(rawCommand);
        this.transmit(apdu, "9000");
        if (apdu.getSW() == 36864) {
            return apdu.getRData();
        }
        return null;
    }
    
    public byte[] transimitRawCommand(final String cmdName, final byte[] rawCommand, final int bitCountToSend, final int FWI, final boolean bAutoCRC) {
        return this.transimitRawCommand(cmdName, rawCommand, bitCountToSend, FWI, bAutoCRC, this.rawProtocol);
    }
    
    public byte[] transimitRawCommand(final String cmdName, final byte[] rawCommand, final int bitCountToSend, final int FWI) {
        return this.transimitRawCommand(cmdName, rawCommand, bitCountToSend, FWI, true, this.rawProtocol);
    }
    
    public byte[] transimitRawCommandREQA() {
        final byte[] reqA = { 38 };
        return this.transimitRawCommand("REQA", reqA, 0, 3, false, 1);
    }
    
    public byte[] transimitRawCommandWUPA() {
        final byte[] WUPA = { 82 };
        return this.transimitRawCommand("WUPA", WUPA, 0, 3, false, 1);
    }
    
    public byte[][] transimitRawCommandAntocollision() {
        byte[][] res1 = null;
        byte[][] res2 = null;
        byte[][] res3 = null;
        res1 = this.transimitRawCommandSEL(1);
        if ((res1[1][0] & 0x4) == 0x4) {
            res2 = this.transimitRawCommandSEL(2);
            if ((res2[1][0] & 0x4) == 0x4) {
                res3 = this.transimitRawCommandSEL(3);
            }
        }
        final byte[][] res4 = new byte[2][];
        if (res3 != null) {
            res4[0] = new byte[10];
            res4[1] = new byte[1];
            System.arraycopy(res1[0], 1, res4[0], 0, 3);
            System.arraycopy(res2[0], 1, res4[0], 3, 3);
            System.arraycopy(res3[0], 0, res4[0], 6, 4);
            res4[1][0] = res3[1][0];
        }
        else if (res2 != null) {
            res4[0] = new byte[7];
            res4[1] = new byte[1];
            System.arraycopy(res1[0], 1, res4[0], 0, 3);
            System.arraycopy(res2[0], 0, res4[0], 3, 4);
            res4[1][0] = res2[1][0];
        }
        else {
            res4[0] = new byte[4];
            res4[1] = new byte[1];
            System.arraycopy(res1[0], 0, res4[0], 0, 4);
            res4[1][0] = res1[1][0];
        }
        return res4;
    }
    
    public byte[][] transimitRawCommandSEL(final int cascadeLevel) {
        final byte[][] res = new byte[2][];
        final byte[] SEL = new byte[2];
        final byte[] SEL_ACK = new byte[7];
        switch (cascadeLevel) {
            case 1: {
                SEL[0] = -109;
                break;
            }
            case 2: {
                SEL[0] = -107;
                break;
            }
            case 3: {
                SEL[0] = -105;
                break;
            }
        }
        SEL[1] = 32;
        res[0] = this.transimitRawCommand("SELECT", SEL, 0, 3, false, 1);
        SEL_ACK[0] = SEL[0];
        SEL_ACK[1] = 112;
        System.arraycopy(res[0], 0, SEL_ACK, 2, 5);
        res[1] = this.transimitRawCommand("SELECT", SEL_ACK, 0, 3, true, 1);
        return res;
    }
    
    public byte[] transimitRawCommandHLTA() {
        final byte[] HLTA = { 80, 0 };
        return this.transimitRawCommand("HLTA", HLTA, 0, 3, true, 1);
    }
    
    public byte[] transimitRawCommandRATS(final int FSDI, final int CID) {
        final byte[] RATS = { -32, 0 };
        RATS[1] = (byte)(FSDI << 4 | (CID & 0xF));
        return this.transimitRawCommand("RATS", RATS, 0, 3, true, 1);
    }
    
    public byte[] transimitRawCommandPPS(final int CID, final int DRI, final int DSI) {
        final byte[] PPS = { -32, 17, 0 };
        PPS[2] = (byte)((DRI << 2 & 0xC) | (DSI & 0x3));
        return this.transimitRawCommand("PPS", PPS, 0, 3, true, 1);
    }
    
    public byte[] transimitRawCommandExchangeBlock(final byte[] block, final boolean bAutoCRC, final int FWI) {
        return this.transimitRawCommand("Exchange Block", block, 0, FWI, bAutoCRC, 1);
    }
    
    public byte[] transimitRawCommandExchangeBlock(final byte[] block) {
        return this.transimitRawCommand("Exchange Block", block, 0, 7, true, 1);
    }
}
