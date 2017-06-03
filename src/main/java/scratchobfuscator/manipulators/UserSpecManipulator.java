package scratchobfuscator.manipulators;

import java.util.Map;

import scratchlib.objects.fixed.data.ScratchObjectAbstractString;
import scratchlib.objects.fixed.data.ScratchObjectUtf8;
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
        new Manipulation(stage).forEachCustomBlock(cb -> {

            // update cb spec
            final String oldSpec = cb.getUserSpec();
            if (specMap.containsKey(oldSpec)) {
                cb.setUserSpec(specMap.get(oldSpec));
            }

        }).forEachBlock(block -> {

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

        }).run();
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
