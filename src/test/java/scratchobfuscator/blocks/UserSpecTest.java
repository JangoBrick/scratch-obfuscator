package scratchobfuscator.blocks;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class UserSpecTest
{
    @Test
    public void allowsAddingLabels()
    {
        UserSpec spec = new UserSpec();
        spec.addLabel("some label");

        assertEquals(1, spec.getPartCount());
        assertFalse(spec.isParameter(0));
        assertEquals("some label", spec.getText(0));
    }

    @Test
    public void allowsAddingParameters()
    {
        UserSpec spec = new UserSpec();
        spec.addParameter("some parameter");

        assertEquals(1, spec.getPartCount());
        assertTrue(spec.isParameter(0));
        assertEquals("some parameter", spec.getText(0));
    }

    @Test
    public void allowsChangingTexts()
    {
        UserSpec spec = new UserSpec();
        spec.addLabel("foo");
        spec.addParameter("bar");
        spec.addLabel("baz");

        spec.setText(0, "newFoo");
        assertEquals("newFoo", spec.getText(0));

        spec.setText(1, "newBar");
        assertEquals("newBar", spec.getText(1));
    }

    @Test
    public void convertsToString()
    {
        UserSpec spec = new UserSpec();
        spec.addLabel("foo");
        spec.addParameter("bar");
        spec.addLabel("baz");

        assertEquals("foo %bar baz", spec.toString());
    }

    @Test
    public void quotesWhenNecessary()
    {
        UserSpec spec = new UserSpec();
        spec.addLabel("foo");
        spec.addParameter("some parameter");
        spec.addLabel("\"");

        assertEquals("foo \"%some parameter\" \"\"\"", spec.toString());
    }

    @Test
    public void checksLabelSimilarity()
    {
        UserSpec spec = new UserSpec();
        spec.addLabel("foo");
        spec.addLabel("bar");

        UserSpec spec1 = new UserSpec();
        spec1.addLabel("foo");
        spec1.addLabel("bar");

        assertTrue(spec.isSimilar(spec1));
        assertTrue(spec1.isSimilar(spec));

        UserSpec spec2 = new UserSpec();
        spec2.addLabel("foo");
        spec2.addLabel("baz");

        assertFalse(spec.isSimilar(spec2));
        assertFalse(spec2.isSimilar(spec));
    }

    @Test
    public void reportsNullAsNotSimilar()
    {
        UserSpec spec = new UserSpec();
        spec.addLabel("foo");
        spec.addLabel("bar");

        assertFalse(spec.isSimilar(null));
    }

    @Test
    public void includesPartTypeInSimilarityCheck()
    {
        UserSpec spec = new UserSpec();
        spec.addLabel("foo");
        spec.addLabel("bar");

        UserSpec spec1 = new UserSpec();
        spec1.addParameter("foo");
        spec1.addLabel("bar");

        assertFalse(spec.isSimilar(spec1));
        assertFalse(spec1.isSimilar(spec));
    }

    @Test
    public void includesSpecLengthInSimilarityCheck()
    {
        UserSpec spec = new UserSpec();
        spec.addLabel("foo");
        spec.addLabel("bar");

        UserSpec spec1 = new UserSpec();
        spec1.addLabel("foo");
        spec1.addLabel("bar");
        spec1.addLabel("baz");

        assertFalse(spec.isSimilar(spec1));
        assertFalse(spec1.isSimilar(spec));
    }

    @Test
    public void ignoresParameterNamesInSimilarityCheck()
    {
        UserSpec spec = new UserSpec();
        spec.addLabel("foo");
        spec.addParameter("some parameter");
        spec.addLabel("bar");

        UserSpec spec1 = new UserSpec();
        spec1.addLabel("foo");
        spec1.addParameter("param");
        spec1.addLabel("bar");

        assertTrue(spec.isSimilar(spec1));
        assertTrue(spec1.isSimilar(spec));
    }
}
