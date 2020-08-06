package scratchobfuscator.manipulators;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import scratchlib.objects.fixed.collections.ScratchObjectAbstractCollection;
import scratchlib.objects.fixed.collections.ScratchObjectArray;
import scratchlib.objects.fixed.data.ScratchObjectAbstractString;
import scratchlib.objects.fixed.data.ScratchObjectString;
import scratchlib.objects.fixed.data.ScratchObjectSymbol;
import scratchlib.objects.fixed.data.ScratchObjectUtf8;
import scratchlib.objects.fixed.dimensions.ScratchObjectPoint;
import scratchlib.objects.user.ScratchObjectCustomBlockDefinition;
import scratchlib.objects.user.morphs.ScratchObjectSpriteMorph;
import scratchlib.objects.user.morphs.ScratchObjectStageMorph;
import scratchlib.project.ScratchProject;
import scratchlib.project.ScratchVersion;

import static org.junit.jupiter.api.Assertions.*;


public class UserSpecManipulatorTest
{
    private ScratchObjectStageMorph makeStage()
    {
        ScratchProject project = new ScratchProject(ScratchVersion.BYOB311);

        ScratchObjectStageMorph stage = project.getStage();
        stage.addCustomBlock(makeCustomBlock1());
        stage.addCustomBlock(makeCustomBlock2());
        stage.addScript(new ScratchObjectPoint((short) 20, (short) 20), makeBlocks());

        ScratchObjectSpriteMorph sprite = new ScratchObjectSpriteMorph();
        sprite.addCustomBlock(makeCustomBlock1());
        sprite.addCustomBlock(makeCustomBlock2());
        sprite.addScript(new ScratchObjectPoint((short) 20, (short) 20), makeBlocks());
        stage.addSprite(sprite);

        return stage;
    }

    private ScratchObjectCustomBlockDefinition makeCustomBlock1()
    {
        ScratchObjectCustomBlockDefinition cb = new ScratchObjectCustomBlockDefinition();
        cb.setUserSpec("say hello to %name");
        cb.setBody(makeBlocks());

        return cb;
    }

    private ScratchObjectCustomBlockDefinition makeCustomBlock2()
    {
        ScratchObjectCustomBlockDefinition cb = new ScratchObjectCustomBlockDefinition();
        cb.setUserSpec("do something with %value");
        cb.setBody(makeBlocks());

        return cb;
    }

    private ScratchObjectArray makeBlocks()
    {
        ScratchObjectArray body = new ScratchObjectArray();

        body.add(makeInvocationBlock("say hello to %name"));

        ScratchObjectArray block = new ScratchObjectArray();
        {
            block.add(new ScratchObjectSymbol("doRepeat"));

            ScratchObjectArray param = new ScratchObjectArray();
            {
                param.add(new ScratchObjectSymbol("byob"));
                param.add(new ScratchObjectString(""));
                param.add(new ScratchObjectSymbol("readBlockVariable"));
                param.add(new ScratchObjectUtf8("name"));
                param.add(new ScratchObjectUtf8("say hello to %name"));
            }
            block.add(param);

            block.add(new ScratchObjectArray(Collections.singletonList(
                    makeInvocationBlock("do something with %value"))));
        }
        body.add(block);

        return body;
    }

    private ScratchObjectArray makeInvocationBlock(String spec)
    {
        ScratchObjectArray block = new ScratchObjectArray();

        block.add(new ScratchObjectSymbol("byob"));
        block.add(new ScratchObjectString(""));
        block.add(new ScratchObjectSymbol("doCustomBlock"));
        block.add(new ScratchObjectUtf8(spec));

        return block;
    }

    private Map<String, String> getSpecMap()
    {
        Map<String, String> specMap = new HashMap<>();
        specMap.put("say hello to %name", "%name");
        specMap.put("do something with %value", "%value x");

        return specMap;
    }

    @Test
    public void renamesCustomBlocksOfStage()
    {
        ScratchObjectStageMorph stage = makeStage();

        UserSpecManipulator.replaceAll(stage, getSpecMap());

        assertEquals("%name", stage.getCustomBlock(0).getUserSpec());
        assertEquals("%value x", stage.getCustomBlock(1).getUserSpec());
    }

    @Test
    public void renamesCustomBlocksOfSprites()
    {
        ScratchObjectStageMorph stage = makeStage();

        UserSpecManipulator.replaceAll(stage, getSpecMap());

        ScratchObjectSpriteMorph sprite = stage.getSprite(0);

        assertEquals("%name", sprite.getCustomBlock(0).getUserSpec());
        assertEquals("%value x", sprite.getCustomBlock(1).getUserSpec());
    }

    @Test
    public void updatesInvocationsInStageScripts()
    {
        ScratchObjectStageMorph stage = makeStage();

        UserSpecManipulator.replaceAll(stage, getSpecMap());

        ScratchObjectAbstractCollection blocks = stage.getScriptBody(0);

        ScratchObjectAbstractCollection block0 = (ScratchObjectAbstractCollection) blocks.get(0);
        assertEquals("%name", ((ScratchObjectAbstractString) block0.get(3)).getValue());

        ScratchObjectAbstractCollection block1 = (ScratchObjectAbstractCollection) blocks.get(1);
        ScratchObjectAbstractCollection block1body = (ScratchObjectAbstractCollection) block1.get(2);
        ScratchObjectAbstractCollection block1bodyBlock = (ScratchObjectAbstractCollection) block1body.get(0);
        assertEquals("%value x", ((ScratchObjectAbstractString) block1bodyBlock.get(3)).getValue());
    }

    @Test
    public void updatesInvocationsInSpriteScripts()
    {
        ScratchObjectStageMorph stage = makeStage();

        UserSpecManipulator.replaceAll(stage, getSpecMap());

        ScratchObjectAbstractCollection blocks = stage.getSprite(0).getScriptBody(0);

        ScratchObjectAbstractCollection block0 = (ScratchObjectAbstractCollection) blocks.get(0);
        assertEquals("%name", ((ScratchObjectAbstractString) block0.get(3)).getValue());

        ScratchObjectAbstractCollection block1 = (ScratchObjectAbstractCollection) blocks.get(1);
        ScratchObjectAbstractCollection block1body = (ScratchObjectAbstractCollection) block1.get(2);
        ScratchObjectAbstractCollection block1bodyBlock = (ScratchObjectAbstractCollection) block1body.get(0);
        assertEquals("%value x", ((ScratchObjectAbstractString) block1bodyBlock.get(3)).getValue());
    }

    @Test
    public void updatesParameterVariablesInStageScripts()
    {
        ScratchObjectStageMorph stage = makeStage();

        UserSpecManipulator.replaceAll(stage, getSpecMap());

        ScratchObjectAbstractCollection blocks = stage.getScriptBody(0);
        ScratchObjectAbstractCollection block = (ScratchObjectAbstractCollection) blocks.get(1);
        ScratchObjectAbstractCollection param = (ScratchObjectAbstractCollection) block.get(1);

        assertEquals("%name", ((ScratchObjectAbstractString) param.get(4)).getValue());
    }

    @Test
    public void updatesParameterVariablesInSpriteScripts()
    {
        ScratchObjectStageMorph stage = makeStage();

        UserSpecManipulator.replaceAll(stage, getSpecMap());

        ScratchObjectAbstractCollection blocks = stage.getSprite(0).getScriptBody(0);
        ScratchObjectAbstractCollection block = (ScratchObjectAbstractCollection) blocks.get(1);
        ScratchObjectAbstractCollection param = (ScratchObjectAbstractCollection) block.get(1);

        assertEquals("%name", ((ScratchObjectAbstractString) param.get(4)).getValue());
    }
}
