package ru.feytox.etherology.magic.staff;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.StringIdentifiable;
import ru.feytox.etherology.util.misc.CodecUtil;

import java.util.List;
import java.util.function.Supplier;

@RequiredArgsConstructor
public enum StaffPart implements StringIdentifiable {
    CORE(false, StaffMaterial.MATERIALS),
    HANDLE(false, StaffColors.COLORS),
    DECOR(true, StaffStyles.STYLES, StaffMetals.METALS),
    HEAD(true, StaffStyles.STYLES, StaffMetals.METALS),
    LENS(false, StaffLenses.LENSES),
    TIP(true, StaffStyles.STYLES, StaffMetals.METALS);

    public static final Codec<StaffPart> CODEC = StringIdentifiable.createBasicCodec(StaffPart::values);
    public static final PacketCodec<ByteBuf, StaffPart> PACKET_CODEC = CodecUtil.ofEnum(values());

    @Getter
    private final boolean styled;

    @Getter
    @NonNull
    private final Supplier<List<? extends StaffPattern>> firstPatterns;

    @Getter
    @NonNull
    private final Supplier<List<? extends StaffPattern>> secondPatterns;

    StaffPart(boolean styled, @NonNull Supplier<List<? extends StaffPattern>> firstPatterns) {
        this(styled, firstPatterns, StaffPattern.memoize(StaffPattern.EMPTY));
    }

    public String getName() {
        return name().toLowerCase();
    }

    @Override
    public String asString() {
        return name();
    }
}
