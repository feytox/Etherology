package ru.feytox.etherology.mixin;

import com.bawnorton.mixinsquared.api.MixinCanceller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class EMixinCanceller implements MixinCanceller {

    private static final Logger LOGGER = LoggerFactory.getLogger("Etherology");

    // TODO: 22.07.2024 fix incompatibility

    @Override
    public boolean shouldCancel(List<String> targetClassNames, String mixinClassName) {
        if (!mixinClassName.startsWith("org.embeddedt.modernfix.common.mixin.perf.reduce_blockstate_cache_rebuilds")) return false;
        LOGGER.warn("mixin.perf.reduce_blockstate_cache_rebuilds optimization from ModernFix has been cancelled because it crashes on world load with Etherology.");
        return true;
    }
}
