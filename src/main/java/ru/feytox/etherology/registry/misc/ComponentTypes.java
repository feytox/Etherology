package ru.feytox.etherology.registry.misc;

import com.mojang.serialization.Codec;
import lombok.experimental.UtilityClass;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import ru.feytox.etherology.magic.corruption.Corruption;
import ru.feytox.etherology.magic.lens.LensComponent;
import ru.feytox.etherology.magic.staff.StaffComponent;
import ru.feytox.etherology.util.misc.EIdentifier;
import ru.feytox.etherology.util.misc.ItemComponent;

import java.util.function.UnaryOperator;

@UtilityClass
public class ComponentTypes {

    public static final ComponentType<Float> STORED_ETHER = register("stored_ether", builder -> builder.codec(Codec.FLOAT).packetCodec(PacketCodecs.FLOAT));
    public static final ComponentType<LensComponent> LENS = register("lens", builder -> builder.codec(LensComponent.CODEC).packetCodec(LensComponent.PACKET_CODEC));
    public static final ComponentType<ItemComponent> STAFF_LENS = register("staff_lens", builder -> builder.codec(ItemComponent.CODEC).packetCodec(ItemComponent.PACKET_CODEC));
    public static final ComponentType<StaffComponent> STAFF = register("staff", builder -> builder.codec(StaffComponent.CODEC).packetCodec(StaffComponent.PACKET_CODEC));
    public static final ComponentType<Corruption> CORRUPTION = register("corruption", builder -> builder.codec(Corruption.CODEC).packetCodec(Corruption.PACKET_CODEC));
    public static final ComponentType<Integer> PSEUDO_DAMAGE = register("pseudo_damage", builder -> builder.codec(Codec.INT).packetCodec(PacketCodecs.VAR_INT));

    public static void registerAll() {}

    private static <T> ComponentType<T> register(String id, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, EIdentifier.of(id), builderOperator.apply(ComponentType.builder()).build());
    }
}
