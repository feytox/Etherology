package name.uwu.feytox.etherology.util.feyapi;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import java.util.function.Consumer;

public class NoDamageHandler {
    public static int damage(ItemStack stack, int amount, LivingEntity entity, Consumer<LivingEntity> breakCallback) {
        return 0;
    }
}
