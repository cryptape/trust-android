package com.trustconnector.scdp.smartcard.application.globalplatform.checkrule;

import com.trustconnector.scdp.smartcard.checkrule.*;

public class InitUpdateResponseChecker extends APDURuleChecker
{
    public ResponseCheckRuleBytes keyDivData;
    public ResponseCheckRuleByte scpID;
    public ResponseCheckRuleByte keyVer;
    public ResponseCheckRuleShort seqCounter;
    public ResponseCheckRuleBytes cardChallenge;
    public ResponseCheckRuleBytes cardRCryptogram;
    
    public InitUpdateResponseChecker() {
        this.keyDivData = new ResponseCheckRuleBytes("Key diversification data", 0, 10);
        this.keyVer = new ResponseCheckRuleByte("Key Version", 10);
        this.scpID = new ResponseCheckRuleByte("Secure Channel Protole Identifier", 11);
        this.seqCounter = new ResponseCheckRuleShort("Sequence Counter", 12);
        this.cardChallenge = new ResponseCheckRuleBytes("Card challenge", 14, 6);
        this.cardRCryptogram = new ResponseCheckRuleBytes("Card cryptogram", 20, 8);
        this.setCheckSW("9000");
        this.addCheckRule(this.keyDivData);
        this.addCheckRule(this.keyVer);
        this.addCheckRule(this.scpID);
        this.addCheckRule(this.seqCounter);
        this.addCheckRule(this.cardChallenge);
        this.addCheckRule(this.cardRCryptogram);
    }
}
