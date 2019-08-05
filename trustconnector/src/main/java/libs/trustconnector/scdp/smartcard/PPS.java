package libs.trustconnector.scdp.smartcard;

public class PPS
{
    private byte[] pps;
    
    public static int fromBytes(final byte[] pps, final int off, final int maxLength) {
        if (pps[off] == -1) {}
        return 0;
    }
    
    public byte[] getValue() {
        return this.pps;
    }
    
    public int getLength() {
        if (this.pps != null) {
            return this.pps.length;
        }
        return 0;
    }
}
