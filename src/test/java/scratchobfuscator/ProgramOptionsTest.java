package scratchobfuscator;

import java.io.File;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import scratchobfuscator.ProgramOptions.ParseException;


public class ProgramOptionsTest
{
    private static final String RESOURCES_FOLDER = "./src/test/resources/";
    private static final String INPUT_EMPTY = RESOURCES_FOLDER + "input-empty.sb";
    private static final String INPUT_NONEXISTANT = RESOURCES_FOLDER + "input-does-not-exist.sb";
    private static final String OUTPUT_EMPTY = RESOURCES_FOLDER + "output-empty.sb";

    @Test
    public void resolvesAbsoluteFiles()
    {
        final File in = new File(INPUT_EMPTY);
        final File out = new File(OUTPUT_EMPTY);

        assertFalse(in.isAbsolute());
        assertFalse(out.isAbsolute());

        final ProgramOptions opts = new ProgramOptions(in, out);

        assertTrue(opts.getInputFile().isAbsolute());
        assertTrue(opts.getOutputFile().isAbsolute());
    }

    @Test
    public void parsesInputAndOutputPaths() throws ParseException
    {
        final ProgramOptions opts = ProgramOptions.parse(new String[] { INPUT_EMPTY, OUTPUT_EMPTY });

        assertEquals(new File(INPUT_EMPTY).getAbsoluteFile(), opts.getInputFile());
        assertEquals(new File(OUTPUT_EMPTY).getAbsoluteFile(), opts.getOutputFile());
    }

    @Test(expected = ParseException.class)
    public void throwsWhenParseMissesOptions() throws ParseException
    {
        ProgramOptions.parse(new String[] { INPUT_EMPTY });
    }

    @Test(expected = ParseException.class)
    public void throwsWhenInputNotFound() throws ParseException
    {
        ProgramOptions.parse(new String[] { INPUT_NONEXISTANT, OUTPUT_EMPTY });
    }

    @Test(expected = ParseException.class)
    public void throwsWhenInputIsDirectory() throws ParseException
    {
        ProgramOptions.parse(new String[] { RESOURCES_FOLDER, OUTPUT_EMPTY });
    }

    @Test(expected = ParseException.class)
    public void throwsWhenOutputIsDirectory() throws ParseException
    {
        ProgramOptions.parse(new String[] { INPUT_EMPTY, RESOURCES_FOLDER });
    }
}
