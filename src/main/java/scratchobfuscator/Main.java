package scratchobfuscator;

import java.io.File;
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
        if (args.length != 2) {
            System.out.println(USAGE);
            return;
        }

        File in = new File(args[0]);
        if (!in.isFile()) {
            System.out.println("The given input path does not denote a file.");
            System.out.println(USAGE);
            return;
        }
        in = in.getAbsoluteFile();

        File out = new File(args[1]);
        if (out.isDirectory()) {
            System.out.println("The given output path denotes a directory.");
            System.out.println(USAGE);
            return;
        }
        out = out.getAbsoluteFile();

        final ScratchObfuscator obf = new ScratchObfuscator();
        try {
            obf.process(in, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
