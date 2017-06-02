package scratchobfuscator.manipulators;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import scratchlib.objects.ScratchObject;
import scratchlib.objects.fixed.collections.ScratchObjectArray;
import scratchlib.objects.fixed.data.ScratchObjectSymbol;
import scratchlib.objects.fixed.data.ScratchObjectUtf8;
import scratchlib.objects.user.morphs.ScratchObjectListMorph;


public class BlockViewTest
{
    private ScratchObjectArray makeByobBlock()
    {
        final ScratchObjectArray block = new ScratchObjectArray();

        block.add(new ScratchObjectSymbol("byob"));
        block.add(new ScratchObjectUtf8(""));
        block.add(new ScratchObjectSymbol("readVariable"));
        block.add(new ScratchObjectUtf8("foo"));

        return block;
    }

    private ScratchObjectArray makeRegularBlock()
    {
        final ScratchObjectArray block = new ScratchObjectArray();

        block.add(new ScratchObjectSymbol("readVariable"));
        block.add(new ScratchObjectUtf8("foo"));

        return block;
    }

    @Test
    public void getSkipsByobElements()
    {
        final ScratchObjectArray block = makeByobBlock();
        final BlockView view = new BlockView(block);

        assertEquals(block.get(2), view.get(0));
    }

    @Test
    public void getDoesNotSkipForRegularBlocks()
    {
        final ScratchObjectArray block = makeRegularBlock();
        final BlockView view = new BlockView(block);

        assertEquals(block.get(0), view.get(0));
    }

    @Test
    public void setSkipsByobElements()
    {
        final ScratchObjectArray block = makeByobBlock();
        final BlockView view = new BlockView(block);

        final ScratchObject prev = block.get(0);
        view.set(0, new ScratchObjectUtf8("newlySetValue"));

        assertNotEquals(prev, view.get(0));
    }

    @Test
    public void setDoesNotSkipForRegularBlocks()
    {
        final ScratchObjectArray block = makeRegularBlock();
        final BlockView view = new BlockView(block);

        final ScratchObject prev = block.get(0);
        view.set(0, new ScratchObjectUtf8("newlySetValue"));

        assertNotEquals(prev, view.get(0));
    }

    @Test
    public void sizeSkipsByobElements()
    {
        final ScratchObjectArray block = makeByobBlock();
        final BlockView view = new BlockView(block);

        assertEquals(block.size() - 2, view.size());
    }

    @Test
    public void sizeDoesNotSkipForRegularBlocks()
    {
        final ScratchObjectArray block = makeRegularBlock();
        final BlockView view = new BlockView(block);

        assertEquals(block.size(), view.size());
    }

    @Test
    public void stringEqualsComparesStrings()
    {
        final ScratchObjectArray block = new ScratchObjectArray();
        block.add(new ScratchObjectSymbol("byob"));
        block.add(new ScratchObjectUtf8(""));
        block.add(new ScratchObjectSymbol("newList:"));
        block.add(new ScratchObjectListMorph());

        final BlockView view = new BlockView(block);

        assertTrue(view.stringEquals(0, "newList:"));
        assertFalse(view.stringEquals(0, "somethingElse"));
    }

    @Test
    public void stringEqualsChecksObjectType()
    {
        final ScratchObjectArray block = new ScratchObjectArray();
        block.add(new ScratchObjectSymbol("byob"));
        block.add(new ScratchObjectUtf8(""));
        block.add(new ScratchObjectSymbol("newList:"));
        block.add(new ScratchObjectListMorph());

        final BlockView view = new BlockView(block);

        assertFalse(view.stringEquals(1, "newList:"));
    }
}
