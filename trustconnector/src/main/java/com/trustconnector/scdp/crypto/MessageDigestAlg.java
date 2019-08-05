package com.trustconnector.scdp.crypto;

import com.trustconnector.scdp.*;
import java.security.*;
import java.util.*;

public abstract class MessageDigestAlg
{
    MessageDigest p_digest;
    static Map<String, MessageDigest> algMap;
    
    protected MessageDigestAlg(final String alg) {
        try {
            this.p_digest = MessageDigest.getInstance(alg);
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new SCDPException(e.getMessage());
        }
    }
    
    public MessageDigest getAlgObject() {
        return this.p_digest;
    }
    
    public void update(final byte[] msg) {
        this.p_digest.update(msg);
    }
    
    public void update(final byte[] msg, final int offset, final int length) {
        this.p_digest.update(msg, offset, length);
    }
    
    public byte[] doFinal(final byte[] msg) {
        this.p_digest.update(msg);
        return this.p_digest.digest();
    }
    
    public byte[] doFinal(final byte[] msg, final int offset, final int length) {
        this.p_digest.update(msg, offset, length);
        return this.p_digest.digest();
    }
    
    public byte[] doFinal() {
        return this.p_digest.digest();
    }
    
    public void reset() {
        this.p_digest.reset();
    }
    
    protected static byte[] calc(final String alg, final byte[] msg) {
        return calc(alg, msg, 0, msg.length);
    }
    
    protected static byte[] calc(final String alg, final byte[] msg, final int offset, final int length) {
        if (MessageDigestAlg.algMap == null) {
            MessageDigestAlg.algMap = new HashMap<String, MessageDigest>();
        }
        MessageDigest digest = MessageDigestAlg.algMap.get(alg);
        if (digest == null) {
            try {
                digest = MessageDigest.getInstance(alg);
                MessageDigestAlg.algMap.put(alg, digest);
            }
            catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return null;
            }
        }
        digest.reset();
        digest.update(msg, offset, length);
        return digest.digest();
    }
    
    static {
        MessageDigestAlg.algMap = new HashMap<String, MessageDigest>();
    }
}
