package ru.feytox.etherology.mixin;

import com.chocohead.mm.api.ClassTinkerers;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import ru.feytox.etherology.registry.block.ExtraBlocksRegistry;

public class EarlyRisers implements Runnable {
    @Override
    public void run() {
        MappingResolver remapper = FabricLoader.getInstance().getMappingResolver();

        String boatType = remapper.mapClassName("intermediary", "net.minecraft.class_1690$class_1692");
        String block = "L" + remapper.mapClassName("intermediary", "net.minecraft.class_2248") + ";";
        ClassTinkerers.enumBuilder(boatType,block, "Ljava/lang/String;").addEnum("ETHEROLOGY_PEACH", () -> new Object[]{ExtraBlocksRegistry.PEACH_PLANKS, "etherology_peach"}).build();

        String armPose = remapper.mapClassName("intermediary", "net.minecraft.class_572$class_573");
        String useAction = remapper.mapClassName("intermediary", "net.minecraft.class_1839");

        addArmPose(armPose, useAction, "OCULUS_ETHEROLOGY", true);
        addArmPose(armPose, useAction, "STAFF_ETHEROLOGY", false);
        addArmPose(armPose, useAction, "TWOHANDHELD_ETHEROLOGY", true);
    }

    private void addArmPose(String armPath, String actionPath, String name, boolean twohanded) {
        ClassTinkerers.enumBuilder(armPath, boolean.class).addEnum(name, twohanded).build();
        ClassTinkerers.enumBuilder(actionPath).addEnum(name).build();
    }
}
