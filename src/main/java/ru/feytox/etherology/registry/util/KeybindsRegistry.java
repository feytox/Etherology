package ru.feytox.etherology.registry.util;

import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import ru.feytox.etherology.Etherology;
import ru.feytox.etherology.util.feyapi.FeyKeybind;

import java.util.function.Consumer;

@UtilityClass
public class KeybindsRegistry {

    public static final FeyKeybind OPEN_LENSE_MENU = register("open_lense_menu", GLFW.GLFW_KEY_R);
    public static final FeyKeybind TAKE_LENSE = register("take_lense", GLFW.GLFW_KEY_K);

    public static void registerAll() {
    }

    private static FeyKeybind register(String keyName, int defaultKey) {
        FeyKeybind keybind = new FeyKeybind(
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
