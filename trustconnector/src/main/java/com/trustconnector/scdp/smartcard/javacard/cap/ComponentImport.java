package com.trustconnector.scdp.smartcard.javacard.cap;

import com.trustconnector.scdp.smartcard.*;
import com.trustconnector.scdp.util.*;

public class ComponentImport extends Component
{
    public static final int DATA_OFF_IMPORT_COUNT = 3;
    public static final int DATA_OFF_PKG_INFO = 4;
    
    public int getImportCount() {
        return this.data.getUnsignedByte(3);
    }
    
    public AID[] getImportPkgs() {
        final int c = this.data.getUnsignedByte(3);
        if (c == 0) {
            return null;
        }
        final AID[] aids = new AID[c];
        int i = 0;
        int offset = 4;
        final byte[] d = this.data.toBytes();
        while (i++ < c) {
            offset += 2;
            final AID a = new AID(d, offset);
            aids[i] = a;
            offset += a.length() + 1;
        }
        return aids;
    }
    
    public int[] getImportPkgsVer() {
        final int c = this.data.getUnsignedByte(3);
        if (c == 0) {
            return null;
        }
        final int[] offsets = new int[c];
        int i = 0;
        int offset = 4;
        while (i++ < c) {
            offsets[i] = this.data.getInt2(offset);
            offset += 2;
            offset += this.data.getUnsignedByte(offset) + 1;
        }
        return offsets;
    }
    
    public AID getImportPkg(final int token) {
        final int c = this.data.getUnsignedByte(3);
        if (c == 0 || token > c) {
            return null;
        }
        int i = 0;
        int offset = 4;
        final byte[] d = this.data.toBytes();
        while (i++ < c) {
            offset += 2;
            if (i == token) {
                return new AID(d, offset);
            }
            offset += this.data.getUnsignedByte(offset) + 1;
        }
        return null;
    }
    
    public int getImportPkgVer(final int token) {
        final int c = this.data.getUnsignedByte(3);
        if (c == 0 || token > c) {
            throw new DataFormatException("pkg version not found with token=" + token);
        }
        int i = 0;
        int offset = 4;
        while (i++ < c) {
            if (i == token) {
                this.data.getInt2(offset);
            }
            offset += 2;
            offset += this.data.getUnsignedByte(offset) + 1;
        }
        throw new DataFormatException("pkg version not found with token=" + token);
    }
}
