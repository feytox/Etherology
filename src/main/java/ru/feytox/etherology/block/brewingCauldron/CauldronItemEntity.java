package ru.feytox.etherology.block.brewingCauldron;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CauldronItemEntity extends ItemEntity {
    public CauldronItemEntity(World world, double x, double y, double z, ItemStack stack) {
        super(world, x, y, z, stack);
    }

    /**
     * @see net.minecraft.util.ItemScatterer#spawn(World, double, double, double, ItemStack)
     */
    public static void spawn(World world, Vec3d pos, ItemStack stack) {
        double d = EntityType.ITEM.getWidth();
        double e = 1.0 - d;
        double f = d / 2.0;
        double g = Math.floor(pos.x) + world.random.nextDouble() * e + f;
        double h = Math.floor(pos.y) + world.random.nextDouble() * e;
        double i = Math.floor(pos.z) + world.random.nextDouble() * e + f;

        while(!stack.isEmpty()) {
            CauldronItemEntity itemEntity = new CauldronItemEntity(world, g, h, i, stack.split(world.random.nextInt(21) + 10));
            itemEntity.setVelocity(world.random.nextTriangular(0.0, 0.11485000171139836), world.random.nextTriangular(0.2, 0.11485000171139836), world.random.nextTriangular(0.0, 0.11485000171139836));
            world.spawnEntity(itemEntity);
        }

    }
}
