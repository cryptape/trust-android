//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.trustconnector.scdp.smartcard.application;

import java.util.HashMap;

public class KeySet extends HashMap<Object, Key> {
    Object cookie;
    private static final long serialVersionUID = 1L;

    public KeySet(Object cookie) {
        this.cookie = cookie;
    }

    public void addKey(Key key) {
        this.put(key.getCookie(), key);
    }

    public Key getKey(Object cookie) {
        return (Key)this.get(cookie);
    }

    public int getCount() {
        return this.size();
    }

    public Object getCookie() {
        return this.cookie;
    }
}
