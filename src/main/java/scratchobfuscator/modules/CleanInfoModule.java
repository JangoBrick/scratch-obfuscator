package scratchobfuscator.modules;

import java.util.Map.Entry;

import scratchlib.objects.ScratchObject;
import scratchlib.objects.ScratchObjectStore;
import scratchlib.objects.fixed.collections.ScratchObjectAbstractDictionary;
import scratchlib.objects.fixed.collections.ScratchObjectDictionary;
import scratchlib.objects.fixed.data.ScratchObjectAbstractString;
import scratchlib.objects.fixed.data.ScratchObjectUtf8;
import scratchlib.project.ScratchProject;


/**
 * Obfuscation module for cleaning up the project's info section.
 * 
 * <p>
 * The following modifications are done:
 * 
 * <ul>
 * <li>thumbnail is removed
 * <li>author and comment are cleared
 * <li>save history is cleared
 * </ul>
 * 
 * <p>
 * Note that all other keys, especially non-standard ones, are left untouched.
 */
public class CleanInfoModule extends Module
{
    @Override
    public void process(ScratchProject project)
    {
        final ScratchObjectAbstractDictionary source = (ScratchObjectAbstractDictionary) project.getInfoSection().get();

        final ScratchObjectDictionary dict = new ScratchObjectDictionary();
        copyClean(dict, source);

        final ScratchObjectStore newInfoStore = new ScratchObjectStore(dict,
                project.getInfoSection().getOrphanedFields());
        project.setInfoSection(newInfoStore);
    }

    private void copyClean(ScratchObjectAbstractDictionary dict, ScratchObjectAbstractDictionary source)
    {
        for (Entry<ScratchObject, ScratchObject> entry : source.entrySet()) {

            final ScratchObject kObj = entry.getKey();
            final ScratchObject vObj = entry.getValue();

            if (!(entry.getKey() instanceof ScratchObjectAbstractString)) {
                dict.put(kObj, vObj);
                continue;
            }

            final String key = ((ScratchObjectAbstractString) kObj).getValue();

            switch (key) {

                // skip thumbnail
                case ScratchProject.INFO_THUMBNAIL:
                    continue;

                // clear comment, author
                case ScratchProject.INFO_AUTHOR:
                case ScratchProject.INFO_COMMENT:
                    dict.put(kObj, new ScratchObjectUtf8(""));
                    continue;

                // clear history
                case ScratchProject.INFO_HISTORY:
                    dict.put(kObj, new ScratchObjectUtf8("\r"));
                    continue;

                // pass through by default
                default:
                    dict.put(kObj, vObj);
                    break;
            }

        }
    }
}
