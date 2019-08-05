package com.trustconnector.scdp.smartcard;

import com.trustconnector.scdp.*;

public class MP300Reader extends SmartCardReader
{
    private boolean bTearing;
    private static final int MAGIC_CODE = 1297130842;
    private static final int ATTR_TYPE_MAGIC = 1297088512;
    private static final int ATTR_TYPE_VOLTAGE = 1297088513;
    private static final int ATTR_TYPE_CLOCK = 1297088514;
    private static final int ATTR_TYPE_TEAR_CLOCK = 1297088515;
    private static final int ATTR_TYPE_SEND_PPS = 1297088516;
    private static final int ATTR_TYPE_WATCH_CMD = 1297088517;
    private static final int ATTR_TYPE_ICC_MAX = 1297088518;
    private static final int ATTR_TYPE_AUTO_PPS = 1297088521;
    public static final int CURRENT_RANGE_250MA = 1;
    public static final int CURRENT_RANGE_100MA = 2;
    public static final int CURRENT_RANGE_25MA = 3;
    public static final int CURRENT_RANGE_5MA = 4;
    public static final int CURRENT_RANGE_500UA = 5;
    
    MP300Reader(final int index) {
        super(index);
    }
    
    @Override
    public void transmit(final APDU apdu, final boolean autoProc616C) {
        if (this.bTearing) {
            super.transmit(apdu, false);
            this.bTearing = false;
        }
        else {
            super.transmit(apdu, autoProc616C);
        }
    }
    
    public static int clockToTime(final int us, final int frequence) {
        return 0;
    }
    
    public static int timeToClock(final int clockCount) {
        return 0;
    }
    
    static MP300Reader checkReader(final String name, final int index) {
        if (name.startsWith("MP")) {
            final MP300Reader r = new MP300Reader(index);
            final int a = r.getAttributeInt(1297088512);
            if (a == 1297130842) {
                return r;
            }
        }
        return null;
    }
    
    public static MP300Reader getReader() {
        final int c = SCDP.readerCount();
        MP300Reader r = null;
        for (int i = 0; i < c; ++i) {
            final String name = SCDP.readerGetName(i);
            r = checkReader(name, i);
            if (r != null) {
                break;
            }
        }
        return r;
    }
    
    public int getVoltage() {
        return this.getAttributeInt(1297088513);
    }
    
    public boolean setVoltage(final int vol) {
        return this.setAttributeInt(1297088513, vol);
    }
    
    public int getClockFrequency() {
        return this.getAttributeInt(1297088514);
    }
    
    public boolean setClockFrequency(final int frequency) {
        return this.setAttributeInt(1297088514, frequency);
    }
    
    public boolean setTearingClock(final int clock) {
        this.bTearing = true;
        return this.setAttributeInt(1297088515, clock);
    }
    
    public boolean enableWatchCmdTime() {
        return this.setAttributeInt(1297088517, 1);
    }
    
    public boolean disableWatchCmdTime() {
        return this.setAttributeInt(1297088517, 0);
    }
    
    public int getCmdTime() {
        return this.getAttributeInt(1297088517);
    }
    
    public boolean enableAutoPPS() {
        return this.setAttributeInt(1297088521, 1);
    }
    
    public boolean disableAutoPPS() {
        return this.setAttributeInt(1297088521, 0);
    }
    
    public byte[] sendPPS(final int FIDI) {
        return this.sendPPS(16, FIDI, 0, 0);
    }
    
    public byte[] sendPPS(final int PPS0, final int PPS1, final int PPS2, final int PPS3) {
        final byte[] pps = { (byte)PPS0, (byte)PPS1, (byte)PPS2, (byte)PPS3 };
        final boolean bRes = this.setAttribute(1297088516, pps);
        if (bRes) {
            return this.getAttribute(1297088516);
        }
        return null;
    }
    
    public boolean startMonitorCurrent(final int currentRange) {
        final byte[] param = new byte[8];
        this.attrSetInt(0, param, 0);
        this.attrSetInt(currentRange, param, 4);
        return this.setAttribute(1297088518, param);
    }
    
    public boolean stopMonitorCurrent() {
        return this.setAttributeInt(1297088518, 1);
    }
    
    public int getMonitorCurrentResult() {
        return this.getAttributeInt(1297088518);
    }
}
