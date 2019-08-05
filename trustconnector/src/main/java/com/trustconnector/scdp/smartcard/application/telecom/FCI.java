//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.trustconnector.scdp.smartcard.application.telecom;

public class FCI extends SelectFileResponse {
    protected static final int FILE_TYPE_MF = 1;
    protected static final int FILE_TYPE_DF = 2;
    protected static final int FILE_TYPE_EF = 4;
    public static final int FILE_STRUCTURE_TRANSPARENT = 0;
    public static final int FILE_STRUCTURE_LINEAR_FIXED = 1;
    public static final int FILE_STRUCTURE_CYCLIC = 3;
    public static final int FCI_OFF_FILE_ID = 4;
    public static final int FCI_OFF_FILE_TYPE = 6;
    public static final int FCI_OFF_DF_FILE_Characteristic = 13;
    public static final int FCI_OFF_DF_DF_NUM = 14;
    public static final int FCI_OFF_DF_EF_NUM = 15;
    public static final int FCI_OFF_DF_CHV1_STATUS = 18;
    public static final int FCI_OFF_DF_UNBLOCK_CHV1_STATUS = 19;
    public static final int FCI_OFF_DF_CHV2_STATUS = 20;
    public static final int FCI_OFF_DF_UNBLOCK_CHV2_STATUS = 21;
    public static final int FCI_OFF_EF_FILE_SIZE = 2;
    public static final int FCI_OFF_EF_INC_ENABLE = 7;
    public static final int FCI_OFF_EF_ACC_UPDATE_READSEEK = 8;
    public static final int FCI_OFF_EF_ACC_INC = 9;
    public static final int FCI_OFF_EF_ACC_INV_REH = 10;
    public static final int FCI_OFF_EF_FILE_STATUS = 11;
    public static final int FCI_OFF_EF_FILE_STRUCTURE = 13;
    public static final int FCI_OFF_EF_RECORD_LEN = 14;

    public FCI(byte[] fci) {
        super(fci);
    }

    public int getFID() {
        return (this.response[4] << 8 | this.response[5] & 255) & '\uffff';
    }

    public int getFileType() {
        switch(this.response[6]) {
            case 1:
                return 1;
            case 2:
                return 2;
            case 4:
                switch(this.response[13]) {
                    case 0:
                        return 132;
                    case 1:
                        return 133;
                    case 2:
                    default:
                        break;
                    case 3:
                        return 134;
                }
            case 3:
            default:
                return -1;
        }
    }

    public boolean isEF() {
        return this.response[6] == 4;
    }

    public int getFileSize() {
        return !this.isEF() ? 0 : (this.response[2] << 8 | this.response[3] & 255) & '\uffff';
    }

    public int getRecordNumber() {
        if (this.isEF()) {
            int recLen = this.getRecordLen();
            if (recLen != 0) {
                int size = this.getFileSize();
                return size / recLen;
            }
        }

        return 0;
    }

    public int getRecordLength() {
        return this.isEF() && this.response.length >= 14 ? this.response[14] & 255 : 0;
    }

    public boolean isCHV1Disable() {
        return (this.response[13] & 128) == 128;
    }

    public int getDFNum() {
        return this.response[14] & 255;
    }

    public int getEFNum() {
        return this.response[15] & 255;
    }

    public int getCHV1LeftTryCount() {
        return this.response[18] & 15;
    }

    public int getPUK1LeftTryCount() {
        return this.response[19] & 15;
    }

    public int getCHV2LeftTryCount() {
        return this.response[20] & 15;
    }

    public int getPUK2LeftTryCount() {
        return this.response[21] & 15;
    }

    public boolean isIncreaseAllowed() {
        return this.getEFStructure() == 3 && (this.response[7] & 128) == 128;
    }

    public int getEFStructure() {
        return this.response[13] & 255;
    }

    public int getUpdateACC() {
        return this.response[8] & 15;
    }

    public int getReadACC() {
        return this.response[8] >> 4 & 15;
    }

    public int getSeekACC() {
        return this.getReadACC();
    }

    public int getIncreaseACC() {
        return this.response[9] >> 4 & 15;
    }

    public int getInvalidateACC() {
        return this.response[10] & 15;
    }

    public int getRehabilitateACC() {
        return this.response[10] >> 4 & 15;
    }

    public boolean isValid() {
        return (this.response[11] & 1) == 1;
    }

    public boolean canReadUpdateWhenInvalid() {
        return (this.response[11] & 4) == 4;
    }

    public int getRecordLen() {
        return this.response.length >= 14 ? this.response[14] & 255 : 0;
    }

    public int getRecordNum() {
        int recLen = this.getRecordLen();
        if (recLen != 0) {
            int size = this.getFileSize();
            return size / recLen;
        } else {
            return 0;
        }
    }

    public static String accToPIN(int acc) {
        switch(acc) {
            case 0:
                return "Always";
            case 1:
                return "CHV1";
            case 2:
                return "CHV2";
            case 4:
                return "CHV4";
            case 15:
                return "Never";
            default:
                return "Unkonwn ACC";
        }
    }

    public String toString() {
        String res = "File ID=";
        res = res + String.format("%04X", this.getFID());
        res = res + "\nFile Type=";
        int ft = this.response[6];
        switch(ft) {
            case 1:
                res = res + "MF";
                break;
            case 2:
                res = res + "DF";
                break;
            case 3:
            default:
                res = res + "Unknown File Type";
                break;
            case 4:
                res = res + "EF";
        }

        if (ft == 4) {
            res = res + "\nFile Structure=";
            switch(this.getEFStructure()) {
                case 0:
                    res = res + "Transprent";
                    break;
                case 1:
                    res = res + "Linear Fixed";
                    break;
                case 2:
                default:
                    res = res + "Unknown";
                    break;
                case 3:
                    res = res + "Cyclic";
            }

            res = res + "\nFile Size=" + String.format("%04X", this.getFileSize());
            res = res + "\nFile Increase Enable=";
            if (this.isIncreaseAllowed()) {
                res = res + "Enable";
            } else {
                res = res + "Disable";
            }

            res = res + "\nRead ACC=" + accToPIN(this.getReadACC());
            res = res + "\nUpdate ACC=" + accToPIN(this.getUpdateACC());
            res = res + "\nIncrease ACC=" + accToPIN(this.getIncreaseACC());
            res = res + "\nInvalidate ACC=" + accToPIN(this.getInvalidateACC());
            res = res + "\nRehabilitate ACC=" + accToPIN(this.getRehabilitateACC());
            res = res + "\nSeek ACC=" + accToPIN(this.getSeekACC());
            res = res + "\nFile State=";
            if (this.isValid()) {
                res = res + "Valid";
            } else {
                res = res + "Invalid";
            }

            res = res + "\nFile Can Read/Update When Invalid=";
            if (this.canReadUpdateWhenInvalid()) {
                res = res + "Yes";
            } else {
                res = res + "No";
            }
        } else {
            res = res + "\nCHV1 State=";
            if (this.isCHV1Disable()) {
                res = res + "Disable";
            } else {
                res = res + "Enable";
            }

            res = res + "\nDF Number=" + String.format("%02X", this.getDFNum());
            res = res + "\nEF Number=" + String.format("%02X", this.getEFNum());
            res = res + "\nCHV1 Left Try Count=" + String.format("%02X", this.getCHV1LeftTryCount());
            res = res + "\nUnblock CHV1 Left Try Count=" + String.format("%02X", this.getPUK1LeftTryCount());
            res = res + "\nCHV2 Left Try Count=" + String.format("%02X", this.getCHV2LeftTryCount());
            res = res + "\nUnblock CHV2 Left Try Count=" + String.format("%02X", this.getPUK2LeftTryCount());
        }

        return res;
    }
}
