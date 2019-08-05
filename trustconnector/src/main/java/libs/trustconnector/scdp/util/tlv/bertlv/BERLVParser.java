package libs.trustconnector.scdp.util.tlv.bertlv;

import libs.trustconnector.scdp.util.tlv.*;

import libs.trustconnector.scdp.util.tlv.LV;
import libs.trustconnector.scdp.util.tlv.LVParser;
import libs.trustconnector.scdp.util.tlv.Length;

public class BERLVParser implements LVParser
{
    @Override
    public LV parse(final byte[] lv, final int offset, final int maxLen) {
        final Length len = new BERLength();
        final int lenOfLen = len.fromBytes(lv, offset, maxLen);
        if (lenOfLen == -1) {
            return null;
        }
        final int valueLen = len.getValue();
        if (valueLen > maxLen - lenOfLen) {
            return null;
        }
        return new LV(len, lv, offset + lenOfLen);
    }
}
