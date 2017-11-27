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
    private TxOutputPointer prevOutputInfo;
    private Sha256Hash unsignedHash;
    private String signature;
    private PublicKey publicKey;
    private Transaction parentTransaction;

    public TxInput(TxOutput from) {
        this.prevOutputInfo = new TxOutputPointer(from);
        this.unsignedHash = computeUnsignedHash();
    }

    public TxInput(Sha256Hash prevTXHash, int prevTxOutputIndex) {
        this.prevOutputInfo = new TxOutputPointer(prevTXHash, prevTxOutputIndex);
        this.unsignedHash = computeUnsignedHash();
    }

    public TxInput(Transaction parentTransaction, Sha256Hash prevTXHash, int prevTxOutputIndex) {
        this.parentTransaction = parentTransaction;
        this.prevOutputInfo = new TxOutputPointer(prevTXHash, prevTxOutputIndex);
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

    //todo throw new exception
    public boolean verify(TxOutput output) {
        if (publicKey == null) {
            return false;
        }
        Address myAddress = Address.generateAddress(publicKey);
        if (!output.getRecipientAddress().equals(myAddress)) {
            return false;
        }
        try {
            SignatureUtils.verify(getSignature(), getPublicKey(), getUnsignedHash());
            return true;
        } catch (GeneralSecurityException e) {
            LOG.error("failed to verify txInput", e);
        }
        return false;
    }

    private Sha256Hash computeUnsignedHash() {
        return Sha256Hash.create(prevOutputInfo.toString());
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

    public TxOutputPointer getPrevOutputInfo() {
        return prevOutputInfo;
    }

    @Override
    public String toString() {
        return "TxInput{" +
                "prevOutputInfo=" + prevOutputInfo +
                ", signature='" + signature + '\'' +
                '}';
    }
}
