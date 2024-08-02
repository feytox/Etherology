package ru.feytox.etherology.network.interaction;

import io.netty.buffer.ByteBuf;
import lombok.RequiredArgsConstructor;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import ru.feytox.etherology.Etherology;
import ru.feytox.etherology.gui.teldecore.data.TeldecoreComponent;
import ru.feytox.etherology.network.util.AbstractC2SPacket;
import ru.feytox.etherology.registry.misc.EtherologyComponents;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class EntityComponentC2SType<C extends Component, V> extends AbstractC2SPacket.PacketType<EntityComponentC2SType.Packet<V>> {

    // TODO: 02.08.2024 consider using smth else... idk
    public static final EntityComponentC2SType<TeldecoreComponent, Identifier> TELDECORE_SELECTED;
    public static final EntityComponentC2SType<TeldecoreComponent, Integer> TELDECORE_PAGE;
    public static final EntityComponentC2SType<TeldecoreComponent, Identifier> TELDECORE_TAB;

    private final Function<C, V> getter;

    public EntityComponentC2SType(CustomPayload.Id<Packet<V>> id, PacketCodec<RegistryByteBuf, Packet<V>> codec, ServerPlayNetworking.PlayPayloadHandler<Packet<V>> handler, Function<C, V> getter) {
        super(id, codec, handler);
        this.getter = getter;
    }

    public void sendToServer(C component) {
        createPacket(getId()).apply(getter.apply(component)).sendToServer();
    }

    public static <C extends Component, V> EntityComponentC2SType<C, V> of(ComponentKey<C> componentKey, String valueName, Function<C, V> getter, BiConsumer<C, V> setter, PacketCodec<ByteBuf, V> valueCodec) {
        CustomPayload.Id<Packet<V>> id = new CustomPayload.Id<>(componentKey.getId().withSuffixedPath("_"+valueName));
        PacketCodec<RegistryByteBuf, Packet<V>> codec = PacketCodec.tuple(valueCodec, p -> p.value, createPacket(id));
        ServerPlayNetworking.PlayPayloadHandler<Packet<V>> handler = (payload, context) -> context.server().execute(() ->
                componentKey.maybeGet(context.player()).ifPresentOrElse(data -> setter.accept(data, payload.value),
                        () -> Etherology.ELOGGER.error("Failed to sync {} data for component {}", valueName, componentKey.getId())));
        return new EntityComponentC2SType<>(id, codec, handler, getter);
    }

    private static <V> Function<V, Packet<V>> createPacket(CustomPayload.Id<Packet<V>> id) {
        return value -> new Packet<V>(value) {
            @Override
            public Id<? extends CustomPayload> getId() {
                return id;
            }
        };
    }

    @RequiredArgsConstructor
    public abstract static class Packet<V> implements AbstractC2SPacket {

        private final V value;
    }

    static {
        TELDECORE_SELECTED = of(EtherologyComponents.TELDECORE, "selected", TeldecoreComponent::getSelected,
                TeldecoreComponent::setSelected, Identifier.PACKET_CODEC);
        TELDECORE_PAGE = of(EtherologyComponents.TELDECORE, "page", TeldecoreComponent::getPage,
                TeldecoreComponent::setPage, PacketCodecs.VAR_INT);
        TELDECORE_TAB = of(EtherologyComponents.TELDECORE, "tab", TeldecoreComponent::getTab,
                TeldecoreComponent::setTab, Identifier.PACKET_CODEC);
    }
}
