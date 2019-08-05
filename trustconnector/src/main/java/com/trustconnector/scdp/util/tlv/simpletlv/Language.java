package com.trustconnector.scdp.util.tlv.simpletlv;

import com.trustconnector.scdp.util.tlv.*;
import com.trustconnector.scdp.util.tlv.bertlv.*;
import com.trustconnector.scdp.util.*;

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
