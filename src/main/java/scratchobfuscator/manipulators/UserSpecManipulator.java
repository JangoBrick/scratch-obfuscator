package scratchobfuscator.manipulators;

import java.util.Map;

import scratchlib.objects.ScratchObject;
import scratchlib.objects.fixed.collections.ScratchObjectAbstractCollection;
import scratchlib.objects.fixed.data.ScratchObjectAbstractString;
import scratchlib.objects.fixed.data.ScratchObjectSymbol;
import scratchlib.objects.fixed.data.ScratchObjectUtf8;
import scratchlib.objects.user.ScratchObjectCustomBlockDefinition;
import scratchlib.objects.user.morphs.ScratchObjectScriptableMorph;
import scratchlib.objects.user.morphs.ScratchObjectStageMorph;


/**
 * Performs the heavy lifting for completely replacing custom block specs and
 * their usages in stage and sprite objects.
 */
public class UserSpecManipulator
{
    private UserSpecManipulator()
    {
    }

    /**
     * Takes a stage and a map of old spec strings to new spec strings. Then
     * finds all occurrences of the old spec strings and replaces them.
     * 
     * <p>
     * This includes usages in the custom blocks themselves as well as in
     * invocation blocks.
     * 
     * @param stage The stage to search in.
     * @param specMap The replacement map.
     */
    public static void replaceAll(ScratchObjectStageMorph stage, Map<String, String> specMap)
    {
        doReplace(stage, specMap);

        for (int i = 0, n = stage.getSpriteCount(); i < n; ++i) {
            doReplace(stage.getSprite(i), specMap);
        }
    }

    /**
     * Performs the actual replacement for the given scriptable morph.
     * 
     * @param morph The morph.
     * @param specMap The replacement map.
     */
    private static void doReplace(ScratchObjectScriptableMorph morph, Map<String, String> specMap)
    {
        // iterate over morph's custom blocks
        for (int i = 0, n = morph.getCustomBlockCount(); i < n; ++i) {

            final ScratchObjectCustomBlockDefinition customBlock = morph.getCustomBlock(i);

            // replace user spec
            final String oldSpec = customBlock.getUserSpec();
            if (specMap.containsKey(oldSpec)) {
                customBlock.setUserSpec(specMap.get(oldSpec));
            }

            // replace usages in block's body
            final ScratchObject body = customBlock.getField(ScratchObjectCustomBlockDefinition.FIELD_BODY);
            if (body != ScratchObject.NIL) {
                updateScript((ScratchObjectAbstractCollection) body, specMap);
            }

        }

        // replace usages in morph's scripts
        for (int i = 0, n = morph.getScriptCount(); i < n; ++i) {
            updateScript(morph.getScriptBody(i), specMap);
        }
    }

    /**
     * Updates all occurrences in a collection of blocks.
     * 
     * @param blocks The script body (block collection).
     * @param specMap The replacement map.
     */
    private static void updateScript(ScratchObjectAbstractCollection blocks, Map<String, String> specMap)
    {
        // iterate over all blocks in script body
        for (int i = 0, n = blocks.size(); i < n; ++i) {
            updateBlock((ScratchObjectAbstractCollection) blocks.get(i), specMap);
        }
    }

    /**
     * Updates all occurrences in a single block, including a recursive search
     * through its parameters.
     * 
     * @param block The block.
     * @param specMap The replacement map.
     */
    private static void updateBlock(ScratchObjectAbstractCollection block, Map<String, String> specMap)
    {
        // given block is invocation of a custom block?
        if (isCustomBlockInvocation(block)) {
            // replace invoked spec
            String invoked = getInvokedUserSpec(block);
            if (specMap.containsKey(invoked)) {
                setInvokedUserSpec(block, specMap.get(invoked));
            }
        }

        // recursively replace usages in nested blocks
        for (int i = 0, n = block.size(); i < n; ++i) {
            ScratchObject param = block.get(i);
            if (param instanceof ScratchObjectAbstractCollection) {
                updateBlock((ScratchObjectAbstractCollection) param, specMap);
            }
        }
    }

    /**
     * Returns {@code true} if the given block is an invocation block for a
     * custom block, {@code false} if not.
     * 
     * @param block The block.
     * @return Whether the block invokes a custom block when run.
     */
    private static boolean isCustomBlockInvocation(ScratchObjectAbstractCollection block)
    {
        if (block.size() < 4) {
            return false;
        }

        ScratchObject o0 = block.get(0);
        if (!(o0 instanceof ScratchObjectSymbol) || !((ScratchObjectSymbol) o0).getValue().equals("byob")) {
            return false;
        }
        ScratchObject o2 = block.get(2);
        if (!(o2 instanceof ScratchObjectSymbol) || !((ScratchObjectSymbol) o2).getValue().equals("doCustomBlock")) {
            return false;
        }

        return true;
    }

    private static String getInvokedUserSpec(ScratchObjectAbstractCollection block)
    {
        return ((ScratchObjectAbstractString) block.get(3)).getValue();
    }

    private static void setInvokedUserSpec(ScratchObjectAbstractCollection block, String spec)
    {
        block.set(3, new ScratchObjectUtf8(spec));
    }
}
