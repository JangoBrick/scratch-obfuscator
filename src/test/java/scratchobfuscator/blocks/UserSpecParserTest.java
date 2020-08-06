package scratchobfuscator.blocks;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class UserSpecParserTest
{
    @Test
    public void splitsSpecs()
    {
        List<String> split = UserSpecParser.split("foo %bar baz %qux");
        assertIterableEquals(Arrays.asList("foo", "%bar", "baz", "%qux"), split);
    }

    @Test
    public void recognizesQuotes()
    {
        List<String> split = UserSpecParser.split("foo \"hello world\" bar");
        assertIterableEquals(Arrays.asList("foo", "hello world", "bar"), split);
    }

    @Test
    public void recognizesQuotedParameters()
    {
        List<String> split = UserSpecParser.split("foo \"%test parameter\" bar");
        assertIterableEquals(Arrays.asList("foo", "%test parameter", "bar"), split);
    }

    @Test
    public void collapsesSpaces()
    {
        List<String> split = UserSpecParser.split("foo    bar   baz     qux");
        assertIterableEquals(Arrays.asList("foo", "bar", "baz", "qux"), split);
    }

    @Test
    public void parsesSimpleLabels()
    {
        UserSpec spec = UserSpecParser.parse("foo");

        assertEquals(1, spec.getPartCount());
        assertFalse(spec.isParameter(0));
        assertEquals("foo", spec.getText(0));
    }

    @Test
    public void parsesQuotedLabels()
    {
        UserSpec spec = UserSpecParser.parse("foo \"\"\" \"quoted\" bar \"baz\"\"");

        assertEquals(5, spec.getPartCount());
        assertEquals("\"", spec.getText(1));
        assertEquals("quoted", spec.getText(2));
        assertEquals("baz\"", spec.getText(4));
    }

    @Test
    public void parsesSimpleParameters()
    {
        UserSpec spec = UserSpecParser.parse("foo %bar baz");

        assertEquals(3, spec.getPartCount());
        assertTrue(spec.isParameter(1));
        assertEquals("bar", spec.getText(1));
        assertFalse(spec.isParameter(2));
    }

    @Test
    public void parsesQuotedParameters()
    {
        UserSpec spec = UserSpecParser.parse("foo \"%test parameter\" bar");

        assertEquals(3, spec.getPartCount());
        assertTrue(spec.isParameter(1));
        assertEquals("test parameter", spec.getText(1));
        assertFalse(spec.isParameter(2));
    }
}
