package scratchobfuscator.modules;

import scratchlib.project.ScratchProject;


/**
 * A module performs exactly one obfuscation step.
 */
public abstract class Module
{
    /**
     * Processes the given project.
     * 
     * @param project The project.
     */
    public abstract void process(ScratchProject project);
}
