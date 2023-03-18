package ru.feytox.etherology.items.glints;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;
import ru.feytox.etherology.util.feyapi.NoDamageHandler;
import ru.feytox.etherology.util.registry.SimpleItem;

public abstract class AbstractGlintItem extends SimpleItem {
    private final float maxEther;

    public AbstractGlintItem(String itemId, float maxEther) {
        super(itemId, new FabricItemSettings().maxDamage(MathHelper.floor(maxEther)).customDamage(NoDamageHandler::damage));
        this.maxEther = maxEther;
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = super.getDefaultStack();
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putFloat("stored_ether", 0);
        stack.setNbt(nbt);

        return stack;
    }

    public float getMaxEther() {
        return maxEther;
    }

    @Override
    public boolean isDamageable() {
        return true;
    }

    /**
     * @return излишек, который не поместился в глинт
     */
    public static float increment(ItemStack stack, float maxEther, float value) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null) return 0;

        float storedEther = nbt.getFloat("stored_ether");
        float newEther = storedEther + value;
        newEther = Math.min(newEther, maxEther);
        nbt.putFloat("stored_ether", newEther);
        // TODO: 16/03/2023 проверить необходимость
        stack.setNbt(nbt);
        stack.setDamage(MathHelper.floor(maxEther - storedEther));

        return value - newEther + storedEther;
    }

    /**
     * @return количество забранного эфира
     */
    public static float decrement(ItemStack stack, float maxEther, float value) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null) return 0;

        float storedEther = nbt.getFloat("stored_ether");
        float newEther = storedEther - value;
        newEther = Math.max(newEther, 0);
        nbt.putFloat("stored_ether", newEther);
        // TODO: 16/03/2023 проверить необходимость
        stack.setNbt(nbt);
        stack.setDamage(MathHelper.floor(maxEther - storedEther));

        return storedEther - newEther;
    }
}
