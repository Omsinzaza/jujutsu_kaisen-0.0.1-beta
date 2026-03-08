package net.jujutsukaisen.client;

import net.jujutsukaisen.capability.CursedEnergyProvider;
import net.jujutsukaisen.capability.TechniqueMove;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class CursedEnergyOverlay {
    private static final ResourceLocation VIGNETTE_LOCATION = ResourceLocation.parse(
            "textures/misc/vignette.png");
    private static final ResourceLocation HOTBAR_LOCATION = ResourceLocation.parse(
            "jujutsukaisen:textures/gui/vertical_technique_bar.png");
    private static final ResourceLocation CE_BAR_BG = ResourceLocation.parse(
            "jujutsukaisen:textures/gui/ce_bar_background.png");
    private static final ResourceLocation SELECTOR_BOX = ResourceLocation.parse(
            "jujutsukaisen:textures/gui/icons/icon_selection_box.png");

    private static boolean wasInfinityActive = false;
    private static int shatterStartTick = -100;
    private static net.minecraft.client.resources.sounds.SimpleSoundInstance warningSoundInstance = null;

    public static final IGuiOverlay HUD_OVERLAY = (gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.hideGui || minecraft.player == null) {
            return;
        }

        minecraft.player.getCapability(CursedEnergyProvider.CURSED_ENERGY).ifPresent(energy -> {
            boolean active = energy.isInfinityActive();
            int ce = energy.getEnergy();
            int maxCe = energy.getMaxEnergy();

            if (wasInfinityActive && !active && ce == 0) {
                shatterStartTick = minecraft.player.tickCount;
                if (warningSoundInstance != null) {
                    minecraft.getSoundManager().stop(warningSoundInstance);
                    warningSoundInstance = null;
                }
                minecraft.getSoundManager().play(net.minecraft.client.resources.sounds.SimpleSoundInstance
                        .forUI(net.minecraft.sounds.SoundEvents.GLASS_BREAK, 1.0F, 1.0F));

                minecraft.getSoundManager().play(net.minecraft.client.resources.sounds.SimpleSoundInstance
                        .forUI(net.minecraft.sounds.SoundEvents.GENERIC_EXPLODE, 1.0F, 0.5F));
            }
            wasInfinityActive = active;

            int shatterTicksElapsed = minecraft.player.tickCount - shatterStartTick;
            boolean isShattering = shatterTicksElapsed >= 0 && shatterTicksElapsed <= 4;

            if (active && ce <= maxCe * 0.2F) {
                if (warningSoundInstance == null || !minecraft.getSoundManager().isActive(warningSoundInstance)) {
                    warningSoundInstance = net.minecraft.client.resources.sounds.SimpleSoundInstance
                            .forUI(net.minecraft.sounds.SoundEvents.PORTAL_AMBIENT, 0.5F, 0.15F);
                    minecraft.getSoundManager().play(warningSoundInstance);
                }
            } else if (warningSoundInstance != null) {
                minecraft.getSoundManager().stop(warningSoundInstance);
                warningSoundInstance = null;
            }

            if (active || isShattering) {
                com.mojang.blaze3d.systems.RenderSystem.enableBlend();
                com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();

                float r = 0.0F;
                float g = 0.0F;
                float b = 0.0F;
                float a = 1.0F;

                if (isShattering) {
                    r = 0.8F;
                    g = 0.9F;
                    b = 1.0F;
                    a = 0.4F;
                } else if (ce <= maxCe * 0.2F) {
                    float cycle = ((minecraft.player.tickCount + partialTick) % 40) / 40.0f;
                    float breathe = (float) (Math.sin(cycle * Math.PI * 2) * 0.5 + 0.5);
                    r = 0.8F;
                    g = 0.4F;
                    b = 1.0F;
                    a = 0.15F * breathe;
                } else {
                    r = 0.2F;
                    g = 0.8F;
                    b = 1.0F;
                    a = 0.4F;
                }

                guiGraphics.setColor(r, g, b, a);
                guiGraphics.blit(VIGNETTE_LOCATION, 0, 0, -90, 0.0F, 0.0F, screenWidth, screenHeight, screenWidth,
                        screenHeight);
                guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

                com.mojang.blaze3d.systems.RenderSystem.disableBlend();
            }

            java.util.List<TechniqueMove> moves = TechniqueMove.getMovesForTechnique(energy.getTechnique());
            int maxSlots = Math.min(moves.size(), 6);
            if (maxSlots <= 0) {
                return;
            }

            Font font = minecraft.font;
            int hotbarX = 0;
            int hotbarY = screenHeight - 149;
            int barWidth = 8;
            int barHeight = 149;
            int barX = hotbarX + 32;
            int barY = hotbarY;

            com.mojang.blaze3d.systems.RenderSystem.enableBlend();
            com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();

            guiGraphics.blit(HOTBAR_LOCATION, hotbarX, hotbarY, 0, 0.0F, 0.0F, 32, 149, 32, 149);

            for (int i = 0; i < maxSlots; i++) {
                TechniqueMove move = moves.get(i);
                int iconX = hotbarX + 6;
                int iconY = hotbarY + 6 + (i * 23);
                ResourceLocation icon = move.getIcon();

                if (icon != null) {
                    guiGraphics.blit(icon, iconX, iconY, 0, 0.0F, 0.0F, 20, 20, 20, 20);
                } else {
                    guiGraphics.fill(iconX, iconY, iconX + 20, iconY + 20, 0xAA111111);
                    int shortWidth = font.width(move.getShortLabel());
                    guiGraphics.drawString(font, move.getShortLabel(),
                            iconX + (20 - shortWidth) / 2, iconY + 6, 0xFFFFFFFF, false);
                }

                int remainingCooldown = energy.getCooldownTicks(move);
                if (remainingCooldown > 0) {
                    guiGraphics.fill(iconX, iconY, iconX + 20, iconY + 20, 0xAA000000);
                    String cdText = formatCompactCooldown(remainingCooldown);
                    int textWidth = font.width(cdText);
                    guiGraphics.drawString(font, cdText,
                            iconX + (20 - textWidth) / 2, iconY + 6, 0xFFFFAA00, true);
                }
            }

            int selectedIndex = energy.getSelectedMoveIndex();
            if (selectedIndex >= 0 && selectedIndex < maxSlots) {
                int boxX = hotbarX + 5;
                int boxY = hotbarY + 5 + (selectedIndex * 23);
                guiGraphics.blit(SELECTOR_BOX, boxX, boxY, 0, 0.0F, 0.0F, 22, 22, 22, 22);

                TechniqueMove selectedMove = moves.get(selectedIndex);
                int infoX = barX + 14;
                int infoY = hotbarY + 6;
                int remainingCooldown = energy.getCooldownTicks(selectedMove);
                int detailColor = remainingCooldown > 0 ? 0xFFFFAA00 : 0xFFFFFFFF;

                guiGraphics.drawString(font, selectedMove.getDisplayName(), infoX, infoY, detailColor, true);
                guiGraphics.drawString(font, "CE: " + selectedMove.getEnergyCost(), infoX, infoY + 12,
                        0xFF7FE3FF, false);
                guiGraphics.drawString(font,
                        remainingCooldown > 0 ? "CD: " + formatCooldownSeconds(remainingCooldown) : "CD: Ready",
                        infoX, infoY + 24, remainingCooldown > 0 ? 0xFFFFAA00 : 0xFF7FFF7F, false);
            }

            guiGraphics.blit(CE_BAR_BG, barX, barY, 0, 0.0F, 0.0F, barWidth, barHeight, barWidth, barHeight);

            float cePercent = maxCe > 0 ? (float) ce / maxCe : 0.0F;
            int innerMargin = 1;
            int fillMaxHeight = barHeight - (innerMargin * 2);
            int fillHeight = (int) (fillMaxHeight * cePercent);
            int fillX = barX + innerMargin;
            int fillY = barY + innerMargin + (fillMaxHeight - fillHeight);
            int fillWidth = barWidth - (innerMargin * 2);

            if (fillHeight > 0) {
                guiGraphics.fill(fillX, fillY, fillX + fillWidth, fillY + fillHeight, 0xDD00BFFF);
            }

            com.mojang.blaze3d.systems.RenderSystem.disableBlend();
        });
    };

    private static String formatCompactCooldown(int ticks) {
        if (ticks >= 20) {
            return Integer.toString((int) Math.ceil(ticks / 20.0D));
        }
        return Integer.toString(Math.max(1, (int) Math.ceil(ticks / 5.0D)));
    }

    private static String formatCooldownSeconds(int ticks) {
        return String.format(java.util.Locale.ROOT, "%.1fs", ticks / 20.0F);
    }
}
