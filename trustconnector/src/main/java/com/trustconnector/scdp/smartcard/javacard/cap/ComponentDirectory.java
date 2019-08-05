package com.trustconnector.scdp.smartcard.javacard.cap;

import com.trustconnector.scdp.util.*;

public class ComponentDirectory extends Component
{
    private int CAP_VERSION;
    public static final int DATA_OFF_COMP_SIZE = 3;
    public static final int DATA_OFF_STATIC_IMG_SIZE_21 = 25;
    public static final int DATA_OFF_STATIC_ARR_INI_COUNT_21 = 27;
    public static final int DATA_OFF_STATIC_ARR_INI_SIZE_21 = 29;
    public static final int DATA_OFF_IMPORT_COUNT_21 = 31;
    public static final int DATA_OFF_APPLET_COUNT_21 = 32;
    public static final int DATA_OFF_CUSTOM_COUNT_21 = 33;
    public static final int DATA_OFF_CUSTOM_COMP_START_21 = 34;
    public static final int DATA_OFF_STATIC_IMG_SIZE_22 = 27;
    public static final int DATA_OFF_STATIC_ARR_INI_COUNT_22 = 29;
    public static final int DATA_OFF_STATIC_ARR_INI_SIZE_22 = 31;
    public static final int DATA_OFF_IMPORT_COUNT_22 = 33;
    public static final int DATA_OFF_APPLET_COUNT_22 = 34;
    public static final int DATA_OFF_CUSTOM_COUNT_22 = 35;
    public static final int DATA_OFF_CUSTOM_COMP_START_22 = 36;
    
    public ComponentDirectory() {
        this.CAP_VERSION = 34;
    }
    
    public void updateCapVersion(final int capVersion) {
        this.CAP_VERSION = capVersion;
    }
    
    public int getComponentSize(final byte componentTag) {
        int customComOff;
        if (this.CAP_VERSION <= 33) {
            if (componentTag < 12) {
                return this.data.getInt(3 + (componentTag - 1) * 2, 2);
            }
            customComOff = 34;
        }
        else {
            if (componentTag <= 12) {
                return this.data.getInt(3 + (componentTag - 1) * 2, 2);
            }
            customComOff = 36;
        }
        for (int length = this.data.length(); customComOff < length; customComOff += 2, customComOff += (this.data.getByte(customComOff) & 0xFF) + 1) {
            if (this.data.getByte(customComOff) == componentTag) {
                return this.data.getInt(customComOff + 1, 2);
            }
        }
        throw new DataFormatException("component with tag=" + componentTag + " not found!");
    }
    
    public int getStaticImageSize() {
        if (this.CAP_VERSION <= 33) {
            return this.data.getInt(25, 2);
        }
        return this.data.getInt(27, 2);
    }
    
    public int getStaticArrayInitCount() {
        if (this.CAP_VERSION <= 33) {
            return this.data.getInt(27, 2);
        }
        return this.data.getInt(29, 2);
    }
    
    public int getStaticArrayInitSize() {
        if (this.CAP_VERSION <= 33) {
            return this.data.getInt(29, 2);
        }
        return this.data.getInt(31, 2);
    }
    
    public int getImportCount() {
        if (this.CAP_VERSION <= 33) {
            return this.data.getUnsignedByte(31);
        }
        return this.data.getUnsignedByte(33);
    }
    
    public int getAppletCount() {
        if (this.CAP_VERSION <= 33) {
            return this.data.getUnsignedByte(32);
        }
        return this.data.getUnsignedByte(34);
    }
    
    public int getCustomCompCount() {
        if (this.CAP_VERSION <= 33) {
            return this.data.getUnsignedByte(33);
        }
        return this.data.getUnsignedByte(35);
    }
}
