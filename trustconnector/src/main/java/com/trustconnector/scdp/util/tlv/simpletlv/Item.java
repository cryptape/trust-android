package com.trustconnector.scdp.util.tlv.simpletlv;

import com.trustconnector.scdp.util.tlv.*;
import com.trustconnector.scdp.util.*;

public class Item extends SimpleTLV
{
    public Item(final SimpleTag tag, final Length len, final byte[] v, final int vOff) {
        super(tag, len, v, vOff);
    }
    
    public boolean isEmpty() {
        return this.value.length() == 0;
    }
    
    public byte getItemID() {
        return this.value.getByte(0);
    }
    
    public String getItemText() {
        final byte retDCS = (byte)(this.value.getByte(1) & 0xFF);
        final byte[] textBytes = this.getItemTextBytes();
        if (retDCS == -128) {
            return ByteArray.convert(textBytes, StringFormat.UCS2);
        }
        if ((retDCS & 0x80) != 0x80) {
            return ByteArray.convert(textBytes, StringFormat.ASCII);
        }
        return "";
    }
    
    public byte[] getItemTextBytes() {
        final int retDCS = this.value.getByte(1) & 0xFF;
        int offset = 1;
        if ((retDCS & 0x80) == 0x80) {
            offset = 2;
        }
        return this.value.toBytes(offset, this.value.length() - offset);
    }
    
    public boolean isUCS2() {
        return this.value.getByte(1) == -128;
    }
    
    @Override
    public String toString() {
        if (this.isEmpty()) {
            return "no Item";
        }
        String res = "Item:" + super.toString();
        res = res + "\n    -Item ID=0x" + String.format("%02X", this.getItemID()) + ",Text=" + this.getItemText();
        return res;
    }
}
