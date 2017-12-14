package com.theovier.democoin.common.transaction;

import com.theovier.democoin.common.Address;
import com.theovier.democoin.common.crypto.Sha256Hash;
import com.theovier.democoin.common.crypto.SignatureUtils;
import com.theovier.democoin.common.templates.FillableTemplate;
import com.theovier.democoin.common.templates.TxInputTemplate;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PublicKey;

public class TxInput implements Serializable {

    private static final Logger LOG = Logger.getLogger(TxInput.class);
    private static final long serialVersionUID = 478420474849537539L;

    private Transaction parentTransaction;

    private TxOutputPointer prevOutputInfo;
    private String signature;
    private PublicKey publicKey;
    private TxOutput from;

    public TxInput(TxOutput from) {
        this.prevOutputInfo = new TxOutputPointer(from);
        this.from = from;
    }

    public TxInput(Sha256Hash prevTXHash, int prevTxOutputIndex) {
        this.prevOutputInfo = new TxOutputPointer(prevTXHash, prevTxOutputIndex);
    }

    public TxInput(Transaction parentTransaction, Sha256Hash prevTXHash, int prevTxOutputIndex) {
        this.parentTransaction = parentTransaction;
        this.prevOutputInfo = new TxOutputPointer(prevTXHash, prevTxOutputIndex);
    }

    public boolean sign(KeyPair keyPair) {
        publicKey = keyPair.getPublic();
        try {
            signature = SignatureUtils.signHex(parentTransaction.getSignableHash(), keyPair.getPrivate());
            return true;
        } catch (GeneralSecurityException e) {
            LOG.error("failed to sign " + toString(), e);
        }
        return false;
    }

    public boolean verify(TxOutput output) {
        if (publicKey == null) {
            return false;
        }
        Address myAddress = Address.generateAddress(publicKey);
        if (!output.getRecipientAddress().equals(myAddress)) {
            return false;
        }
        try {
            return SignatureUtils.verify(getSignature(), getPublicKey(), parentTransaction.getSignableHash());
        } catch (GeneralSecurityException e) {
            LOG.error("failed to verify txInput", e);
        }
        return false;
    }

    public void setReferencedOutput(TxOutput from) {
        this.from = from;
    }

    public void setParentTransaction(Transaction parentTransaction) {
        this.parentTransaction = parentTransaction;
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

    public String unsigned() {
        //return the raw txInput data. this is called by the parentTX when constructing a signableHash.
        return getPrevOutputInfo().toString();
    }

    public long getClaimedValue() {
        return from.getValue();
    }

    public String toXML() {
        return new TxInputTemplate(this).getFilledTemplate();
    }

    @Override
    public String toString() {
        return "TxInput{" +
                "prevOutputInfo=" + prevOutputInfo +
                ", signature='" + signature + '\'' +
                '}';
    }
}
