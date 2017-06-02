package scratchobfuscator.manipulators;

import java.util.Map;

import scratchlib.objects.ScratchObject;
import scratchlib.objects.fixed.collections.ScratchObjectAbstractCollection;
import scratchlib.objects.fixed.data.ScratchObjectAbstractString;
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
            final BlockView view = new BlockView((ScratchObjectAbstractCollection) blocks.get(i));
            updateBlock(view, specMap);
        }
    }

    /**
     * Updates all occurrences in a single block, including a recursive search
     * through its parameters.
     * 
     * @param block The block.
     * @param specMap The replacement map.
     */
    private static void updateBlock(BlockView block, Map<String, String> specMap)
    {
        // given block is invocation of a custom block?
        if (isCustomBlockInvocation(block)) {
            // replace invoked spec
            String invoked = getInvokedUserSpec(block);
            if (specMap.containsKey(invoked)) {
                setInvokedUserSpec(block, specMap.get(invoked));
            }
        }

        // given block is a parameter?
        if (isParameterVariable(block)) {
            // replace spec
            String spec = getParameterVariableSpec(block);
            if (specMap.containsKey(spec)) {
                setParameterVariableSpec(block, specMap.get(spec));
            }
        }

        // recursively replace usages in nested blocks
        for (int i = 0, n = block.size(); i < n; ++i) {
            ScratchObject param = block.get(i);
            if (param instanceof ScratchObjectAbstractCollection) {
                final BlockView view = new BlockView((ScratchObjectAbstractCollection) param);
                updateBlock(view, specMap);
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
    private static boolean isCustomBlockInvocation(BlockView block)
    {
        return block.size() >= 2 && block.stringEquals(0, "doCustomBlock");
    }

    private static String getInvokedUserSpec(BlockView block)
    {
        return ((ScratchObjectAbstractString) block.get(1)).getValue();
    }

    private static void setInvokedUserSpec(BlockView block, String spec)
    {
        block.set(1, new ScratchObjectUtf8(spec));
    }

    /**
     * Returns {@code true} if the given block is a variable block resolving to
     * a custom block's parameter, {@code false} otherwise.
     * 
     * @param block The block.
     * @return Whether the block is a "parameter variable".
     */
    private static boolean isParameterVariable(BlockView block)
    {
        return block.size() >= 3 && block.stringEquals(0, "readBlockVariable")
                && block.get(2) instanceof ScratchObjectUtf8;
    }

    private static String getParameterVariableSpec(BlockView block)
    {
        return ((ScratchObjectAbstractString) block.get(2)).getValue();
    }

    private static void setParameterVariableSpec(BlockView block, String spec)
    {
        block.set(2, new ScratchObjectUtf8(spec));
    }
}
