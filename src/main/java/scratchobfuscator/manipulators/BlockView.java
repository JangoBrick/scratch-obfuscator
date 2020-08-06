package scratchobfuscator.manipulators;

import scratchlib.objects.ScratchObject;
import scratchlib.objects.fixed.collections.ScratchObjectAbstractCollection;
import scratchlib.objects.fixed.data.ScratchObjectAbstractString;
import scratchlib.objects.fixed.data.ScratchObjectSymbol;


/**
 * Convenience mapper around block collections that is Scratch/BYOB agnostic.
 */
public class BlockView
{
    /**
     * The number of array entries preceding the actual block data for BYOB-specific blocks.
     */
    private static final int BYOB_ARRAY_OFFSET = 2;

    private final ScratchObjectAbstractCollection block;
    private final boolean isByob;

    /**
     * Constructs a new view of the given block.
     *
     * @param block The block.
     */
    public BlockView(ScratchObjectAbstractCollection block)
    {
        this.block = block;
        this.isByob = checkIsByob(block);
    }

    private static boolean checkIsByob(ScratchObjectAbstractCollection block)
    {
        ScratchObject first = block.get(0);
        return first instanceof ScratchObjectSymbol && ((ScratchObjectSymbol) first).getValue().equals("byob");
    }

    /**
     * Obtains the object at the given index. The BYOB block elements, if
     * present, are always skipped (so that no logic needs to be implemented for
     * working with different indexes).
     *
     * @param index The index of the object to obtain.
     * @return The object.
     */
    public ScratchObject get(int index)
    {
        // skip over "byob" string and morph name string
        return block.get(isByob ? index + BYOB_ARRAY_OFFSET : index);
    }

    /**
     * Sets the object at the given index to the given object. The index behaves
     * the same as with {@link #get(int)}.
     *
     * @param index The index of the object to update.
     * @param value The new object.
     */
    public void set(int index, ScratchObject value)
    {
        block.set(isByob ? index + BYOB_ARRAY_OFFSET : index, value);
    }

    /**
     * @return The number of objects composing the block, with BYOB elements ignored.
     */
    public int size()
    {
        int s = block.size();
        return isByob ? s - BYOB_ARRAY_OFFSET : s;
    }

    /**
     * Convenience method for checking whether the element obtained through
     * {@link #get(int)} is a string and has a string value equal to the given
     * one.
     *
     * @param index The index to check.
     * @param value The value the string must be equal to.
     * @return Whether the object is a string and has a value equal to the given
     *         one.
     */
    public boolean stringEquals(int index, String value)
    {
        ScratchObject obj = get(index);
        return obj instanceof ScratchObjectAbstractString
                && ((ScratchObjectAbstractString) obj).getValue().equals(value);
    }
}
