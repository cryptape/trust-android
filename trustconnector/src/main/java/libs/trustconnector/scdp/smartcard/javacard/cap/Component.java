package libs.trustconnector.scdp.smartcard.javacard.cap;

import libs.trustconnector.scdp.util.*;

import libs.trustconnector.scdp.util.ByteArray;

public abstract class Component
{
    ByteArray data;
    public static final int DATA_OFF_TAG = 0;
    public static final int DATA_OFF_LEN = 1;
    public static final int DATA_OFF_COM = 3;
    public static final byte COMPONENT_TAG_HEADER = 1;
    public static final byte COMPONENT_TAG_DIRECTORY = 2;
    public static final byte COMPONENT_TAG_APPLET = 3;
    public static final byte COMPONENT_TAG_IMPORT = 4;
    public static final byte COMPONENT_TAG_CONSTANTPOOL = 5;
    public static final byte COMPONENT_TAG_CLASS = 6;
    public static final byte COMPONENT_TAG_METHOD = 7;
    public static final byte COMPONENT_TAG_STATICFIELD = 8;
    public static final byte COMPONENT_TAG_REFERENCELOCATION = 9;
    public static final byte COMPONENT_TAG_EXPORT = 10;
    public static final byte COMPONENT_TAG_DESCRIPTOR = 11;
    public static final byte COMPONENT_TAG_DEBUG = 12;
    
    public byte getTag() {
        return this.data.getByte(0);
    }
    
    public int getTotalLen() {
        return this.data.length();
    }
    
    public int getDataLen() {
        return this.data.length() - 3;
    }
    
    public ByteArray getBytes() {
        return new ByteArray(this.data.toBytes());
    }
    
    public byte[] getRawBytes() {
        return this.data.toBytes();
    }
    
    protected void parse(final byte[] data) {
        this.data = new ByteArray(data);
    }
    
    public static Component buildComponent(final byte[] data) {
        Component com = null;
        switch (data[0]) {
            case 1: {
                com = new ComponentHeader();
                break;
            }
            case 2: {
                com = new ComponentDirectory();
                break;
            }
            case 3: {
                com = new ComponentApplet();
                break;
            }
            case 4: {
                com = new ComponentImport();
                break;
            }
            case 5: {
                com = new ComponentConstantPool();
                break;
            }
            case 6: {
                com = new ComponentClass();
                break;
            }
            case 7: {
                com = new ComponentMethod();
                break;
            }
            case 8: {
                com = new ComponentStaticField();
                break;
            }
            case 9: {
                com = new ComponentReferenceLocation();
                break;
            }
            case 10: {
                com = new ComponentExport();
                break;
            }
            case 11: {
                com = new ComponentDescriptor();
                break;
            }
            case 12: {
                com = new ComponentDebug();
                break;
            }
            default: {
                com = new ComponentSelDefine();
                break;
            }
        }
        com.parse(data);
        return com;
    }
}
