package com.capitalone.dashboard.tools;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.capitalone.dashboard.tools.utils.EncryptionTool;
import com.capitalone.dashboard.util.EncryptionException;

/**
 * A quick utility to support some functions needed inside Hygieia
 *
 */
public class App {

    Options options = new Options();

    public static void main(String[] args) throws ParseException,
            EncryptionException {

        App app = new App();
        CommandLine line = null;
        try {
            line = app.parseArgs(args);
        } catch (ParseException e) {
            throw e;
        }

        if (!line.iterator().hasNext()) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("tools.jar", app.options);
        }
        try {
            app.process(line);
        } catch (EncryptionException e) {
            throw e;
        }
    }

    public CommandLine parseArgs(String[] args) throws ParseException {

        options.addOption(Option.builder(Args.encrypt.name()).numberOfArgs(2)
                .desc("encrypt git passwords: <key> <password>").build());

        options.addOption(Option
                .builder(Args.genkey.name())
                .optionalArg(true)
                .hasArg(false)
                .desc("generate an encryption key for your application.properties, eg. github.key")
                .build());

        CommandLineParser parser = new DefaultParser();
        CommandLine line = parser.parse(options, args);
        return line;

    }

    public void process(CommandLine line) throws EncryptionException {

        if (line.hasOption(Args.encrypt.name())) {
            String[] kp = line.getOptionValues(Args.encrypt.name());
            System.out.println(EncryptionTool.encrypt(kp[1], kp[0]));
        } else if (line.hasOption(Args.genkey.name())) {
            System.out.println(EncryptionTool.genkey());
        }

    }

}
