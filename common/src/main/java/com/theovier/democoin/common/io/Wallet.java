package com.theovier.democoin.common.io;

import com.theovier.democoin.common.Address;
import com.theovier.democoin.common.crypto.KeyGenerator;
import org.apache.log4j.Logger;
import org.bouncycastle.util.encoders.Hex;

import java.io.*;
import java.security.*;

import static com.theovier.democoin.common.io.Config.PRIVATE_KEY_FILE_EXTENSION;


public class Wallet {

    private static final Logger LOG = Logger.getLogger(Wallet.class);

    public static KeyPair loadKeyPair(String keyFileName) throws IOException, GeneralSecurityException {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(keyFileName), "UTF-8"))
        ) {
            String privateKeyHex = in.readLine();
            return KeyGenerator.getKeyPairFromPrivateKey(privateKeyHex);
        }
    }

    public static void saveKeyPair(KeyPair keyPair) throws IOException {
        PrivateKey privateKey = keyPair.getPrivate();
        String privateKeyHex = Hex.toHexString(privateKey.getEncoded());
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
