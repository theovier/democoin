package com.theovier.democoin.client;

import com.google.common.collect.Lists;
import com.theovier.democoin.common.Address;
import com.theovier.democoin.common.crypto.Sha256Hash;
import com.theovier.democoin.common.transaction.Transaction;
import com.theovier.democoin.common.transaction.TxInput;
import com.theovier.democoin.common.transaction.TxOutput;
import org.apache.commons.cli.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.KeyPair;
import java.security.Security;
import java.util.*;

public class ClientMain {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ClientMain.class);

    private static final int NUMBER_INPUT_ARGS = 3;
    private static final int NUMBER_OUTPUT_ARGS = 2;

    public static void main (String[] args2) {
        Security.addProvider(new BouncyCastleProvider());

        String[] args = {
                "-in", "986afc4e482ff3fec2527cfa1140e2991bc500aa4022b30a4e8719f5e1eebb71", "0", "pathToKeyPairFile",
                "-out", "1AVuQjcnquXEaXgggJx7TsyMBjbatiBtNB", "130",
                "-msg", "my Message"
        };

        CommandLineParser parser = new DefaultParser();
        Options options = createOptions();
        try {
            CommandLine cmd = parser.parse(options, args);
            executeCommand(cmd);
        } catch (ParseException e) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("client", options , true);
        }
    }

    private static Options createOptions() {
        Options options = new Options();
        options.addOption(Option.builder("in").
                longOpt("input").
                hasArg().
                numberOfArgs(NUMBER_INPUT_ARGS).
                argName("txId> <outputIndex> <privateKey").
                desc("adds an input to spent").
                required().
                build());

        options.addOption(Option.builder("out").
                longOpt("output").
                hasArg().
                numberOfArgs(NUMBER_OUTPUT_ARGS).
                argName("address> <amount (int)").
                desc("adds an output to spent to").
                required().
                build());

        options.addOption(Option.builder("msg").
                longOpt("message").
                hasArg().
                argName("message").
                desc("optional message").
                build());
        return options;
    }

    private static void executeCommand(CommandLine cmd) throws ParseException {
        Transaction tx = new Transaction("");
        List<InputKeyHelper> inputs = new ArrayList<>();
        List<TxOutput> outputs;

        if (cmd.hasOption("in")) {
            String[] optionValues = cmd.getOptionValues("in");
            inputs = createInputs(optionValues);
            inputs.forEach(helper -> tx.addInput(helper.getInput()));
        }

        if (cmd.hasOption("out")) {
            String[] optionValues = cmd.getOptionValues("out");
            outputs = createOutputs(optionValues);
            outputs.forEach(tx::addOutput);
        }

        if (cmd.hasOption("msg")) {
            String msg = cmd.getOptionValue("msg");
        }

        for (int i = 0; i < inputs.size(); i++) {
           KeyPair keyPair = inputs.get(i).getKeyPair();
           tx.signInput(i, keyPair);
        }
        tx.build();

        //todo send the tx to default hosts or given param
    }

    private static int parseAsInteger(String from) throws ParseException {
        try {
            return Integer.valueOf(from);
        } catch (NumberFormatException e) {
            throw new ParseException(from + " has to be an integer");
        }
    }

    private static List<InputKeyHelper> createInputs(String[] inputArgs) throws ParseException {
        List<InputKeyHelper> inputs = new ArrayList<>();
        List<String> inputArgsList = Arrays.asList(inputArgs);
        for (List<String> args : Lists.partition(inputArgsList, NUMBER_INPUT_ARGS)) {
            InputKeyHelper input = createInput(args);
            inputs.add(input);
        }
        return inputs;
    }

    private static InputKeyHelper createInput(List<String> args) throws ParseException {
        String txId = args.get(0);
        int outputIndex = parseAsInteger(args.get(1));
        String privateKey = args.get(2);
        TxInput input = new TxInput(new Sha256Hash(txId), outputIndex);
        return new InputKeyHelper(input, privateKey);
    }

    private static List<TxOutput> createOutputs(String[] outputArgs) throws ParseException {
        List<TxOutput> outputs = new ArrayList<>();
        List<String> outputArgsList = Arrays.asList(outputArgs);
        for (List<String> args : Lists.partition(outputArgsList, NUMBER_OUTPUT_ARGS)) {
            TxOutput output = createOutput(args);
            outputs.add(output);
        }
        return outputs;
    }

    private static TxOutput createOutput(List<String> args) throws ParseException {
        String address = args.get(0);
        int amount = parseAsInteger(args.get(1));
        return new TxOutput(new Address(address), amount);
    }
}
