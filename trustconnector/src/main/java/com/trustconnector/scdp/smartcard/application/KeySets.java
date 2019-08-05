//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.trustconnector.scdp.smartcard.application;

import java.util.HashMap;

public final class KeySets extends HashMap<Object, KeySet> {
    Object firstCookie;
    private static final long serialVersionUID = 1L;

    public KeySets() {
    }

    public void addKeySet(KeySet keyset) {
        if (this.firstCookie == null) {
            this.firstCookie = keyset.getCookie();
        }

        this.put(keyset.getCookie(), keyset);
    }

    public KeySet findKeySet(Object cookie) {
        return (KeySet)this.get(cookie);
    }

    public boolean isKeySetExist(Object cookie) {
        return this.containsKey(cookie);
    }

    public Object getFirstCookie() {
        return this.firstCookie;
    }
}
