package ru.feytox.etherology.magic.ether;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.val;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.cca.api.v3.component.ComponentV3;
import org.ladysnake.cca.api.v3.component.CopyableComponent;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;
import ru.feytox.etherology.mixin.InGameHudAccessor;
import ru.feytox.etherology.registry.misc.EtherologyComponents;
import ru.feytox.etherology.util.misc.EIdentifier;

// TODO: 15.06.2024 split into multiple files
@RequiredArgsConstructor
public class EtherComponent implements ComponentV3, CopyableComponent<EtherComponent>, ServerTickingComponent, AutoSyncedComponent {

    private static final float EXHAUSTION_1 = 5.0f;
    private static final float EXHAUSTION_2 = 3.0f;
    private static final float EXHAUSTION_3 = 2.0f;
    private static final float EXHAUSTION_4 = 0.1f;
    private static final float EX_1_CHANCE = 1.0f / (100*20);
    private static final float EX_2_CHANCE = 1.0f / (80*20);
    private static final float EX_3_CHANCE = 1.0f / (60*20);
    private static final int CURSE_TICKS = 100;
    private static final int UNCURSE_TICKS = 50;
    private static final int REGEN_TICKS = 40;
    private static final int EX_TICKS = 20;

    private static final Identifier HEALTH_MODIFIER_ID = EIdentifier.of("devastating_health");
    private static final Identifier SPEED_MODIFIER_ID = EIdentifier.of("devastating_speed");
    private static final Identifier OUTLINE = EIdentifier.of("textures/misc/corruption_outline.png");

    private static final Identifier FULL_HEART = EIdentifier.of("hud/heart/devastating_full");
    private static final Identifier FULL_BLINKING_HEART = EIdentifier.of("hud/heart/devastating_full_blinking");
    private static final Identifier HALF_HEART = EIdentifier.of("hud/heart/devastating_half");
    private static final Identifier HALF_BLINKING_HEART = EIdentifier.of("hud/heart/devastating_half_blinking");
    private static final Identifier HARDCORE_FULL_HEART = EIdentifier.of("hud/heart/devastating_hardcore_full");
    private static final Identifier HARDCORE_FULL_BLINKING_HEART = EIdentifier.of("hud/heart/devastating_hardcore_full_blinking");
    private static final Identifier HARDCORE_HALF_HEART = EIdentifier.of("hud/heart/devastating_hardcore_half");
    private static final Identifier HARDCORE_HALF_BLINKING_HEART = EIdentifier.of("hud/heart/devastating_hardcore_half_blinking");

    private final LivingEntity entity;
    @Getter @Setter
    private float points = 20.0f;
    @Getter @Setter
    private float maxPoints = 20.0f;
    @Getter @Setter
    private float pointsRegen = 0.001f;
    private boolean hasCurse = false;
    private float healthModifier = 1.0f;

    public static boolean isEnough(LivingEntity entity, float etherCost) {
        val optionalData = EtherologyComponents.ETHER.maybeGet(entity);
        return optionalData.filter(etherComponent -> etherComponent.points >= etherCost).isPresent();
    }

    /**
     * @return true if ether incremented, otherwise - false
     */
    public static boolean increment(LivingEntity entity, float value) {
        val optionalData = EtherologyComponents.ETHER.maybeGet(entity);
        if (optionalData.isEmpty()) return false;

        val data = optionalData.get();
        if (data.points >= data.maxPoints) return false;

        data.points = Math.min(data.maxPoints, data.points + value);
        data.sync();
        return true;
    }

    /**
     * @return true if ether decremented, otherwise - false
     */
    public static boolean decrement(LivingEntity entity, float value) {
        val optionalData = EtherologyComponents.ETHER.maybeGet(entity);
        if (optionalData.isEmpty()) return false;

        val data = optionalData.get();
        if (data.points < value) return false;

        data.points -= value;
        data.sync();
        return true;
    }

    @Override
    public void serverTick() {
        World world = entity.getWorld();
        if (world == null) return;

        tickRegeneration(world);
        tickExhaustion(world);
    }

    private void tickRegeneration(World world) {
        if (world.getTime() % REGEN_TICKS != 0) return;
        if (points >= maxPoints) return;
        points = Math.min(maxPoints, points + pointsRegen * REGEN_TICKS);
        EtherologyComponents.ETHER.sync(entity);
    }

    private void tickExhaustion(World world) {
        if (hasCurse || healthModifier < 1.0f) tickCurse(world);
        if (world.getTime() % EX_TICKS != 0) return;
        tickMaxHealth();
        tickSpeed();
        if (hasCurse) tickCurseHealth();

        if (points > EXHAUSTION_1) return;
        if (checkRand(world, EX_1_CHANCE)) {
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 300));
        }

        if (points > EXHAUSTION_2) return;
        if (checkRand(world, EX_2_CHANCE)) {
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 250, 1));
        }

        if (points > EXHAUSTION_3) return;
        if (checkRand(world, EX_3_CHANCE)) {
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 200));
        }

        if (points > EXHAUSTION_4 || hasCurse) return;
        hasCurse = true;
        sync();
    }

    private void tickCurse(World world) {
        if (world.getTime() % (hasCurse ? CURSE_TICKS : UNCURSE_TICKS) != 0) return;
        if (hasCurse && points >= EXHAUSTION_3) {
            hasCurse = false;
            sync();
        }

        if (hasCurse) healthModifier = Math.max(0.1f, healthModifier - 0.05f);
        else healthModifier = Math.min(1.0f, healthModifier + 0.1f);
        sync();
    }

    private void tickCurseHealth() {
        entity.setHealth(Math.min(entity.getHealth(), entity.getMaxHealth()));
    }

    private void tickMaxHealth() {
        val attrInstance = entity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        if (attrInstance == null) return;

        attrInstance.removeModifier(HEALTH_MODIFIER_ID);
        if (healthModifier >= 1.0f) return;

        double baseModifier = 20.0f / attrInstance.getValue();
        attrInstance.addTemporaryModifier(new EntityAttributeModifier(HEALTH_MODIFIER_ID, healthModifier * baseModifier - 1.0f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    }

    private void tickSpeed() {
        val attrInstance = entity.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (attrInstance == null) return;

        double modifier = 1 - points / EXHAUSTION_3;
        attrInstance.removeModifier(SPEED_MODIFIER_ID);
        if (modifier >= 1.0f || modifier <= 0.0f) return;

        modifier *= -0.025;
        attrInstance.addTemporaryModifier(new EntityAttributeModifier(SPEED_MODIFIER_ID, modifier, EntityAttributeModifier.Operation.ADD_VALUE));
    }

    private boolean checkRand(World world, float chance) {
        return world.getRandom().nextFloat() <= chance * EX_TICKS;
    }

    public static void renderOverlay(DrawContext context, InGameHud hud) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;

        Float opacity = getOverlayOpacity(player);
        if (opacity == null) return;
        ((InGameHudAccessor) hud).callRenderOverlay(context, OUTLINE, opacity);
    }

    private static Float getOverlayOpacity(PlayerEntity player) {
        return EtherologyComponents.ETHER.maybeGet(player)
                .map(EtherComponent::getPoints)
                .filter(points -> points <= EXHAUSTION_3)
                .map(points -> 1 - points / EXHAUSTION_3)
                .orElse(null);
    }

    private static boolean hasCurse(PlayerEntity player) {
        return EtherologyComponents.ETHER.maybeGet(player)
                .map(etherComponent -> etherComponent.hasCurse).orElse(false);
    }

    @Nullable
    public static Identifier getDevastatingTexture(InGameHud.HeartType originalHeart, boolean hardcore, boolean half, boolean blinking) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (originalHeart.equals(InGameHud.HeartType.CONTAINER) || player == null) return null;
        if (originalHeart.equals(InGameHud.HeartType.ABSORBING)) return null;
        if (!EtherComponent.hasCurse(player)) return null;

        if (hardcore) {
            if (half) return blinking ? HARDCORE_HALF_BLINKING_HEART :  HARDCORE_HALF_HEART;
            return blinking ?  HARDCORE_FULL_BLINKING_HEART :  HARDCORE_FULL_HEART;
        }

        if (half) return blinking ? HALF_BLINKING_HEART : HALF_HEART;
        return blinking ? FULL_BLINKING_HEART : FULL_HEART;
    }

    private void sync() {
        EtherologyComponents.ETHER.sync(entity);
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        points = tag.getFloat("points");
        maxPoints = tag.getFloat("max_points");
        pointsRegen = tag.getFloat("points_regen");
        hasCurse = tag.getBoolean("has_curse");
        healthModifier = tag.getFloat("health_modifier");
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putFloat("points", points);
        tag.putFloat("max_points", maxPoints);
        tag.putFloat("points_regen", pointsRegen);
        tag.putBoolean("has_curse", hasCurse);
        tag.putFloat("health_modifier", healthModifier);
    }

    @Override
    public void copyFrom(EtherComponent other, RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound tag = new NbtCompound();
        other.writeToNbt(tag, registryLookup);
        this.readFromNbt(tag, registryLookup);
    }
}
