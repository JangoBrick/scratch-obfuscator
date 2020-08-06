package scratchobfuscator;

import java.io.File;

import org.junit.jupiter.api.Test;
import scratchobfuscator.ProgramOptions.ParseException;

import static org.junit.jupiter.api.Assertions.*;


public class ProgramOptionsTest
{
    private static final String RESOURCES_FOLDER = "./src/test/resources/";
    private static final String INPUT_EMPTY = RESOURCES_FOLDER + "input-empty.sb";
    private static final String INPUT_NONEXISTENT = RESOURCES_FOLDER + "input-does-not-exist.sb";
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

    @Test
    public void throwsWhenParseMissesOptions()
    {
        assertThrows(ParseException.class,
                () -> ProgramOptions.parse(new String[] { INPUT_EMPTY }));
    }

    @Test
    public void throwsWhenInputNotFound()
    {
        assertThrows(ParseException.class,
                () -> ProgramOptions.parse(new String[] {INPUT_NONEXISTENT, OUTPUT_EMPTY }));
    }

    @Test
    public void throwsWhenInputIsDirectory()
    {
        assertThrows(ParseException.class,
                () -> ProgramOptions.parse(new String[] { RESOURCES_FOLDER, OUTPUT_EMPTY }));
    }

    @Test
    public void throwsWhenOutputIsDirectory()
    {
        assertThrows(ParseException.class,
                () -> ProgramOptions.parse(new String[] { INPUT_EMPTY, RESOURCES_FOLDER }));
    }
}
