package scratchobfuscator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import scratchlib.project.ScratchProject;
import scratchlib.reader.ScratchReader;
import scratchlib.writer.ScratchWriter;
import scratchobfuscator.modules.Module;


/**
 * Performs obfuscation of {@link ScratchProject} instances by running a series
 * of modules. Can also operate on files directly.
 * 
 * @see Module
 */
public class ScratchObfuscator
{
    private List<Module> modules = new ArrayList<>();

    /**
     * Construct a new obfuscator with the default set of modules.
     */
    public ScratchObfuscator()
    {
    }

    /**
     * Loads the given {@code in} file, processes the project, and writes it to
     * the {@code out} file.
     * 
     * @param in The input file.
     * @param out The output file.
     * 
     * @throws IOException If an I/O error occurs.
     */
    public void process(File in, File out) throws IOException
    {
        final ScratchReader reader = new ScratchReader();
        final ScratchWriter writer = new ScratchWriter(out);

        final ScratchProject project = reader.read(in);

        process(project);
        writer.write(project);
    }

    /**
     * Processes the given project, obfuscating it in-place.
     * 
     * @param project The project.
     */
    public void process(ScratchProject project)
    {
        if (project != null) {
            modules.forEach(m -> m.process(project));
        }
    }
}
