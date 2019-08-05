package libs.trustconnector.scdp.smartcard;

import libs.trustconnector.scdp.util.*;

import libs.trustconnector.scdp.util.ByteArray;

public class ATR
{
    private byte[] atr;
    
    public ATR(final byte[] atr) {
        this.setATR(atr, 0, atr.length);
    }
    
    public ATR(final byte[] atr, final int offset, final int length) {
        this.setATR(atr, offset, length);
    }
    
    public ATR(final String atr) {
        this.setATR(atr);
    }
    
    public void setATR(final byte[] atr) {
        this.setATR(atr, 0, atr.length);
    }
    
    public void setATR(final String atr) {
        this.atr = ByteArray.convert(atr);
    }
    
    public void setATR(final byte[] atr, final int offset, final int length) {
        if (atr == null) {
            this.atr = null;
        }
        else {
            System.arraycopy(atr, offset, this.atr = new byte[length], 0, length);
        }
    }
    
    public boolean hasTCK() {
        byte u8Protocols = 0;
        byte u8Y = (byte)(this.atr[1] & 0xF0);
        byte u8Off = 2;
        while (u8Y != 0) {
            if ((u8Y & 0x10) == 0x10) {
                ++u8Off;
            }
            if ((u8Y & 0x20) == 0x20) {
                ++u8Off;
            }
            if ((u8Y & 0x40) == 0x40) {
                ++u8Off;
            }
            if ((u8Y & 0x80) == 0x80) {
                u8Y = (byte)(this.atr[u8Off] & 0xF0);
                final byte u8T = (byte)(this.atr[u8Off] & 0xF);
                if (0 == u8T) {
                    u8Protocols |= 0x1;
                }
                if (15 == u8T) {
                    u8Protocols |= 0x2;
                }
                ++u8Off;
            }
            else {
                u8Y = 0;
            }
        }
        return (u8Protocols & 0x3) == 0x3;
    }
    
    public byte[] getValue() {
        return this.atr;
    }
    
    public int getLength() {
        if (this.atr != null) {
            return this.atr.length;
        }
        return 0;
    }
    
    public byte[] getHistoryBytes() {
        byte u8Protocols = 0;
        byte u8Y = (byte)(this.atr[1] & 0xF0);
        byte u8Off = 2;
        while (u8Y != 0) {
            if ((u8Y & 0x10) == 0x10) {
                ++u8Off;
            }
            if ((u8Y & 0x20) == 0x20) {
                ++u8Off;
            }
            if ((u8Y & 0x40) == 0x40) {
                ++u8Off;
            }
            if ((u8Y & 0x80) == 0x80) {
                u8Y = (byte)(this.atr[u8Off] & 0xF0);
                final byte u8T = (byte)(this.atr[u8Off] & 0xF);
                if (0 == u8T) {
                    u8Protocols |= 0x1;
                }
                if (15 == u8T) {
                    u8Protocols |= 0x2;
                }
                ++u8Off;
            }
            else {
                u8Y = 0;
            }
        }
        int hisLen = this.atr.length - u8Off;
        if ((u8Protocols & 0x3) == 0x3) {
            --hisLen;
        }
        if (hisLen > 0) {
            final byte[] his = new byte[hisLen];
            System.arraycopy(this.atr, u8Off, his, 0, hisLen);
            return his;
        }
        return null;
    }
    
    public byte getTA1() {
        if ((this.atr[1] & 0x10) == 0x10) {
            return this.atr[2];
        }
        return -1;
    }
    
    public boolean isDirectConvension() {
        return this.atr != null && this.atr[0] == 59;
    }
    
    @Override
    public String toString() {
        return ByteArray.convert(this.atr);
    }
}
