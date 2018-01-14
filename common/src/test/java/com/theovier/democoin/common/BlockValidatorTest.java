package com.theovier.democoin.common;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.File;
import java.io.IOException;
import java.security.Security;

/**
 * a block can only be validated in the context of a blockchain.
 * the test resources contain blockchains which are either valid
 * or invalid. The invalid ones always contain the error in the
 * last block, so we can access it by calling chain.getLastBlock().
 * These tests aim to detect invalid candidates to proof they are
 * functional.
 */
class BlockValidatorTest {

    @BeforeEach
    void setUp() {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    void isValid() throws IOException {
        File xmlFile = new File(getClass().getResource("/validBlockchains/valid_with_1_transaction.xml").getFile());
        Blockchain blockchain = Blockchain.loadFromFile(xmlFile);
        assertTrue(blockchain.isValid());
    }

    @Test
    void hasValidProofOfWork() throws IOException {
        File file = new File(getClass().getResource("/invalidBlockchains/invalid_pow.xml").getFile());
        Blockchain invalidBlockchain = Blockchain.loadFromFile(file);
        Block invalidBlock = invalidBlockchain.get(1);
        assertFalse(BlockValidator.hasValidProofOfWork(invalidBlock, invalidBlockchain));
    }

    @Test
    void hasValidIndex() throws IOException {
        File file = new File(getClass().getResource("/invalidBlockchains/invalid_index.xml").getFile());
        Blockchain invalidBlockchain = Blockchain.loadFromFile(file);
        Block prevBlock = invalidBlockchain.get(0);
        Block invalidBlock = invalidBlockchain.get(1);
        assertFalse(BlockValidator.hasValidIndex(invalidBlock, prevBlock));
    }

    @Test
    void hasValidHashChain() throws IOException {
        File file = new File(getClass().getResource("/invalidBlockchains/invalid_hashchain.xml").getFile());
        Blockchain invalidBlockchain = Blockchain.loadFromFile(file);
        Block prevBlock = invalidBlockchain.get(0);
        Block invalidBlock = invalidBlockchain.get(1);
        assertFalse(BlockValidator.hasValidHashChain(invalidBlock, prevBlock));
    }

    @Test
    void hasValidBlockHash() throws IOException {
        File file = new File(getClass().getResource("/validBlockchains/valid_with_1_transaction.xml").getFile());
        Blockchain validBlockchain = Blockchain.loadFromFile(file);
        Block validBlock = validBlockchain.get(1);
        assertTrue(BlockValidator.hasValidBlockHash(validBlock));
    }

    @Test
    void hasValidMerkleRoot() throws IOException {
        File file = new File(getClass().getResource("/invalidBlockchains/invalid_merkleRoot.xml").getFile());
        Blockchain invalidBlockchain = Blockchain.loadFromFile(file);
        Block invalidBlock = invalidBlockchain.get(1);
        assertFalse(BlockValidator.hasValidMerkleRoot(invalidBlock));
    }

    @Test
    void hasValidTransactionCount() throws IOException {
        File file = new File(getClass().getResource("/validBlockchains/valid_with_1_transaction.xml").getFile());
        Blockchain validBlockchain = Blockchain.loadFromFile(file);
        Block validBlock = validBlockchain.get(1);
        assertTrue(BlockValidator.hasValidTransactionCount(validBlock));
    }

    @Test
    void hasOnlyValidTransactions() throws IOException {
        //todo
    }

    @Test
    void hasCoinbaseTx() throws IOException {
        File file = new File(getClass().getResource("/invalidBlockchains/invalid_missingCoinbase.xml").getFile());
        Blockchain invalidBlockchain = Blockchain.loadFromFile(file);
        Block invalidBlock = invalidBlockchain.get(1);
        assertFalse(BlockValidator.hasCoinbaseTx(invalidBlock));
    }

    @Test
    void hasValidCoinbaseTx() throws IOException {
        //todo
    }
}