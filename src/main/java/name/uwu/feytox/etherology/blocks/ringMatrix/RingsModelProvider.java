package name.uwu.feytox.etherology.blocks.ringMatrix;

import name.uwu.feytox.etherology.enums.RingType;
import name.uwu.feytox.etherology.util.RingIdentifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;
import software.bernie.geckolib3.file.AnimationFileLoader;
import software.bernie.geckolib3.geo.exception.GeckoLibException;
import software.bernie.geckolib3.geo.raw.pojo.Converter;
import software.bernie.geckolib3.geo.raw.pojo.FormatVersion;
import software.bernie.geckolib3.geo.raw.pojo.RawGeoModel;
import software.bernie.geckolib3.geo.raw.tree.RawGeometryTree;
import software.bernie.geckolib3.geo.render.GeoBuilder;
import software.bernie.geckolib3.geo.render.built.GeoModel;

import java.util.HashMap;
import java.util.Map;

public class RingsModelProvider {
    public static final RingsModelProvider INSTANCE = new RingsModelProvider();

    Map<RingIdentifier, GeoModel> cachedModels = new HashMap<>();

    private RingsModelProvider() {
    }

    public GeoModel generateModel(RingIdentifier location) {
        try {
            // TODO: clear cache after reload
            if (cachedModels.containsKey(location)) return cachedModels.get(location);

            ResourceManager resourceManager = MinecraftClient.getInstance().getResourceManager();

            String jsonString = AnimationFileLoader.getResourceAsString(location, resourceManager);
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

            RawGeoModel rawModel = Converter
                    .fromJsonString(jsonString);
            if (rawModel.getFormatVersion() != FormatVersion.VERSION_1_12_0) {
                throw new GeckoLibException(location, "Wrong geometry json version, expected 1.12.0");
            }

            // Parse the flat list of bones into a raw hierarchical tree of "BoneGroup"s
            RawGeometryTree rawGeometryTree = RawGeometryTree.parseHierarchy(rawModel);

            // Build the quads and cubes from the raw tree into a built and ready to be
            // rendered GeoModel
            GeoModel geoModel = GeoBuilder.getGeoBuilder(location.getNamespace()).constructGeoModel(rawGeometryTree);
            cachedModels.put(location, geoModel);
            return geoModel;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
