package ru.feytox.etherology.mixin;

import net.minecraft.client.render.model.BakedModelManager;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BakedModelManager.class)
public class BakedModelManagerMixin {

//    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "INVOKE", target = "Ljava/util/Map;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Map;"))
//    private static Map<Identifier, Identifier> injectEtherAtlases(Map<Identifier, Identifier> original) {
//        Map<Identifier, Identifier> result = new Object2ObjectOpenHashMap<>(original);
//        result.put(STAFF_TRIM_TEXTURE, EIdentifier.of("staff_trims"));
//        return result;
//    }
}
