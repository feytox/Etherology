package ru.feytox.etherology.datagen.util;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import ru.feytox.etherology.magic.lens.LensModifier;

import java.util.Optional;

public class RuTranslationBuilder {

    private final FabricLanguageProvider.TranslationBuilder builder;

    public RuTranslationBuilder(FabricLanguageProvider.TranslationBuilder builder) {
        this.builder = builder;
    }

    public void add(Block block, String value) {
        builder.add(block, value);
    }

    public void add(Item item, String value) {
        builder.add(item, value);
    }

    public void add(LensModifier modifier, String value) {
        builder.add(modifier.modifierId(), value);
    }

    public void add(StatusEffect effect, String value) {
        builder.add(effect, value);
    }

    public void add(RegistryEntry<Potion> potion, String value) {
        builder.add(Potion.finishTranslationKey(Optional.of(potion), "item.minecraft.potion.effect."), "Зелье " + value);
        builder.add(Potion.finishTranslationKey(Optional.of(potion), "item.minecraft.splash_potion.effect."), "Взрывное зелье " + value);
        builder.add(Potion.finishTranslationKey(Optional.of(potion), "item.minecraft.lingering_potion.effect."), "Туманное зелье " + value);
        builder.add(Potion.finishTranslationKey(Optional.of(potion), "item.minecraft.tipped_arrow.effect."), "Стрела " + value);
    }

    public void add(String key, String value) {
        builder.add(key, value);
    }

    public void add(RegistryKey<Enchantment> enchantment, String value) {
        builder.add(enchantment.getValue().toTranslationKey("enchantment"), value);
    }

    public void add(TagKey<?> tag, String value) {
        builder.add(tag, value);
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
