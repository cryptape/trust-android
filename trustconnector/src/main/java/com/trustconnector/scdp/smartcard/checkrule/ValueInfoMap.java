package com.trustconnector.scdp.smartcard.checkrule;

import java.util.*;

public class ValueInfoMap extends HashMap<String, String>
{
    private static final long serialVersionUID = -5472354642058150665L;
    
    public ValueInfoMap() {
    }
    
    public ValueInfoMap(final String valueInfo) {
        this.buildMap(valueInfo);
    }
    
    public void buildMap(final String valueInfo) {
        final String[] v = valueInfo.split("\\|");
        for (int c = v.length, i = 0; i < c; i += 2) {
            this.put(v[i], v[i + 1]);
        }
    }
    
    public static Map<String, String> buildValueInfoMap(final String valueInfo) {
        return new ValueInfoMap(valueInfo);
    }
}
