package ru.feytox.etherology.datagen.lang;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.block.Block;

public class BlockRuTranslationBuilder {
    private final FabricLanguageProvider.TranslationBuilder builder;

    public BlockRuTranslationBuilder(FabricLanguageProvider.TranslationBuilder builder) {
        this.builder = builder;
    }

    public void add(Block block, String name) {
        builder.add(block, name);
    }

    /**
     * @param blockType название типа блока с большой (!) буквы
     * @param suffix 1 слово - прилагательное, 2 слова - из...
     */
    private void addTranslation(Block block, String blockType, String suffix) {
        String result = suffix + " " + blockType.toLowerCase();
        if (suffix.split(" ").length > 1) {
            result = blockType + " из " + suffix;
        }
        add(block, result);
    }

    public void addStairs(Block block, String suffix) {
        addTranslation(block, "Ступеньки", suffix);
    }

    public void addPlate(Block block, String suffix) {
        addTranslation(block, "Нажимная плита", suffix);
    }

    public void addSlab(Block block, String suffix) {
        addTranslation(block, "Плита", suffix);
    }

    public void addButton(Block block, String suffix) {
        addTranslation(block, "Кнопка", suffix);
    }

    public void addWall(Block block, String suffix) {
        addTranslation(block, "Ограда", suffix);
    }
}
