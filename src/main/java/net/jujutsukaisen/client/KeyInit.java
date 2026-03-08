package net.jujutsukaisen.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class KeyInit {
        public static final String CATEGORY = "key.categories.jujutsukaisen";

        public static final KeyMapping SWITCH_TECHNIQUE_KEY = new KeyMapping(
                        "key.jujutsukaisen.switch_technique",
                        KeyConflictContext.IN_GAME,
                        InputConstants.Type.KEYSYM,
                        GLFW.GLFW_KEY_Z,
                        CATEGORY);

        public static final KeyMapping CAST_TECHNIQUE_KEY = new KeyMapping(
                        "key.jujutsukaisen.cast_technique",
                        KeyConflictContext.IN_GAME,
                        InputConstants.Type.KEYSYM,
                        GLFW.GLFW_KEY_R,
                        CATEGORY);

        public static final KeyMapping DASH_KEY = new KeyMapping(
                        "key.jujutsukaisen.dash",
                        KeyConflictContext.IN_GAME,
                        InputConstants.Type.KEYSYM,
                        GLFW.GLFW_KEY_X,
                        CATEGORY);
}