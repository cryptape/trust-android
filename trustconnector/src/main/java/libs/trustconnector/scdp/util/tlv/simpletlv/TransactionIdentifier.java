package libs.trustconnector.scdp.util.tlv.simpletlv;

import libs.trustconnector.scdp.util.tlv.*;
import libs.trustconnector.scdp.util.tlv.bertlv.*;
import libs.trustconnector.scdp.util.*;

import libs.trustconnector.scdp.util.ByteArray;
import libs.trustconnector.scdp.util.tlv.Length;
import libs.trustconnector.scdp.util.tlv.Tag;
import libs.trustconnector.scdp.util.tlv.bertlv.BERLength;

public class TransactionIdentifier extends SimpleTLV
{
    public TransactionIdentifier(final Tag tag, final Length len, final byte[] v, final int vOff) {
        super(tag, len, v, vOff);
    }
    
    public TransactionIdentifier(final int id) {
        this.tag = new SimpleTag(37);
        this.len = new BERLength(1);
        (this.value = new ByteArray(1)).setByte(0, id);
    }
    
    public TransactionIdentifier(final byte[] transID) {
        this.tag = new SimpleTag(37);
        this.len = new BERLength(transID.length);
        this.value = new ByteArray(transID);
    }
}
