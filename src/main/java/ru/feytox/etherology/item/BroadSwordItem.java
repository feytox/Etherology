package ru.feytox.etherology.item;

import lombok.val;
import net.fabricmc.fabric.api.item.v1.EnchantingContext;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import ru.feytox.etherology.enums.EArmPose;
import ru.feytox.etherology.particle.effects.ScalableParticleEffect;
import ru.feytox.etherology.registry.misc.EtherSounds;
import ru.feytox.etherology.registry.particle.EtherParticleTypes;
import ru.feytox.etherology.util.misc.DoubleModel;
import ru.feytox.etherology.util.misc.EIdentifier;

public class BroadSwordItem extends TwoHandheldSword implements DoubleModel {

    private static final Identifier RANGE_ID = EIdentifier.of("broad_sword_range");
    private static final Identifier SWEEP_ID = EIdentifier.of("broad_sword_sweep");

    public BroadSwordItem() {
        super(ToolMaterials.IRON, new Settings().maxDamage(476).attributeModifiers(SwordItem.createAttributeModifiers(ToolMaterials.IRON, 5, -3.1f)
                .with(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE, new EntityAttributeModifier(RANGE_ID, 0.33f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), AttributeModifierSlot.MAINHAND)
                .with(EntityAttributes.PLAYER_SWEEPING_DAMAGE_RATIO, new EntityAttributeModifier(SWEEP_ID, 0.5f, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)));
    }

    public static boolean isUsing(PlayerEntity player) {
        return isUsing(player, BroadSwordItem.class);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return EArmPose.TWOHANDHELD_ETHEROLOGY.getUseAction();
    }

    public static void replaceSweepParticle(ServerWorld world, double x, double y, double z) {
        val effect = new ScalableParticleEffect(EtherParticleTypes.SCALABLE_SWEEP, 2.0f);
        effect.spawnParticles(world, 1, 0, new Vec3d(x, y, z));
    }

    public static SoundEvent replaceAttackSound(PlayerEntity player, SoundEvent sound) {
        if (!BroadSwordItem.isUsing(player)) return sound;
        return EtherSounds.BROADSWORD;
    }

    @Override
    public boolean canBeEnchantedWith(ItemStack stack, RegistryEntry<Enchantment> enchantment, EnchantingContext context) {
        if (!super.canBeEnchantedWith(stack, enchantment, context)) return false;
        return !enchantment.matchesKey(Enchantments.LOOTING);
    }
}
