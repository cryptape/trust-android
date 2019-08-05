package libs.trustconnector.scdp.util.tlv.simpletlv;

import libs.trustconnector.scdp.util.tlv.*;

import libs.trustconnector.scdp.util.tlv.Length;
import libs.trustconnector.scdp.util.tlv.Tag;

public class Duration extends SimpleTLV
{
    public static final int UNIT_MINITES = 0;
    public static final int UNIT_SECONDS = 1;
    public static final int UNIT_TENTHS_SECONDS = 2;
    
    public Duration(final Tag tag, final Length len, final byte[] v, final int vOff) {
        super(tag, len, v, vOff);
    }
    
    public int getUnit() {
        return this.value.getByte(0);
    }
    
    public int getTimeInterval() {
        return this.value.getByte(1) & 0xFF;
    }
    
    @Override
    public String toString() {
        String res = "Duration=" + super.toString();
        final int u = this.getUnit();
        String unit;
        if (u == 0) {
            unit = "Minite";
        }
        else if (u == 1) {
            unit = "Second";
        }
        else if (u == 2) {
            unit = "Tenth Seconds";
        }
        else {
            unit = "Unknown";
        }
        res = res + "\n    -Unit=:" + unit;
        res = res + "\n    -Invervalu=:" + this.getTimeInterval();
        return res;
    }
}
