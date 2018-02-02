package com.theovier.democoin.client;

import com.theovier.democoin.common.transaction.Transaction;
import org.apache.commons.cli.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;


import java.io.IOException;
import java.security.Security;

import static com.theovier.democoin.common.ConsensusParams.DEFAULT_TX_MSG;

public class ClientMain {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ClientMain.class);

    static final int NUMBER_INPUT_ARGS = 3;
    static final int NUMBER_OUTPUT_ARGS = 2;

    public static void main (String[] args) {
        String[] testArgs = {
                "-in", "986afc4e482ff3fec2527cfa1140e2991bc500aa4022b30a4e8719f5e1eebb71", "0", "./1AVuQjcnquXEaXgggJx7TsyMBjbatiBtNB.key",
                "-out", "1AVuQjcnquXEaXgggJx7TsyMBjbatiBtNB", "130",
                "-msg", "my Message",
                "-host", "192.168.1.48",
        };

        Security.addProvider(new BouncyCastleProvider());
        CommandLineParser parser = new DefaultParser();
        Options options = createOptions();
        try {
            CommandLine cmd = parser.parse(options, testArgs);
            Transaction tx = assembleTransaction(cmd);
            sendTransactionToHost(tx, cmd.getOptionValue("h"));
        } catch (ParseException e) {
            LOG.info("some arguments could not be read or were missing");
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("s", options , true);
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

        options.addOption(Option.builder("h").
                longOpt("host").
                hasArg().
                argName("ip").
                desc("host to send the transaction to").
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

    private static Transaction assembleTransaction(CommandLine cmd) throws ParseException {
        String[] inputArguments = cmd.getOptionValues("in");
        String[] outputArguments = cmd.getOptionValues("out");
        String message = cmd.hasOption("msg") ? cmd.getOptionValue("msg") : DEFAULT_TX_MSG;
        TxAssembler assembler = new TxAssembler();
        return assembler.assembleTransaction(inputArguments, outputArguments, message);
    }

    private static void sendTransactionToHost(Transaction tx, String host) {
        try {
            boolean accepted = TxTransmitter.sendTransactionToHost(tx, host);
            String status = accepted ? "%s has accepted the transaction." : "%s has rejected the transaction.";
            String statusMsg = String.format(status, host);
            LOG.info(statusMsg);
        } catch (IOException | InterruptedException e) {
            String errorMsg = String.format("the node %s is not responding, can't send transaction.", host);
            LOG.error(errorMsg);
        }
    }
}
