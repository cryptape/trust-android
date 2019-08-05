package com.trustconnector.scdp.util.tlv.simpletlv;

import com.trustconnector.scdp.util.*;

public class CellBroadcastPage extends SimpleTLV
{
    int serialNum;
    int msgID;
    int DCS;
    int pageParam;
    byte[] msg;
    public static final int LEN = 88;
    
    public CellBroadcastPage() {
    }
    
    public CellBroadcastPage(final byte[] msg) {
        if (msg != null) {
            this.msg = msg.clone();
        }
    }
    
    public CellBroadcastPage(final int serialNum, final int msgID, final int DCS, final int count, final int curIndex, final byte[] msg) {
        this.serialNum = serialNum;
        this.msgID = msgID;
        this.DCS = DCS;
        this.pageParam = ((curIndex << 4 & 0xF0) | (count & 0xF));
        if (msg != null) {
            this.msg = msg.clone();
        }
    }
    
    public void setMsg(final byte[] msg) {
        if (msg != null) {
            this.msg = msg.clone();
        }
    }
    
    @Override
    public byte[] toBytes() {
        final byte[] res = new byte[90];
        res[0] = 12;
        res[1] = 88;
        res[2] = (byte)(this.serialNum >> 8);
        res[3] = (byte)this.serialNum;
        res[4] = (byte)(this.msgID >> 8);
        res[5] = (byte)this.msgID;
        res[6] = (byte)this.DCS;
        res[7] = (byte)this.pageParam;
        Util.arrayFill(res, 8, 82, (byte)(-1));
        if (this.msg != null) {
            System.arraycopy(this.msg, 0, res, 6, this.msg.length);
        }
        return res;
    }
}
