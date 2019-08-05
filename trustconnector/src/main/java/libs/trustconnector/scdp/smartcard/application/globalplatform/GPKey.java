package libs.trustconnector.scdp.smartcard.application.globalplatform;

import libs.trustconnector.scdp.smartcard.application.*;
import org.jdom2.*;
import libs.trustconnector.scdp.util.*;

import libs.trustconnector.scdp.smartcard.application.Key;
import libs.trustconnector.scdp.util.XMLUtil;

public class GPKey implements Key
{
    protected byte[] value;
    int type;
    int version;
    int id;
    public static final int KEY_TYPE_DES = 128;
    public static final int KEY_TYPE_AES = 136;
    public static final int KEY_TYPE_RSA_PUBLIC_E = 160;
    public static final int KEY_TYPE_RSA_PUBLIC_N = 161;
    public static final int KEY_TYPE_RSA_PRIVATE_N = 162;
    public static final int KEY_TYPE_RSA_PRIVATE_D = 163;
    public static final int KEY_TYPE_RSA_PRIVATE_CRT_P = 164;
    public static final int KEY_TYPE_RSA_PRIVATE_CRT_Q = 165;
    public static final int KEY_TYPE_RSA_PRIVATE_CRT_DP = 166;
    public static final int KEY_TYPE_RSA_PRIVATE_CRT_DQ = 167;
    public static final int KEY_TYPE_RSA_PRIVATE_CRT_PQ = 168;
    public static final int KEY_TYPE_NULL = 0;
    public static final int KEY_VER_TOKEN_KEY = 112;
    public static final int KEY_VER_DAP_KEY = 113;
    public static final String KEY_TYPE_DES_S = "DES";
    public static final String KEY_TYPE_AES_S = "AES";
    public static final String KEY_TYPE_RSA_PUBLIC_E_S = "RSA_PUB_E";
    public static final String KEY_TYPE_RSA_PUBLIC_N_S = "RSA_PUB_N";
    public static final String KEY_TYPE_RSA_PRIVATE_N_S = "RSA_PRV_N";
    public static final String KEY_TYPE_RSA_PRIVATE_D_S = "RSA_PRV_D";
    public static final String KEY_TYPE_RSA_PRIVATE_CRT_P_S = "RSA_PRV_CRT_P";
    public static final String KEY_TYPE_RSA_PRIVATE_CRT_Q_S = "RSA_PRV_CRT_Q";
    public static final String KEY_TYPE_RSA_PRIVATE_CRT_DP_S = "RSA_PRV_CRT_DP";
    public static final String KEY_TYPE_RSA_PRIVATE_CRT_DQ_S = "RSA_PRV_CRT_DQ";
    public static final String KEY_TYPE_RSA_PRIVATE_CRT_PQ_S = "RSA_PRV_CRT_PQ";
    
    public GPKey() {
    }
    
    public GPKey(final int version, final int type, final byte[] value) {
        this.version = version;
        this.type = type;
        this.value = value.clone();
    }
    
    public GPKey(final int version, final int type, final int id, final byte[] value) {
        this.version = version;
        this.type = type;
        this.value = value.clone();
        this.id = id;
    }
    
    public GPKey(final Element keyNode) {
        this.version = XMLUtil.getNodeAttrHex(keyNode, "version");
        this.id = XMLUtil.getNodeAttrInt(keyNode, "id");
        this.value = XMLUtil.getNodeAttrBytes(keyNode, "value");
        final String typeS = keyNode.getAttributeValue("type");
        if (typeS == null || typeS.compareToIgnoreCase("DES") == 0) {
            this.type = 128;
        }
        else if (typeS.compareToIgnoreCase("RSA_PUB_N") == 0) {
            this.type = 161;
        }
        else if (typeS.compareToIgnoreCase("RSA_PUB_E") == 0) {
            this.type = 160;
        }
        else if (typeS.compareToIgnoreCase("RSA_PRV_N") == 0) {
            this.type = 162;
        }
        else if (typeS.compareToIgnoreCase("RSA_PRV_D") == 0) {
            this.type = 163;
        }
        else if (typeS.compareToIgnoreCase("RSA_PRV_CRT_P") == 0) {
            this.type = 164;
        }
        else if (typeS.compareToIgnoreCase("RSA_PRV_CRT_Q") == 0) {
            this.type = 165;
        }
        else if (typeS.compareToIgnoreCase("RSA_PRV_CRT_DP") == 0) {
            this.type = 166;
        }
        else if (typeS.compareToIgnoreCase("RSA_PRV_CRT_DQ") == 0) {
            this.type = 167;
        }
        else if (typeS.compareToIgnoreCase("RSA_PRV_CRT_PQ") == 0) {
            this.type = 168;
        }
        else if (typeS.compareToIgnoreCase("AES") == 0) {
            this.type = 136;
        }
    }
    
    @Override
    public int getLength() {
        return this.value.length;
    }
    
    @Override
    public byte[] getValue() {
        return this.value;
    }
    
    @Override
    public int getType() {
        return this.type;
    }
    
    public int getVersion() {
        return this.version;
    }
    
    public int getID() {
        return this.id;
    }
    
    @Override
    public Object getCookie() {
        return new Integer(this.id);
    }
    
    public void updateValue(final byte[] value) {
        this.value = value.clone();
    }
    
    public static GPKey creaetKey(final Element keyNode) {
        return new GPKey(keyNode);
    }
}
