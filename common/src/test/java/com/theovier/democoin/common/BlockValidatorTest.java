package com.theovier.democoin.common;

import com.theovier.democoin.common.transaction.CoinbaseTransaction;
import com.theovier.democoin.common.transaction.Transaction;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;


public class BlockValidatorTest {

    @Test
    public void testValidIndex() {
        Blockchain blockchain = setupBlockchain();
        Block block = new Block(blockchain.getLastBlock(),  0, "", new Address(""), new ArrayList<>());
        assert BlockValidator.hasValidIndex(block, blockchain.getLastBlock());
    }

    @Test
    public void testValidHashChain() {
        Blockchain blockchain = setupBlockchain();
        Block block = new Block(blockchain.getLastBlock(), 0, "", new Address(""),  new ArrayList<>());
        assert BlockValidator.hasValidHashChain(block, blockchain.getLastBlock());
    }

    @Test
    public void testValidTransactionCount() {
        List<Transaction> txs = new ArrayList<>();
        for (int i = 0; i <= ConsensusParams.MAX_TRANSACTIONS_PER_BLOCK; i++) {
           txs.add(new CoinbaseTransaction(new Address("...")));
        }
        Blockchain blockchain = setupBlockchain();
        Block block = new Block(blockchain.getLastBlock(),  0,"", new Address(""), txs);
        assert !BlockValidator.hasValidTransactionCount(block);
    }

    @Test
    public void testHasCoinbaseTX() {
        List<Transaction> txs = new ArrayList<>();
        txs.add(new CoinbaseTransaction(new Address("...")));
        txs.add(new CoinbaseTransaction(new Address("...")));
        Blockchain blockchain = setupBlockchain();
        Block block = new Block(blockchain.getLastBlock(), 0, "", new Address(""),  new ArrayList<>());
        assert !BlockValidator.hasCoinbaseTx(block);
    }

    public Blockchain setupBlockchain() {
        Blockchain blockchain = new Blockchain();
        Block genesisBlock = Block.generateGenesisBlock();
        blockchain.append(genesisBlock);
        return blockchain;
    }
}
