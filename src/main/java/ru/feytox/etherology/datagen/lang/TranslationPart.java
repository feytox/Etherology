package ru.feytox.etherology.datagen.lang;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.block.Block;

public class TranslationPart {
    private final BlockRuTranslationBuilder builder;
    private final String suffix;

    public TranslationPart(FabricLanguageProvider.TranslationBuilder builder, String suffix) {
        this.builder = new BlockRuTranslationBuilder(builder);
        this.suffix = suffix;
    }

    public TranslationPart(BlockRuTranslationBuilder builder, String suffix) {
        this.builder = builder;
        this.suffix = suffix;
    }

    public TranslationPart stairs(Block block) {
        builder.addStairs(block, suffix);
        return this;
    }

    public TranslationPart slab(Block block) {
        builder.addSlab(block, suffix);
        return this;
    }

    public TranslationPart plate(Block block) {
        builder.addPlate(block, suffix);
        return this;
    }

    public TranslationPart button(Block block) {
        builder.addButton(block, suffix);
        return this;
    }

    public TranslationPart wall(Block block) {
        builder.addWall(block, suffix);
        return this;
    }
}
