package com.theovier.democoin.common.io;

import com.theovier.democoin.common.Address;
import com.theovier.democoin.common.crypto.KeyGenerator;
import org.bouncycastle.util.encoders.Hex;

import java.io.*;
import java.security.*;
import java.security.interfaces.ECPrivateKey;

import static com.theovier.democoin.common.io.Config.PRIVATE_KEY_FILE_EXTENSION;


public class Wallet {

    public static KeyPair loadKeyPair(final File file) throws IOException, GeneralSecurityException {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))
        ) {
            String privateKeyHex = in.readLine();
            return KeyGenerator.getKeyPairFromPrivateKeyHex(privateKeyHex);
        }
    }

    public static void saveKeyPair(KeyPair keyPair) throws IOException {
        ECPrivateKey privateKey = (ECPrivateKey) keyPair.getPrivate();
        String privateKeyHex = Hex.toHexString(privateKey.getS().toByteArray());
        Address address = Address.generateAddress(keyPair.getPublic());
        String filename = address + PRIVATE_KEY_FILE_EXTENSION;
        writeKeyFile(filename, privateKeyHex);
    }

    private static void writeKeyFile(String filename, String privateKeyHex) throws IOException {
        try (
            Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF-8"))
        ) {
            out.write(privateKeyHex);
        }
    }
}
