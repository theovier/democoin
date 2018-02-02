package com.theovier.democoin.client;

import com.google.common.collect.Lists;
import com.theovier.democoin.common.Address;
import com.theovier.democoin.common.crypto.Sha256Hash;
import com.theovier.democoin.common.io.Wallet;
import com.theovier.democoin.common.transaction.Transaction;
import com.theovier.democoin.common.transaction.TxInput;
import com.theovier.democoin.common.transaction.TxOutput;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.theovier.democoin.client.ClientMain.NUMBER_INPUT_ARGS;
import static com.theovier.democoin.client.ClientMain.NUMBER_OUTPUT_ARGS;

public class TxAssembler {

    private static final Logger LOG = Logger.getLogger(TxAssembler.class);

    public Transaction assembleTransaction(String[] inputArgs, String[] outputArgs, String message) throws ParseException {
        Transaction tx = new Transaction(message);
        List<InputKeyHelper> inputsWithKeys = createInputsWithKeys(inputArgs);
        List<TxOutput> outputs = createOutputs(outputArgs);
        inputsWithKeys.forEach(helper -> tx.addInput(helper.getInput()));
        outputs.forEach(tx::addOutput);
        signAllInputs(tx, inputsWithKeys);
        tx.build();
        return tx;
    }

    private void signAllInputs(final Transaction tx, final List<InputKeyHelper> inputsWithKeys) {
        for (int i = 0; i < inputsWithKeys.size(); i++) {
            KeyPair keyPair = inputsWithKeys.get(i).getKeyPair();
            tx.signInput(i, keyPair);
        }
    }

    private List<InputKeyHelper> createInputsWithKeys(final String[] inputArgs) throws ParseException {
        List<InputKeyHelper> inputs = new ArrayList<>();
        List<String> inputArgsList = Arrays.asList(inputArgs);
        for (List<String> args : Lists.partition(inputArgsList, NUMBER_INPUT_ARGS)) {
            InputKeyHelper input = createInputWithKey(args);
            inputs.add(input);
        }
        return inputs;
    }

    private InputKeyHelper createInputWithKey(final List<String> args) throws ParseException {
        String txId = args.get(0);
        int outputIndex = parseAsInteger(args.get(1));
        String privateKey = args.get(2);
        TxInput input = new TxInput(new Sha256Hash(txId), outputIndex);
        try {
            return new InputKeyHelper(input, privateKey);
        } catch (IOException e) {
            LOG.error(e);
            throw new ParseException("could not read the file with the private key.");
        } catch (GeneralSecurityException e) {
            LOG.error(e);
            throw new ParseException("the given file does not contain a valid private key");
        }
    }

    private List<TxOutput> createOutputs(final String[] outputArgs) throws ParseException {
        List<TxOutput> outputs = new ArrayList<>();
        List<String> outputArgsList = Arrays.asList(outputArgs);
        for (List<String> args : Lists.partition(outputArgsList, NUMBER_OUTPUT_ARGS)) {
            TxOutput output = createOutput(args);
            outputs.add(output);
        }
        return outputs;
    }

    private TxOutput createOutput(final List<String> args) throws ParseException {
        String address = args.get(0);
        int amount = parseAsInteger(args.get(1));
        return new TxOutput(new Address(address), amount);
    }

    private int parseAsInteger(final String from) throws ParseException {
        try {
            return Integer.valueOf(from);
        } catch (NumberFormatException e) {
            throw new ParseException(from + " has to be an integer");
        }
    }

    private class InputKeyHelper {
        private final TxInput input;
        private final KeyPair keyPair;

        InputKeyHelper(final TxInput input, final String key) throws IOException, GeneralSecurityException {
            this.input = input;
            this.keyPair = Wallet.loadKeyPair(new File(key));
        }

        TxInput getInput() {
            return input;
        }

        KeyPair getKeyPair() {
            return keyPair;
        }
    }
}
