package scratchobfuscator;

import java.io.File;


/**
 * Utility class for parsing command-line options accessing them.
 */
public class ProgramOptions
{
    private final File input, output;

    /**
     * @param input The file to read from.
     * @param output The file to write the program output to.
     */
    public ProgramOptions(File input, File output)
    {
        this.input = input.getAbsoluteFile();
        this.output = output.getAbsoluteFile();
    }

    /**
     * @return The file to read from.
     */
    public File getInputFile()
    {
        return input;
    }

    /**
     * @return The file to write the program output to.
     */
    public File getOutputFile()
    {
        return output;
    }

    /**
     * Parses and validates the given program options.
     * 
     * @param args The arguments to parse.
     * @return An object describing the options.
     * 
     * @throws ParseException If the arguments are invalid.
     */
    public static ProgramOptions parse(String[] args) throws ParseException
    {
        if (args.length != 2) {
            throw new ParseException(null);
        }

        File in = new File(args[0]);
        if (!in.isFile()) {
            throw new ParseException("The given input path does not denote a file.");
        }

        File out = new File(args[1]);
        if (out.isDirectory()) {
            throw new ParseException("The given output path denotes a directory.");
        }

        return new ProgramOptions(in, out);
    }

    /**
     * Exception thrown when the options are invalid and cannot be parsed.
     */
    public static class ParseException extends Exception
    {
        private static final long serialVersionUID = 375463718225870537L;

        /**
         * @param reason The reason for this exception.
         */
        public ParseException(String reason)
        {
            super(reason);
        }
    }
}
