package com.theovier.democoin.common.crypto;


import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;

import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class SignatureUtils {

    public static KeyPair generateKeyPair() throws GeneralSecurityException {
        ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("secp256r1");
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
        keyGen.initialize(ecSpec, new SecureRandom());
        return keyGen.generateKeyPair();
    }

    public static KeyPair getKeyPairFromPrivateKey(ECPrivateKey privateKey) throws GeneralSecurityException {
        PublicKey publicKey = getPublicKeyFromPrivateKey(privateKey);
        return new KeyPair(publicKey, privateKey);
    }

    public static PublicKey getPublicKeyFromPrivateKey(ECPrivateKey privateKey) throws GeneralSecurityException {
        ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("secp256r1");
        ECPoint Q = ecSpec.getG().multiply(privateKey.getS());
        return KeyFactory.getInstance("ECDSA", "BC").
                generatePublic(new ECPublicKeySpec(Q, ecSpec));
    }

    public static KeyPair getKeyPair(byte[] x509pubKey, byte[] pkcs8privKey) throws GeneralSecurityException {
        PublicKey publicKey = getPublicKey(x509pubKey);
        PrivateKey privateKey = getPrivateKey(pkcs8privKey);
        return new KeyPair(publicKey, privateKey);
    }

    public static byte[] sign(byte[] data, PrivateKey privateKey) throws GeneralSecurityException {
        Signature ecdsaSign = Signature.getInstance("SHA256withECDSA", "BC");
        ecdsaSign.initSign(privateKey);
        ecdsaSign.update(data);
        return ecdsaSign.sign();
    }

    public static byte[] sign(byte[] data, byte[] pkcs8key) throws GeneralSecurityException {
        PrivateKey privateKey = getPrivateKey(pkcs8key);
        return sign(data, privateKey);
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

    public static boolean verify(byte[] signedData, byte[] x509key, byte[] unsignedData) throws GeneralSecurityException {
        PublicKey publicKey = getPublicKey(x509key);
        return verify(signedData, publicKey, unsignedData);
    }

    public static boolean verify(String signedDataHex, PublicKey publicKey, Sha256Hash unsignedDataHash) throws GeneralSecurityException {
        byte[] signedData = Hex.decode(signedDataHex);
        byte[] unsignedData = unsignedDataHash.getBytes();
        return verify(signedData, publicKey, unsignedData);
    }

    public static PrivateKey getPrivateKey(byte[] pkcs8key) throws GeneralSecurityException {
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(pkcs8key);
        KeyFactory keyFactory = KeyFactory.getInstance("ECDSA", "BC");
        return keyFactory.generatePrivate(spec);
    }

    public static PublicKey getPublicKey(byte[] x509key) throws GeneralSecurityException {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(x509key);
        KeyFactory keyFactory = KeyFactory.getInstance("ECDSA");
        return keyFactory.generatePublic(keySpec);
    }

    public static PublicKey getPublicKeyOrNull(String hex) {
        try {
            byte[] x509key = Hex.decode(hex);
            return getPublicKey(x509key);
        } catch (GeneralSecurityException e) {
            return null;
        }
    }

}
