package com.theovier.democoin.common;

import com.theovier.democoin.common.crypto.Sha256Hash;
import com.theovier.democoin.common.crypto.SignatureUtils;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PublicKey;

public class TxInput implements Serializable {

    private static final Logger LOG = Logger.getLogger(TxInput.class);
    private static final long serialVersionUID = 478420474849537539L;
    private Sha256Hash prevTXHash;
    private int prevTxOutputIndex;
    private Sha256Hash unsignedHash;
    private String signature;
    private PublicKey publicKey;
    private Transaction parentTransaction;

    public TxInput(TxOutput from) {
        this.prevTXHash = from.getParentTransaction().getTxId();
        this.prevTxOutputIndex = from.getIndex();
        this.unsignedHash = computeUnsignedHash();
    }

    public TxInput(Sha256Hash prevTXHash, int prevTxOutputIndex) {
        this.prevTXHash = prevTXHash;
        this.prevTxOutputIndex = prevTxOutputIndex;
        this.unsignedHash = computeUnsignedHash();
    }

    public TxInput(Transaction parentTransaction, Sha256Hash prevTXHash, int prevTxOutputIndex) {
        this.parentTransaction = parentTransaction;
        this.prevTXHash = prevTXHash;
        this.prevTxOutputIndex = prevTxOutputIndex;
        this.unsignedHash = computeUnsignedHash();
    }

    public boolean sign(KeyPair keyPair) {
        publicKey = keyPair.getPublic();
        try {
            signature = SignatureUtils.signHex(unsignedHash, keyPair.getPrivate());
            return true;
        } catch (GeneralSecurityException e) {
            LOG.error("failed to sign " + toString(), e);
        }
        return false;
    }

    private Sha256Hash computeUnsignedHash() {
        StringBuilder content = new StringBuilder();
        content.append(prevTXHash);
        content.append(String.valueOf(prevTxOutputIndex));
        return Sha256Hash.create(content.toString());
    }

    public void setParentTransaction(Transaction parentTransaction) {
        this.parentTransaction = parentTransaction;
    }

    public Sha256Hash getUnsignedHash() {
        return unsignedHash;
    }

    public String getSignature() {
        return signature;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public Transaction getParentTransaction() {
        return parentTransaction;
    }

    @Override
    public String toString() {
        return "in{" +
                "prevTXHash='" + prevTXHash + '\'' +
                ", prevTxOutputIndex=" + prevTxOutputIndex +
                ", signature='" + signature + '\'' +
                '}';
    }
}
