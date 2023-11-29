package ru.feytox.etherology.registry.item;

import com.google.common.collect.ImmutableList;
import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import ru.feytox.etherology.util.feyapi.EIdentifier;

import java.util.Arrays;
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
                BREWING_CAULDRON, PEDESTAL_BLOCK, ARMILLARY_MATRIX, EMPOWERMENT_TABLE, INVENTOR_TABLE, ETHEREAL_STORAGE,
                ETHEREAL_FURNACE, ETHEREAL_CHANNEL, ETHEREAL_FORK, ETHEREAL_SOCKET, ETHEREAL_SPINNER, ETHEREAL_METRONOME,
                ESSENCE_DETECTOR_BLOCK, LEVITATOR, SEDIMENTARY_BLOCK, FURNITURE_SLAB, SHELF_SLAB, CLOSET_SLAB,
                SAMOVAR_BLOCK, JUG, CLAY_JUG).with(EItems.SPILL_BARREL);
        // peach
        etherItems.with(
                PEACH_LOG, STRIPPED_PEACH_LOG, PEACH_WOOD, STRIPPED_PEACH_WOOD, PEACH_PLANKS, PEACH_STAIRS, PEACH_SLAB, PEACH_BUTTON)
                .with(DecoBlockItems.PEACH_DOOR, DecoBlockItems.PEACH_SIGN).with(PEACH_FENCE, PEACH_FENCE_GATE, PEACH_PRESSURE_PLATE, PEACH_TRAPDOOR, PEACH_LEAVES, PEACH_SAPLING);
        // ethereal stones
        etherItems.with(
                ETHEREAL_STONE, COBBLED_ETHEREAL_STONE, ETHEREAL_STONE_BRICKS, CHISELED_ETHEREAL_STONE_BRICKS,
                MOSSY_ETHEREAL_STONE_BRICKS, MOSSY_COBBLED_ETHEREAL_STONE, POLISHED_ETHEREAL_STONE, ETHEREAL_STONE_STAIRS,
                COBBLED_ETHEREAL_STONE_STAIRS, ETHEREAL_STONE_BRICK_STAIRS, CHISELED_ETHEREAL_STONE_BRICK_STAIRS, MOSSY_ETHEREAL_STONE_BRICK_STAIRS,
                MOSSY_COBBLED_ETHEREAL_STONE_STAIRS, POLISHED_ETHEREAL_STONE_STAIRS, ETHEREAL_STONE_SLAB, COBBLED_ETHEREAL_STONE_SLAB,
                ETHEREAL_STONE_BRICK_SLAB, MOSSY_ETHEREAL_STONE_BRICK_SLAB, MOSSY_COBBLED_ETHEREAL_STONE_SLAB, POLISHED_ETHEREAL_STONE_SLAB,
                ETHEREAL_STONE_WALL, COBBLED_ETHEREAL_STONE_WALL, ETHEREAL_STONE_BRICK_WALL, ETHEREAL_STONE_BUTTON, ETHEREAL_STONE_PRESSURE_PLATE);
        // clay tiles
        etherItems.with(
                CLAY_TILES, BLUE_CLAY_TILES, GREEN_CLAY_TILES, RED_CLAY_TILES, YELLOW_CLAY_TILES,
                CLAY_TILES_SLAB, BLUE_CLAY_TILES_SLAB, GREEN_CLAY_TILES_SLAB, RED_CLAY_TILES_SLAB, YELLOW_CLAY_TILES_SLAB,
                CLAY_TILES_STAIRS, BLUE_CLAY_TILES_STAIRS, GREEN_CLAY_TILES_STAIRS, RED_CLAY_TILES_STAIRS, YELLOW_CLAY_TILES_STAIRS,
                CLAY_TILES_WALL, BLUE_CLAY_TILES_WALL, GREEN_CLAY_TILES_WALL, RED_CLAY_TILES_WALL, YELLOW_CLAY_TILES_WALL);
        // metals
        etherItems.with(ATTRAHITE_INGOT, ETHRIL_INGOT, TELDER_STEEL_INGOT, ATTRAHITE_NUGGET, ETHRIL_NUGGET, TELDER_STEEL_NUGGET);
        etherItems.with(ATTRAHITE_BLOCK, ETHRIL_BLOCK, TELDER_STEEL_BLOCK);
        // armor
        etherItems.with(ETHRIL_HELMET, ETHRIL_CHESTPLATE, ETHRIL_LEGGINGS, ETHRIL_BOOTS, TELDER_STEEL_HELMET, TELDER_STEEL_CHESTPLATE, TELDER_STEEL_LEGGINGS, TELDER_STEEL_BOOTS);
        // tools
        etherItems.with(ETHRIL_PICKAXE, TELDER_STEEL_PICKAXE, ETHRIL_AXE, TELDER_STEEL_AXE, ETHRIL_SWORD, TELDER_STEEL_SWORD, ETHRIL_SHOVEL, TELDER_STEEL_SHOVEL, ETHRIL_HOE, TELDER_STEEL_HOE);
        etherItems.with(BATTLE_PICKAXES);
        etherItems.with(HAMMERS);
        etherItems.with(GLAIVES);
        etherItems.with(IRON_SHIELD);
        // patterns
        etherItems.with(PATTERN_TABLETS);
        // staff and lenses
        etherItems.with(STAFF, UNADJUSTED_LENS, REDSTONE_LENS);
        // magic
        etherItems.with(
                OCULUS, TELDECORE, PRIMOSHARD_CLOS, PRIMOSHARD_KETA, PRIMOSHARD_RELLA, PRIMOSHARD_VIA,
                ETHRIL_MATRIX_RING, TELDER_STEEL_MATRIX_RING, NETHERITE_MATRIX_RING, GLINT, ETHEREAL_OIL, CORRUPTION_BUCKET);
        // plants
        etherItems.with(BEAMER_SEEDS, BEAM_FRUIT);

        ItemGroupEvents.modifyEntriesEvent(ETHEROLOGY_ITEMS).register(content -> etherItems.build().forEach(content::add));
    }

    private class Builder {
        private final ImmutableList.Builder<Item> listBuilder = new ImmutableList.Builder<>();

        public Builder with(Item... items) {
            listBuilder.add(items);
            return this;
        }

        public Builder with(Block... blocks) {
            listBuilder.addAll(Arrays.stream(blocks).map(Block::asItem).toList());
            return this;
        }

        public List<Item> build() {
            return listBuilder.build();
        }
    }
}
