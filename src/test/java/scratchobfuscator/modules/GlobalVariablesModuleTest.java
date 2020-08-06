package scratchobfuscator.modules;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import scratchlib.objects.fixed.collections.ScratchObjectDictionary;
import scratchlib.objects.fixed.data.ScratchObjectAbstractString;
import scratchlib.objects.fixed.data.ScratchObjectUtf8;
import scratchlib.objects.user.morphs.ScratchObjectScriptableMorph;
import scratchlib.objects.user.morphs.ScratchObjectSpriteMorph;
import scratchlib.objects.user.morphs.ScratchObjectStageMorph;
import scratchlib.project.ScratchProject;
import scratchlib.project.ScratchVersion;
import scratchlib.util.ScratchNumbers;

import static org.junit.jupiter.api.Assertions.*;


public class GlobalVariablesModuleTest
{
    private ScratchObjectDictionary makeVars()
    {
        ScratchObjectDictionary vars = new ScratchObjectDictionary();
        vars.put(new ScratchObjectUtf8("foo"), new ScratchObjectUtf8("hello"));
        vars.put(new ScratchObjectUtf8("bar"), new ScratchObjectUtf8("world"));
        vars.put(new ScratchObjectUtf8("baz"), ScratchNumbers.of(42));

        return vars;
    }

    @Test
    public void replacesAllInstances()
    {
        ScratchObjectStageMorph stage = new ScratchObjectStageMorph();
        ScratchObjectDictionary stageVars = makeVars();
        stage.setField(ScratchObjectScriptableMorph.FIELD_VARS, stageVars);

        ScratchObjectSpriteMorph sprite = new ScratchObjectSpriteMorph();
        ScratchObjectDictionary spriteVars = makeVars();
        sprite.setField(ScratchObjectScriptableMorph.FIELD_VARS, spriteVars);
        stage.addSprite(sprite);

        ScratchProject project = new ScratchProject(ScratchVersion.BYOB311);
        project.setStage(stage);

        new GlobalVariablesModule().process(project);

        List<String> stageVarNames = stageVars.keySet().stream()
                .map(obj -> ((ScratchObjectAbstractString) obj).getValue()).collect(Collectors.toList());
        assertIterableEquals(Arrays.asList(" ", "  ", "   "), stageVarNames);

        List<String> spriteVarNames = spriteVars.keySet().stream()
                .map(obj -> ((ScratchObjectAbstractString) obj).getValue()).collect(Collectors.toList());
        assertIterableEquals(Arrays.asList(" ", "  ", "   "), spriteVarNames);
    }
}
