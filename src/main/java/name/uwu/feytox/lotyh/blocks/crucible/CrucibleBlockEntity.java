package name.uwu.feytox.lotyh.blocks.crucible;

import com.google.common.collect.Lists;
import io.wispforest.owo.util.ImplementedInventory;
import name.uwu.feytox.lotyh.BlocksRegistry;
import name.uwu.feytox.lotyh.enums.MixTypes;
import name.uwu.feytox.lotyh.recipes.alchemy.AlchemyRecipe;
import name.uwu.feytox.lotyh.util.LIdentifier;
import name.uwu.feytox.lotyh.util.LVec3d;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class CrucibleBlockEntity extends BlockEntity implements IAnimatable, ImplementedInventory {
    private AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private boolean is_filled = false;
    private boolean is_mixing = false;
    private int mixingTicks = -1;
    private int currentMix = 0;
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(128, ItemStack.EMPTY);
    private int lastSlotNum = -1;


    public CrucibleBlockEntity(BlockPos pos, BlockState state) {
        super(BlocksRegistry.CRUCIBLE_BLOCK_ENTITY, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, CrucibleBlockEntity be) {
        CrucibleBlockEntity crucible = (CrucibleBlockEntity) world.getBlockEntity(pos);
        if (crucible != null) {
            if (crucible.mixingTicks > -1) {
                crucible.mixingTicks -= 1;
            } else if (crucible.mixingTicks == -1) {
                crucible.is_mixing = false;
                crucible.markDirty();
            }
        }
    }

    // TODO: static to class method
    public static void interact(World world, BlockPos pos, PlayerEntity player, Hand hand) {
        CrucibleBlockEntity crucible = (CrucibleBlockEntity) world.getBlockEntity(pos);


        if (crucible != null) {
            if (player.getStackInHand(hand).getItem().equals(Items.WATER_BUCKET)) {
                if (!crucible.is_filled) {
                    crucible.fill();
                    player.setStackInHand(hand, Items.BUCKET.getDefaultStack());
                }
            } else if (player.getStackInHand(hand).getItem().equals(Items.BUCKET)) {
                if (crucible.is_filled) {
                    crucible.empty();
                    player.setStackInHand(hand, Items.WATER_BUCKET.getDefaultStack());
                }
            } else if (player.getStackInHand(hand).isEmpty() && crucible.is_filled) {
                List<ItemEntity> itemEntities = world.getEntitiesByType(TypeFilter.instanceOf(ItemEntity.class),
                        Box.of(LVec3d.of(crucible.pos), 1D, 1D, 1D),
                        itemEntity -> true);
                crucible.mix(world, itemEntities);
            }
        }
    }

    public void fill() {
        this.is_filled = true;
        markDirty();
    }

    public void empty() {
        this.is_filled = false;
        markDirty();
    }

    public String getCurrentColor() {
        return MixTypes.getByNum(this.currentMix).name().toLowerCase();
    }

    public void mix(World world, List<ItemEntity> itemEntities) {
        if (!this.is_mixing) {
            this.mixingTicks = 18;
            int currentMix = MixTypes.getNextMixId(this.currentMix);
            this.currentMix = currentMix;

            if (!itemEntities.isEmpty()) {
                List<ItemStack> itemStacks = new ArrayList<>(Lists.transform(itemEntities, ItemEntity::getStack));
                itemStacks.sort(Comparator.comparing(itemStack -> Registry.ITEM.getId(itemStack.getItem()).getPath()));
                itemEntities.forEach(Entity::kill);

                for (ItemStack itemStack : itemStacks) {
                    boolean result = this.addStack(itemStack);
                    if (!result) {
                        this.getWorld().breakBlock(this.getPos(), false);
                        return;
                    }
                }

                if (this.checkRecipe(world)) return;

                this.addStack(Registry.ITEM.get(new LIdentifier(MixTypes.getByNum(currentMix).getLangKey()))
                        .getDefaultStack());
            } else {
                this.items.set(this.lastSlotNum,
                        Registry.ITEM.get(new LIdentifier(MixTypes.getByNum(currentMix).getLangKey()))
                                .getDefaultStack());
            }
            this.is_mixing = true;
            markDirty();
            System.out.println(this.currentMix);
        }
    }

    private boolean checkRecipe(World world) {
        if (!world.isClient) {
            Optional<AlchemyRecipe> match = world.getRecipeManager()
                    .getFirstMatch(AlchemyRecipe.Type.INSTANCE, this, world);

            if (match.isPresent()) {
                world.spawnEntity(new ItemEntity(world, this.pos.getX(), this.pos.getY()+2, this.pos.getZ(),
                        match.get().getOutput().copy()));
                this.clear();
                this.is_filled = false;
                this.currentMix = 0;
                this.lastSlotNum = -1;
                this.markDirty();
                return true;
            }
        }
        return false;
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putBoolean("is_filled", this.is_filled);
        nbt.putBoolean("is_mixing", this.is_mixing);
        nbt.putInt("currentMix", this.currentMix);
        nbt.putInt("mixingTicks", this.mixingTicks);
        nbt.putInt("lastSlotNum", this.lastSlotNum);
        Inventories.writeNbt(nbt, this.items);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        Inventories.readNbt(nbt, this.items);
        this.is_filled = nbt.getBoolean("is_filled");
        this.is_mixing = nbt.getBoolean("is_mixing");
        this.currentMix = nbt.getInt("currentMix");
        this.mixingTicks = nbt.getInt("mixingTicks");
        this.lastSlotNum = nbt.getInt("lastSlotNum");
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<>
                (this, "controller", 1, this::predicate));
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if (this.is_mixing) {
            event.getController().setAnimation(new AnimationBuilder()
                    .loop("animation.brewingCauldron.mixing"));
        } else {
            event.getController().setAnimation(new AnimationBuilder()
                    .playOnce("animation.brewingCauldron.idle"));
        }

        return PlayState.CONTINUE;
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.items;
    }

    public boolean addStack(ItemStack itemStack) {
        ItemStack oneStack = itemStack.copy();
        oneStack.setCount(1);
        int t = itemStack.getCount();
        for (int i = 0; i < t; i++) {
            this.lastSlotNum += 1;
            if (this.lastSlotNum >= this.size()) {
                return false;
            }
            this.setStack(this.lastSlotNum, oneStack);
        }
        return true;
    }
}
