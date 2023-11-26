package ru.feytox.etherology.block.armillar;

import com.google.common.base.Suppliers;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.Etherology;
import ru.feytox.etherology.enums.RingType;
import ru.feytox.etherology.util.feyapi.RingIdentifier;
import software.bernie.geckolib.GeckoLibException;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.loading.json.FormatVersion;
import software.bernie.geckolib.loading.json.raw.Model;
import software.bernie.geckolib.loading.object.BakedModelFactory;
import software.bernie.geckolib.loading.object.GeometryTree;
import software.bernie.geckolib.util.JsonUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static software.bernie.geckolib.loading.FileLoader.getFileContents;

public class RingsModelProvider {
    public static final RingsModelProvider INSTANCE = new RingsModelProvider();
    private final Supplier<Map<RingIdentifier, BakedGeoModel>> cacheSupplier = Suppliers.memoizeWithExpiration(Object2ObjectOpenHashMap::new, 1, TimeUnit.MINUTES);

    @Nullable
    public BakedGeoModel generateModel(RingIdentifier location) {
        Map<RingIdentifier, BakedGeoModel> cache = cacheSupplier.get();
        if (cache.containsKey(location)) return cache.get(location);

        try {
            ResourceManager resourceManager = MinecraftClient.getInstance().getResourceManager();
            String jsonString = getFileContents(location, resourceManager);
            List<RingType> ringTypes = location.getRingsTypes();

            if (!ringTypes.isEmpty()) {
                for (int i = 0; i < ringTypes.size(); i++) {
                    RingType ringType = ringTypes.get(i);
                    int j = i + 1;
                    jsonString = jsonString.replace("-" + j + "1, 0, 0", "64, 64, 9")
                            .replace("-" + j + "2, 0, 0", "56, 56, 9")
                            .replace(j + "110", String.valueOf(ringType.getU1()))
                            .replace(j + "120", String.valueOf(ringType.getV1()))
                            .replace(j + "210", String.valueOf(ringType.getU2()))
                            .replace(j + "220", String.valueOf(ringType.getV2()));
                }
            }

            Model model = JsonUtil.GEO_GSON.fromJson(JsonHelper.deserialize(JsonUtil.GEO_GSON, jsonString, JsonObject.class), Model.class);

            if (model.formatVersion() != FormatVersion.V_1_12_0)
                throw new GeckoLibException(location, "Unsupported geometry json version. Supported versions: 1.12.0");

            BakedGeoModel result = BakedModelFactory.getForNamespace(location.getNamespace()).constructGeoModel(GeometryTree.fromModel(model));
            cache.put(location, result);
            return result;
        } catch (Exception e) {
            Etherology.ELOGGER.error(e.getMessage());
        }
        return null;
    }
}
