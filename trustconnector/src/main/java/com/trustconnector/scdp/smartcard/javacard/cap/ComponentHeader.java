package com.trustconnector.scdp.smartcard.javacard.cap;

import com.trustconnector.scdp.smartcard.*;

public class ComponentHeader extends Component
{
    public static final int DATA_OFF_MAGIC = 3;
    public static final int DATA_OFF_CAP_VER_MINOR = 7;
    public static final int DATA_OFF_CAP_VER_MAJOR = 8;
    public static final int DATA_OFF_FLAGS = 9;
    public static final int DATA_OFF_PKG_VER_MINOR = 10;
    public static final int DATA_OFF_PKG_VER_MAJOR = 11;
    public static final int DATA_OFF_PKG_AID_LEN = 12;
    public static final int DATA_OFF_PKG_AID = 13;
    public static final int MAGIC = -557121555;
    public static final int CAP_VERSION_2_1 = 33;
    public static final int CAP_VERSION_2_2 = 34;
    public static final int CAP_VERSION_3_0 = 48;
    public static final byte FLAGS_ACC_INT = 1;
    public static final byte FLAGS_ACC_EXPORT = 2;
    public static final byte FLAGS_ACC_APPLET = 4;
    
    public int getMagic() {
        return this.data.getInt(3, 4);
    }
    
    public byte getCapMinorVersion() {
        return this.data.getByte(7);
    }
    
    public byte getCapMajorVersion() {
        return this.data.getByte(8);
    }
    
    public int getCapVersion() {
        int version = this.data.getByte(8) & 0xFF;
        version <<= 4;
        version |= (this.data.getByte(7) & 0xFF);
        return version;
    }
    
    public byte getFlags() {
        return this.data.getByte(9);
    }
    
    public boolean isIntSupport() {
        return (this.data.getByte(9) & 0x1) == 0x1;
    }
    
    public boolean hasExportComponent() {
        return (this.data.getByte(9) & 0x2) == 0x2;
    }
    
    public boolean hasAppletComponent() {
        return (this.data.getByte(9) & 0x4) == 0x4;
    }
    
    public byte getPkgMinorVersion() {
        return this.data.getByte(10);
    }
    
    public byte getPkgMajorVersion() {
        return this.data.getByte(11);
    }
    
    public int getPkgVersion() {
        int version = this.data.getByte(11) & 0xFF;
        version <<= 4;
        version |= (this.data.getByte(10) & 0xFF);
        return version;
    }
    
    public AID getAID() {
        final byte aidLen = this.data.getByte(12);
        final AID aid = new AID(this.data.toBytes(13, aidLen));
        return aid;
    }
    
    public String getPkgName() {
        if (this.getCapVersion() < 34) {
            return null;
        }
        final byte aidLen = this.data.getByte(12);
        if (13 + aidLen + 1 >= this.data.length()) {
            return null;
        }
        final byte nameLen = this.data.getByte(13 + aidLen);
        if (nameLen == 0) {
            return null;
        }
        return new String(this.data.toBytes(13 + aidLen + 1, nameLen));
    }
}
