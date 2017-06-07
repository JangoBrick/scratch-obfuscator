package scratchobfuscator.manipulators;

import static org.hamcrest.CoreMatchers.is;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import scratchlib.objects.fixed.collections.ScratchObjectArray;
import scratchlib.objects.fixed.collections.ScratchObjectDictionary;
import scratchlib.objects.fixed.data.ScratchObjectAbstractString;
import scratchlib.objects.fixed.data.ScratchObjectSymbol;
import scratchlib.objects.fixed.data.ScratchObjectUtf8;
import scratchlib.objects.fixed.dimensions.ScratchObjectPoint;
import scratchlib.objects.user.morphs.ScratchObjectMorph;
import scratchlib.objects.user.morphs.ScratchObjectScriptableMorph;
import scratchlib.objects.user.morphs.ScratchObjectSpriteMorph;
import scratchlib.objects.user.morphs.ScratchObjectStageMorph;
import scratchlib.objects.user.morphs.ui.ScratchObjectWatcherMorph;


public class GlobalVariablesManipulatorTest
{
    @Test
    public void renamesStageVariables()
    {
        // preparation
        Map<String, String> nameMap = new HashMap<>();
        nameMap.put("foo", "newFoo");
        nameMap.put("bar", "newBar");

        ScratchObjectStageMorph stage = new ScratchObjectStageMorph();
        ScratchObjectDictionary vars = new ScratchObjectDictionary();
        vars.put(new ScratchObjectUtf8("foo"), new ScratchObjectUtf8("hello"));
        vars.put(new ScratchObjectUtf8("bar"), new ScratchObjectUtf8("world"));
        stage.setField(ScratchObjectScriptableMorph.FIELD_VARS, vars);

        // replacement
        GlobalVariablesManipulator.replaceAll(stage, nameMap);

        // checks
        List<String> newNames = vars.keySet().stream().map(obj -> ((ScratchObjectAbstractString) obj).getValue())
                .collect(Collectors.toList());
        assertThat(newNames, is(Arrays.asList("newFoo", "newBar")));
    }

    @Test
    public void renamesSpriteVariables()
    {
        // preparation
        Map<String, String> nameMap = new HashMap<>();
        nameMap.put("foo", "newFoo");
        nameMap.put("bar", "newBar");

        ScratchObjectStageMorph stage = new ScratchObjectStageMorph();

        ScratchObjectSpriteMorph sprite1 = new ScratchObjectSpriteMorph();
        stage.addSprite(sprite1);

        ScratchObjectDictionary vars = new ScratchObjectDictionary();
        vars.put(new ScratchObjectUtf8("foo"), new ScratchObjectUtf8("hello"));
        vars.put(new ScratchObjectUtf8("bar"), new ScratchObjectUtf8("world"));
        sprite1.setField(ScratchObjectScriptableMorph.FIELD_VARS, vars);

        // replacement
        GlobalVariablesManipulator.replaceAll(stage, nameMap);

        // checks
        List<String> newNames = vars.keySet().stream().map(obj -> ((ScratchObjectAbstractString) obj).getValue())
                .collect(Collectors.toList());
        assertThat(newNames, is(Arrays.asList("newFoo", "newBar")));
    }

    @Test
    public void updatesAccessorBlocks()
    {
        // preparation
        Map<String, String> nameMap = new HashMap<>();
        nameMap.put("foo", "newFoo");

        ScratchObjectStageMorph stage = new ScratchObjectStageMorph();

        ScratchObjectArray block = new ScratchObjectArray();
        block.add(new ScratchObjectSymbol("readVariable"));
        block.add(new ScratchObjectUtf8("foo"));
        stage.addScript(new ScratchObjectPoint(20, 20), new ScratchObjectArray(Arrays.asList(block)));

        // replacement
        GlobalVariablesManipulator.replaceAll(stage, nameMap);

        // checks
        assertEquals("newFoo", ((ScratchObjectAbstractString) block.get(1)).getValue());
    }

    @Test
    public void updatesChangeBlocks()
    {
        // preparation
        Map<String, String> nameMap = new HashMap<>();
        nameMap.put("foo", "newFoo");

        ScratchObjectStageMorph stage = new ScratchObjectStageMorph();

        ScratchObjectArray block = new ScratchObjectArray();
        block.add(new ScratchObjectSymbol("changeVariable"));
        block.add(new ScratchObjectUtf8("foo"));
        block.add(new ScratchObjectSymbol("setVar:to:"));
        block.add(new ScratchObjectUtf8("hello world"));
        stage.addScript(new ScratchObjectPoint(20, 20), new ScratchObjectArray(Arrays.asList(block)));

        // replacement
        GlobalVariablesManipulator.replaceAll(stage, nameMap);

        // checks
        assertEquals("newFoo", ((ScratchObjectAbstractString) block.get(1)).getValue());
    }

    @Test
    public void updatesShowHideBlocks()
    {
        // preparation
        Map<String, String> nameMap = new HashMap<>();
        nameMap.put("foo", "newFoo");

        ScratchObjectStageMorph stage = new ScratchObjectStageMorph();

        ScratchObjectArray block1 = new ScratchObjectArray();
        block1.add(new ScratchObjectSymbol("showVariable:"));
        block1.add(new ScratchObjectUtf8("foo"));
        ScratchObjectArray block2 = new ScratchObjectArray();
        block2.add(new ScratchObjectSymbol("hideVariable:"));
        block2.add(new ScratchObjectUtf8("foo"));
        stage.addScript(new ScratchObjectPoint(20, 20), new ScratchObjectArray(Arrays.asList(block1, block2)));

        // replacement
        GlobalVariablesManipulator.replaceAll(stage, nameMap);

        // checks
        assertEquals("newFoo", ((ScratchObjectAbstractString) block1.get(1)).getValue());
        assertEquals("newFoo", ((ScratchObjectAbstractString) block2.get(1)).getValue());
    }

    @Test
    public void updatesWatchers()
    {
        // preparation
        Map<String, String> nameMap = new HashMap<>();
        nameMap.put("foo", "newFoo");

        ScratchObjectStageMorph stage = new ScratchObjectStageMorph();

        ScratchObjectWatcherMorph watcher = new ScratchObjectWatcherMorph();
        watcher.getTitleMorph().setContents("foo");
        watcher.getReadoutMorph().setParameter("foo");
        stage.setField(ScratchObjectMorph.FIELD_SUBMORPHS, new ScratchObjectArray(Arrays.asList(watcher)));

        // replacement
        GlobalVariablesManipulator.replaceAll(stage, nameMap);

        // checks
        assertEquals("newFoo", watcher.getTitleMorph().getContents());
        assertEquals("newFoo", watcher.getReadoutMorph().getParameter());
    }
}
