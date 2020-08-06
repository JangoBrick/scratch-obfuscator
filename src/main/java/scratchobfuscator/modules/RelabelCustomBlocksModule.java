package scratchobfuscator.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scratchlib.objects.user.ScratchObjectCustomBlockDefinition;
import scratchlib.objects.user.morphs.ScratchObjectScriptableMorph;
import scratchlib.objects.user.morphs.ScratchObjectStageMorph;
import scratchlib.project.ScratchProject;
import scratchlib.project.ScratchVersion;
import scratchobfuscator.blocks.UserSpec;
import scratchobfuscator.blocks.UserSpecParser;
import scratchobfuscator.manipulators.UserSpecManipulator;


/**
 * Obfuscation module that reduces all custom block headers to the absolute
 * minimum.
 *
 * <p>
 * The following modifications are done:
 *
 * <ul>
 * <li>all labels are removed, only parameters kept
 * <li>to disambiguate, if two blocks share the same parameters, some character
 * is appended
 * <li>all usages (invocations) of custom blocks are updated accordingly
 * </ul>
 */
public class RelabelCustomBlocksModule extends Module
{
    private static final String SPEC_SUFFIX = "x";

    @Override
    public void process(ScratchProject project)
    {
        if (project.getVersion() != ScratchVersion.BYOB311) {
            return;
        }

        final ScratchObjectStageMorph stage = project.getStage();

        // generate map, then apply
        final Map<String, String> specMap = generateSpecMap(stage);
        UserSpecManipulator.replaceAll(stage, specMap);
    }

    /**
     * Generates a mapping of all old custom block user specs to their
     * obfuscated replacements. The specs are searched for in the given stage
     * and the stage's sprites.
     *
     * @param stage The search target.
     * @return A map of old specs to replacement specs.
     */
    private Map<String, String> generateSpecMap(ScratchObjectStageMorph stage)
    {
        final Map<String, String> specMap = new HashMap<>();
        final List<UserSpec> existing = new ArrayList<>();

        // fill with the stage's custom blocks
        fillSpecMap(stage, specMap, existing);

        // fill with the sprites' custom blocks
        for (int i = 0, n = stage.getSpriteCount(); i < n; ++i) {
            fillSpecMap(stage.getSprite(i), specMap, existing);
        }

        return specMap;
    }

    /**
     * Obfuscates all of the given morph's custom blocks, storing the resulting
     * mappings in {@code target} and using {@code existing} to avoid collisions
     * with already obfuscated specs.
     *
     * @param morph The source morph.
     * @param target The target map to fill.
     * @param existing The list of already obfuscated specs.
     */
    private void fillSpecMap(ScratchObjectScriptableMorph morph, Map<String, String> target, List<UserSpec> existing)
    {
        for (int i = 0, n = morph.getCustomBlockCount(); i < n; ++i) {

            final ScratchObjectCustomBlockDefinition block = morph.getCustomBlock(i);
            final String specString = block.getUserSpec();

            // check if new custom block spec
            if (!target.containsKey(specString)) {
                // obfuscate spec and insert
                final UserSpec newSpec = obfuscateSpec(specString, existing);
                target.put(specString, newSpec.toString());
                existing.add(newSpec);
            }

        }
    }

    /**
     * Obfuscates the given user spec string, making sure to avoid collisions
     * with the existing obfuscated specs.
     *
     * @param specString The spec to obfuscate.
     * @param existing The list of already obfuscated specs.
     * @return The obfuscation result.
     */
    private UserSpec obfuscateSpec(String specString, List<UserSpec> existing)
    {
        final UserSpec spec = UserSpecParser.parse(specString);
        final UserSpec newSpec = generateParamOnlySpec(spec);

        boolean exists;
        do {
            exists = existing.stream().anyMatch(newSpec::isSimilar);
            if (exists) {
                appendSuffix(newSpec, SPEC_SUFFIX);
            }
        } while (exists);

        return newSpec;
    }

    /**
     * Creates a duplicate of the given user spec but only containing parameters
     * (all labels are removed). The original is unchanged.
     *
     * @param spec The original user spec.
     * @return A parameter-only spec.
     */
    private UserSpec generateParamOnlySpec(UserSpec spec)
    {
        final UserSpec newSpec = new UserSpec();
        for (int i = 0, n = spec.getPartCount(); i < n; ++i) {
            if (spec.isParameter(i)) {
                newSpec.addParameter(spec.getText(i));
            }
        }
        return newSpec;
    }

    /**
     * Intelligently appends the given suffix to the user spec.
     *
     * @param spec The user spec.
     * @param suffix The suffix string to append.
     */
    private void appendSuffix(UserSpec spec, String suffix)
    {
        int last = spec.getPartCount() - 1;
        if (last < 0 || spec.isParameter(last)) {
            spec.addLabel(suffix);
        } else {
            spec.setText(last, spec.getText(last) + suffix);
        }
    }
}
