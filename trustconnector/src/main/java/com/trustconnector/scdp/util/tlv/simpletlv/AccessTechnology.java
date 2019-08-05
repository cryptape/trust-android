package com.trustconnector.scdp.util.tlv.simpletlv;

import com.trustconnector.scdp.util.tlv.*;

public class AccessTechnology extends SimpleTLV
{
    public static final byte ACC_TECH_GSM = 0;
    public static final byte ACC_TECH_TIA_EIA_553 = 1;
    public static final byte ACC_TECH_TIA_EIA_136_C = 2;
    public static final byte ACC_TECH_UTRAN = 3;
    public static final byte ACC_TECH_TETRA = 4;
    public static final byte ACC_TECH_TIA_EIA_95 = 5;
    public static final byte ACC_TECH_CDMA2000_1X = 6;
    public static final byte ACC_TECH_CDMA2000_HRPD = 7;
    public static final byte ACC_TECH_E_UTRAN = 8;
    
    public AccessTechnology(final Tag tag, final Length len, final byte[] v, final int vOff) {
        super(tag, len, v, vOff);
    }
    
    public AccessTechnology(final byte accTech) {
        super(63);
        final byte[] v = { accTech };
        this.appendValue(v);
    }
    
    public AccessTechnology(final byte[] accTech) {
        super(63);
        this.appendValue(accTech);
    }
    
    public byte getAccTech() {
        return this.value.getByte(0);
    }
    
    public byte[] getAccTechs() {
        return this.value.toBytes();
    }
}
