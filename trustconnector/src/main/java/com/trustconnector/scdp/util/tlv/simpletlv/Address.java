package com.trustconnector.scdp.util.tlv.simpletlv;

import com.trustconnector.scdp.util.tlv.*;
import com.trustconnector.scdp.util.tlv.bertlv.*;
import com.trustconnector.scdp.util.*;

public class Address extends SimpleTLV
{
    public Address(final Tag tag, final Length len, final byte[] v, final int vOff) {
        super(tag, len, v, vOff);
    }
    
    public Address(final byte[] tlv, final int offset, final int maxLen) {
        super(6);
    }
    
    public Address(final byte[] addr) {
        this.tag = new SimpleTag(6);
        this.len = new BERLength(addr.length);
        this.value = new ByteArray(addr);
    }
    
    public String getAddr() {
        return null;
    }
    
    public void setAddr(final String number) {
    }
    
    public static byte[] encodingAddrLV(final String addr) {
        final StringBuilder addNum = new StringBuilder();
        byte TON = -127;
        if (addr.charAt(0) == '+') {
            TON = -111;
            addNum.append(addr.substring(1));
        }
        else {
            addNum.append(addr);
        }
        final int len = addNum.length();
        if ((addNum.length() & 0x1) == 0x1) {
            addNum.append('F');
        }
        final String revNum = Util.strHighLowRevert(addNum.toString());
        final String res = String.format("%02X%02X" + revNum, len, TON);
        return ByteArray.convert(res);
    }
    
    public static String decodingAddrLV(final byte[] addr, int offset) {
        final int strC = addr[offset];
        ++offset;
        final byte ton = addr[offset];
        ++offset;
        String revNum = ByteArray.convert(addr, offset, (strC + 1) / 2);
        revNum = Util.strHighLowRevert(revNum);
        if (strC % 2 == 1) {
            revNum = revNum.substring(0, revNum.length() - 1);
        }
        if (ton == 145) {
            return "+" + revNum;
        }
        return revNum;
    }
}
