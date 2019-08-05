package com.trustconnector.scdp.smartcard.application.edep;

import com.trustconnector.scdp.*;
import org.jdom2.filter.*;
import org.jdom2.*;
import org.jdom2.xpath.*;
import java.util.*;
import com.trustconnector.scdp.smartcard.application.*;
import com.trustconnector.scdp.util.*;
import com.trustconnector.scdp.smartcard.*;
import com.trustconnector.scdp.smartcard.checkrule.*;
import com.trustconnector.scdp.smartcard.application.edep.checker.*;
import com.trustconnector.scdp.crypto.*;

public class EDEP extends Application
{
    public boolean bShowSessionDetail;
    public String tradeDateTime;
    static final String KEYSETS_ITEM_NAME = "keysets";
    static final String KEYSET_ITEM_NAME = "keyset";
    static final String KEY_ITEM_NAME = "key";
    static final String KEY_ITEM_ATTR_TYPE = "type";
    static final String KEY_ITEM_ATTR_ID = "id";
    static final String KEY_ITEM_ATTR_VALUE = "value";
    public static final int APP_TYPE_ED = 1;
    public static final int APP_TYPE_EP = 2;
    public static final int TRADE_TYPE_ED_LOAD = 1;
    public static final int TRADE_TYPE_EP_LOAD = 2;
    public static final int TRADE_TYPE_UNLOAD = 3;
    public static final int TRADE_TYPE_ED_WITHDRAW = 4;
    public static final int TRADE_TYPE_ED_PURCHASE = 5;
    public static final int TRADE_TYPE_EP_PURCHASE = 6;
    public static final int TRADE_TYPE_ED_UPDATE = 7;
    public static final int TRADE_TYPE_CREDIT_PURCHASE = 8;
    
    public EDEP(final SmartCardReader reader, final AID aid) {
        super(reader, aid);
        this.bShowSessionDetail = false;
        this.loadKeyset();
    }
    
    protected void loadKeyset() {
        final Element root = SCDP.getCfgSubNode("keysets");
        if (root == null) {
            System.out.println("app keysets config root node not found:" + this.aid);
            return;
        }
        Element keysetNode = null;
        try {
            final String exp = "./keyset[@appaid='" + this.aid + "']";
            final XPathExpression<Element> xpath = (XPathExpression<Element>)XPathFactory.instance().compile(exp, Filters.element());
            keysetNode = (Element)xpath.evaluateFirst((Object)root);
        }
        catch (Exception e) {
            System.out.println("app keyset config root node not found:" + this.aid);
            return;
        }
        if (keysetNode == null) {
            System.out.println("app keyset config node not found:" + this.aid);
            return;
        }
        final List<Element> keyList = (List<Element>)keysetNode.getChildren("key");
        for (final Element keyNode : keyList) {
            final Key newKey = new EDEPKey(keyNode);
            KeySet keyset = this.keySets.findKeySet(new Integer(newKey.getType()));
            if (keyset == null) {
                keyset = new KeySet(new Integer(newKey.getType()));
                this.keySets.addKeySet(keyset);
            }
            keyset.addKey(newKey);
        }
    }
    
    public void updateKey(final int keyType, final int index, final byte[] keyValue) {
        final Key newKey = new EDEPKey(keyType, index, keyValue);
        KeySet keyset = this.keySets.findKeySet(new Integer(newKey.getType()));
        if (keyset == null) {
            keyset = new KeySet(new Integer(newKey.getType()));
            this.keySets.addKeySet(keyset);
        }
        keyset.addKey(newKey);
    }
    
    public byte[] getKeyValue(final int keyType, final int index) {
        final KeySet keyset = this.keySets.findKeySet(new Integer(keyType));
        if (keyset != null) {
            final Key key = keyset.getKey(new Integer(index));
            if (key != null && key instanceof EDEPKey) {
                final EDEPKey t = (EDEPKey)key;
                return t.getValue();
            }
        }
        return null;
    }
    
    public byte[] getKeyValue(final int keyType) {
        return this.getKeyValue(keyType, 0);
    }
    
    public boolean purchase(final int appType, final int keyIndex, final int amount, final String terminalNo, final String terminalExNo) {
        String groupName = "";
        int tradeType = 0;
        if (appType == 1) {
            groupName += "ED";
            tradeType = 5;
        }
        else {
            if (appType != 2) {
                return false;
            }
            groupName += "EP";
            tradeType = 6;
        }
        groupName += "\u6d88\u8d39";
        groupName += ":";
        groupName += String.format("%08X", amount);
        final int cookie = SCDP.beginGroup(groupName);
        this.initForPurchase(appType, keyIndex, amount, terminalNo);
        final ByteArray rsp = new ByteArray(this.apdu.getRData());
        final ByteArray sessionKeyData = new ByteArray();
        sessionKeyData.append(rsp.toBytes(11, 4));
        sessionKeyData.append(rsp.toBytes(4, 2));
        final ByteArray teminalExNoD = new ByteArray(terminalExNo);
        sessionKeyData.append(teminalExNoD.right(2));
        this.showSessionDetail("sessionKey Data", sessionKeyData.toBytes());
        final byte[] sessionKey = this.genSessionKey(tradeType, keyIndex, sessionKeyData.toBytes());
        this.showSessionDetail("sessionKey", sessionKey);
        final String dateTime = this.getDateTime();
        final ByteArray mac1Data = new ByteArray();
        mac1Data.append(amount, 4);
        mac1Data.append((byte)tradeType);
        mac1Data.append(ByteArray.convert(terminalNo));
        mac1Data.append(ByteArray.convert(dateTime));
        this.showSessionDetail("MAC1 data", mac1Data.toBytes());
        final byte[] mac1 = this.calcTMAC(sessionKey, mac1Data.toBytes());
        this.showSessionDetail("MAC1", mac1);
        final ByteArray tacData = new ByteArray();
        tacData.append(amount, 4);
        tacData.append((byte)tradeType);
        tacData.append(ByteArray.convert(terminalNo));
        tacData.append(ByteArray.convert(terminalExNo));
        tacData.append(ByteArray.convert(dateTime));
        this.showSessionDetail("TAC Data", tacData.toBytes());
        final byte[] tacKey = this.getKeyValue(7);
        this.showSessionDetail("TAC Key", tacKey);
        final byte[] tagSessionKey = this.genTACSessionKey(tacKey);
        this.showSessionDetail("TAC Session Key", tagSessionKey);
        final byte[] TAC = this.calcTMAC(tagSessionKey, tacData.toBytes());
        this.showSessionDetail("TAC", TAC);
        final byte[] amountB = Util.intToBytes(amount, 4);
        this.showSessionDetail("MAC2 Data", amountB);
        final byte[] MAC2 = this.calcTMAC(sessionKey, amountB);
        this.showSessionDetail("MAC2", MAC2);
        final DebitForPurchaseResponseChecker debetForPurchase = new DebitForPurchaseResponseChecker();
        debetForPurchase.TAC.setMatch(TAC);
        debetForPurchase.MAC2.setMatch(MAC2);
        this.debitForPurchase(terminalExNo, dateTime, mac1, debetForPurchase);
        final byte[] oldB = rsp.toBytes(0, 4);
        final int oldBalance = Util.bytesToInt(oldB);
        final int newBalance = oldBalance - amount;
        final APDURuleChecker c = new APDURuleChecker();
        c.setCheckSW(36864);
        final ResponseCheckRuleInt br = new ResponseCheckRuleInt("\u4f59\u989d", 0);
        br.setMatch(newBalance);
        c.addCheckRule(br);
        this.getBalance(appType, c);
        SCDP.endGroup(cookie);
        return true;
    }
    
    public boolean load(final int appType, final int keyIndex, final int amount, final String terminalNo) {
        String groupName = "";
        int tradeType = 0;
        if (appType == 1) {
            groupName += "ED";
            tradeType = 1;
        }
        else {
            if (appType != 2) {
                return false;
            }
            groupName += "EP";
            tradeType = 2;
        }
        groupName += "\u5708\u5b58";
        groupName += ":";
        groupName += String.format("%08X", amount);
        final int cookie = SCDP.beginGroup(groupName);
        this.initForLoad(appType, keyIndex, amount, terminalNo);
        final ByteArray rsp = new ByteArray(this.apdu.getRData());
        final byte[] balance = rsp.toBytes(0, 4);
        final byte[] onlineSN = rsp.toBytes(4, 2);
        final byte[] random = rsp.toBytes(8, 4);
        final byte[] mac1 = rsp.toBytes(12, 4);
        final ByteArray sessionKeyData = new ByteArray();
        sessionKeyData.append(random);
        sessionKeyData.append(onlineSN);
        final byte[] a = { -128, 0 };
        sessionKeyData.append(a);
        this.showSessionDetail("sessionKey Data", sessionKeyData.toBytes());
        final byte[] sessionKey = this.genSessionKey(tradeType, keyIndex, sessionKeyData.toBytes());
        this.showSessionDetail("sessionKey", sessionKey);
        final ByteArray mac1Data = new ByteArray();
        mac1Data.append(balance);
        final byte[] amountByte = Util.intToBytes(amount, 4);
        mac1Data.append(amountByte);
        mac1Data.append((byte)tradeType);
        mac1Data.append(ByteArray.convert(terminalNo));
        this.showSessionDetail("MAC1 Data", mac1Data.toBytes());
        final byte[] mac1Exp = this.calcTMAC(sessionKey, mac1Data.toBytes());
        this.showSessionDetail("MAC1", mac1Exp);
        if (!Util.arrayCompare(mac1Exp, 0, mac1, 0, 4)) {
            SCDP.reportAPDUExpErr("\u68c0\u9a8cMAC\u5931\u8d25,\u671f\u5f85:" + ByteArray.convert(mac1Exp) + ",\u5b9e\u9645\u8fd4\u56de:" + ByteArray.convert(mac1));
            SCDP.endGroup(cookie);
            return false;
        }
        final String dateTime = this.getDateTime();
        mac1Data.reinit();
        mac1Data.append(amountByte);
        mac1Data.append((byte)tradeType);
        mac1Data.append(ByteArray.convert(terminalNo));
        mac1Data.append(ByteArray.convert(dateTime));
        this.showSessionDetail("MAC2 Data", mac1Data.toBytes());
        final byte[] mac2 = this.calcTMAC(sessionKey, mac1Data.toBytes());
        this.showSessionDetail("MAC2", mac2);
        mac1Data.reinit();
        final int ob = Util.bytesToInt(balance);
        mac1Data.append(Util.intToBytes(ob + amount, 4));
        mac1Data.append(onlineSN);
        mac1Data.append(amountByte);
        mac1Data.append((byte)tradeType);
        mac1Data.append(ByteArray.convert(terminalNo));
        mac1Data.append(ByteArray.convert(dateTime));
        this.showSessionDetail("TAC Data", mac1Data.toBytes());
        final byte[] tacKey = this.getKeyValue(7);
        this.showSessionDetail("TAC Key", tacKey);
        final byte[] tagSessionKey = this.genTACSessionKey(tacKey);
        this.showSessionDetail("TAC Session Key", tagSessionKey);
        final byte[] TAC = this.calcTMAC(tagSessionKey, mac1Data.toBytes());
        this.showSessionDetail("TAC", TAC);
        final DebetForLoadResponseChecker debetLoad = new DebetForLoadResponseChecker();
        debetLoad.TAC1.setMatch(TAC);
        this.debetForLoad(dateTime, mac2, debetLoad);
        final int newBalance = ob + amount;
        final APDURuleChecker c = new APDURuleChecker();
        c.setCheckSW(36864);
        final ResponseCheckRuleInt br = new ResponseCheckRuleInt("\u4f59\u989d", 0);
        br.setMatch(newBalance);
        c.addCheckRule(br);
        this.getBalance(appType, c);
        SCDP.endGroup(cookie);
        return true;
    }
    
    public void verifyPIN() {
        this.verifyPIN(this.getKeyValue(12));
    }
    
    public void initForPurchase(final int appType, final int keyIndex, final int amount, final String terminalNo, final APDUChecker checker) {
        this.apdu.setCAPDU("Init For Purchase", "805001020B0100000001112233445566");
        this.apdu.setP2(appType);
        final ByteArray br = new ByteArray();
        br.append((byte)keyIndex);
        br.append(amount, 4);
        br.append(ByteArray.convert(terminalNo));
        this.apdu.setCData(br.toBytes());
        this.apdu.setRAPDUChecker(checker);
        this.transmit();
    }
    
    public void initForPurchase(final int appType, final int keyIndex, final int amount, final String terminalNo) {
        this.initForPurchase(appType, keyIndex, amount, terminalNo, new InitForPurchaseResponseChecker());
    }
    
    public void initForLoad(final int appType, final int keyIndex, final int amount, final String terminalNo, final APDUChecker checker) {
        this.apdu.setCAPDU("Init For Load", "805000020B0100000001112233445566");
        this.apdu.setP2(appType);
        final ByteArray br = new ByteArray();
        br.append((byte)keyIndex);
        br.append(amount, 4);
        br.append(ByteArray.convert(terminalNo));
        this.apdu.setCData(br.toBytes());
        this.apdu.setRAPDUChecker(checker);
        this.transmit();
    }
    
    public void initForLoad(final int appType, final int keyIndex, final int amount, final String terminalNo) {
        this.initForLoad(appType, keyIndex, amount, terminalNo, new InitForLoadResponseChecker());
    }
    
    public void debetForLoad(final String dateTime, final byte[] mac2, final APDUChecker checker) {
        this.apdu.setCAPDU("Debit For Load", "805200000B20151020114000000000");
        final ByteArray br = new ByteArray();
        br.append(ByteArray.convert(dateTime));
        br.append(mac2);
        this.apdu.setCData(br.toBytes());
        this.apdu.setRAPDUChecker(checker);
        this.transmit();
    }
    
    public void debetForLoad(final String dateTime, final byte[] mac2) {
        this.debetForLoad(dateTime, mac2, new DebetForLoadResponseChecker());
    }
    
    public void debitForPurchase(final String terminalExNo, final String dateTime, final byte[] mac1, final APDUChecker checker) {
        this.apdu.setCAPDU("Debit For Purchase", "805401000F0000002015102011400000112233");
        final ByteArray br = new ByteArray();
        br.append(ByteArray.convert(terminalExNo));
        br.append(ByteArray.convert(dateTime));
        br.append(mac1);
        this.apdu.setCData(br.toBytes());
        this.apdu.setRAPDUChecker(checker);
        this.transmit();
    }
    
    public void debitForPurchase(final String terminalExNo, final String dateTime, final byte[] mac1) {
        this.debitForPurchase(terminalExNo, dateTime, mac1, new DebitForPurchaseResponseChecker());
    }
    
    public void debitForCashWithdraw(final String terminalExNo, final String dateTime, final byte[] mac1, final APDUChecker checker) {
        this.apdu.setCAPDU("Debit For Cash Withdraw", "805401000F0000002015102011400000112233");
        final ByteArray br = new ByteArray();
        br.append(ByteArray.convert(terminalExNo));
        br.append(ByteArray.convert(dateTime));
        br.append(mac1);
        this.apdu.setCData(br.toBytes());
        this.apdu.setRAPDUChecker(checker);
        this.transmit();
    }
    
    public void debitForCashWithdraw(final String terminalExNo, final String dateTime, final byte[] mac1) {
        this.debitForCashWithdraw(terminalExNo, dateTime, mac1, new DebetForCashWithdrawResponseChecker());
    }
    
    public void getBalance(final int tradeType) {
        final APDURuleChecker c = new APDURuleChecker();
        c.setCheckSW(36864);
        this.getBalance(tradeType, c);
    }
    
    public void getBalance(final int tradeType, final APDUChecker checker) {
        this.apdu.setCAPDU("Get Balance", "805C000004");
        this.apdu.setP2(tradeType);
        this.apdu.setRAPDUChecker(checker);
        this.transmit();
    }
    
    public void verifyPIN(final byte[] pin) {
        this.apdu.setCAPDU("Verify PIN", "0020000000");
        this.apdu.setCData(pin);
        this.transmit();
    }
    
    public void selectFile(final AID aid) {
        this.apdu.setCAPDU("Select File", "00A4000000");
        this.apdu.setCData(aid.toBytes());
        this.transmit();
    }
    
    public void readBinary(final int sfi, final int offset, final int length) {
        this.apdu.setCAPDU("Read Binary", "00B0000000");
        this.apdu.setP1((sfi & 0x1F) | 0x80);
        this.apdu.setP2(offset);
        this.apdu.setP3(length);
        this.transmit();
    }
    
    public void updateBinary() {
    }
    
    public void readRecord() {
    }
    
    public void updateRecord() {
    }
    
    public void unblockPIN() {
    }
    
    public void applicationBlock() {
    }
    
    public void applicationUnblock() {
    }
    
    public void internalAuth() {
    }
    
    public void externalAuth(final byte[] extAuthData) {
        this.apdu.setCAPDU("External Auth", "0082000000");
        this.apdu.setCData(extAuthData);
        this.transmit();
    }
    
    public void getChallenge() {
        this.apdu.setCAPDU("Get Challenge", "0084000004");
        this.transmit();
    }
    
    private byte[] genSessionKey(final int tradeType, final int keyIndex, final byte[] data) {
        int keyType = 0;
        switch (tradeType) {
            case 1:
            case 2: {
                keyType = 5;
                break;
            }
            case 3: {
                keyType = 6;
                break;
            }
            case 4:
            case 5:
            case 6:
            case 8: {
                keyType = 4;
                break;
            }
            case 7: {
                keyType = 8;
                break;
            }
        }
        final byte[] keyValue = this.getKeyValue(keyType, keyIndex);
        return DES.doCrypto(data, keyValue, 273);
    }
    
    byte[] genTACSessionKey(final byte[] keyDTK) {
        final byte[] sk = new byte[8];
        ByteArray.xor(keyDTK, 0, keyDTK, 8, sk, 0, 8);
        return sk;
    }
    
    byte[] calcTMAC(final byte[] sessionKey, final byte[] data) {
        return DES.calcMAC(data, 4, sessionKey, null, 13089);
    }
    
    public void enableShowSessionDetail() {
        this.bShowSessionDetail = true;
    }
    
    public void disableShowSessionDetail() {
        this.bShowSessionDetail = false;
    }
    
    void showSessionDetail(final String msg, final byte[] data) {
        if (this.bShowSessionDetail) {
            final String log = msg + ":" + ByteArray.convert(data);
            SCDP.addLog(log);
        }
    }
    
    public void setTradeDateTime(final String tradeDateTime) {
        this.tradeDateTime = tradeDateTime;
    }
    
    public String getDateTime() {
        if (this.tradeDateTime == null) {
            return Util.getCurrentDateTime();
        }
        return this.tradeDateTime;
    }
}
