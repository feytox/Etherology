package ru.feytox.etherology.registry.block;

import lombok.experimental.UtilityClass;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import ru.feytox.etherology.util.misc.EBlock;
import ru.feytox.etherology.util.misc.EIdentifier;

/**
 * The registry class to prevent Re-entrance Error. You can deprecate this class once the boat type is de-hardcoded.
 * @see ru.feytox.etherology.mixin.EarlyRisers
 */
@UtilityClass
public class ExtraBlocksRegistry {

    public static final Block PEACH_PLANKS = register("peach_planks", new Block(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).burnable())).withItem();

    public static void registerAll() {}

    private static EBlock register(String id, Block block) {
        return new EBlock(Registry.register(Registries.BLOCK, EIdentifier.of(id), block));
    }
}
