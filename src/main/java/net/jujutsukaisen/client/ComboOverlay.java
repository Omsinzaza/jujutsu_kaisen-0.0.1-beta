package net.jujutsukaisen.client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class ComboOverlay {
    public static int comboCount = 0;
    public static long lastHitTime = 0;
    private static final long COMBO_TIMEOUT = 1500;

    public static void registerHit() {
        long time = System.currentTimeMillis();

        if (time - lastHitTime > COMBO_TIMEOUT) {
            comboCount = 1;
            lastHitTime = time;
        }

        else if (time - lastHitTime > 20) {
            comboCount++;
            lastHitTime = time;
        }
    }

    public static final IGuiOverlay HUD_OVERLAY = (gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.player == null) return;

        long time = System.currentTimeMillis();
        long timeSinceHit = time - lastHitTime;

        if (timeSinceHit > COMBO_TIMEOUT) {
            comboCount = 0;
            return;
        }

        if (comboCount > 0) {
            String rank;
            String word;
            int color;
            float baseScale;

            if (comboCount < 5) { rank = "D"; word = "Dismal"; color = 0xFFAAAAAA; baseScale = 1.0f; }
            else if (comboCount < 10) { rank = "C"; word = "Crazy"; color = 0xFF00AAFF; baseScale = 1.2f; }
            else if (comboCount < 20) { rank = "B"; word = "Badass"; color = 0xFF55FF55; baseScale = 1.4f; }
            else if (comboCount < 35) { rank = "A"; word = "Apocalyptic"; color = 0xFFFFFF55; baseScale = 1.6f; }
            else if (comboCount < 50) { rank = "S"; word = "Savage"; color = 0xFFFFAA00; baseScale = 1.8f; }
            else if (comboCount < 75) { rank = "SS"; word = "Sick Skills"; color = 0xFFFF5555; baseScale = 2.0f; }
            else {
                rank = "SSS";
                word = "Smokin' Sexy Style!!";

                float hue = (time % 2000) / 2000.0f;
                color = Mth.hsvToRgb(hue, 1.0f, 1.0f) | 0xFF000000;
                baseScale = 2.4f;
            }

            float pulse = Math.max(0, 1.0f - (timeSinceHit / 200.0f));
            float scale = baseScale + (pulse * 0.8f);

            float shakeX = 0;
            float shakeY = 0;
            if (comboCount >= 20) {
                float shakeIntensity = Math.min((comboCount - 15) * 0.2f, 8.0f);
                shakeX = (float)((Math.random() - 0.5) * shakeIntensity);
                shakeY = (float)((Math.random() - 0.5) * shakeIntensity);
            }

            int x = screenWidth - 120 + (int)shakeX;
            int y = screenHeight / 4 + (int)shakeY;

            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(x, y, 0);
            guiGraphics.pose().scale(scale, scale, 1.0f);

            guiGraphics.drawString(mc.font, rank, 0, 0, color, true);
            guiGraphics.pose().popPose();

            guiGraphics.pose().pushPose();

            guiGraphics.pose().translate(x - (10 * scale), y + (12 * scale), 0);

            float wordScale = scale * 0.35f;
            guiGraphics.pose().scale(wordScale, wordScale, 1.0f);

            guiGraphics.drawString(mc.font, word, 0, 0, 0xFFFFFFFF, true);
            guiGraphics.drawString(mc.font, comboCount + " Hits!", 0, 12, 0xFFCCCCCC, true);
            guiGraphics.pose().popPose();

            float timeRemaining = 1.0f - ((float)timeSinceHit / COMBO_TIMEOUT);
            int barWidth = 80;
            int fillWidth = (int)(barWidth * timeRemaining);

            int barX = screenWidth - 120 - 10;
            int barY = y + (int)(25 * scale);

            guiGraphics.fill(barX, barY, barX + barWidth, barY + 3, 0x88000000);
            guiGraphics.fill(barX, barY, barX + fillWidth, barY + 3, color);
        }
    };
}
