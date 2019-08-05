package libs.trustconnector.scdp.crypto;

import libs.trustconnector.scdp.util.*;

import libs.trustconnector.scdp.util.ByteArray;

public class ECCKeyPair
{
    byte[] publicKeyX;
    byte[] publicKeyY;
    byte[] privateKey;
    
    public ECCKeyPair(final byte[] publicKeyX, final byte[] publicKeyY, final byte[] privateKey) {
        this.publicKeyX = publicKeyX;
        this.publicKeyY = publicKeyY;
        this.privateKey = privateKey;
    }
    
    public ECCKeyPair(final byte[] publicKey, final byte[] privateKey) {
        this.publicKeyX = new byte[privateKey.length];
        this.publicKeyY = new byte[privateKey.length];
        System.arraycopy(publicKey, 0, this.publicKeyX, 0, 32);
        System.arraycopy(publicKey, 32, this.publicKeyY, 0, 32);
        this.privateKey = privateKey;
    }
    
    public ECCKeyPair(final String publicKeyX, final String publicKeyY, final String privateKey) {
        this(ByteArray.convert(publicKeyX), ByteArray.convert(publicKeyY), ByteArray.convert(privateKey));
    }
    
    public ECCKeyPair(final String publicKey, final String privateKey) {
        this(ByteArray.convert(publicKey), ByteArray.convert(privateKey));
    }
    
    public byte[] getPublicKeyX() {
        return this.publicKeyX;
    }
    
    public byte[] getPublicKeyY() {
        return this.publicKeyY;
    }
    
    public byte[] getPublicKey() {
        final byte[] publicKey = new byte[this.privateKey.length * 2];
        System.arraycopy(this.publicKeyX, 0, publicKey, 0, this.publicKeyX.length);
        System.arraycopy(this.publicKeyY, 0, publicKey, this.publicKeyX.length, this.publicKeyY.length);
        return publicKey;
    }
    
    public byte[] getPrivateKey() {
        return this.privateKey;
    }
    
    public int getLength() {
        return this.privateKey.length;
    }
    
    @Override
    public String toString() {
        String res = "PublicKey=" + ByteArray.convert(this.publicKeyX) + ByteArray.convert(this.publicKeyY) + "\r\n";
        res = res + "PrivateKey=" + ByteArray.convert(this.privateKey);
        return res;
    }
}
