package com.trustconnector.scdp.smartcard.application.globalplatform;

import com.trustconnector.scdp.util.tlv.bertlv.*;

public class Privilege
{
    private static final String[] privilegeName;
    private int privilege;
    public static final int PRIVILEGE_SD = 128;
    public static final int PRIVILEGE_DAP_VERIFY = 64;
    public static final int PRIVILEGE_DELEGATE = 32;
    public static final int PRIVILEGE_CARD_LOCK = 16;
    public static final int PRIVILEGE_CARD_TERMINATE = 8;
    public static final int PRIVILEGE_DEF_SELECT = 4;
    public static final int PRIVILEGE_CARD_RESET = 4;
    public static final int PRIVILEGE_CVM_MANAGE = 2;
    public static final int PRIVILEGE_MANDATE_DAP = 1;
    public static final int PRIVILEGE_TRUSTED_PATH = 32768;
    public static final int PRIVILEGE_AUTHORIZED_MANAGEMENT = 16384;
    public static final int PRIVILEGE_TOKEN_MANAGEMENT = 8192;
    public static final int PRIVILEGE_GLOBAL_DELETE = 4096;
    public static final int PRIVILEGE_GLOBAL_LOCK = 2048;
    public static final int PRIVILEGE_GLOBAL_REGISTRY = 1024;
    public static final int PRIVILEGE_FINAL_APPLICATION = 512;
    public static final int PRIVILEGE_GLOBAL_SERVICE = 256;
    public static final int PRIVILEGE_RECEIPT_GENERATION = 8388608;
    public static final int PRIVILEGE_CIPHERED_LOAD_FILE_DATA_BLOCK = 4194304;
    public static final int PRIVILEGE_CONTACTLESS_ACTIVATION = 2097152;
    public static final int PRIVILEGE_CONTACTLESS_SELF_ACTIVATION = 1048576;
    
    public Privilege() {
    }
    
    public Privilege(final int privilege) {
        this.privilege = privilege;
    }
    
    public Privilege(final byte[] privilege) {
        for (int i = 0; i < privilege.length; ++i) {
            this.privilege |= ((privilege[i] & 0xFF) << i * 8 & 0xFFFFFF);
        }
    }
    
    public Privilege(final byte[] privilege, final int off, final int length) {
        for (int i = 0; i < length; ++i) {
            this.privilege |= privilege[off + i];
        }
    }
    
    public boolean isPrivilegeSet(final int nPrivilege) {
        return (this.privilege & nPrivilege) == nPrivilege;
    }
    
    public void setPrivilege(final int nPrivilege) {
        this.privilege |= nPrivilege;
    }
    
    public String parseToString(final String split) {
        String rsp = "";
        final int tp = this.privilege;
        int mask = 1;
        for (int j = 0; j < 13; ++j) {
            if ((tp & mask) != 0x0) {
                if (j != 0) {
                    rsp += split;
                }
                rsp += Privilege.privilegeName[j];
            }
            mask <<= 1;
        }
        return rsp;
    }
    
    public byte[] toBytes() {
        if (this.privilege >= 256) {
            final byte[] p = { (byte)this.privilege, (byte)(this.privilege >> 8), (byte)(this.privilege >> 16) };
            return p;
        }
        final byte[] p = { (byte)this.privilege };
        return p;
    }
    
    public byte[] toLV() {
        return BERLVBuilder.buildLV(this.toBytes()).toBytes();
    }
    
    static {
        privilegeName = new String[] { "Mandated DAP Verification", "CVM Management", "Card Reset", "Card Terminate", "Card Lock", "Delegated Management", "DAP Verification", "Security Domain", "Global Service", "Final Application", "Global Registry", "Global Lock", "Global Delete", "Token Management", "Authorized Management", "Trusted Path", "RFU", "RFU", "RFU", "RFU", "Contactless Self-Activation", "Contactless Activation", "Ciphered Load File Data Block", "Receipt Generation" };
    }
}
