package com.theovier.democoin.node;

import com.theovier.democoin.common.Address;
import com.theovier.democoin.node.network.Node;
import org.apache.commons.cli.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.IOException;
import java.security.Security;

public class NodeMain {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(NodeMain.class);

    public static void main(String[] args)  {
        Security.addProvider(new BouncyCastleProvider());
        Options options = createOptions();
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            startNode(cmd);
        } catch (ParseException e) {
            LOG.fatal("there is something wrong with the arguments.");
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("node", options , true);
        }
    }

    private static void startNode(CommandLine cmd) {
        Node node;
        if (cmd.hasOption("a") && cmd.hasOption("msg")) {
            node = new Node(new Address(cmd.getOptionValue("a")), cmd.getOptionValue("msg"));
        } else if (cmd.hasOption("a")) {
            node = new Node(new Address(cmd.getOptionValue("a")));
        }  else if (cmd.hasOption("m")) {
            node = new Node(cmd.getOptionValue("msg"));
        }  else {
            node = new Node();
        }
        try {
            node.start();
            LOG.info("node started.");
        } catch (IOException e) {
            LOG.fatal("shutting down node.", e);
            node.shutdown();
        }
    }

    private static Options createOptions() {
        Options options = new Options();
        options.addOption(Option.builder("a").
                longOpt("address").
                hasArg().
                argName("address").
                desc("address the coinbase reward is sent to.").
                build());
        options.addOption(Option.builder("m").
                longOpt("msg").
                hasArg().
                argName("message").
                desc("optional message for the coinbase transaction").
                build());
        return options;
    }
}
