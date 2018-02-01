package com.theovier.democoin.common.crypto;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class KeyGenerator {

    public static KeyPair generateKeyPair() throws GeneralSecurityException {
        ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("secp256r1");
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
        keyGen.initialize(ecSpec, new SecureRandom());
        return keyGen.generateKeyPair();
    }

    public static KeyPair getKeyPairFromPrivateKeyHex(String privateKeyHex) throws GeneralSecurityException {
        BigInteger d = new BigInteger(privateKeyHex, 16);
        return getKeyPairFromPrivateNumber(d);
    }

    public static KeyPair getKeyPairFromPrivateNumber(BigInteger d) throws GeneralSecurityException {
        PrivateKey privateKey = getPrivateKeyFromPrivateNumber(d);
        return getKeyPairFromPrivateKey((ECPrivateKey) privateKey);
    }

    public static PrivateKey getPrivateKeyFromPrivateNumber(BigInteger d) throws GeneralSecurityException {
        ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("secp256r1");
        return KeyFactory.getInstance("ECDSA", "BC").
                generatePrivate(new ECPrivateKeySpec(d, ecSpec));
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

    public static PublicKey getPublicKey(byte[] x509key) throws GeneralSecurityException {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(x509key);
        KeyFactory keyFactory = KeyFactory.getInstance("ECDSA");
        return keyFactory.generatePublic(keySpec);
    }

    public static PublicKey getPublicKey(String hex) throws GeneralSecurityException {
        byte[] x509key = Hex.decode(hex);
        return getPublicKey(x509key);
    }
}
