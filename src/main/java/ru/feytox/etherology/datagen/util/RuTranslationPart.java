package ru.feytox.etherology.datagen.util;

import lombok.RequiredArgsConstructor;
import net.minecraft.block.Block;

@RequiredArgsConstructor(staticName = "of")
public class RuTranslationPart {

    private final RuTranslationBuilder builder;
    private final String suffix;

    public RuTranslationPart stairs(Block block) {
        builder.addStairs(block, suffix);
        return this;
    }

    public RuTranslationPart slab(Block block) {
        builder.addSlab(block, suffix);
        return this;
    }

    public RuTranslationPart plate(Block block) {
        builder.addPlate(block, suffix);
        return this;
    }

    public RuTranslationPart button(Block block) {
        builder.addButton(block, suffix);
        return this;
    }

    public RuTranslationPart wall(Block block) {
        builder.addWall(block, suffix);
        return this;
    }
}
