package libs.trustconnector.scdp.smartcard.application.telecom;

import libs.trustconnector.scdp.util.*;

import libs.trustconnector.scdp.util.ByteArray;
import libs.trustconnector.scdp.util.Util;

public abstract class SelectFileResponse
{
    protected byte[] response;
    public static final int FILE_TYPE_MF = 1;
    public static final int FILE_TYPE_DF = 2;
    public static final int FILE_TYPE_ADF = 3;
    public static final int FILE_TYPE_EF_TP = 132;
    public static final int FILE_TYPE_EF_LF = 133;
    public static final int FILE_TYPE_EF_CY = 134;
    public static final int FILE_TYPE_UNKONWN = -1;
    
    public SelectFileResponse(final byte[] rsp) {
        this.response = rsp.clone();
    }
    
    public boolean isEF() {
        return (this.getFileType() & 0x80) == 0x80;
    }
    
    public byte[] getResponse() {
        return this.response;
    }
    
    public int getResponseLength() {
        return this.response.length;
    }
    
    @Override
    public boolean equals(final Object fci) {
        if (super.equals(fci)) {
            return true;
        }
        if (fci instanceof FCI) {
            final FCI t = (FCI)fci;
            if (Util.arrayCompare(this.response, t.response)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        return ByteArray.convert(this.response);
    }
    
    public abstract int getFID();
    
    public abstract int getFileType();
    
    public abstract int getFileSize();
    
    public abstract int getRecordNumber();
    
    public abstract int getRecordLength();
}
