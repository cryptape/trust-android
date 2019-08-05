package com.trustconnector.scdp.smartcard.application.telecom.checkrule.cat;

public class RefreshChecker extends ProactiveCommandChecker
{
    public RefreshChecker() {
        super(19, -127, -125);
    }
    
    @Override
    public boolean check() {
        return true;
    }
}
