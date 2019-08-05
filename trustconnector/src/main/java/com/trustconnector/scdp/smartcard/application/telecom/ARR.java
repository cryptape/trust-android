package com.trustconnector.scdp.smartcard.application.telecom;

import com.trustconnector.scdp.util.*;
import com.trustconnector.scdp.util.tlv.bertlv.*;
import java.util.*;
import com.trustconnector.scdp.util.tlv.*;

public class ARR
{
    TLVList dataObjectList;
    boolean bEF;
    public static final BERTag TAG_ACC_MODE_BYTE;
    public static final BERTag TAG_ACC_MODE_INS;
    public static final BERTag TAG_SEC_CONDITION_CONTROL_REF_TEMPL;
    public static final BERTag TAG_SEC_CONDITION_ALWAYS;
    public static final BERTag TAG_SEC_CONDITION_NEVER;
    public static final int ACC_TYPE_EF_DELETE = 64;
    public static final int ACC_TYPE_EF_TERMINATE = 32;
    public static final int ACC_TYPE_EF_ACTIVE = 16;
    public static final int ACC_TYPE_EF_DEACTIVE = 8;
    public static final int ACC_TYPE_EF_UPDATE = 2;
    public static final int ACC_TYPE_EF_READ = 1;
    public static final int ACC_TYPE_DF_DELETE_SELF = 64;
    public static final int ACC_TYPE_DF_TERMINATE = 32;
    public static final int ACC_TYPE_DF_ACTIVE = 16;
    public static final int ACC_TYPE_DF_DEACTIVE = 8;
    public static final int ACC_TYPE_DF_CREATE_DF = 4;
    public static final int ACC_TYPE_DF_CREATE_EF = 2;
    public static final int ACC_TYPE_DF_DELETE_CHILD = 1;
    public static final int ACC_TYPE_EF_INCREASE = 12800;
    public static final int ACC_TYPE_EF_RESIZE = 54272;
    
    public ARR(final byte[] arr) {
        this.bEF = true;
        this.fromBytes(arr);
    }
    
    public ARR(final String arr) {
        this.bEF = true;
        this.fromBytes(ByteArray.convert(arr));
    }
    
    public int getACC(final int accType) {
        final Iterator<TLV> ite = this.dataObjectList.iterator();
        while (ite.hasNext()) {
            final TLV tlv = ite.next();
            final Tag tag = tlv.getTag();
            final byte[] value = tlv.getValue();
            boolean bMatch = false;
            if (tag.equals(ARR.TAG_ACC_MODE_BYTE)) {
                if ((value[0] & accType) == accType) {
                    bMatch = true;
                }
            }
            else if (tag.equals(ARR.TAG_ACC_MODE_INS) && value[0] == (byte)(accType >> 8)) {
                bMatch = true;
            }
            if (ite.hasNext()) {
                final TLV acc = ite.next();
                if (!bMatch) {
                    continue;
                }
                final Tag accTag = acc.getTag();
                final byte[] accValue = acc.getValue();
                if (accTag.equals(ARR.TAG_SEC_CONDITION_CONTROL_REF_TEMPL)) {
                    final TLVTree a = new TLVTree(accValue);
                    final TLVTreeItem item = a.findTLV(BERTLVBuilder.buildTagList("83"));
                    final byte[] accPinRef = item.getValue();
                    return accPinRef[0] & 0xFF;
                }
                if (accTag.equals(ARR.TAG_SEC_CONDITION_ALWAYS)) {
                    return 0;
                }
                if (accTag.equals(ARR.TAG_SEC_CONDITION_NEVER)) {
                    return -1;
                }
                continue;
            }
        }
        return -1;
    }
    
    public void fromBytes(final byte[] arr) {
        this.dataObjectList = new TLVList(arr);
    }
    
    public byte[] toBytes() {
        return this.dataObjectList.toBytes();
    }
    
    public void setEFARR(final boolean bEF) {
        this.bEF = bEF;
    }
    
    public String toString(final boolean bEF) {
        String res = "";
        if (bEF) {
            res = "Read Acc=" + accToPIN(this.getACC(1));
            res = res + "\nUpdate Acc=" + accToPIN(this.getACC(2));
            res = res + "\nDeactive Acc=" + accToPIN(this.getACC(8));
            res = res + "\nActive Acc=" + accToPIN(this.getACC(16));
            res = res + "\nTerminate Acc=" + accToPIN(this.getACC(32));
            res = res + "\nDelete Acc=" + accToPIN(this.getACC(64));
            res = res + "\nIncrease Acc=" + accToPIN(this.getACC(12800));
            res = res + "\nResize Acc=" + accToPIN(this.getACC(54272));
        }
        else {
            res = "Delete Child Acc=" + accToPIN(this.getACC(1));
            res = res + "\nCreate EF Acc=" + accToPIN(this.getACC(2));
            res = res + "\nCreate DF Acc=" + accToPIN(this.getACC(4));
            res = res + "\nDeactive Acc=" + accToPIN(this.getACC(8));
            res = res + "\nActive Acc=" + accToPIN(this.getACC(16));
            res = res + "\nTerminate Acc=" + accToPIN(this.getACC(32));
            res = res + "\nDelete Self Acc=" + accToPIN(this.getACC(64));
        }
        return res;
    }
    
    public static String accToPIN(final int acc) {
        switch (acc) {
            case 0: {
                return "Always";
            }
            case 1: {
                return "PIN1";
            }
            case 129: {
                return "PIN2";
            }
            case 10: {
                return "ADM";
            }
            case -1: {
                return "Never";
            }
            default: {
                return "Unkonwn ACC";
            }
        }
    }
    
    public static void main(final String[] p) {
        final ARR a = new ARR("800101A406830101950108800102A406830181950108800118A40683010A950108");
        System.out.println(a.toString(true));
    }
    
    static {
        TAG_ACC_MODE_BYTE = new BERTag(128);
        TAG_ACC_MODE_INS = new BERTag(132);
        TAG_SEC_CONDITION_CONTROL_REF_TEMPL = new BERTag(164);
        TAG_SEC_CONDITION_ALWAYS = new BERTag(144);
        TAG_SEC_CONDITION_NEVER = new BERTag(151);
    }
}
