package ru.feytox.etherology.util.misc;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;


/**
 * @see ru.feytox.etherology.block.pedestal.PedestalRenderer#renderVanillaGroundItem(MatrixStack, World, ItemStack, VertexConsumerProvider, float, int, ItemRenderer, Vec3d, float, BlockPos)
 */
public class PseudoLivingEntity extends LivingEntity {

    // TODO: 17.06.2024 consider replacing this class with something BETTER

    public PseudoLivingEntity(World world, BlockPos blockPos) {
        super(EntityType.FOX, world);
        setPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return null;
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot) {
        return null;
    }

    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack) {}

    @Override
    public Arm getMainArm() {
        return Arm.RIGHT;
    }
}
