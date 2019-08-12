package scratchobfuscator.modules;

import static scratchlib.objects.user.morphs.ScratchObjectScriptableMorph.FIELD_VARS;

import java.util.HashMap;
import java.util.Map;

import scratchlib.objects.ScratchObject;
import scratchlib.objects.fixed.collections.ScratchObjectAbstractDictionary;
import scratchlib.objects.fixed.collections.ScratchObjectDictionary;
import scratchlib.objects.fixed.data.ScratchObjectAbstractString;
import scratchlib.objects.user.morphs.ScratchObjectScriptableMorph;
import scratchlib.objects.user.morphs.ScratchObjectStageMorph;
import scratchlib.project.ScratchProject;
import scratchobfuscator.manipulators.GlobalVariablesManipulator;


/**
 * Obfuscation module that changes all global variables' names to consist only
 * of spaces, i.e. the first variable is called {@code " "} (1 space), the
 * second {@code "  "} (2 spaces), and so on. Their usages are updated as well.
 */
public class GlobalVariablesModule extends Module
{
    @Override
    public void process(ScratchProject project)
    {
        final ScratchObjectStageMorph stage = project.getStage();

        // generate map, then apply
        final Map<String, String> nameMap = generateNameMap(stage);
        GlobalVariablesManipulator.replaceAll(stage, nameMap);
    }

    /**
     * Generates a mapping of all old global variable names to their obfuscated
     * replacements. The names are searched for in the given stage and the
     * stage's sprites.
     *
     * @param stage The search target.
     * @return A map of old names to replacement names.
     */
    private Map<String, String> generateNameMap(ScratchObjectStageMorph stage)
    {
        final Map<String, String> nameMap = new HashMap<>();

        // fill with the stage's variables
        fillNameMap(stage, nameMap);

        // fill with the sprites' variables
        for (int i = 0, n = stage.getSpriteCount(); i < n; ++i) {
            fillNameMap(stage.getSprite(i), nameMap);
        }

        return nameMap;
    }

    /**
     * Obfuscates all of the given morph's variables, storing the resulting
     * mappings in {@code target}.
     *
     * @param morph The source morph.
     * @param target The target map to fill.
     */
    private void fillNameMap(ScratchObjectScriptableMorph morph, Map<String, String> target)
    {
        final ScratchObjectAbstractDictionary vars = (ScratchObjectDictionary) morph.getField(FIELD_VARS);

        for (final ScratchObject key : vars.keySet()) {
            final String name = ((ScratchObjectAbstractString) key).getValue();
            // name not yet found? - generate and put
            if (!target.containsKey(name)) {
                target.put(name, generateVariableName(target.size()));
            }
        }
    }

    private String generateVariableName(int position)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < position + 1; ++i) {
            sb.append(' ');
        }

        return sb.toString();
    }
}
