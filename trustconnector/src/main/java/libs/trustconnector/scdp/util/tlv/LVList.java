package libs.trustconnector.scdp.util.tlv;

import libs.trustconnector.scdp.util.*;
import java.util.*;

import libs.trustconnector.scdp.util.ByteArray;

public class LVList
{
    private Vector<LV> lvList;
    
    public LVList() {
        this.lvList = new Vector<LV>();
    }
    
    public static int fromBytes(final byte[] lvs, final int offset, final int length, final LVParser lvParser) {
        final LVList lvL = new LVList();
        return lvL.initFromBytes(lvs, offset, length, lvParser);
    }
    
    public int initFromBytes(final byte[] lvs, int offset, int length, final LVParser lvParser) {
        this.lvList.removeAllElements();
        int totalLen = 0;
        while (length > 0) {
            final LV lv = lvParser.parse(lvs, offset, length);
            if (lv == null) {
                break;
            }
            this.lvList.add(lv);
            final int lvLen = lv.getTotalLength();
            totalLen += lvLen;
            offset += lvLen;
            length -= lvLen;
        }
        return totalLen;
    }
    
    public byte[] toBytes() {
        final ByteArray value = new ByteArray();
        for (final LV lv : this.lvList) {
            value.append(lv.toBytes());
        }
        return value.toBytes();
    }
    
    public LV getLV(final int index) {
        return this.lvList.get(index);
    }
    
    public void remove(final int index) {
        this.lvList.remove(index);
    }
    
    public void append(final LV lv) {
        this.lvList.add(lv);
    }
    
    public void insert(final int index, final LV lv) {
        this.lvList.add(index, lv);
    }
}
