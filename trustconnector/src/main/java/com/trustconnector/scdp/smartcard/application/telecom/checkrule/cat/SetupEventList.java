package com.trustconnector.scdp.smartcard.application.telecom.checkrule.cat;

public class SetupEventList extends ProactiveCommandChecker
{
    public SetupEventList(final int cmdType, final int srcDevID, final int dstDevID) {
        super(5, -127, -126);
    }
    
    @Override
    public boolean check() {
        return false;
    }
}
