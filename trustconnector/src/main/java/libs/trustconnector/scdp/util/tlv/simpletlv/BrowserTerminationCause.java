package libs.trustconnector.scdp.util.tlv.simpletlv;

import libs.trustconnector.scdp.util.tlv.*;

import libs.trustconnector.scdp.util.tlv.Length;
import libs.trustconnector.scdp.util.tlv.Tag;

public class BrowserTerminationCause extends SimpleTLV
{
    public static final byte CAUSE_USER_TERMINATION = 0;
    public static final byte CAUSE_ERROR_TERMINATION = 1;
    
    public BrowserTerminationCause(final Tag tag, final Length len, final byte[] v, final int vOff) {
        super(tag, len, v, vOff);
    }
    
    public BrowserTerminationCause(final byte cause) {
        super(52);
        final byte[] data = { cause };
        this.appendValue(data);
    }
    
    public byte getCause() {
        return this.value.getByte(0);
    }
}
