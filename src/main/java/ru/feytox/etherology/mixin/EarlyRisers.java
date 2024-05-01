package ru.feytox.etherology.mixin;

import com.chocohead.mm.api.ClassTinkerers;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;

public class EarlyRisers implements Runnable {
    @Override
    public void run() {
        MappingResolver remapper = FabricLoader.getInstance().getMappingResolver();

        String enchantmentTarget = remapper.mapClassName("intermediary", "net.minecraft.class_1886");
        ClassTinkerers.enumBuilder(enchantmentTarget).addEnumSubclass("ETHEROLOGY_SHIELD", "ru.feytox.etherology.enchantment.target.EtherShieldEnchantmentTarget").build();
        ClassTinkerers.enumBuilder(enchantmentTarget).addEnumSubclass("ETHEROLOGY_TUNING_MACE", "ru.feytox.etherology.enchantment.target.TuningMaceEnchantmentTarget").build();

        String armPose = remapper.mapClassName("intermediary", "net.minecraft.class_572$class_573");
        String useAction = remapper.mapClassName("intermediary", "net.minecraft.class_1839");

//        Arrays.stream(EArmPose.values()).forEach(customArmPose -> {
//            ClassTinkerers.enumBuilder(armPose, boolean.class).addEnum(customArmPose.name(), customArmPose.isTwoHanded()).build();
//            ClassTinkerers.enumBuilder(useAction).addEnum(customArmPose.name()).build();
//        });

        addEArmPose(armPose, useAction, "OCULUS_ETHEROLOGY", true);
        addEArmPose(armPose, useAction, "STAFF_ETHEROLOGY", false);
        addEArmPose(armPose, useAction, "TUNING_MACE_ETHEROLOGY", true);
    }

    private void addEArmPose(String armPath, String actionPath, String name, boolean twohanded) {
        ClassTinkerers.enumBuilder(armPath, boolean.class).addEnum(name, twohanded).build();
        ClassTinkerers.enumBuilder(actionPath).addEnum(name).build();
    }
}
