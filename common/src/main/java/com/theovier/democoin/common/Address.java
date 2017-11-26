package com.theovier.democoin.common;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

public class Address {

    private static final Logger LOG = Logger.getLogger(Address.class);
    public static final byte PREFIX = 0x00; //0x0F -> 7.

    /**
     * Using the same algorithms to generate addresses as Bitcoin.
     *https://en.bitcoin.it/wiki/Technical_background_of_version_1_Bitcoin_addresses
     */

    //todo: write test cases based on the linked URL.

    public static String generateAddress(byte[] publicKey) {
        byte[] sha256 = DigestUtils.sha256(publicKey);
        byte[] ripemd160 = ripemd160(sha256);
        byte[] prefixedRipmed160 = addVersionPrefix(ripemd160);
        return Base58Check.encode(prefixedRipmed160);
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

    public static void main (String[] args) {
        String pubKey = "0450863AD64A87AE8A2FE83C1AF1A8403CB53F53E486D8511DAD8A04887E5B23522CD470243453A299FA9E77237716103ABC11A1DF38855ED6F2EE187E9C582BA6";

        String address = generateAddress(Hex.decode(pubKey));
        LOG.info(address);
    }
}
