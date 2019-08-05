package libs.trustconnector.scdp.util.tlv.simpletlv;

import libs.trustconnector.scdp.util.tlv.*;
import libs.trustconnector.scdp.util.tlv.bertlv.*;
import libs.trustconnector.scdp.util.*;

import libs.trustconnector.scdp.util.ByteArray;
import libs.trustconnector.scdp.util.tlv.Length;
import libs.trustconnector.scdp.util.tlv.Tag;
import libs.trustconnector.scdp.util.tlv.bertlv.BERLength;

public class ItemIdentifier extends SimpleTLV
{
    public ItemIdentifier(final Tag tag, final Length len, final byte[] v, final int vOff) {
        super(tag, len, v, vOff);
    }
    
    public ItemIdentifier(final byte id) {
        this.tag = new SimpleTag(16);
        this.len = new BERLength(1);
        final byte[] v = { id };
        this.value = new ByteArray(v);
    }
    
    public byte getID() {
        return this.value.getByte(0);
    }
    
    public void setID(final byte id) {
        this.value.setByte(0, id);
    }
}
