package ru.feytox.etherology.util.misc;

import com.mojang.serialization.Codec;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record ItemComponent(ItemStack stack) {
    public static final Codec<ItemComponent> CODEC = ItemStack.OPTIONAL_CODEC.xmap(ItemComponent::new, ItemComponent::stack).stable();
    public static final PacketCodec<RegistryByteBuf, ItemComponent> PACKET_CODEC = ItemStack.OPTIONAL_PACKET_CODEC.xmap(ItemComponent::new, ItemComponent::stack);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemComponent that)) return false;

        return ItemStack.areItemsAndComponentsEqual(stack, that.stack);
    }

    @Override
    public int hashCode() {
        return ItemStack.hashCode(stack);
    }
}
