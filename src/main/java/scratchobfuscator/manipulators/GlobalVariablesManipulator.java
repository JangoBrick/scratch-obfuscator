package scratchobfuscator.manipulators;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import scratchlib.objects.ScratchObject;
import scratchlib.objects.fixed.collections.ScratchObjectAbstractDictionary;
import scratchlib.objects.fixed.data.ScratchObjectAbstractString;
import scratchlib.objects.fixed.data.ScratchObjectUtf8;
import scratchlib.objects.user.morphs.ScratchObjectScriptableMorph;
import scratchlib.objects.user.morphs.ScratchObjectStageMorph;
import scratchlib.objects.user.morphs.ui.ScratchObjectStringMorph;
import scratchlib.objects.user.morphs.ui.ScratchObjectUpdatingStringMorph;


/**
 * Performs the heavy lifting for completely replacing global variable names and
 * their usages in stage and sprite objects.
 */
public class GlobalVariablesManipulator
{
    private GlobalVariablesManipulator()
    {
    }

    /**
     * Takes a stage and a map of old variable names to new names. Then finds
     * all occurrences of the old name strings and replaces them.
     *
     * <p>
     * This includes usages in the owning sprites themselves as well as in
     * invocation blocks.
     *
     * @param stage The stage to search in.
     * @param nameMap The replacement map.
     */
    public static void replaceAll(ScratchObjectStageMorph stage, Map<String, String> nameMap)
    {
        new Manipulation(stage).forEachMorph(morph -> {

            final ScratchObjectAbstractDictionary vars = (ScratchObjectAbstractDictionary) (morph
                    .getField(ScratchObjectScriptableMorph.FIELD_VARS));

            final Map<ScratchObject, ScratchObject> newEntries = new LinkedHashMap<>();

            // iterate over entries, replace if possible
            final Iterator<Entry<ScratchObject, ScratchObject>> entryIterator = vars.entrySet().iterator();
            while (entryIterator.hasNext()) {

                final Entry<ScratchObject, ScratchObject> varEntry = entryIterator.next();

                final String oldName = ((ScratchObjectAbstractString) varEntry.getKey()).getValue();
                if (nameMap.containsKey(oldName)) {
                    newEntries.put(new ScratchObjectUtf8(nameMap.get(oldName)), varEntry.getValue());
                    entryIterator.remove();
                }

            }

            newEntries.forEach(vars::put);

        }).forEachBlock(block -> {

            // block is global variable usage?
            if (isGlobalVariableBlock(block)) {
                // replace accessed name
                String invoked = getGlobalVariableBlockName(block);
                if (nameMap.containsKey(invoked)) {
                    setGlobalVariableBlockName(block, nameMap.get(invoked));
                }
                return;
            }

            // block is global variable setter?
            if (isVariableChangeBlock(block)) {
                // replace accessed name
                String invoked = getVariableChangeBlockName(block);
                if (nameMap.containsKey(invoked)) {
                    setVariableChangeBlockName(block, nameMap.get(invoked));
                }
                return;
            }

            // block is global variable setter?
            if (isVariableShowHideBlock(block)) {
                // replace accessed name
                String invoked = getVariableShowHideBlockName(block);
                if (nameMap.containsKey(invoked)) {
                    setVariableShowHideBlockName(block, nameMap.get(invoked));
                }
                return;
            }

        }).forEachWatcher(watcher -> {

            // update title morph

            final ScratchObjectStringMorph titleMorph = watcher.getTitleMorph();
            final String title = titleMorph.getContents();
            if (nameMap.containsKey(title)) {
                titleMorph.setContents(nameMap.get(title));
            }

            // update readout string morph

            final ScratchObjectUpdatingStringMorph readoutMorph = watcher.getReadoutMorph();
            final String parameter = readoutMorph.getParameter();
            if (nameMap.containsKey(parameter)) {
                readoutMorph.setParameter(nameMap.get(parameter));
            }

        }).run();
    }

    /**
     * Returns {@code true} if the given block is an accessor for a global
     * variable, {@code false} if not.
     *
     * @param block The block.
     * @return Whether the block provides the value of a global variable.
     */
    private static boolean isGlobalVariableBlock(BlockView block)
    {
        return block.size() >= 2 && block.stringEquals(0, "readVariable");
    }

    private static String getGlobalVariableBlockName(BlockView block)
    {
        return ((ScratchObjectAbstractString) block.get(1)).getValue();
    }

    private static void setGlobalVariableBlockName(BlockView block, String name)
    {
        block.set(1, new ScratchObjectUtf8(name));
    }

    /**
     * Returns {@code true} if the given block is a setter or changer for a
     * global variable, {@code false} if not.
     *
     * @param block The block.
     * @return Whether the block is a setter for a global variable.
     */
    private static boolean isVariableChangeBlock(BlockView block)
    {
        return block.size() >= 2 && block.stringEquals(0, "changeVariable");
    }

    private static String getVariableChangeBlockName(BlockView block)
    {
        return ((ScratchObjectAbstractString) block.get(1)).getValue();
    }

    private static void setVariableChangeBlockName(BlockView block, String name)
    {
        block.set(1, new ScratchObjectUtf8(name));
    }

    /**
     * Returns {@code true} if the given block is a show or hide block for a
     * global variable, {@code false} if not.
     *
     * @param block The block.
     * @return Whether the block is a show/hide block.
     */
    private static boolean isVariableShowHideBlock(BlockView block)
    {
        return block.size() >= 2 && (block.stringEquals(0, "showVariable:") || block.stringEquals(0, "hideVariable:"));
    }

    private static String getVariableShowHideBlockName(BlockView block)
    {
        return ((ScratchObjectAbstractString) block.get(1)).getValue();
    }

    private static void setVariableShowHideBlockName(BlockView block, String name)
    {
        block.set(1, new ScratchObjectUtf8(name));
    }
}
