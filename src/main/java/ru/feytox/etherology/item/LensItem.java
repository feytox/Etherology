package ru.feytox.etherology.item;

import com.google.common.base.Suppliers;
import lombok.Getter;
import lombok.val;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.magic.ether.EtherComponent;
import ru.feytox.etherology.magic.lens.LensComponent;
import ru.feytox.etherology.magic.lens.LensModifier;
import ru.feytox.etherology.magic.lens.LensModifiersData;
import ru.feytox.etherology.magic.staff.*;
import ru.feytox.etherology.registry.misc.ComponentTypes;
import ru.feytox.etherology.util.misc.ItemComponent;
import ru.feytox.etherology.util.misc.ItemData;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @see LensComponent
 * @see StaffItem
 */
public abstract class LensItem extends Item {

    private static final Supplier<Random> RANDOM_PROVIDER = Suppliers.memoize(Random::create);
    public static final int CHARGE_LIMIT = 100;

    @Nullable @Getter
    private final StaffLenses lensType;
    private final float streamCost;
    private final float chargeCost;

    protected LensItem(@Nullable StaffLenses lensType, float streamCost, float chargeCost) {
        super(new Settings().maxCount(1).maxDamage(100).component(ComponentTypes.LENS, LensComponent.EMPTY));
        this.lensType = lensType;
        this.streamCost = streamCost;
        this.chargeCost = chargeCost;
    }

    /**
     * @return true if lens damaged, otherwise - false
     */
    public abstract boolean onStreamUse(World world, LivingEntity entity, ItemData<LensComponent> lensData, ItemStack lensStack, boolean hold, Supplier<Hand> handGetter);

    /**
     * @return true if lens damaged, otherwise - false
     */
    public abstract boolean onChargeUse(World world, LivingEntity entity, ItemData<LensComponent> lensData, ItemStack lensStack, boolean hold, Supplier<Hand> handGetter);

    /**
     * @return true if lens damaged, otherwise - false
     */
    public boolean onStreamStop(World world, LivingEntity entity, ItemData<LensComponent> lensData, ItemStack lensStack, int holdTicks, Supplier<Hand> handGetter) {
        return false;
    }

    /**
     * @return true if lens damaged, otherwise - false
     */
    public boolean onChargeStop(World world, LivingEntity entity, ItemData<LensComponent> lensData, ItemStack lensStack, int holdTicks, Supplier<Hand> handGetter) {
        return false;
    }

    /**
     * @return false if ether decremented, otherwise - true
     */
    public static boolean decrementEther(LivingEntity entity, ItemStack lensStack, LensComponent lensData) {
        if (!(lensStack.getItem() instanceof LensItem lensItem)) return true;
        return !EtherComponent.decrement(entity, lensItem.getEtherCost(lensData));
    }

    public float getEtherCost(LensComponent lensData) {
        float cost = switch (lensData.mode()) {
            case STREAM -> streamCost;
            case CHARGE -> chargeCost;
        };
        return getEtherCost(lensData, cost);
    }

    private float getEtherCost(LensComponent lensData, float baseCost) {
        return baseCost * lensData.calcValue(LensModifier.SAVING, 1, 0.1f, 0.75f);
    }

    public int getStreamCooldown(LensComponent lensData) {
        return lensData.calcRoundValue(LensModifier.STREAM, 16, 1, 0.67f);
    }

    public int getChargeTime(LensComponent lensData, int holdTicks) {
        return Math.min(CHARGE_LIMIT, Math.round(holdTicks * lensData.calcValue(LensModifier.CHARGE, 1, 4, 0.8f)));
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        val lensData = LensComponent.get(stack).orElse(null);
        if (lensData == null) return;

        lensData.modifiers().getModifiers().forEach((id, level) -> {
            MutableText text = Text.translatable(id.toTranslationKey()).formatted(Formatting.GRAY);
            text.append(" ").append(Text.translatable("enchantment.level." + level));
            tooltip.add(text);
        });
    }

    /**
     * @return true if damaged, otherwise - false
     */
    public static boolean damageLens(ServerWorld world, ItemStack lensStack, int damage) {
        if (!(lensStack.getItem() instanceof LensItem)) return false;
        Random random = RANDOM_PROVIDER.get();
        float damageChance = getDamageChance(lensStack);

        boolean isDamaged = false;
        for (int i = 0; i < damage; i++) {
            if (lensStack.isEmpty()) break;
            if (damageChance < 1.0f && random.nextFloat() > damageChance) continue;
            isDamaged = true;
            lensStack.damage(1, world, null, item -> {});
        }

        return isDamaged;
    }

    public static void playLensBrakeSound(ServerWorld world, Vec3d pos) {
        Random random = world.getRandom();
        world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 0.8f, 0.8f + random.nextFloat()*0.4f);
    }

    public static void spawnLensBrakeParticles(ServerWorld world, Item item, Vec3d pos, float pitch, float yaw) {
        Random random = world.getRandom();
        val effect = new ItemStackParticleEffect(ParticleTypes.ITEM, item.getDefaultStack());
        for(int i = 0; i < 5; ++i) {
            Vec3d velocity = new Vec3d((random.nextDouble() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
            velocity = velocity.rotateX(-pitch * 0.017453292F);
            velocity = velocity.rotateY(-yaw * 0.017453292F);
            double d = -random.nextDouble() * 0.6 - 0.3;
            Vec3d particlePos = new Vec3d((random.nextDouble() - 0.5) * 0.3, d, 0.6);
            particlePos = particlePos.rotateX(-pitch * 0.017453292F);
            particlePos = particlePos.rotateY(-yaw * 0.017453292F);
            particlePos = particlePos.add(pos);
            world.spawnParticles(effect, particlePos.x, particlePos.y, particlePos.z, 1, velocity.x, velocity.y + 0.05, velocity.z, 0.1);
        }
    }

    private static float getDamageChance(ItemStack lensStack) {
        return LensComponent.get(lensStack)
                .map(component -> component.calcValue(LensModifier.FILTERING, 1, 0.1f, 0.7f))
                .orElse(0.0f);
    }

    /**
     * Places a lens on a staff. Before this you need to take old lens from staff
     *
     * @param staffStack the ItemStack representing the staff
     * @param lensStack the ItemStack representing the lens
     */
    public static void placeLensOnStaff(ItemStack staffStack, ItemStack lensStack) {
        if (!(staffStack.getItem() instanceof StaffItem)) return;
        if (!(lensStack.getItem() instanceof LensItem lensItem)) return;
        if (lensItem.isUnadjusted()) return;

        StaffLenses lensType = StaffLenses.getLens(lensStack);
        if (lensType == null) return;

        StaffItem.setLensComponent(staffStack, lensStack.copy());
        StaffComponent.getWrapper(staffStack)
                .ifPresent(staff -> staff.set(new StaffPartInfo(StaffPart.LENS, lensType, StaffPattern.EMPTY), StaffComponent::setPartInfo).save());

        lensStack.decrement(1);
    }

    /**
     * Takes a lens from the staff.
     *
     * @param staffStack the staff item stack
     * @return the lens item stack, or null if the staff does not have a lens
     */
    @Nullable
    public static ItemStack takeLensFromStaff(ItemStack staffStack) {
        if (!(staffStack.getItem() instanceof StaffItem)) return null;

        ItemStack lensStack = getStaffLens(staffStack);
        if (lensStack == null) return null;

        StaffComponent.getWrapper(staffStack)
                .ifPresent(staff -> staff.set(StaffPart.LENS, StaffComponent::removePartInfo).save());
        staffStack.remove(ComponentTypes.STAFF_LENS);

        return lensStack;
    }

    @Nullable
    public static LensComponent getStaffLensComponent(ItemStack staffStack) {
        ItemStack lensStack = getStaffLens(staffStack);
        return lensStack == null ? null : LensComponent.get(lensStack).orElse(null);
    }

    @Nullable
    public static ItemStack getStaffLens(ItemStack staffStack) {
        return Optional.ofNullable(staffStack.get(ComponentTypes.STAFF_LENS))
                .map(ItemComponent::stack).filter(stack -> !stack.isEmpty()).orElse(null);
    }

    public boolean isUnadjusted() {
        return false;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return !LensComponent.get(stack).map(LensComponent::modifiers).map(LensModifiersData::isEmpty).orElse(true);
    }
}
