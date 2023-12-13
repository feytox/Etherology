package ru.feytox.etherology.mixin;

import com.chocohead.mm.api.ClassTinkerers;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;

public class EarlyRisers implements Runnable {
    @Override
    public void run() {
        MappingResolver remapper = FabricLoader.getInstance().getMappingResolver();

        String enchantmentTarget = remapper.mapClassName("intermediary", "net.minecraft.class_1886");
        ClassTinkerers.enumBuilder(enchantmentTarget).addEnumSubclass("HAMMER", "ru.feytox.etherology.enchantment.target.HammerEnchantmentTarget").build();
        ClassTinkerers.enumBuilder(enchantmentTarget).addEnumSubclass("ETHEROLOGY_SHIELD", "ru.feytox.etherology.enchantment.target.EtherShieldEnchantmentTarget").build();

        String armPose = remapper.mapClassName("intermediary", "net.minecraft.class_572$class_573");
        String useAction = remapper.mapClassName("intermediary", "net.minecraft.class_1839");

//        Arrays.stream(EArmPose.values()).forEach(customArmPose -> {
//            ClassTinkerers.enumBuilder(armPose, boolean.class).addEnum(customArmPose.name(), customArmPose.isTwoHanded()).build();
//            ClassTinkerers.enumBuilder(useAction).addEnum(customArmPose.name()).build();
//        });

        // TODO: 13.12.2023 rename for compatibility

        ClassTinkerers.enumBuilder(armPose, boolean.class).addEnum("OCULUS", true).build();
        ClassTinkerers.enumBuilder(armPose, boolean.class).addEnum("HAMMER", true).build();
        ClassTinkerers.enumBuilder(useAction).addEnum("OCULUS").build();
        ClassTinkerers.enumBuilder(useAction).addEnum("HAMMER").build();
        ClassTinkerers.enumBuilder(useAction).addEnum("STAFF").build();
    }
}
