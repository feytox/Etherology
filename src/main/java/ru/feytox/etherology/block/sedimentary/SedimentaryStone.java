package ru.feytox.etherology.block.sedimentary;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.Etherology;
import ru.feytox.etherology.magic.zones.EssenceZoneType;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static ru.feytox.etherology.registry.block.EBlocks.SEDIMENTARY_BLOCK_ENTITY;
import static ru.feytox.etherology.registry.block.EBlocks.SEDIMENTARY_STONE;

@Getter
public class SedimentaryStone extends Block implements BlockEntityProvider {

    private static final String BASE_ID = "sedimentary_stone";
    private static final Map<SedimentaryType, SedimentaryStone> TYPE_TO_STONE = new Object2ObjectOpenHashMap<>();
    public static final BooleanProperty POWERED = Properties.POWERED;

    private final EssenceZoneType zoneType;
    private final EssenceLevel essenceLevel;
    private final String zoneId;

    public SedimentaryStone(EssenceZoneType zoneType, EssenceLevel essenceLevel) {
        super(Settings.copy(Blocks.STONE));
        this.zoneType = zoneType;
        this.essenceLevel = essenceLevel;
        this.zoneId = StringUtils.capitalize(zoneType.asString());
        TYPE_TO_STONE.put(new SedimentaryType(zoneType, essenceLevel), this);

        setDefaultState(getDefaultState().with(POWERED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClient || state.get(POWERED) == world.isReceivingRedstonePower(pos)) return;
        world.setBlockState(pos, state.cycle(POWERED), NOTIFY_LISTENERS);
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(POWERED, ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos()));
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType options) {
        super.appendTooltip(stack, context, tooltip, options);
        if (zoneType.isZone()) tooltip.add(1, Text.translatable("lore.etherology.primoshard", zoneId).formatted(Formatting.DARK_PURPLE));
    }

    @Override
    public String getTranslationKey() {
        if (this.equals(SEDIMENTARY_STONE)) return super.getTranslationKey();
        return SEDIMENTARY_STONE.getTranslationKey();
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (newState.getBlock() instanceof SedimentaryStone) return;
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!essenceLevel.isFull() || !(stack.getItem() instanceof AxeItem)) return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        boolean result = false;
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof SedimentaryStoneBlockEntity sedimentaryBlock) {
            result = sedimentaryBlock.onUseAxe(state, world, zoneType, hit.getSide().getVector());
        }
        return result ? ItemActionResult.SUCCESS : ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        generateDrop(world, pos, player);

        return super.onBreak(world, pos, state, player);
    }

    private void generateDrop(World world, BlockPos pos, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof SedimentaryStoneBlockEntity sedimentary)) return;
        if (world.isClient || player.isCreative()) return;

        ItemStack stack = SEDIMENTARY_STONE.asItem().getDefaultStack();

        RegistryEntry<Enchantment> silkTouch = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(Enchantments.SILK_TOUCH).orElse(null);
        if (silkTouch != null && EnchantmentHelper.getEquipmentLevel(silkTouch, player) > 0) {
            stack = asItem().getDefaultStack();
            stack.applyComponentsFrom(sedimentary.createComponentMap());
        }

        ItemEntity itemEntity = new ItemEntity(world, (double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5, stack);
        itemEntity.setToDefaultPickupDelay();
        world.spawnEntity(itemEntity);
    }

    @Override
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        ItemStack itemStack = super.getPickStack(world, pos, state);
        world.getBlockEntity(pos, SEDIMENTARY_BLOCK_ENTITY).ifPresent((blockEntity) ->
                blockEntity.setStackNbt(itemStack, world.getRegistryManager()));
        return itemStack;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SedimentaryStoneBlockEntity(pos, state, essenceLevel);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return SedimentaryStoneBlockEntity.getTicker(world, type, SEDIMENTARY_BLOCK_ENTITY);
    }

    public static String createId(EssenceZoneType zone, EssenceLevel essenceLevel) {
        String id = BASE_ID;
        if (zone.isZone()) id = id.concat("_" + zone.asString());
        if (essenceLevel.isPresent()) id = id.concat("_" + essenceLevel.asString());
        return id;
    }

    public static void executeOnStone(BlockState state, Consumer<SedimentaryStone> stoneConsumer) {
        if (!(state.getBlock() instanceof SedimentaryStone stone)) {
            Etherology.ELOGGER.error("Failed to get sedimentary stone from block state");
            return;
        }

        stoneConsumer.accept(stone);
    }

    public static BlockState transformState(BlockState oldState, EssenceZoneType zone, EssenceLevel level) {
        SedimentaryStone block = TYPE_TO_STONE.get(new SedimentaryType(zone, level));
        if (oldState.getBlock().equals(block)) return oldState;
        if (block == null) {
            Etherology.ELOGGER.error("Failed to get sedimentary stone for zone {} and level {}", zone, level);
            return oldState;
        }

        return block.getDefaultState().with(POWERED, oldState.get(POWERED));
    }

    private record SedimentaryType(EssenceZoneType zone, EssenceLevel essenceLevel) {}
}
