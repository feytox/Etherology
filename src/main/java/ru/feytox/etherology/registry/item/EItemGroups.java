package ru.feytox.etherology.registry.item;

import com.google.common.collect.ImmutableList;
import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import ru.feytox.etherology.util.misc.EIdentifier;

import java.util.List;

import static ru.feytox.etherology.registry.block.DecoBlocks.*;
import static ru.feytox.etherology.registry.block.EBlocks.*;
import static ru.feytox.etherology.registry.item.ArmorItems.*;
import static ru.feytox.etherology.registry.item.DecoBlockItems.*;
import static ru.feytox.etherology.registry.item.EItems.*;
import static ru.feytox.etherology.registry.item.ToolItems.*;

@UtilityClass
public class EItemGroups {

    private static final ItemGroup ETHEROLOGY_ITEMS = FabricItemGroup.builder(new EIdentifier("etherology_items"))
            .icon(() -> new ItemStack(TELDECORE))
            .displayName(Text.of("Etherology"))
            .build();

    public static void registerAll() {
        registerMainGroup();
    }

    private static void registerMainGroup() {
        Builder etherItems = new Builder();
        // functional blocks
        etherItems.with(
                BREWING_CAULDRON, PEDESTAL_BLOCK, ARMILLARY_MATRIX, EMPOWERMENT_TABLE, INVENTOR_TABLE, JEWELRY_TABLE, ETHEREAL_STORAGE,
                ETHEREAL_FURNACE, ETHEREAL_CHANNEL, ETHEREAL_CHANNEL_CASE, ETHEREAL_FORK, ETHEREAL_SOCKET, ETHEREAL_SPINNER, ETHEREAL_METRONOME,
                ESSENCE_DETECTOR_BLOCK, LEVITATOR, SEDIMENTARY_BLOCK, FURNITURE_SLAB, SHELF_SLAB, CLOSET_SLAB,
                SAMOVAR_BLOCK, JUG, CLAY_JUG, CRATE, TUNING_FORK).with(EItems.SPILL_BARREL);
        // peach
        etherItems.with(
                PEACH_LOG, STRIPPED_PEACH_LOG, PEACH_WOOD, STRIPPED_PEACH_WOOD, WEEPING_PEACH_LOG, PEACH_PLANKS, PEACH_STAIRS, PEACH_SLAB, PEACH_BUTTON)
                .with(DecoBlockItems.PEACH_DOOR, DecoBlockItems.PEACH_SIGN).with(PEACH_FENCE, PEACH_FENCE_GATE, PEACH_PRESSURE_PLATE, PEACH_TRAPDOOR, PEACH_LEAVES, PEACH_SAPLING);
        // ethereal stones
        etherItems.with(
                ETHEREAL_STONE, ETHEREAL_STONE_BRICKS, CHISELED_ETHEREAL_STONE_BRICKS, CRACKED_ETHEREAL_STONE_BRICKS, POLISHED_ETHEREAL_STONE,
                ETHEREAL_STONE_STAIRS, ETHEREAL_STONE_BRICK_STAIRS, CRACKED_ETHEREAL_STONE_BRICK_STAIRS, POLISHED_ETHEREAL_STONE_STAIRS,
                ETHEREAL_STONE_SLAB, ETHEREAL_STONE_BRICK_SLAB, CRACKED_ETHEREAL_STONE_BRICK_SLAB, POLISHED_ETHEREAL_STONE_SLAB,
                ETHEREAL_STONE_WALL, ETHEREAL_STONE_BRICK_WALL, POLISHED_ETHEREAL_STONE_WALL,
                ETHEREAL_STONE_BUTTON, ETHEREAL_STONE_PRESSURE_PLATE);
        // attrahite
        etherItems.with(
                ATTRAHITE, ATTRAHITE_BRICKS, ATTRAHITE_BRICK_SLAB, ATTRAHITE_BRICK_STAIRS, ATTRAHITE_BRICK, ENRICHED_ATTRAHITE, RAW_AZEL
        );
        // metals
        etherItems.with(AZEL_INGOT, ETHRIL_INGOT, EBONY_INGOT, AZEL_NUGGET, ETHRIL_NUGGET, EBONY_NUGGET);
        etherItems.with(AZEL_BLOCK, ETHRIL_BLOCK, EBONY_BLOCK);
        // armor
        etherItems.with(ETHRIL_HELMET, ETHRIL_CHESTPLATE, ETHRIL_LEGGINGS, ETHRIL_BOOTS, EBONY_HELMET, EBONY_CHESTPLATE, EBONY_LEGGINGS, EBONY_BOOTS);
        // tools
        etherItems.with(ETHRIL_PICKAXE, EBONY_PICKAXE, ETHRIL_AXE, EBONY_AXE, ETHRIL_SWORD, EBONY_SWORD, ETHRIL_SHOVEL, EBONY_SHOVEL, ETHRIL_HOE, EBONY_HOE);
        etherItems.with(BATTLE_PICKAXES);
        etherItems.with(TUNING_MACE, BROADSWORD, IRON_SHIELD, STREAM_KEY, WARP_COUNTER);
        // patterns
        etherItems.with(PATTERN_TABLETS);
        // staff and lenses
        etherItems.with(STAFF, UNADJUSTED_LENS, REDSTONE_LENS);
        // magic
        // TODO: 12.04.2024 fix corruption bucket
        etherItems.with(
                OCULUS, REVELATION_VIEW, TELDECORE, PRIMOSHARD_CLOS, PRIMOSHARD_KETA, PRIMOSHARD_RELLA,
                PRIMOSHARD_VIA, GLINT, THUJA_OIL, CORRUPTION_BUCKET);
        // materials
        etherItems.with(ETHEROSCOPE, BINDER, EBONY, RESONATING_WAND);
        // plants
        etherItems.with(FOREST_LANTERN, FOREST_LANTERN_CRUMB, LIGHTELET, BEAMER_SEEDS, THUJA_SEEDS, BEAM_FRUIT);

        ItemGroupEvents.modifyEntriesEvent(ETHEROLOGY_ITEMS).register(content -> etherItems.build().forEach(content::add));
    }

    private class Builder {
        private final ImmutableList.Builder<ItemConvertible> listBuilder = new ImmutableList.Builder<>();

        public Builder with(ItemConvertible... items) {
            listBuilder.add(items);
            return this;
        }

        public List<ItemConvertible> build() {
            return listBuilder.build();
        }
    }
}
