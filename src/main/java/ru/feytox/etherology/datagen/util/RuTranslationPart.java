package ru.feytox.etherology.datagen.util;

import net.minecraft.block.Block;

public class RuTranslationPart {
    private final BlockRuTranslationBuilder builder;
    private final String suffix;

    public RuTranslationPart(BlockRuTranslationBuilder builder, String suffix) {
        this.builder = builder;
        this.suffix = suffix;
    }

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
