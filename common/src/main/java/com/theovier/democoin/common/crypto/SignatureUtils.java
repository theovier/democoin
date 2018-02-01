package com.theovier.democoin.common.crypto;


import org.bouncycastle.util.encoders.Hex;

import java.security.*;

public class SignatureUtils {

    public static byte[] sign(byte[] data, PrivateKey privateKey) throws GeneralSecurityException {
        Signature ecdsaSign = Signature.getInstance("SHA256withECDSA", "BC");
        ecdsaSign.initSign(privateKey);
        ecdsaSign.update(data);
        return ecdsaSign.sign();
    }

    public static String signHex(byte[] data, PrivateKey privateKey) throws GeneralSecurityException {
        return Hex.toHexString(sign(data, privateKey));
    }

    public static String signHex(Sha256Hash hash, PrivateKey privateKey) throws GeneralSecurityException {
        return signHex(hash.getBytes(), privateKey);
    }

    public static boolean verify(byte[] signedData, PublicKey publicKey, byte[] unsignedData) throws GeneralSecurityException {
        Signature ecdsaVerify = Signature.getInstance("SHA256withECDSA", "BC");
        ecdsaVerify.initVerify(publicKey);
        ecdsaVerify.update(unsignedData);
        return ecdsaVerify.verify(signedData);
    }

    public static boolean verify(String signedDataHex, PublicKey publicKey, Sha256Hash unsignedDataHash) throws GeneralSecurityException {
        byte[] signedData = Hex.decode(signedDataHex);
        byte[] unsignedData = unsignedDataHash.getBytes();
        return verify(signedData, publicKey, unsignedData);
    }
}
