package ru.feytox.etherology.magic.staff;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.experimental.UtilityClass;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import ru.feytox.etherology.Etherology;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@UtilityClass
public class StaffPatterns {

    public static final Codec<StaffPattern> CODEC = Codec.STRING.xmap(StaffPatterns::get, StaffPattern::getName).stable();
    public static final PacketCodec<ByteBuf, StaffPattern> PACKET_CODEC = PacketCodecs.STRING.xmap(StaffPatterns::get, StaffPattern::getName);
    private static final Map<String, StaffPattern> PATTERNS = new Object2ObjectOpenHashMap<>();

    public static void registerAll() {
        register(StaffPattern.EMPTY);
        register(StaffStyles.STYLES);
        register(StaffMetals.METALS);
        register(StaffColors.COLORS);
        register(StaffMaterial.MATERIALS);
        register(StaffLenses.LENSES);
    }

    public static StaffPattern get(String name) {
        StaffPattern result = PATTERNS.get(name);
        if (result != null) return result;

        Etherology.ELOGGER.error("Failed to load '{}' staff pattern", name);
        return StaffPattern.EMPTY;
    }

    private static void register(Supplier<List<? extends StaffPattern>> patternsSupplier) {
        patternsSupplier.get().forEach(StaffPatterns::register);
    }

    private static void register(StaffPattern pattern) {
        if (PATTERNS.containsKey(pattern.getName())) throw new IllegalArgumentException("Duplicate staff pattern name: " + pattern.getName());
        PATTERNS.put(pattern.getName(), pattern);
    }
}
