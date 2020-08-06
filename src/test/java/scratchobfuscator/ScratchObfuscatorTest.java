package scratchobfuscator;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import scratchlib.project.ScratchProject;
import scratchlib.project.ScratchVersion;
import scratchobfuscator.modules.Module;

import static org.junit.jupiter.api.Assertions.*;


public class ScratchObfuscatorTest
{
    private static class RunCheckModule extends Module
    {
        private boolean hasRun = false;

        @Override
        public void process(ScratchProject project)
        {
            this.hasRun = true;
        }
    }

    @Test
    public void ignoresNullProjects()
    {
        ScratchObfuscator so = new ScratchObfuscator();
        so.process(null);
    }

    @Test
    public void runsAllModules()
    {
        RunCheckModule m1 = new RunCheckModule(), m2 = new RunCheckModule(), m3 = new RunCheckModule();

        ScratchObfuscator so = new ScratchObfuscator(Arrays.asList(m1, m2, m3));
        so.process(new ScratchProject(ScratchVersion.SCRATCH14));

        assertTrue(m1.hasRun);
        assertTrue(m2.hasRun);
        assertTrue(m3.hasRun);
    }
}
