package ru.feytox.etherology.registry.misc;

import lombok.experimental.UtilityClass;
import lombok.val;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import ru.feytox.etherology.Etherology;
import ru.feytox.etherology.network.interaction.StaffTakeLensC2S;

import java.util.function.Consumer;

@UtilityClass
public class KeybindsRegistry {

    public static final KeyBinding STAFF_INTERACTION = register("staff_interaction", GLFW.GLFW_KEY_R);

    public static void registerAll() {
        registerHandler(client -> {
            while (client.currentScreen == null && isPressed(client.options.sneakKey) && STAFF_INTERACTION.wasPressed()) {
                val packet = new StaffTakeLensC2S();
                packet.sendToServer();
            }
        });
    }

    private static KeyBinding register(String keyName, int defaultKey) {
        KeyBinding keybind = new KeyBinding(
                "key." + Etherology.MOD_ID + "." + keyName,
                InputUtil.Type.KEYSYM,
                defaultKey,
                "category." + Etherology.MOD_ID + ".main"
        );
        KeyBindingHelper.registerKeyBinding(keybind);
        return keybind;
    }

    private static void registerHandler(Consumer<MinecraftClient> clientConsumer) {
        ClientTickEvents.END_CLIENT_TICK.register(clientConsumer::accept);
    }

    public static boolean isPressed(KeyBinding keyBinding) {
        return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), keyBinding.boundKey.getCode());
    }
}
