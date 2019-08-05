package com.trustconnector.scdp.smartcard;

public interface APDUChecker
{
    void check(final APDU p0) throws APDUCheckException;
}
