package scratchobfuscator.modules;

import org.junit.jupiter.api.Test;
import scratchlib.objects.user.ScratchObjectCustomBlockDefinition;
import scratchlib.objects.user.morphs.ScratchObjectScriptableMorph;
import scratchlib.objects.user.morphs.ScratchObjectSpriteMorph;
import scratchlib.objects.user.morphs.ScratchObjectStageMorph;
import scratchlib.project.ScratchProject;
import scratchlib.project.ScratchVersion;

import static org.junit.jupiter.api.Assertions.*;


public class RelabelCustomBlocksModuleTest
{
    private ScratchObjectStageMorph makeStage()
    {
        ScratchProject project = new ScratchProject(ScratchVersion.BYOB311);

        ScratchObjectStageMorph stage = project.getStage();
        addCustomBlocks(stage);

        ScratchObjectSpriteMorph sprite = new ScratchObjectSpriteMorph();
        addCustomBlocks(sprite);
        stage.addSprite(sprite);

        return stage;
    }

    private void addCustomBlocks(ScratchObjectScriptableMorph morph)
    {
        ScratchObjectCustomBlockDefinition cb0 = new ScratchObjectCustomBlockDefinition();
        cb0.setUserSpec("say hello to %name");
        morph.addCustomBlock(cb0);

        ScratchObjectCustomBlockDefinition cb1 = new ScratchObjectCustomBlockDefinition();
        cb1.setUserSpec("do something with %value");
        morph.addCustomBlock(cb1);

        ScratchObjectCustomBlockDefinition cb2 = new ScratchObjectCustomBlockDefinition();
        cb2.setUserSpec("%n !");
        morph.addCustomBlock(cb2);

        ScratchObjectCustomBlockDefinition cb3 = new ScratchObjectCustomBlockDefinition();
        cb3.setUserSpec("%a XOR %b");
        morph.addCustomBlock(cb3);
    }

    @Test
    public void ignoresNonByobProjects()
    {
        new RelabelCustomBlocksModule().process(new ScratchProject(ScratchVersion.SCRATCH14));
    }

    @Test
    public void obfuscatesAllInstances()
    {
        ScratchProject project = new ScratchProject(ScratchVersion.BYOB311);
        project.setStage(makeStage());

        new RelabelCustomBlocksModule().process(project);

        ScratchObjectStageMorph stage = project.getStage();
        assertEquals("%name", stage.getCustomBlock(0).getUserSpec());
        assertEquals("%value x", stage.getCustomBlock(1).getUserSpec());
        assertEquals("%n xx", stage.getCustomBlock(2).getUserSpec());
        assertEquals("%a %b", stage.getCustomBlock(3).getUserSpec());

        ScratchObjectSpriteMorph sprite = stage.getSprite(0);
        assertEquals("%name", sprite.getCustomBlock(0).getUserSpec());
        assertEquals("%value x", sprite.getCustomBlock(1).getUserSpec());
        assertEquals("%n xx", sprite.getCustomBlock(2).getUserSpec());
        assertEquals("%a %b", sprite.getCustomBlock(3).getUserSpec());
    }
}
