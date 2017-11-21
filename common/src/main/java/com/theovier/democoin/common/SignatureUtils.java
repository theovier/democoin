package com.theovier.democoin.common;

import org.apache.log4j.Logger;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.util.encoders.Hex;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class SignatureUtils {

    private static final Logger LOG = Logger.getLogger(SignatureUtils.class);

    public static KeyPair generateKeyPair() throws GeneralSecurityException {
        ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("secp256r1");
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
        keyGen.initialize(ecSpec, new SecureRandom());
        return keyGen.generateKeyPair();
    }

    private static PrivateKey getPrivateKey(byte[] pkcs8key) throws GeneralSecurityException {
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(pkcs8key);
        KeyFactory keyFactory = KeyFactory.getInstance("ECDSA", "BC");
        return keyFactory.generatePrivate(spec);
    }

    private static PublicKey getPublicKey(byte[] x509key) throws GeneralSecurityException {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(x509key);
        KeyFactory keyFactory = KeyFactory.getInstance("ECDSA");
        return keyFactory.generatePublic(keySpec);
    }

    public static byte[] sign(byte[] data, byte[] pkcs8key) throws GeneralSecurityException {
        PrivateKey privateKey = getPrivateKey(pkcs8key);
        Signature ecdsaSign = Signature.getInstance("SHA256withECDSA", "BC");
        ecdsaSign.initSign(privateKey);
        ecdsaSign.update(data);
        return ecdsaSign.sign();
    }

    public static boolean verify(byte[] signedData, byte[] x509key, byte[] unsignedData) throws GeneralSecurityException {
        PublicKey publicKey = getPublicKey(x509key);
        Signature ecdsaVerify = Signature.getInstance("SHA256withECDSA", "BC");
        ecdsaVerify.initVerify(publicKey);
        ecdsaVerify.update(unsignedData);
        return ecdsaVerify.verify(signedData);
    }

    public static void main(String[] args) throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        //generate and save keys.
        KeyPair pair = generateKeyPair();
        PublicKey pub = pair.getPublic();
        PrivateKey priv = pair.getPrivate();
        byte[] publicKeyBytes = pub.getEncoded();
        byte[] privateKeyBytes = priv.getEncoded();
        Files.write(Paths.get("key.priv"), privateKeyBytes);
        Files.write(Paths.get("key.pub"), publicKeyBytes);

        byte[] unsignedData = "SIGN ME".getBytes("UTF-8");
        byte[] signedData = sign(unsignedData, privateKeyBytes);
        boolean isValid = verify(signedData, publicKeyBytes, unsignedData);
        LOG.info("signed data: " + Hex.toHexString(signedData));
        LOG.info(isValid);

        //read from file and verify previous signature.
        publicKeyBytes = Files.readAllBytes(Paths.get("key.pub"));
        isValid = verify(signedData, publicKeyBytes, unsignedData);
        LOG.info(isValid);
    }
}
