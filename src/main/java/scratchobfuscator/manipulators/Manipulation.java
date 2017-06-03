package scratchobfuscator.manipulators;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import scratchlib.objects.fixed.collections.ScratchObjectAbstractCollection;
import scratchlib.objects.user.ScratchObjectCustomBlockDefinition;
import scratchlib.objects.user.morphs.ScratchObjectScriptableMorph;
import scratchlib.objects.user.morphs.ScratchObjectStageMorph;


/**
 * Builder class for generating a manipulation plan for some stage that can then
 * be run.
 */
public class Manipulation
{
    private final ScratchObjectStageMorph stage;
    private final List<Consumer<ScratchObjectScriptableMorph>> morphConsumers = new ArrayList<>();
    private final List<Consumer<ScratchObjectCustomBlockDefinition>> customBlockConsumers = new ArrayList<>();
    private final List<Consumer<BlockView>> blockConsumers = new ArrayList<>();

    /**
     * @param stage The stage to operate on.
     */
    public Manipulation(ScratchObjectStageMorph stage)
    {
        this.stage = stage;
    }

    /**
     * Binds the given consumer to be executed for each scriptable morph (stage
     * and sprites) that is traversed. Multiple consumers can be bound.
     * 
     * @param consumer The action to bind.
     * @return This instance, for call chaining.
     */
    public Manipulation forEachMorph(Consumer<ScratchObjectScriptableMorph> consumer)
    {
        morphConsumers.add(consumer);
        return this;
    }

    /**
     * Binds the given consumer to be executed for each custom block that is
     * traversed. Multiple consumers can be bound.
     * 
     * @param consumer The action to bind.
     * @return This instance, for call chaining.
     */
    public Manipulation forEachCustomBlock(Consumer<ScratchObjectCustomBlockDefinition> consumer)
    {
        customBlockConsumers.add(consumer);
        return this;
    }

    /**
     * Binds the given consumer to be executed for each script block that is
     * traversed (including nested blocks). Multiple consumers can be bound.
     * 
     * <p>
     * Note that it is not strictly guaranteed that all instances are, in fact,
     * blocks. Appropriate checks should be performed.
     * 
     * @param consumer The action to bind.
     * @return This instance, for call chaining.
     */
    public Manipulation forEachBlock(Consumer<BlockView> consumer)
    {
        blockConsumers.add(consumer);
        return this;
    }

    /**
     * Applies this manipulation by traversing the stage and applying all
     * registered consumers.
     */
    public void run()
    {
        Stream.concat(Stream.of(stage), stage.streamSprites()).forEach(morph -> {

            trigger(morphConsumers, morph);

            morph.streamCustomBlocks().forEach(cb -> {
                trigger(customBlockConsumers, cb);
                final ScratchObjectAbstractCollection body = cb.getBody();
                if (body != null) {
                    processScript(body);
                }
            });

            morph.streamScriptBodies().forEach(this::processScript);

        });
    }

    private void processScript(ScratchObjectAbstractCollection script)
    {
        script.stream().map(obj -> (ScratchObjectAbstractCollection) obj).forEach(this::processBlock);
    }

    private void processBlock(ScratchObjectAbstractCollection block)
    {
        trigger(blockConsumers, new BlockView(block));

        block.stream().forEach(param -> {
            if (param instanceof ScratchObjectAbstractCollection) {
                processBlock((ScratchObjectAbstractCollection) param);
            }
        });
    }

    private <T> void trigger(List<Consumer<T>> consumers, T value)
    {
        consumers.forEach(c -> c.accept(value));
    }
}
