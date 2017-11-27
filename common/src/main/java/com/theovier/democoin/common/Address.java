package com.theovier.democoin.common;

import com.theovier.democoin.common.codec.Base58Check;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

import java.security.PublicKey;

public class Address {

    private static final Logger LOG = Logger.getLogger(Address.class);
    public static final byte PREFIX = 0x00; //0x0F -> 7.
    private final String address;

    /**
     * Using the same algorithms to generate addresses as Bitcoin.
     *https://en.bitcoin.it/wiki/Technical_background_of_version_1_Bitcoin_addresses
     */
    //todo: write test cases based on the linked URL.

    public Address(String address) {
        this.address = address;
    }

    public static Address generateAddress(String publicKeyHex) {
        byte[] publicKey = Hex.decode(publicKeyHex);
        return generateAddress(publicKey);
    }

    public static Address generateAddress(PublicKey publicKey) {
        return generateAddress(publicKey.getEncoded());
    }

    public static Address generateAddress(byte[] publicKey) {
        byte[] sha256 = DigestUtils.sha256(publicKey);
        byte[] ripemd160 = ripemd160(sha256);
        byte[] prefixedRipmed160 = addVersionPrefix(ripemd160);
        String address = Base58Check.encode(prefixedRipmed160);
        return new Address(address);
    }

    public static byte[] ripemd160(byte[] data) {
        RIPEMD160Digest digest = new RIPEMD160Digest();
        digest.update(data, 0, data.length);
        byte[] out = new byte[20];
        digest.doFinal(out, 0);
        return out;
    }

    private static byte[] addVersionPrefix(byte[] ripemd160) {
        return Arrays.prepend(ripemd160, PREFIX);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return this.toString().equals(o.toString());
    }

    @Override
    public String toString() {
        return address;
    }

    public static void main (String[] args) {
        String pubKey = "0450863AD64A87AE8A2FE83C1AF1A8403CB53F53E486D8511DAD8A04887E5B23522CD470243453A299FA9E77237716103ABC11A1DF38855ED6F2EE187E9C582BA6";
        LOG.info(generateAddress(pubKey));
    }
}
