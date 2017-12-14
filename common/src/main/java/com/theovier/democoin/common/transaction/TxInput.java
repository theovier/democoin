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

    private long claimedValue;

    public TxInput(TxOutput from) {
        this.prevOutputInfo = new TxOutputPointer(from);
        this.claimedValue = from.getValue();
    }

    //if the claimedValue turns out to be invalid, the parentTransaction will be invalid.
    public TxInput(Sha256Hash prevTXHash, int prevTxOutputIndex, long claimedValue) {
        this.prevOutputInfo = new TxOutputPointer(prevTXHash, prevTxOutputIndex);
        this.claimedValue = claimedValue;
    }

    public TxInput(Transaction parentTransaction, Sha256Hash prevTXHash, int prevTxOutputIndex) {
        this.parentTransaction = parentTransaction;
        this.prevOutputInfo = new TxOutputPointer(prevTXHash, prevTxOutputIndex);
        this.claimedValue = lookupClaimedValue();
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

    /**
     * returns the value that this input claims.
     * if no matching UTXO (hence no value) is found, 0 is returned.
     * The parentTransaction will then be invalid because of the missing UTXO.
     */
    private long lookupClaimedValue() {
        try {
            TxOutput from = UTXOPool.getUTXO(prevOutputInfo);
            return from.getValue();
        } catch (MissingUTXOException e) {
            return 0;
        }
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
        return claimedValue;
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
