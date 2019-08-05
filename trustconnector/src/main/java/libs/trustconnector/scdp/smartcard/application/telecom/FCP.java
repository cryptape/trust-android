package libs.trustconnector.scdp.smartcard.application.telecom;

import libs.trustconnector.scdp.smartcard.*;
import libs.trustconnector.scdp.util.tlv.bertlv.*;
import libs.trustconnector.scdp.util.tlv.*;
import libs.trustconnector.scdp.util.*;

import libs.trustconnector.scdp.smartcard.AID;
import libs.trustconnector.scdp.util.ByteArray;
import libs.trustconnector.scdp.util.Util;
import libs.trustconnector.scdp.util.tlv.TLVTree;
import libs.trustconnector.scdp.util.tlv.TLVTreeItem;
import libs.trustconnector.scdp.util.tlv.bertlv.BERTLVBuilder;
import libs.trustconnector.scdp.util.tlv.bertlv.BERTLVParser;

public class FCP extends SelectFileResponse
{
    protected TLVTree fcp;
    protected static final int FILE_TYPE_WROKING_EF = 0;
    protected static final int FILE_TYPE_INTERNAL_EF = 8;
    protected static final int FILE_TYPE_DF_ADF = 56;
    public static final int LIFE_CYCLIC_STATUS_MASK_ACTIVATED = 5;
    public static final int LIFE_CYCLIC_STATUS_MASK_DEACTIVATED = 4;
    public static final int LIFE_CYCLIC_STATUS_MASK_TERMINATION = 12;
    public static final int FILE_STRUCTURE_TRANSPARENT = 1;
    public static final int FILE_STRUCTURE_LINEAR_FIXED = 2;
    public static final int FILE_STRUCTURE_CYCLIC = 6;
    
    public FCP(final byte[] fcp) {
        super(fcp);
        this.fcp = new TLVTree(fcp, new BERTLVParser());
    }
    
    @Override
    public int getFID() {
        final byte[] value = this.getValueOfTag("6283");
        if (value != null) {
            return (value[0] << 8 | (value[1] & 0xFF)) & 0xFFFF;
        }
        return -1;
    }
    
    @Override
    public int getFileType() {
        final byte[] value = this.getValueOfTag("6282");
        if (value != null) {
            final int t = value[0] & 0x38;
            if (t == 56) {
                if (this.getFID() == 16128) {
                    return 1;
                }
                if (this.getADFAID() != null) {
                    return 3;
                }
                return 2;
            }
            else if (t == 0 || t == 1) {
                switch (this.getFileStructure()) {
                    case 1: {
                        return 132;
                    }
                    case 2: {
                        return 133;
                    }
                    case 6: {
                        return 134;
                    }
                }
            }
        }
        return -1;
    }
    
    @Override
    public int getFileSize() {
        final byte[] value = this.getValueOfTag("6280");
        if (value != null) {
            return (value[0] << 8 | (value[1] & 0xFF)) & 0xFFFF;
        }
        return 0;
    }
    
    @Override
    public int getRecordNumber() {
        return this.getRecNum();
    }
    
    @Override
    public int getRecordLength() {
        return this.getRecLen();
    }
    
    public boolean isShareable() {
        final byte[] value = this.getValueOfTag("6282");
        return value != null && (value[0] & 0xC0) == 0x40;
    }
    
    public int getFileStructure() {
        final byte[] value = this.getValueOfTag("6282");
        if (value != null) {
            return value[0] & 0x7;
        }
        return -1;
    }
    
    public int getRecLen() {
        final byte[] value = this.getValueOfTag("6282");
        if (value != null && value.length == 5) {
            return (value[2] << 8 | (value[3] & 0xFF)) & 0xFFFF;
        }
        return -1;
    }
    
    public int getRecNum() {
        final byte[] value = this.getValueOfTag("6282");
        if (value != null && value.length == 5) {
            return value[4] & 0xFF;
        }
        return -1;
    }
    
    public int getTotalFileSize() {
        final byte[] value = this.getValueOfTag("6281");
        if (value != null) {
            return (value[0] << 8 | (value[1] & 0xFF)) & 0xFFFF;
        }
        return -1;
    }
    
    public int getSFI() {
        final byte[] value = this.getValueOfTag("6288");
        if (value == null) {
            return this.getFID() & 0x1F;
        }
        if (value.length == 0) {
            return 0;
        }
        return value[0] >> 3 & 0x1F;
    }
    
    public AID getADFAID() {
        final byte[] value = this.getValueOfTag("6284");
        if (value != null) {
            return new AID(value);
        }
        return null;
    }
    
    public int getLifeCycle() {
        final byte[] value = this.getValueOfTag("628A");
        if (value != null && value.length == 1) {
            return value[0];
        }
        return -1;
    }
    
    public byte[] getSecAttrRefExpFormat() {
        return this.getValueOfTag("628B");
    }
    
    public int getARRFileID() {
        final byte[] v = this.getValueOfTag("628B");
        if (v != null) {
            return Util.getShort(v, 0);
        }
        return -1;
    }
    
    public int getARRRecID() {
        return this.getARRRecID(0);
    }
    
    public int getARRRecID(final int SEID) {
        final byte[] v = this.getValueOfTag("628B");
        if (v != null) {
            if (v.length == 3) {
                return v[2] & 0xFF;
            }
            for (int i = 2; i < v.length; i += 2) {
                if ((v[i] & 0xFF) == SEID) {
                    return v[i + 1] & 0xFF;
                }
            }
        }
        return -1;
    }
    
    public byte[] getSecAttrCompactFormat() {
        return this.getValueOfTag("628C");
    }
    
    public byte[] getSecAttrExpandedFormat() {
        return this.getValueOfTag("62AB");
    }
    
    public byte[] getValueOfTag(final String tagPath) {
        final TLVTreeItem desc = this.fcp.findTLV(BERTLVBuilder.buildTagList(tagPath));
        if (desc != null) {
            return desc.getValue();
        }
        return null;
    }
    
    @Override
    public String toString() {
        String res = "";
        final byte[] v_6283 = this.getValueOfTag("6283");
        boolean bFirst = true;
        if (v_6283 != null) {
            res += "File ID=";
            res += String.format("%04X", this.getFID());
            bFirst = false;
        }
        final byte[] v_6284 = this.getValueOfTag("6282");
        if (!bFirst) {
            res += "\n";
            bFirst = false;
        }
        res += "File Type=";
        if ((v_6284[0] & 0x38) == 0x38) {
            res += "DF/ADF";
            final AID aid = this.getADFAID();
            if (aid != null) {
                if (bFirst) {
                    res += "\n";
                    bFirst = false;
                }
                res += "AID=";
                res += aid.toString();
            }
        }
        else {
            res += "EF";
        }
        if (!bFirst) {
            res += "\n";
            bFirst = false;
        }
        res += "Shareable=";
        if ((v_6284[0] & 0xC0) == 0x40) {
            res += "True";
        }
        else {
            res += "False";
        }
        res += "\nLife Cycle Status=";
        final int lc = this.getLifeCycle();
        if ((lc & 0x5) == 0x5) {
            res += "Activated";
        }
        else if ((lc & 0x4) == 0x4) {
            res += "Deactivated";
        }
        else if ((lc & 0xC) == 0xC) {
            res += "Termination";
        }
        else {
            res += "Unknown";
        }
        if ((v_6284[0] & 0x38) != 0x38) {
            res += "\nEF Structure=";
            final int efS = v_6284[0] & 0x7;
            switch (efS) {
                case 1: {
                    res += "transparent";
                    break;
                }
                case 2: {
                    res += "linear Fixed";
                    break;
                }
                case 6: {
                    res += "Cyclic";
                    break;
                }
                default: {
                    res += "Unknown";
                    break;
                }
            }
            res = res + "\nFile Size=0x" + String.format("%04X", this.getFileSize());
            if (v_6284.length > 2) {
                res = res + "\nRecord Length=0x" + String.format("%02X%02X", v_6284[2], v_6284[3]);
                res = res + "\nRecord Number=0x" + String.format("%02X", v_6284[4]);
            }
            final byte[] v_6285 = this.getValueOfTag("6288");
            if (v_6285 != null && v_6285.length > 0) {
                res = res + "\nFile SFI=0x" + String.format("%02X", v_6285[0] >> 3);
            }
        }
        final byte[] v_628B = this.getValueOfTag("628B");
        if (v_628B != null) {
            res = res + "\nARR Info=" + ByteArray.convert(v_628B);
        }
        return res;
    }
}
