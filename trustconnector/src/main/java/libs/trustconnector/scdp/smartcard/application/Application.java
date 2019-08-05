package libs.trustconnector.scdp.smartcard.application;

import org.jdom2.*;
import libs.trustconnector.scdp.smartcard.*;
import libs.trustconnector.scdp.*;
import org.jdom2.filter.*;
import org.jdom2.xpath.*;

import libs.trustconnector.scdp.SCDP;
import libs.trustconnector.scdp.smartcard.AID;
import libs.trustconnector.scdp.smartcard.APDU;
import libs.trustconnector.scdp.smartcard.APDUChecker;
import libs.trustconnector.scdp.smartcard.NormalAPDUChecker;
import libs.trustconnector.scdp.smartcard.SmartCardReader;

public class Application
{
    protected SmartCardReader reader;
    protected AID aid;
    protected Element appCfgNode;
    protected KeySets keySets;
    protected APDU apdu;
    protected byte[] privilege;
    protected int lifeCycle;
    protected int channelNo;
    protected boolean bDisable616C;
    protected boolean bDisable616CTemp;
    private static final String APP_CFG_NODE_NAME = "apps";
    
    protected Application(final AID aid) {
        this(null, aid);
    }
    
    public Application(final SmartCardReader reader, final AID aid) {
        this.reader = reader;
        this.aid = aid;
        if (aid != null) {
            this.appCfgNode = getAppCfgNode(aid.toString());
        }
        this.keySets = new KeySets();
        this.apdu = new APDU("0000000000");
    }
    
    public void setReader(final SmartCardReader reader) {
        this.reader = reader;
    }
    
    public SmartCardReader getReader() {
        return this.reader;
    }
    
    public void transmit(final APDU apdu) {
        this.transmit(apdu, true);
    }
    
    public void transmit(final APDU apdu, final String simpleRule) {
        this.transmit(apdu, simpleRule, true);
    }
    
    public void transmit(final APDU apdu, final boolean bAutoProc61XX) {
        this.reader.transmit(apdu, bAutoProc61XX);
    }
    
    public void transmit(final APDU apdu, final String simpleRule, final boolean bAutoProc61XX) {
        this.reader.transmit(apdu, simpleRule, bAutoProc61XX);
    }
    
    public void reset() {
        this.reader.reset();
    }
    
    public AID getAID() {
        return this.aid;
    }
    
    public APDU getAPDU() {
        return this.apdu;
    }
    
    public int getLastSW() {
        return this.apdu.getSW();
    }
    
    public byte[] getLastRData() {
        return this.apdu.getRData();
    }
    
    public void setChannel(final int channel) {
        this.channelNo = channel;
    }
    
    public KeySets getKeySets() {
        return this.keySets;
    }
    
    public void select() {
        this.select(true);
    }
    
    public void select(final boolean bAutoProc616C) {
        this.select(bAutoProc616C, new NormalAPDUChecker(36864));
    }
    
    public void select(final boolean bAutoProc616C, final APDUChecker c) {
        String name = "Select";
        if (this.aid != null) {
            name += "[";
            name += this.aid.toString();
            name += "]";
        }
        this.apdu.setCAPDU(name, "00A4040000");
        if (this.aid != null) {
            this.apdu.setCData(this.aid.toBytes());
        }
        this.apdu.setRAPDUChecker(c);
        this.transmit(bAutoProc616C);
    }
    
    public void disable616C(final boolean bTemp) {
        this.bDisable616C = true;
        this.bDisable616CTemp = bTemp;
    }
    
    protected void transmit() {
        this.apdu.setChannel(this.channelNo);
        if (this.bDisable616C) {
            this.reader.transmit(this.apdu, false);
            if (this.bDisable616CTemp) {
                this.bDisable616C = false;
            }
        }
        else {
            this.reader.transmit(this.apdu);
        }
    }
    
    protected void transmit(final String simpleRule) {
        this.apdu.setChannel(this.channelNo);
        if (this.bDisable616C) {
            this.reader.transmit(this.apdu, simpleRule, false);
            if (this.bDisable616CTemp) {
                this.bDisable616C = false;
            }
        }
        else {
            this.reader.transmit(this.apdu, simpleRule);
        }
    }
    
    protected void transmit(final boolean bAutoProc616C) {
        this.apdu.setChannel(this.channelNo);
        this.reader.transmit(this.apdu, bAutoProc616C);
    }
    
    protected void transmit(final String simpleRule, final boolean bAutoProc616C) {
        this.apdu.setChannel(this.channelNo);
        this.reader.transmit(this.apdu, simpleRule, bAutoProc616C);
    }
    
    public static Element getAppCfgNode(final String aid) {
        final Element root = SCDP.getCfgSubNode("apps");
        if (root == null) {
            return null;
        }
        Element node = null;
        try {
            final XPathExpression<Element> xpath = (XPathExpression<Element>)XPathFactory.instance().compile("./app[@aid='" + aid + "']", Filters.element());
            node = (Element)xpath.evaluateFirst((Object)root);
        }
        catch (Exception ex) {}
        if (node == null) {
            System.out.println("app config node not found:" + aid);
        }
        return node;
    }
}
