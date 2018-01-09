package com.theovier.democoin.common.io;

import com.theovier.democoin.common.Address;
import com.theovier.democoin.common.Block;
import com.theovier.democoin.common.Blockchain;
import com.theovier.democoin.common.codec.AddressConverter;
import com.theovier.democoin.common.codec.PublicKeyConverter;
import com.theovier.democoin.common.codec.Sha256HashConverter;
import com.theovier.democoin.common.transaction.*;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;

import java.io.*;
import java.security.PublicKey;

public final class XMLSerializer {

    private final XStream xstream = new XStream();
    private final char[] TAB = new char[]{' ', ' ', ' ', ' '};

    public XMLSerializer() {
        registerConverters();
        initAliases();
        initAliasFields();
        initAttributes();
        addImplicitCollections();
        addImmutableTypes();
        setupSecurity();
    }

    private void registerConverters() {
        xstream.registerConverter(new AddressConverter());
        xstream.registerConverter(new Sha256HashConverter());
        xstream.registerConverter(new PublicKeyConverter());
    }

    private void initAliases() {
        xstream.alias("blockchain", Blockchain.class);
        xstream.alias("block", Block.class);
        xstream.alias("coinbaseTX", CoinbaseTransaction.class);
        xstream.alias("transaction", Transaction.class);
        xstream.alias("input", TxInput.class);
        xstream.alias("output", TxOutput.class);
        xstream.alias("address", Address.class);
        xstream.alias("publicKey", PublicKey.class, BCECPublicKey.class);
    }

    private void initAliasFields() {
        xstream.aliasField("target", Block.class, "powTarget");
        xstream.aliasField("fee", Transaction.class, "transactionFee");
        xstream.aliasField("parentTX", TxInput.class, "parentTransaction");
        xstream.aliasField("referencedOutput", TxInput.class, "prevOutputInfo");
        xstream.aliasField("txId", TxOutputPointer.class, "transactionHash");
        xstream.aliasField("index", TxOutputPointer.class, "outputIndex");
        xstream.aliasField("parentTX", TxOutput.class, "parentTransaction");
        xstream.aliasField("address", TxOutput.class, "recipientAddress");
    }

    private void initAttributes() {
        xstream.useAttributeFor(Block.class, "hash");
        xstream.useAttributeFor(Transaction.class, "txId");
        xstream.useAttributeFor(TxOutputPointer.class, "transactionHash");
        xstream.useAttributeFor(TxOutputPointer.class, "outputIndex");
    }

    private void addImplicitCollections() {
        xstream.addImplicitCollection(Blockchain.class, "blockchain");
    }

    private void addImmutableTypes() {
        xstream.addImmutableType(Address.class, false);
        xstream.addImmutableType(PublicKey.class, false);
        xstream.addImmutableType(BCECPublicKey.class, false);
        xstream.addImmutableType(TxOutput.class, false);
    }

    private void setupSecurity() {
        XStream.setupDefaultSecurity(xstream);
        xstream.allowTypesByWildcard(new String[] {
                "com.theovier.**"
        });
    }

    public void saveAsXML(final Object obj, final String filename) throws IOException {
        try (
             Writer out = new BufferedWriter(new OutputStreamWriter(
                     new FileOutputStream(filename), "UTF-8"))
        ) {
            PrettyPrintWriter printer = new PrettyPrintWriter(out, TAB);
            xstream.marshal(obj, printer);
            printer.close();
        }
    }

    public Object loadFromXML(final String filename) throws IOException {
        Reader in = new BufferedReader(new InputStreamReader(
                        new FileInputStream(filename), "UTF-8"));
        return xstream.fromXML(in);
    }
}
