package libs.trustconnector.scdp.smartcard.application.globalplatform;

import libs.trustconnector.scdp.*;
import org.jdom2.filter.*;
import org.jdom2.*;
import org.jdom2.xpath.*;
import libs.trustconnector.scdp.smartcard.application.*;

import libs.trustconnector.scdp.SCDP;
import libs.trustconnector.scdp.crypto.AES;
import libs.trustconnector.scdp.crypto.DES;
import libs.trustconnector.scdp.crypto.RSA;
import libs.trustconnector.scdp.crypto.SHA_1;
import libs.trustconnector.scdp.smartcard.AID;
import libs.trustconnector.scdp.smartcard.APDU;
import libs.trustconnector.scdp.smartcard.application.Key;
import libs.trustconnector.scdp.smartcard.application.globalplatform.checkrule.*;
import libs.trustconnector.scdp.smartcard.javacard.cap.*;
import libs.trustconnector.scdp.smartcard.*;
import libs.trustconnector.scdp.util.tlv.bertlv.*;
import libs.trustconnector.scdp.util.tlv.*;
import libs.trustconnector.scdp.util.*;
import libs.trustconnector.scdp.crypto.*;
import java.security.*;
import java.security.interfaces.*;
import java.util.*;

import libs.trustconnector.scdp.smartcard.APDUChecker;
import libs.trustconnector.scdp.smartcard.NormalAPDUChecker;
import libs.trustconnector.scdp.smartcard.SmartCardReader;
import libs.trustconnector.scdp.smartcard.SmartCardReaderException;
import libs.trustconnector.scdp.smartcard.application.Application;
import libs.trustconnector.scdp.smartcard.application.KeySet;
import libs.trustconnector.scdp.smartcard.application.globalplatform.checkrule.SDSelectResponseChecker;
import libs.trustconnector.scdp.smartcard.javacard.cap.CAP;
import libs.trustconnector.scdp.util.ByteArray;
import libs.trustconnector.scdp.util.Util;
import libs.trustconnector.scdp.util.tlv.LV;
import libs.trustconnector.scdp.util.tlv.TLV;
import libs.trustconnector.scdp.util.tlv.bertlv.BERLVBuilder;
import libs.trustconnector.scdp.util.tlv.bertlv.BERTLVBuilder;

public class SD extends Application
{
    protected static Map<String, SD> sdMap;
    protected static SmartCardReader defReader;
    protected static ISD isd;
    protected static int defaultI;
    protected int defaultSecLevel;
    protected int defaultKeyVerision;
    protected SCP02 scp02;
    protected SD associateSD;
    public static final int GET_STATUS_P1_ISD = 128;
    public static final int GET_STATUS_P1_APP = 64;
    public static final int GET_STATUS_P1_PKG = 32;
    public static final int GET_STATUS_P1_CAP = 16;
    public static final int CARD_LIFE_CYCLE_OP_READY = 1;
    public static final int CARD_LIFE_CYCLE_NITIALIZED = 7;
    public static final int CARD_LIFE_CYCLE_SECURED = 15;
    public static final int CARD_LIFE_CYCLE_CARD_LOCKED = 127;
    public static final int CARD_LIFE_CYCLE_TERMINATED = 255;
    public static final int APP_LIFE_CYCLE_INSTALLED = 3;
    public static final int APP_LIFE_CYCLE_SELECTABLE = 7;
    public static final int SD_LIFE_CYCLE_INSTALLED = 3;
    public static final int SD_LIFE_CYCLE_SELECTABLE = 7;
    public static final int SD_LIFE_CYCLE_PERSONALIZED = 15;
    public static final int DGI_NORMAL = 112;
    public static final int DGI_SD_DATA_SCP_KEY_DATA = 36609;
    public static final int DGI_SD_DATA_SCP_KEY_RELATIED_DATA = 32513;
    public static final int DGI_SD_DATA_CARD_LIFE_CYCLE = 40816;
    public static final int DGI_SD_DATA_KEY_DERIVATION_DATA = 207;
    public static final int DGI_SD_DATA_DAP_KEY_DATA = 37122;
    public static final int DGI_SD_DATA_DAP_KEY_RELATIED_DATA = 258;
    public static final int TAG_SD_DATA_KEY_INFO = 224;
    public static final int TAG_SD_DATA_CONFIRM_COUNTER = 194;
    public static final int TAG_SD_DATA_TAG_SEQ = 193;
    public static final int TAG_SD_DATA_TAG_KEY = 192;
    public static final int TAG_SD_DATA_TAG_AID = 79;
    public static final int TAG_SD_DATA_TAG_KEY_DERIVATION_DATA = 207;
    public static final int TAG_SD_DATA_TAG_LIST_OF_APPLICATIONS = 12032;
    public static final int TAG_SD_DATA_TAG_MENU_PARAM = 65311;
    public static final int TAG_SD_DATA_TAG_CARD_RESOURCES = 65312;
    public static final int TAG_SD_DATA_TAG_EXT_CARD_RESOURCES = 65313;
    private static final byte[] zero;
    private static final byte[] LVzero;
    private static final byte[] installDefParam;
    static final String KEYSETS_ITEM_NAME = "keysets";
    static final String KEYSET_ITEM_NAME = "keyset";
    static final String KEYSET_ITEM_ATTR_VERSION = "version";
    static final String KEY_ITEM_NAME = "key";
    static final String KEY_ITEM_ATTR_ID = "id";
    static final String KEY_ITEM_ATTR_VALUE = "value";
    public static final int KEY_VERSION_TOKENVERIFY = 112;
    public static final int KEY_VERSION_RECEIPT = 113;
    public static final int KEY_VERSION_DAPVERIFY = 115;
    public static final int KEY_VERSION_CIPHER = 117;
    public static final byte LC_PKG_LOADED = 1;
    public static final byte LC_APP_INSTALLED = 3;
    public static final byte LC_APP_SELECTABLE = 7;
    public static final byte LC_APP_MASK_LOCKED = -125;
    public static final byte LC_SD_INSTALLED = 3;
    public static final byte LC_SD_SELECTABLE = 7;
    public static final byte LC_SD_PERSONALIZED = 15;
    public static final byte LC_SD_MASK_LOCKED = -125;
    public static final byte CARD_LC_OP_READY = 1;
    public static final byte CARD_LC_INITIALIZED = 7;
    public static final byte CARD_LC_SECURED = 15;
    public static final byte CARD_LC_CARD_LOCKED = Byte.MAX_VALUE;
    public static final byte CARD_LC_TERMINATED = -1;
    public static final short SW_MORE_DATA = 25360;
    public static final byte TAG_AID = 79;
    public static final byte TAG_TAG_LIST = 92;
    
    public static SD getSD(final AID aid) {
        if (aid == null) {
            return getISD();
        }
        SD sd = SD.sdMap.get(aid.toString());
        if (sd == null) {
            sd = new SD(SD.defReader, aid);
            SD.sdMap.put(aid.toString(), sd);
        }
        return sd;
    }
    
    public static ISD getISD() {
        if (SD.isd == null) {
            SD.isd = new ISD(SD.defReader);
        }
        return SD.isd;
    }
    
    public static void setDefaultReader(final SmartCardReader reader) {
        SD.defReader = reader;
    }
    
    public static SD getSD(final SmartCardReader reader, final AID aid) {
        if (aid == null) {
            return getISD(reader);
        }
        SD sd = SD.sdMap.get(aid.toString());
        if (sd == null) {
            sd = new SD(reader, aid);
            SD.sdMap.put(aid.toString(), sd);
        }
        sd.setReader(reader);
        return sd;
    }
    
    public static ISD getISD(final SmartCardReader reader) {
        if (SD.isd == null) {
            SD.isd = new ISD(reader);
        }
        SD.isd.setReader(reader);
        return SD.isd;
    }
    
    public static void setSCP02I(final int i) {
        SD.defaultI = i;
    }
    
    public static void onSDDelete(final AID aid) {
        SD.sdMap.remove(aid.toString());
    }
    
    protected SD(final SmartCardReader reader, final AID aid) {
        super(reader, aid);
        this.defaultKeyVerision = 0;
        this.defaultSecLevel = 1;
        this.updateKeyset();
        this.scp02 = new SCP02(this, SD.defaultI);
    }
    
    protected SD(final SmartCardReader reader, final AID aid, final int SCP02_I) {
        super(reader, aid);
        this.defaultKeyVerision = 0;
        this.updateKeyset();
        this.scp02 = new SCP02(this, SCP02_I);
    }
    
    protected void updateKeyset() {
        final Element root = SCDP.getCfgSubNode("keysets");
        if (root == null) {
            System.out.println("app keyset config root node not found:" + this.aid);
            return;
        }
        Element keysetNode = null;
        try {
            String exp = "./keyset[@appaid='";
            if (this.aid == null) {
                exp += "ISD";
            }
            else {
                exp += this.aid;
            }
            exp += "']";
            final XPathExpression<Element> xpath = (XPathExpression<Element>)XPathFactory.instance().compile(exp, Filters.element());
            keysetNode = (Element)xpath.evaluateFirst((Object)root);
        }
        catch (Exception ex) {}
        if (keysetNode == null) {
            return;
        }
        final List<Element> keyList = (List<Element>)keysetNode.getChildren("key");
        for (final Element keyNode : keyList) {
            final GPKey newKey = GPKey.creaetKey(keyNode);
            final Integer ver = newKey.getVersion();
            KeySet keyset = this.keySets.findKeySet(ver);
            if (keyset == null) {
                keyset = new SCPKeySet((int)ver);
                this.keySets.addKeySet(keyset);
            }
            keyset.addKey(newKey);
        }
    }
    
    @Override
    public void select() {
        this.select(true);
    }
    
    @Override
    public void select(final boolean bAutoProc616C) {
        this.scp02.close();
        final SDSelectResponseChecker sdFCP = new SDSelectResponseChecker();
        super.select(true, sdFCP);
        if (this.aid == null) {
            this.aid = new AID(sdFCP.sdAID.getReturnValue());
        }
    }
    
    @Override
    public void transmit(final APDU apdu, final String rule) {
        if (apdu.getChannel() == this.apdu.getChannel()) {
            switch (apdu.getIns()) {
                case -30: {
                    this.apdu.setName("Store Data");
                    break;
                }
                case -28: {
                    this.apdu.setName("Delete");
                    break;
                }
                case -26: {
                    this.apdu.setName("Install");
                    break;
                }
                case -24: {
                    this.apdu.setName("Load");
                    break;
                }
                case -40: {
                    this.apdu.setName("put key");
                    break;
                }
                case -14: {
                    this.apdu.setName("Get Status");
                    break;
                }
                case -16: {
                    this.apdu.setName("Set Status");
                    break;
                }
                case 80: {
                    this.scp02.initUpdate(apdu.getP1(), apdu.getCData());
                    return;
                }
                case -126: {
                    this.scp02.ExtAuth(apdu.getP1());
                    return;
                }
                case -54: {
                    this.apdu.setName("Get Data");
                    break;
                }
                case -92: {
                    this.apdu.setName("Select");
                    this.scp02.close();
                    break;
                }
            }
            this.transmitRawAPDU(apdu, rule);
            return;
        }
        super.transmit(apdu, rule);
    }
    
    @Override
    public void transmit(final APDU apdu) {
        if (apdu.getChannel() == this.apdu.getChannel()) {
            switch (apdu.getIns()) {
                case -30: {
                    this.apdu.setName("Store Data");
                    break;
                }
                case -28: {
                    this.apdu.setName("Delete");
                    break;
                }
                case -26: {
                    this.apdu.setName("Install");
                    break;
                }
                case -24: {
                    this.apdu.setName("Load");
                    break;
                }
                case -40: {
                    this.apdu.setName("put key");
                    break;
                }
                case -14: {
                    this.apdu.setName("Get Status");
                    break;
                }
                case -16: {
                    this.apdu.setName("Set Status");
                    break;
                }
                case 80: {
                    this.scp02.initUpdate(apdu.getP1(), apdu.getCData());
                    return;
                }
                case -126: {
                    this.scp02.ExtAuth(apdu.getP1());
                    return;
                }
                case -54: {
                    this.apdu.setName("Get Data");
                    break;
                }
                case -92: {
                    this.apdu.setName("Select");
                    this.scp02.close();
                    break;
                }
            }
            this.transmitRawAPDU(apdu);
            return;
        }
        super.transmit(apdu);
    }
    
    private void transmitRawAPDU(final APDU apdu, final String simpleRule) {
        if ((apdu.getCls() & 0x4) == 0x4) {
            apdu.setClass((byte)(apdu.getCls() & 0xFB));
            final byte[] cdata = apdu.getCData();
            final byte[] tcdata = new byte[cdata.length - 8];
            System.arraycopy(cdata, 0, tcdata, 0, tcdata.length);
            apdu.setCData(tcdata);
        }
        final APDUChecker checkerOld = this.apdu.setRAPDUChecker(apdu.getRAPDUChecker());
        this.apdu.setCAPDU(apdu.getCAPDU(), false);
        this.transmit(simpleRule);
        this.apdu.setRAPDUChecker(checkerOld);
    }
    
    private void transmitRawAPDU(final APDU apdu) {
        if ((apdu.getCls() & 0x4) == 0x4) {
            apdu.setClass((byte)(apdu.getCls() & 0xFB));
            final byte[] cdata = apdu.getCData();
            final byte[] tcdata = new byte[cdata.length - 8];
            System.arraycopy(cdata, 0, tcdata, 0, tcdata.length);
            apdu.setCData(tcdata);
        }
        final APDUChecker checkerOld = this.apdu.setRAPDUChecker(apdu.getRAPDUChecker());
        this.apdu.setCAPDU(apdu.getCAPDU(), false);
        this.transmit();
        this.apdu.setRAPDUChecker(checkerOld);
    }
    
    public void setAssociateSD(final SD assSD) {
        this.associateSD = assSD;
    }
    
    public SD getAssociateSD() {
        return this.associateSD;
    }
    
    @Override
    public void setChannel(final int channel) {
        super.setChannel(channel);
        this.scp02.setChannel(channel);
    }
    
    public void addSCP02Key(final int version, final int keyid, final byte[] keyV) {
        KeySet keyset = this.keySets.findKeySet(new Integer(version));
        if (keyset == null) {
            keyset = new SCPKeySet((int)new Integer(version));
            this.keySets.addKeySet(keyset);
        }
        final Key key = new GPKey(version, 128, keyid, keyV);
        keyset.addKey(key);
    }
    
    public Key getKey(final int version, final int keyid) {
        final KeySet keyset = this.keySets.findKeySet(new Integer(version));
        if (keyset == null) {
            return null;
        }
        return keyset.getKey(new Integer(keyid));
    }
    
    public byte[] getKeyValue(final int version, final int keyid) {
        final Key k = this.getKey(version, keyid);
        if (k == null) {
            return null;
        }
        return k.getValue();
    }
    
    public int getKeyType(final int version, final int keyid) {
        final Key k = this.getKey(version, keyid);
        if (k == null) {
            return 0;
        }
        if (!(k instanceof GPKey)) {
            return 0;
        }
        final GPKey gpkey = (GPKey)k;
        return gpkey.getType();
    }
    
    public SCP02 getSCP02Service() {
        return this.scp02;
    }
    
    public void closeSCP02() {
        this.scp02.close();
    }
    
    public void openSCP02() {
        this.openSCP02(this.defaultKeyVerision, this.defaultSecLevel);
    }
    
    public void openSCP02(final int scpkeyVersion, final int secLevel) {
        this.scp02.open(scpkeyVersion, secLevel);
    }
    
    public boolean loadPackage(final CAP cap) {
        return this.loadPackage(cap, null);
    }
    
    public boolean loadPackage(final CAP cap, final ByteArray param) {
        final AID aidPkg = cap.getAID();
        final NormalAPDUChecker checker = new NormalAPDUChecker();
        this.apdu.setRAPDUChecker(checker);
        final int cookie = SCDP.beginGroup("Load Package AID=" + aidPkg);
        boolean bRes = false;
        try {
            final ByteArray capContent = cap.getDownloadBytes(false, false);
            final ByteArray loadConent = new ByteArray("C4");
            loadConent.append(capContent.toBERLV());
            final byte[] data = capContent.toBytes();
            final byte[] hash = SHA_1.calc(data, 0, data.length);
            this.installForLoad(aidPkg, null, new ByteArray(hash), param);
            final byte[][] blocks = loadConent.split(240);
            final int blockCount = blocks.length - 1;
            for (int i = 0; i < blockCount; ++i) {
                this.load(i, blocks[i], false);
            }
            this.load(blockCount, blocks[blockCount], true);
            bRes = true;
        }
        catch (SmartCardReaderException e) {
            SCDP.reportError("Pkg Load Failed!");
            e.printStackTrace();
        }
        SCDP.endGroup(cookie);
        return bRes;
    }
    
    public boolean loadPackage(final String pkg) {
        final CAP cap = CAP.loadFromFile(pkg);
        if (cap == null) {
            SCDP.reportError("cap file not found:" + pkg);
            return false;
        }
        return this.loadPackage(cap);
    }
    
    public boolean loadPackage(final String pkg, final ByteArray param) {
        final CAP cap = CAP.loadFromFile(pkg);
        if (cap == null) {
            SCDP.reportError("cap file not found:" + pkg);
            return false;
        }
        return this.loadPackage(cap, param);
    }
    
    @Override
    protected void transmit() {
        this.scp02.wrap(this.apdu);
        super.transmit();
        this.scp02.unwrap(this.apdu);
    }
    
    @Override
    protected void transmit(final String simpleRule) {
        this.scp02.wrap(this.apdu);
        super.transmit(simpleRule);
        this.scp02.unwrap(this.apdu);
    }
    
    public void installForLoad(final AID pkgAID) {
        this.installForLoad(pkgAID, null, null, null);
    }
    
    public void installForLoad(final AID pkgAID, final AID sd, final ByteArray hash, final ByteArray param) {
        final NormalAPDUChecker c = new NormalAPDUChecker();
        c.setCheck(new byte[1], 36864);
        this.installForLoad(pkgAID, sd, hash, param, c);
    }
    
    public void installForLoad(final AID pkgAID, final AID sd, final ByteArray hash, final ByteArray param, final APDUChecker c) {
        this.apdu.setCAPDU("Install for load[" + pkgAID.toString() + "]", "80E6020000");
        this.apdu.appendCData(pkgAID.toLV());
        if (sd != null) {
            this.apdu.appendCData(sd.toLV());
        }
        else {
            this.apdu.appendCData(SD.zero);
        }
        if (hash != null) {
            this.apdu.appendCData(hash.toLV());
        }
        else {
            this.apdu.appendCData(SD.zero);
        }
        if (param != null) {
            this.apdu.appendCData(param.toBERLV());
        }
        else {
            this.apdu.appendCData(SD.zero);
        }
        this.apdu.appendCData(SD.zero);
        this.apdu.setRAPDUChecker(c);
        this.transmit();
    }
    
    public void installForLoad(final AID pkgAID, final AID sd, final ByteArray hash, final ByteArray param, final byte[] token) {
        final NormalAPDUChecker c = new NormalAPDUChecker();
        c.setCheck(new byte[1], 36864);
        this.installForLoad(pkgAID, sd, hash, param, token, c);
    }
    
    public void installForLoad(final AID pkgAID, final AID sd, final ByteArray hash, final ByteArray param, final byte[] token, final APDUChecker c) {
        this.apdu.setCAPDU("Install for load[" + pkgAID.toString() + "]", "80E6020000");
        this.apdu.appendCData(pkgAID.toLV());
        if (sd != null) {
            this.apdu.appendCData(sd.toLV());
        }
        else {
            this.apdu.appendCData(SD.zero);
        }
        if (hash != null) {
            this.apdu.appendCData(hash.toLV());
        }
        else {
            this.apdu.appendCData(SD.zero);
        }
        if (param != null) {
            this.apdu.appendCData(param.toBERLV());
        }
        else {
            this.apdu.appendCData(SD.zero);
        }
        this.apdu.appendCData(BERLVBuilder.buildLV(token).toBytes());
        this.apdu.setRAPDUChecker(c);
        this.transmit();
    }
    
    public void installForInstall(final String pkgAID, final String clsAID, final String app, final byte[] privilege, final byte[] param) {
        final NormalAPDUChecker c = new NormalAPDUChecker();
        c.setCheck(new byte[1], 36864);
        this.installForInstall(new AID(pkgAID), new AID(clsAID), new AID(app), new Privilege(privilege), new LV(param), c);
    }
    
    public void installForInstall(final AID pkgAID, final AID clsAID, final AID app, final Privilege privilege, final LV param) {
        final NormalAPDUChecker c = new NormalAPDUChecker();
        c.setCheck(new byte[1], 36864);
        this.installForInstall(pkgAID, clsAID, app, privilege, param, c);
    }
    
    public void installForInstall(final AID pkgAID, final AID clsAID, final AID app, final Privilege privilege, final LV param, final APDUChecker c) {
        this.apdu.setCAPDU("Install for install[" + app.toString() + "]", "80E6040000");
        this.apdu.appendCData(pkgAID.toLV());
        this.apdu.appendCData(clsAID.toLV());
        this.apdu.appendCData(app.toLV());
        if (privilege != null) {
            this.apdu.appendCData(privilege.toLV());
        }
        else {
            this.apdu.appendCData(SD.LVzero);
        }
        if (param != null) {
            this.apdu.appendCData(param.toBytes());
        }
        else {
            this.apdu.appendCData(SD.installDefParam);
        }
        this.apdu.appendCData(SD.zero);
        this.apdu.setRAPDUChecker(c);
        this.transmit();
    }
    
    public void installForInstall(final AID pkgAID, final AID clsAID, final AID app, final Privilege privilege, final LV param, final byte[] token) {
        final NormalAPDUChecker c = new NormalAPDUChecker();
        c.setCheck(new byte[1], 36864);
        this.installForInstall(pkgAID, clsAID, app, privilege, param, token, c);
    }
    
    public void installForInstall(final AID pkgAID, final AID clsAID, final AID app, final Privilege privilege, final LV param, final byte[] token, final APDUChecker c) {
        this.apdu.setCAPDU("Install for install[" + app.toString() + "]", "80E6040000");
        this.apdu.appendCData(pkgAID.toLV());
        this.apdu.appendCData(clsAID.toLV());
        this.apdu.appendCData(app.toLV());
        if (privilege != null) {
            this.apdu.appendCData(privilege.toLV());
        }
        else {
            this.apdu.appendCData(SD.LVzero);
        }
        if (param != null) {
            this.apdu.appendCData(param.toBytes());
        }
        else {
            this.apdu.appendCData(SD.installDefParam);
        }
        this.apdu.appendCData(BERLVBuilder.buildLV(token).toBytes());
        this.apdu.setRAPDUChecker(c);
        this.transmit();
    }
    
    public void installForMakeSelectable(final AID app, final Privilege privilege, final LV param) {
        final NormalAPDUChecker c = new NormalAPDUChecker();
        c.setCheck(new byte[1], 36864);
        this.installForMakeSelectable(app, privilege, param, c);
    }
    
    public void installForMakeSelectable(final AID app, final Privilege privilege, final LV param, final APDUChecker c) {
        this.apdu.setCAPDU("Install for install[" + app.toString() + "]", "80E6080000");
        this.apdu.appendCData(SD.zero);
        this.apdu.appendCData(SD.zero);
        this.apdu.appendCData(app.toLV());
        if (privilege != null) {
            this.apdu.appendCData(privilege.toLV());
        }
        else {
            this.apdu.appendCData(SD.LVzero);
        }
        if (param != null) {
            this.apdu.appendCData(param.toBytes());
        }
        else {
            this.apdu.appendCData(SD.zero);
        }
        this.apdu.appendCData(SD.zero);
        this.apdu.setRAPDUChecker(c);
        this.transmit();
    }
    
    public void installForMakeSelectable(final AID app, final Privilege privilege, final LV param, final byte[] token) {
        final NormalAPDUChecker c = new NormalAPDUChecker();
        c.setCheck(new byte[1], 36864);
        this.installForMakeSelectable(app, privilege, param, token);
    }
    
    public void installForMakeSelectable(final AID app, final Privilege privilege, final LV param, final byte[] token, final APDUChecker c) {
        this.apdu.setCAPDU("Install for install[" + app.toString() + "]", "80E6080000");
        this.apdu.appendCData(SD.zero);
        this.apdu.appendCData(SD.zero);
        this.apdu.appendCData(app.toLV());
        if (privilege != null) {
            this.apdu.appendCData(privilege.toLV());
        }
        else {
            this.apdu.appendCData(SD.LVzero);
        }
        if (param != null) {
            this.apdu.appendCData(param.toBytes());
        }
        else {
            this.apdu.appendCData(SD.zero);
        }
        this.apdu.appendCData(BERLVBuilder.buildLV(token).toBytes());
        this.apdu.setRAPDUChecker(c);
        this.transmit();
    }
    
    public void installForInstallAndMakeSel(final String pkgAID, final String clsAID, final String app, final byte[] privilege, final byte[] param) {
        final NormalAPDUChecker c = new NormalAPDUChecker();
        c.setCheck(new byte[1], 36864);
        this.installForInstallAndMakeSel(new AID(pkgAID), new AID(clsAID), new AID(app), new Privilege(privilege), new LV(param), c);
    }
    
    public void installForInstallAndMakeSel(final String pkgAID, final String clsAID, final String app, final byte[] privilege, final byte[] param, final String rule) {
        final NormalAPDUChecker c = new NormalAPDUChecker(rule);
        this.installForInstallAndMakeSel(new AID(pkgAID), new AID(clsAID), new AID(app), new Privilege(privilege), new LV(param), c);
    }
    
    public void installForInstallAndMakeSel(final AID pkgAID, final AID clsAID, final AID app, final Privilege privilege, final LV param) {
        final NormalAPDUChecker c = new NormalAPDUChecker();
        c.setCheck(new byte[1], 36864);
        this.installForInstallAndMakeSel(pkgAID, clsAID, app, privilege, param, c);
    }
    
    public void installForInstallAndMakeSel(final AID pkgAID, final AID clsAID, final AID app, final Privilege privilege, final LV param, final APDUChecker c) {
        this.apdu.setCAPDU("Install for install[" + app.toString() + "]", "80E60C0000");
        this.apdu.appendCData(pkgAID.toLV());
        this.apdu.appendCData(clsAID.toLV());
        this.apdu.appendCData(app.toLV());
        if (privilege != null) {
            this.apdu.appendCData(privilege.toLV());
        }
        else {
            this.apdu.appendCData(SD.LVzero);
        }
        if (param != null) {
            this.apdu.appendCData(param.toBytes());
        }
        else {
            this.apdu.appendCData(SD.installDefParam);
        }
        this.apdu.appendCData(SD.zero);
        this.apdu.setRAPDUChecker(c);
        this.transmit();
    }
    
    public void installForInstallAndMakeSel(final AID pkgAID, final AID clsAID, final AID app, final Privilege privilege, final LV param, final byte[] token) {
        this.apdu.setCAPDU("Install for install[" + app.toString() + "]", "80E60C0000");
        this.apdu.appendCData(pkgAID.toLV());
        this.apdu.appendCData(clsAID.toLV());
        this.apdu.appendCData(app.toLV());
        if (privilege != null) {
            this.apdu.appendCData(privilege.toLV());
        }
        else {
            this.apdu.appendCData(SD.LVzero);
        }
        if (param != null) {
            this.apdu.appendCData(param.toBytes());
        }
        else {
            this.apdu.appendCData(SD.installDefParam);
        }
        this.apdu.appendCData(BERLVBuilder.buildLV(token).toBytes());
        final NormalAPDUChecker c = new NormalAPDUChecker();
        c.setCheck(new byte[1], 36864);
        this.apdu.setRAPDUChecker(c);
        this.transmit();
    }
    
    public void installForExtradition(final AID sd, final AID app, final LV param) {
        final NormalAPDUChecker c = new NormalAPDUChecker();
        c.setCheck(new byte[1], 36864);
        this.installForExtradition(sd, app, param, c);
    }
    
    public void installForExtradition(final AID sd, final AID app, final LV param, final APDUChecker c) {
        this.apdu.setCAPDU("Install for Extradition,app[" + app.toString() + "] to SD[" + sd.toString() + "]", "80E6100000");
        this.apdu.appendCData(sd.toLV());
        this.apdu.appendCData(SD.zero);
        this.apdu.appendCData(app.toLV());
        this.apdu.appendCData(SD.zero);
        if (param != null) {
            this.apdu.appendCData(param.toBytes());
        }
        else {
            this.apdu.appendCData(SD.zero);
        }
        this.apdu.appendCData(SD.zero);
        this.apdu.setRAPDUChecker(c);
        this.transmit();
    }
    
    public void installForExtradition(final AID sd, final AID app, final LV param, final byte[] token) {
        final NormalAPDUChecker c = new NormalAPDUChecker();
        c.setCheck(new byte[1], 36864);
        this.installForExtradition(sd, app, param, token, c);
    }
    
    public void installForExtradition(final AID sd, final AID app, final LV param, final byte[] token, final APDUChecker c) {
        this.apdu.setCAPDU("Install for Extradition,app[" + app.toString() + "] to SD[" + sd.toString() + "]", "80E6100000");
        this.apdu.appendCData(sd.toLV());
        this.apdu.appendCData(SD.zero);
        this.apdu.appendCData(app.toLV());
        this.apdu.appendCData(SD.zero);
        if (param != null) {
            this.apdu.appendCData(param.toBytes());
        }
        else {
            this.apdu.appendCData(SD.zero);
        }
        this.apdu.appendCData(BERLVBuilder.buildLV(token).toBytes());
        this.apdu.setRAPDUChecker(c);
        this.transmit();
    }
    
    public void installForPersonalization(final AID app) {
        final NormalAPDUChecker c = new NormalAPDUChecker();
        c.setCheck(new byte[1], 36864);
        this.installForPersonalization(app, c);
    }
    
    public void installForPersonalization(final AID app, final String simpleRule) {
        final NormalAPDUChecker c = new NormalAPDUChecker(simpleRule);
        this.installForPersonalization(app, c);
    }
    
    public void installForPersonalization(final AID app, final APDUChecker c) {
        this.apdu.setCAPDU("Install for Personalization[" + app.toString() + "]", "80E6200000");
        this.apdu.appendCData(SD.zero);
        this.apdu.appendCData(SD.zero);
        this.apdu.appendCData(app.toLV());
        this.apdu.appendCData(SD.zero);
        this.apdu.appendCData(SD.zero);
        this.apdu.appendCData(SD.zero);
        this.apdu.setRAPDUChecker(c);
        this.transmit();
    }
    
    public void installForPersonalization(final AID app, final byte[] token) {
        final NormalAPDUChecker c = new NormalAPDUChecker();
        c.setCheck(new byte[1], 36864);
        this.installForPersonalization(app, token, c);
    }
    
    public void installForPersonalization(final AID app, final byte[] token, final APDUChecker c) {
        this.apdu.setCAPDU("Install for Personalization[" + app.toString() + "]", "80E6200000");
        this.apdu.appendCData(SD.LVzero);
        this.apdu.appendCData(SD.LVzero);
        this.apdu.appendCData(app.toLV());
        this.apdu.appendCData(SD.LVzero);
        this.apdu.appendCData(SD.LVzero);
        this.apdu.appendCData(BERLVBuilder.buildLV(token).toBytes());
        this.apdu.setRAPDUChecker(c);
        this.transmit();
    }
    
    public void load(final int num, final byte[] data, final boolean bLast) {
        final NormalAPDUChecker c = new NormalAPDUChecker();
        c.setCheck(new byte[1], 36864);
        this.load(num, data, bLast, c);
    }
    
    public void load(final int num, final byte[] data, final boolean bLast, final String simpleRule) {
        final NormalAPDUChecker c = new NormalAPDUChecker(simpleRule);
        this.load(num, data, bLast, c);
    }
    
    public void load(final int num, final byte[] data, final boolean bLast, final APDUChecker c) {
        this.apdu.setCAPDU("Load", "80E8000000");
        this.apdu.setP2((byte)num);
        if (bLast) {
            this.apdu.setP1(-128);
        }
        this.apdu.appendCData(data);
        this.apdu.setRAPDUChecker(c);
        this.transmit();
    }
    
    public void delete(final AID aid) {
        this.delete(aid, false, null, "009000");
    }
    
    public void delete(final AID aid, final String simpleRule) {
        this.delete(aid, false, null, new NormalAPDUChecker(simpleRule));
    }
    
    public void delete(final AID aid, final APDUChecker c) {
        this.delete(aid, false, null, c);
    }
    
    public void delete(final AID aid, final boolean bDeleteRefObj) {
        this.delete(aid, bDeleteRefObj, null, "9000");
    }
    
    public void delete(final AID aid, final boolean bDeleteRefObj, final String simpleRule) {
        this.delete(aid, bDeleteRefObj, null, new NormalAPDUChecker(simpleRule));
    }
    
    public void delte(final AID aid, final boolean bDeleteRefObj, final APDUChecker c) {
        this.delete(aid, bDeleteRefObj, null, c);
    }
    
    public void delete(final AID aid, final boolean bDeleteRefObj, final byte[] controlRef) {
        final NormalAPDUChecker c = new NormalAPDUChecker();
        c.setCheck(new byte[1], 36864);
        this.delete(aid, bDeleteRefObj, controlRef, c);
    }
    
    public void delete(final AID aid, final boolean bDeleteRefObj, final byte[] controlRef, final String simpleRule) {
        this.delete(aid, bDeleteRefObj, controlRef, new NormalAPDUChecker(simpleRule));
    }
    
    public void delete(final AID aid, final boolean bDeleteRefObj, final byte[] controlRef, final APDUChecker c) {
        this.apdu.setCAPDU("Delete " + aid.toString(), "80E4000000");
        if (bDeleteRefObj) {
            this.apdu.setP2(-128);
        }
        final TLV aid4F = BERTLVBuilder.buildTLV(79, aid.toBytes());
        final ByteArray data = new ByteArray(aid4F.toBytes());
        if (controlRef != null) {
            final TLV B6 = BERTLVBuilder.buildTLV(182, controlRef);
            data.append(B6.toBytes());
        }
        this.apdu.appendCData(data.toBytes());
        this.apdu.setRAPDUChecker(c);
        this.transmit();
        if (this.apdu.getSW() == 36864) {
            onSDDelete(aid);
        }
    }
    
    public void delete(final AID aid, final boolean bDeleteRefObj, final byte[] controlRef, final byte[] token) {
        final NormalAPDUChecker c = new NormalAPDUChecker();
        final int[] expSW = { -28672, 27272 };
        c.setCheck(expSW);
        this.delete(aid, bDeleteRefObj, controlRef, token, c);
    }
    
    public void delete(final AID aid, final boolean bDeleteRefObj, final byte[] controlRef, final byte[] token, final APDUChecker c) {
        this.apdu.setCAPDU("Delete Applet", "80E4000000");
        if (bDeleteRefObj) {
            this.apdu.setP2(-128);
        }
        final TLV aid4F = BERTLVBuilder.buildTLV(79, aid.toBytes());
        final ByteArray data = new ByteArray(aid4F.toBytes());
        if (controlRef != null) {
            final TLV B6 = BERTLVBuilder.buildTLV(182, controlRef);
            data.append(B6.toBytes());
        }
        this.apdu.appendCData(data.toBytes());
        this.apdu.appendCData(BERLVBuilder.buildLV(token).toBytes());
        final TLV Tag9E = BERTLVBuilder.buildTLV(158, token);
        this.apdu.appendCData(Tag9E.toBytes());
        this.apdu.setRAPDUChecker(c);
        this.transmit();
        if (this.apdu.getSW() == 36864) {
            onSDDelete(aid);
        }
    }
    
    public void deleteKey(final byte keyVer, final byte kid) {
        final NormalAPDUChecker c = new NormalAPDUChecker();
        c.setCheck("009000|6A88");
        this.deleteKey(keyVer, kid, c);
    }
    
    public void deleteKey(final byte keyVer, final byte kid, final String simpleRule) {
        this.deleteKey(keyVer, kid, new NormalAPDUChecker(simpleRule));
    }
    
    public void deleteKey(final byte keyVer, final byte kid, final APDUChecker c) {
        this.apdu.setCAPDU("Delete Key80E4000000");
        final ByteArray data = new ByteArray();
        if (kid != 255) {
            final TLV kidT = BERTLVBuilder.buildTLV(208, kid);
            data.append(kidT.toBytes());
        }
        if (keyVer != 255) {
            final TLV Tag9E = BERTLVBuilder.buildTLV(210, keyVer);
            data.append(Tag9E.toBytes());
        }
        this.apdu.appendCData(data.toBytes());
        this.apdu.setRAPDUChecker(c);
        this.transmit();
    }
    
    public void putDESKey(final int version, final int kid, final String keyValue) {
    }
    
    public void putDESKey() {
    }
    
    public void putKey(final KeySet ks) {
        this.putKey(ks, true, "9000|6A80");
        if (this.apdu.getSW() != 36864) {
            this.putKey(ks, false, "9000");
        }
    }
    
    public void putKey(final KeySet ks, final boolean bAddNew) {
        this.putKey(ks, bAddNew, new NormalAPDUChecker(36864));
        if (this.apdu.getSW() == 36864) {
            this.keySets.addKeySet(ks);
        }
    }
    
    public void putKey(final KeySet ks, final boolean bAddNew, final String simpleRule) {
        this.putKey(ks, bAddNew, new NormalAPDUChecker(simpleRule));
    }
    
    public void putKey(final KeySet ks, final boolean bAddNew, final APDUChecker c) {
        this.apdu.setCAPDU("Put Key", "80D8000100");
        final ByteArray cdata = new ByteArray();
        final Integer vers = (Integer)ks.getCookie();
        if (!bAddNew) {
            this.apdu.setP1(vers.byteValue());
        }
        cdata.append(vers.byteValue());
        if (ks.getCount() > 1) {
            this.apdu.setP2(129);
        }
        if (vers < 80) {
            for (int i = 1; i <= 3; ++i) {
                final GPKey key = (GPKey)ks.getKey(new Integer(i));
                cdata.append((byte)key.getType());
                final byte[] keyValS = key.getValue();
                final byte[] keyValE = this.scp02.encrypt(keyValS, 0, keyValS.length);
                cdata.append((byte)keyValE.length);
                cdata.append(keyValE);
                if (key.getType() == 128) {
                    final byte[] checkS = new byte[8];
                    final byte[] checkV = DES.doCrypto(checkS, keyValS, 273);
                    cdata.append((byte)3);
                    cdata.append(checkV, 0, 3);
                }
                else if (key.getType() == 136) {
                    final byte[] checkS = new byte[16];
                    Util.arrayFill(checkS, 0, 16, (byte)1);
                    final byte[] checkV = AES.encrypt(checkS, keyValS);
                    cdata.append((byte)3);
                    cdata.append(checkV, 0, 3);
                }
            }
        }
        this.apdu.appendCData(cdata.toBytes());
        this.apdu.setRAPDUChecker(c);
        this.transmit();
        if (this.apdu.getSW() == 36864) {
            this.keySets.addKeySet(ks);
        }
    }
    
    public byte[] getStatus(final int P1, final boolean rspTLV, final AID aid, final byte[] tagList, final boolean autoGetNext, final APDUChecker c) {
        String cmdName = "Get Status";
        switch (P1) {
            case -128: {
                cmdName = "Get Status(ISD)";
                break;
            }
            case 64: {
                cmdName = "Get Status(APP)";
                break;
            }
            case 32: {
                cmdName = "Get Status(PKG)";
                break;
            }
            case 16: {
                cmdName = "Get Status(CAP)";
                break;
            }
        }
        this.apdu.setCAPDU(cmdName, "80F2000000");
        this.apdu.setP1(P1);
        if (rspTLV) {
            this.apdu.setP2(2);
        }
        if (aid != null) {
            final TLV aidTLV = BERTLVBuilder.buildTLV(79, aid.toBytes());
            this.apdu.setCData(aidTLV.toBytes());
        }
        else {
            final byte[] cdata = { 79, 0 };
            this.apdu.setCData(cdata);
        }
        if (rspTLV && tagList != null) {
            final TLV tagListTLV = BERTLVBuilder.buildTLV(92, tagList);
            this.apdu.appendCData(tagListTLV.toBytes());
        }
        final ByteArray response = new ByteArray();
        final byte p2 = this.apdu.getP2();
        this.apdu.setRAPDUChecker(c);
        this.transmit(this.apdu);
        int sw = this.apdu.getSW();
        if (sw == 36864 || sw == 25360) {
            response.append(this.apdu.getRData());
        }
        if (autoGetNext) {
            while (sw == 25360) {
                this.apdu.setP2(p2 | 0x1);
                final NormalAPDUChecker ctemp = new NormalAPDUChecker();
                final int[] swE = { -28672, 25360 };
                ctemp.setCheck(swE);
                this.transmit(this.apdu);
                response.append(this.apdu.getRData());
                sw = this.apdu.getSW();
                if (sw == 36864) {
                    response.append(this.apdu.getRData());
                    break;
                }
            }
        }
        return response.toBytes();
    }
    
    public byte[] getStatusISD(final boolean rspTLV) {
        final NormalAPDUChecker ctemp = new NormalAPDUChecker();
        final int[] swE = { 36864, 25360 };
        ctemp.setCheck(swE);
        return this.getStatusISD(rspTLV, ctemp);
    }
    
    public byte[] getStatusISD(final boolean rspTLV, final APDUChecker c) {
        return this.getStatus(128, rspTLV, null, null, true, c);
    }
    
    public byte[] getStatusApp(final boolean rspTLV, final AID aid, final byte[] tagList) {
        final NormalAPDUChecker ctemp = new NormalAPDUChecker();
        final int[] swE = { 36864, 25360 };
        ctemp.setCheck(swE);
        return this.getStatusApp(rspTLV, aid, null, ctemp);
    }
    
    public byte[] getStatusApp(final boolean rspTLV, final AID aid, final byte[] tagList, final APDUChecker c) {
        return this.getStatus(64, rspTLV, aid, tagList, true, c);
    }
    
    public byte[] getStatusPKG(final boolean rspTLV, final AID aid, final byte[] tagList) {
        final NormalAPDUChecker ctemp = new NormalAPDUChecker();
        final int[] swE = { 36864, 25360 };
        ctemp.setCheck(swE);
        return this.getStatusPKG(rspTLV, aid, tagList, ctemp);
    }
    
    public byte[] getStatusPKG(final boolean rspTLV, final AID aid, final byte[] tagList, final APDUChecker c) {
        return this.getStatus(32, rspTLV, aid, tagList, true, c);
    }
    
    public byte[] getStatusCAP(final boolean rspTLV, final AID aid, final byte[] tagList) {
        final NormalAPDUChecker ctemp = new NormalAPDUChecker();
        final int[] swE = { 36864, 25360 };
        ctemp.setCheck(swE);
        return this.getStatusCAP(rspTLV, aid, tagList, ctemp);
    }
    
    public byte[] getStatusCAP(final boolean rspTLV, final AID aid, final byte[] tagList, final APDUChecker c) {
        return this.getStatus(16, rspTLV, aid, tagList, true, c);
    }
    
    public void setStatusISD(final byte status) {
        this.apdu.setName("Set ISD Status");
        this.apdu.setCAPDU("80F0800000");
        this.apdu.setP2(status);
        this.apdu.setRAPDUChecker(new NormalAPDUChecker(36864));
        this.transmit();
    }
    
    public void setStatusApp(final AID aid, final byte status) {
        this.apdu.setCAPDU("Set App Status", "80F0400000");
        this.apdu.setP2(status);
        this.apdu.setCData(aid.toBytes());
        this.apdu.setRAPDUChecker(new NormalAPDUChecker(36864));
        this.transmit();
    }
    
    public void setStatusSD(final AID aid, final byte status, final boolean bSetSubApp) {
        if (bSetSubApp) {
            this.apdu.setCAPDU("Set SD Status", "80F0600000");
        }
        else {
            this.apdu.setCAPDU("Set SD Status", "80F0400000");
        }
        this.apdu.setP2(status);
        this.apdu.setCData(aid.toBytes());
        this.apdu.setRAPDUChecker(new NormalAPDUChecker(36864));
        this.transmit();
    }
    
    public void storeData(final TLV tlv) {
        this.storeData(112, tlv.toBytes());
    }
    
    public void storeData(final TLV tlv, final APDUChecker c) {
        this.storeData(112, tlv.toBytes(), c);
    }
    
    public void storeData(final int DGI, final byte[] DGIdata) {
        this.storeData(DGI, DGIdata, new NormalAPDUChecker(36864));
    }
    
    public void storeData(final int DGI, final byte[] DGIdata, final APDUChecker c) {
        final ByteArray cdata = new ByteArray();
        cdata.append(DGI, 2);
        final LV lv = BERLVBuilder.buildLV(DGIdata);
        cdata.append(lv.toBytes());
        this.storeData(136, 0, cdata.toBytes(), c);
    }
    
    public void storeData(final int p1, final int p2, final byte[] cdata, final APDUChecker c) {
        this.apdu.setCAPDU("Store Data", "80E2000000");
        this.apdu.setP1(p1);
        this.apdu.setP2(p2);
        this.apdu.setCData(cdata);
        this.apdu.setRAPDUChecker(c);
        this.transmit();
    }
    
    public byte[] getData(final int tag) {
        this.getData(tag, new NormalAPDUChecker(36864));
        return this.apdu.getRData();
    }
    
    public void getData(final int tag, final APDUChecker c) {
        this.apdu.setCAPDU("Get Data", "80CA000000");
        this.apdu.setP1(tag >> 8);
        this.apdu.setP2(tag);
        this.apdu.setRAPDUChecker(c);
        this.transmit();
    }
    
    public KeySet decryptPutDESKeyCmd(final String putKeyAPDU, final int keyVersion, final int seqCounter) {
        final byte[] sessionKey = this.scp02.initSessionKey(keyVersion, 2, seqCounter);
        final byte[] apduPutKey = ByteArray.convert(putKeyAPDU);
        int putKeyVer = apduPutKey[2];
        if (putKeyVer == 0) {
            putKeyVer = apduPutKey[5];
        }
        int keyIdStart = (byte)(apduPutKey[3] & 0x7F);
        final KeySet ks = new KeySet(new Integer(putKeyVer));
        int checkValueLen;
        for (int apduLen = apduPutKey.length, offset = 6; offset + 14 <= apduLen; offset = ++offset + checkValueLen) {
            if (apduPutKey[offset] != -128) {
                return null;
            }
            ++offset;
            final int encKeyLen = apduPutKey[offset];
            if (encKeyLen != 8 && encKeyLen != 16) {
                return null;
            }
            ++offset;
            final byte[] keyV = DES.doCrypto(apduPutKey, offset, encKeyLen, sessionKey, 0, sessionKey.length, null, 0, 274);
            final GPKey key = new GPKey(putKeyVer, 128, keyIdStart, keyV);
            ks.addKey(key);
            ++keyIdStart;
            offset += encKeyLen;
            checkValueLen = apduPutKey[offset];
            if (checkValueLen != 3 && encKeyLen != 0) {
                return null;
            }
        }
        return ks;
    }
    
    public byte[] calcToken(final APDU apdu, final byte[] extData) {
        try {
            final Signature signature = Signature.getInstance("SHA1withRSA");
            final KeySet keyset = this.keySets.findKeySet(112);
            if (keyset == null) {
                return null;
            }
            final GPKey n = (GPKey)keyset.getKey(161);
            final GPKey d = (GPKey)keyset.getKey(163);
            final byte[] nValue = n.getValue();
            final byte[] dValue = d.getValue();
            final byte[] nv = new byte[nValue.length + 1];
            final byte[] dv = new byte[dValue.length + 1];
            System.arraycopy(nValue, 0, nv, 1, nValue.length);
            System.arraycopy(dValue, 0, dv, 1, dValue.length);
            final RSAPrivateKey priTokenKey = RSA.generateRSAPrivateKey(nv, dv);
            signature.initSign(priTokenKey);
            final byte[] data = apdu.getCAPDU();
            int length = data.length;
            if (apdu.getApduCase() == 4) {
                --length;
            }
            length -= 2;
            signature.update(data, 2, length);
            if (extData != null) {
                signature.update(extData);
            }
            return signature.sign();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    static {
        SD.sdMap = new HashMap<String, SD>();
        SD.defaultI = 21;
        zero = new byte[] { 0 };
        LVzero = new byte[] { 1, 0 };
        installDefParam = new byte[] { 2, -55, 0 };
    }
}
