package libs.trustconnector.scdp.util.tlv.simpletlv;

import libs.trustconnector.scdp.util.tlv.*;
import libs.trustconnector.scdp.util.tlv.bertlv.*;
import libs.trustconnector.scdp.util.*;

import libs.trustconnector.scdp.util.ByteArray;
import libs.trustconnector.scdp.util.StringFormat;
import libs.trustconnector.scdp.util.tlv.Length;
import libs.trustconnector.scdp.util.tlv.Tag;
import libs.trustconnector.scdp.util.tlv.bertlv.BERLength;

public class Language extends SimpleTLV
{
    public Language(final Tag tag, final Length len, final byte[] v, final int vOff) {
        super(tag, len, v, vOff);
    }
    
    public Language(final String language) {
        this.tag = new SimpleTag(45);
        this.len = new BERLength(2);
        final byte[] code = ByteArray.convert(language, StringFormat.ASCII);
        (this.value = new ByteArray(2)).setBytes(0, code);
    }
    
    public String getLanguage() {
        return ByteArray.convert(this.value.toBytes(), StringFormat.ASCII);
    }
}
