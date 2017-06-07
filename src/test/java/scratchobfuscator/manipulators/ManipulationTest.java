package scratchobfuscator.manipulators;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import scratchlib.objects.fixed.collections.ScratchObjectAbstractCollection;
import scratchlib.objects.fixed.collections.ScratchObjectArray;
import scratchlib.objects.fixed.data.ScratchObjectSymbol;
import scratchlib.objects.fixed.data.ScratchObjectUtf8;
import scratchlib.objects.fixed.dimensions.ScratchObjectPoint;
import scratchlib.objects.user.ScratchObjectCustomBlockDefinition;
import scratchlib.objects.user.morphs.ScratchObjectMorph;
import scratchlib.objects.user.morphs.ScratchObjectScriptableMorph;
import scratchlib.objects.user.morphs.ScratchObjectSpriteMorph;
import scratchlib.objects.user.morphs.ScratchObjectStageMorph;
import scratchlib.objects.user.morphs.ui.ScratchObjectWatcherMorph;


public class ManipulationTest
{
    @Test
    public void runsAllMorphsOnce()
    {
        ScratchObjectStageMorph stage = new ScratchObjectStageMorph();
        stage.addSprite(new ScratchObjectSpriteMorph());
        stage.addSprite(new ScratchObjectSpriteMorph());

        List<ScratchObjectScriptableMorph> done = new ArrayList<>();
        new Manipulation(stage).forEachMorph(morph -> {
            done.add(morph);
        }).run();

        assertEquals(3, done.size());
    }

    @Test
    public void runsAllCustomBlocksOnce()
    {
        ScratchObjectStageMorph stage = new ScratchObjectStageMorph();
        stage.addCustomBlock(new ScratchObjectCustomBlockDefinition());
        stage.addCustomBlock(new ScratchObjectCustomBlockDefinition());

        ScratchObjectSpriteMorph sprite = new ScratchObjectSpriteMorph();
        sprite.addCustomBlock(new ScratchObjectCustomBlockDefinition());
        sprite.addCustomBlock(new ScratchObjectCustomBlockDefinition());
        stage.addSprite(sprite);

        List<ScratchObjectCustomBlockDefinition> done = new ArrayList<>();
        new Manipulation(stage).forEachCustomBlock(cb -> {
            done.add(cb);
        }).run();

        assertEquals(4, done.size());
    }

    @Test
    public void runsAllBlocksOnce()
    {
        final Supplier<ScratchObjectArray> scriptGenerator = new Supplier<ScratchObjectArray>() {
            @Override
            public ScratchObjectArray get()
            {
                ScratchObjectArray script = new ScratchObjectArray();

                ScratchObjectArray block1 = new ScratchObjectArray();
                {
                    block1.add(new ScratchObjectSymbol("blockName"));
                    block1.add(new ScratchObjectUtf8("something"));
                }
                script.add(block1);

                ScratchObjectArray block2 = new ScratchObjectArray();
                {
                    block2.add(new ScratchObjectSymbol("otherBlockName"));
                    ScratchObjectArray block2Inner = new ScratchObjectArray();
                    {
                        block2Inner.add(new ScratchObjectSymbol("byob"));
                        block2Inner.add(new ScratchObjectUtf8(""));
                        block2Inner.add(new ScratchObjectSymbol("inner"));
                    }
                    block2.add(block2Inner);
                }
                script.add(block2);

                return script;
            }
        };

        ScratchObjectStageMorph stage = new ScratchObjectStageMorph();
        stage.addScript(new ScratchObjectPoint(20, 20), scriptGenerator.get());
        ScratchObjectCustomBlockDefinition stageCb = new ScratchObjectCustomBlockDefinition();
        stageCb.setBody(scriptGenerator.get());
        stage.addCustomBlock(stageCb);

        ScratchObjectSpriteMorph sprite = new ScratchObjectSpriteMorph();
        sprite.addScript(new ScratchObjectPoint(20, 20), scriptGenerator.get());
        ScratchObjectCustomBlockDefinition spriteCb = new ScratchObjectCustomBlockDefinition();
        spriteCb.setBody(scriptGenerator.get());
        sprite.addCustomBlock(spriteCb);
        stage.addSprite(sprite);

        List<BlockView> done = new ArrayList<>();
        new Manipulation(stage).forEachBlock(block -> {
            done.add(block);
        }).run();

        // 2 morphs * 2 locations (cb + script) * 3 blocks
        assertEquals(2 * 2 * 3, done.size());
    }

    @Test
    public void runsAllWatchersOnce()
    {
        ScratchObjectStageMorph stage = new ScratchObjectStageMorph();

        // add submorphs
        ScratchObjectAbstractCollection submorphs = (ScratchObjectAbstractCollection) (stage
                .getField(ScratchObjectMorph.FIELD_SUBMORPHS));
        submorphs.add(new ScratchObjectWatcherMorph());
        submorphs.add(new ScratchObjectWatcherMorph());
        submorphs.add(new ScratchObjectWatcherMorph());

        // add sprite to test submorphs filter
        stage.addSprite(new ScratchObjectSpriteMorph());

        List<ScratchObjectWatcherMorph> done = new ArrayList<>();
        new Manipulation(stage).forEachWatcher(watcher -> {
            done.add(watcher);
        }).run();

        assertEquals(3, done.size());
    }
}
