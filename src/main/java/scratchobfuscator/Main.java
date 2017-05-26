package scratchobfuscator;

import java.io.IOException;


/**
 * Application entry point.
 */
public class Main
{
    private static final String USAGE = "Usage: java -jar scratch-obfuscator.jar <in-path> <out-path>";

    /**
     * Entry point.
     * 
     * @param args Command-line arguments.
     */
    public static void main(String[] args)
    {
        final ProgramOptions opts;
        try {
            opts = ProgramOptions.parse(args);
        } catch (ProgramOptions.ParseException e) {
            String msg = e.getMessage();
            if (msg != null) {
                System.out.println(msg);
            }
            System.out.println(USAGE);
            return;
        }

        final ScratchObfuscator obf = new ScratchObfuscator();
        try {
            obf.process(opts.getInputFile(), opts.getOutputFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
