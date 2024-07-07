package ru.feytox.etherology.datagen.util;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.registry.entry.RegistryEntry;
import ru.feytox.etherology.magic.lens.LensModifier;

import java.util.Optional;

public class RuTranslationBuilder {

    private final FabricLanguageProvider.TranslationBuilder builder;

    public RuTranslationBuilder(FabricLanguageProvider.TranslationBuilder builder) {
        this.builder = builder;
    }

    public void add(Block block, String name) {
        builder.add(block, name);
    }

    public void add(Item item, String name) {
        builder.add(item, name);
    }

    public void add(LensModifier modifier, String name) {
        builder.add(modifier.modifierId(), name);
    }

    public void add(StatusEffect effect, String name) {
        builder.add(effect, name);
    }

    public void add(RegistryEntry<Potion> potion, String name) {
        builder.add(Potion.finishTranslationKey(Optional.of(potion), "item.minecraft.potion.effect."), "Зелье " + name);
        builder.add(Potion.finishTranslationKey(Optional.of(potion), "item.minecraft.splash_potion.effect."), "Взрывное зелье " + name);
        builder.add(Potion.finishTranslationKey(Optional.of(potion), "item.minecraft.lingering_potion.effect."), "Туманное зелье " + name);
    }

    public void add(String key, String name) {
        builder.add(key, name);
    }

    public void add(Enchantment enchantment, String name) {
        builder.add(enchantment, name);
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
