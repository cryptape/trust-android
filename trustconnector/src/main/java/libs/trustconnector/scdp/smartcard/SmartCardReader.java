package libs.trustconnector.scdp.smartcard;

import libs.trustconnector.scdp.*;
import libs.trustconnector.scdp.util.*;

import libs.trustconnector.scdp.SCDP;
import libs.trustconnector.scdp.util.ByteArray;

public class SmartCardReader
{
    int index;
    private String name;
    private ATR atr;
    private int protocol;
    static byte[] getResponse;
    public static final int PROTOCOL_T0 = 1;
    public static final int PROTOCOL_T1 = 2;
    
    public int getProtocol() {
        return this.protocol;
    }
    
    SmartCardReader(final int index) {
        this.index = index;
        this.name = SCDP.readerGetName(index);
    }
    
    public void connect() {
        final byte[] atrb = SCDP.readerConnect(this.index);
        if (atrb == null) {
            SmartCardReaderException.throwIt("Connect Reader Failed!Name:" + this.name);
        }
        if (this.atr == null) {
            this.atr = new ATR(atrb);
        }
        this.atr.setATR(atrb);
        this.protocol = SCDP.readerGetProtocol(this.index);
    }
    
    public void transmit(final APDU apdu) {
        this.transmit(apdu, true);
    }
    
    public void transmit(final APDU apdu, final String simpleCheckRule) {
        this.transmit(apdu, simpleCheckRule, true);
    }
    
    public void transmit(final APDU apdu, final String simpleCheckRule, final boolean autoProc616C) {
        final NormalAPDUChecker c = new NormalAPDUChecker(simpleCheckRule);
        apdu.setRAPDUChecker(c);
        this.transmit(apdu, autoProc616C);
    }
    
    public void transmit(final APDU apdu, final boolean autoProc616C) {
        final byte[] apdudata = apdu.getCAPDU();
        int apduDataLen = apdudata.length;
        if (apdu.getApduCase() == 4) {
            --apduDataLen;
        }
        byte[] resdata = SCDP.readerTransmit(this.index, apdu.getName(), apdudata, 0, apduDataLen);
        if (resdata == null || resdata.length < 2) {
            SmartCardReaderException.throwIt("Transmit APDU Exception:APDU=" + ByteArray.convert(apdudata));
        }
        final double time = SCDP.readerGetLastCmdTime(this.index);
        if (autoProc616C) {
            byte sw1 = resdata[resdata.length - 2];
            byte sw2 = resdata[resdata.length - 1];
            if (sw1 == 97) {
                final ByteArray rsp = new ByteArray();
                while (sw1 == 97) {
                    SmartCardReader.getResponse[0] = (byte)(apdudata[0] & 0x3);
                    SmartCardReader.getResponse[4] = sw2;
                    resdata = SCDP.readerTransmit(this.index, "Get Response", SmartCardReader.getResponse, 0, 5);
                    if (resdata == null || resdata.length < 2) {
                        SmartCardReaderException.throwIt("Transmit APDU Exception:APDU=" + ByteArray.convert(apdudata));
                    }
                    if (rsp.length() > 65535) {
                        SmartCardReaderException.throwIt("Transmit APDU Exception:APDU=" + ByteArray.convert(apdudata));
                    }
                    rsp.append(resdata, 0, resdata.length - 2);
                    sw1 = resdata[resdata.length - 2];
                    sw2 = resdata[resdata.length - 1];
                }
                rsp.append(sw1);
                rsp.append(sw2);
                apdu.setRAPDU(rsp.toBytes(), time);
            }
            else if (sw1 == 108 && apduDataLen <= 5) {
                apdudata[4] = sw2;
                resdata = SCDP.readerTransmit(this.index, apdu.getName(), apdudata, 0, 5);
                if (resdata == null || resdata.length < 2) {
                    SmartCardReaderException.throwIt("Transmit APDU Exception:APDU=" + ByteArray.convert(apdudata));
                }
                apdu.setRAPDU(resdata, time);
            }
            else {
                apdu.setRAPDU(resdata, time);
            }
        }
        else {
            apdu.setRAPDU(resdata, time);
        }
        final APDUChecker checker = apdu.getRAPDUChecker();
        if (checker == null) {
            return;
        }
        checker.check(apdu);
    }
    
    public boolean disconnect() {
        this.atr = null;
        return SCDP.readerDisConnect(this.index);
    }
    
    public void reset() {
        final byte[] atrb = SCDP.readerReset(this.index, true);
        if (atrb == null) {
            SmartCardReaderException.throwIt("Reset Reader Failed! Name:" + this.name);
        }
        if (this.atr == null) {
            this.atr = new ATR(atrb);
        }
        this.atr.setATR(atrb);
        this.protocol = SCDP.readerGetProtocol(this.index);
    }
    
    public void reset(final boolean bColdReset) {
        final byte[] atrb = SCDP.readerReset(this.index, bColdReset);
        if (atrb == null) {
            SmartCardReaderException.throwIt("Reset Reader Failed! Name:" + this.name);
        }
        if (this.atr == null) {
            this.atr = new ATR(atrb);
        }
        this.atr.setATR(atrb);
        this.protocol = SCDP.readerGetProtocol(this.index);
    }
    
    public String getName() {
        return this.name;
    }
    
    public ATR getATR() {
        return this.atr;
    }
    
    public String getATRString() {
        return this.atr.toString();
    }
    
    public byte[] getAttribute(final int attributeType) {
        return SCDP.readerGetAttr(this.index, attributeType);
    }
    
    public boolean setAttribute(final int attributeType, final byte[] newAttr) {
        return SCDP.readerSetAttr(this.index, attributeType, newAttr);
    }
    
    public int getAttributeInt(final int attrType) {
        final byte[] attr = this.getAttribute(attrType);
        if (attr != null) {
            return this.attrGetInt(attr, 0);
        }
        return -1;
    }
    
    public boolean setAttributeInt(final int attrType, final int attrValue) {
        final byte[] attr = new byte[4];
        this.attrSetInt(attrValue, attr, 0);
        return this.setAttribute(attrType, attr);
    }
    
    public String getAttributeString(final int attrType) {
        final byte[] attr = this.getAttribute(attrType);
        if (attr != null) {
            final StringBuilder s = new StringBuilder();
            final int f = this.attrGetStr(attr, 0, s);
            if (f == attr.length) {
                return s.toString();
            }
        }
        return null;
    }
    
    public boolean setAttributeString(final int attrType, final String attr) {
        final byte[] attrB = new byte[this.getAttrStrLen(attr)];
        this.attrSetStr(attr, attrB, 0);
        return this.setAttribute(attrType, attrB);
    }
    
    protected int attrSetInt(final int value, final byte[] destBuf, final int offset) {
        destBuf[offset] = (byte)(value >> 24);
        destBuf[offset + 1] = (byte)(value >> 16);
        destBuf[offset + 2] = (byte)(value >> 8);
        destBuf[offset + 3] = (byte)value;
        return 4;
    }
    
    protected int attrGetInt(final byte[] buf, final int offset) {
        int iRes = 0;
        iRes |= (buf[offset] & 0xFF);
        iRes <<= 8;
        iRes |= (buf[offset + 1] & 0xFF);
        iRes <<= 8;
        iRes |= (buf[offset + 2] & 0xFF);
        iRes <<= 8;
        iRes |= (buf[offset + 3] & 0xFF);
        return iRes;
    }
    
    protected int attrSetStr(final String content, final byte[] destBuf, int offset) {
        if (content != null && content.length() != 0) {
            final byte[] message = content.getBytes();
            offset += this.attrSetInt(message.length, destBuf, offset);
            System.arraycopy(message, 0, destBuf, offset, message.length);
            return message.length + 4;
        }
        return this.attrSetInt(0, destBuf, offset);
    }
    
    protected int attrGetStr(final byte[] buf, int offset, final StringBuilder builder) {
        final int length = this.attrGetInt(buf, offset);
        offset += 4;
        builder.delete(0, builder.capacity());
        for (int j = 0; j < length; ++j) {
            builder.append((char)buf[offset + j]);
        }
        return 4 + length;
    }
    
    private int getAttrStrLen(final String str) {
        if (str == null || str.length() == 0) {
            return 4;
        }
        return str.getBytes().length + 4;
    }
    
    static {
        SmartCardReader.getResponse = new byte[] { 0, -64, 0, 0, 0 };
    }
}
