package name.uwu.feytox.etherology.blocks.ringMatrix;

import com.google.gson.JsonObject;
import name.uwu.feytox.etherology.enums.RingType;
import name.uwu.feytox.etherology.util.RingIdentifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.JsonHelper;
import software.bernie.geckolib.GeckoLibException;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.loading.json.FormatVersion;
import software.bernie.geckolib.loading.json.raw.Model;
import software.bernie.geckolib.loading.object.BakedModelFactory;
import software.bernie.geckolib.loading.object.GeometryTree;
import software.bernie.geckolib.util.JsonUtil;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

import static software.bernie.geckolib.loading.FileLoader.getFileContents;

public class RingsModelProvider {
    public static final RingsModelProvider INSTANCE = new RingsModelProvider();

    Map<RingIdentifier, BakedGeoModel> cachedModels = new HashMap<>();

    private RingsModelProvider() {
    }

    @Nullable
    public BakedGeoModel generateModel(RingIdentifier location) {
        // TODO: clear after reload
        if (cachedModels.containsKey(location)) return cachedModels.get(location);

        try {
            ResourceManager resourceManager = MinecraftClient.getInstance().getResourceManager();
            String jsonString = getFileContents(location, resourceManager);

            if (location.getRingsNum() != 0) {
                for (int i = 1; i <= location.getRingsNum(); i++) {
                    RingType ringType = location.getRingsTypes().get(i - 1);
                    jsonString = jsonString.replace("-" + i + "1, 0, 0", "64, 64, 9")
                            .replace("-" + i + "2, 0, 0", "56, 56, 9")
                            .replace(i + "110", String.valueOf(ringType.getU1()))
                            .replace(i + "120", String.valueOf(ringType.getV1()))
                            .replace(i + "210", String.valueOf(ringType.getU2()))
                            .replace(i + "220", String.valueOf(ringType.getV2()));
                }
            }

            Model model = JsonUtil.GEO_GSON.fromJson(JsonHelper.deserialize(JsonUtil.GEO_GSON, jsonString, JsonObject.class), Model.class);

            if (model.formatVersion() != FormatVersion.V_1_12_0)
                throw new GeckoLibException(location, "Unsupported geometry json version. Supported versions: 1.12.0");

            return BakedModelFactory.getForNamespace(location.getNamespace()).constructGeoModel(GeometryTree.fromModel(model));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
