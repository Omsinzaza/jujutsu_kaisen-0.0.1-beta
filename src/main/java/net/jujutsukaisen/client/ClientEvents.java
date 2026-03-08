package net.jujutsukaisen.client;

import net.jujutsukaisen.JujutsuMod;
import net.jujutsukaisen.init.EntityInit;
import net.jujutsukaisen.network.JujutsuNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class ClientEvents {

    @Mod.EventBusSubscriber(modid = JujutsuMod.MODID, value = Dist.CLIENT)
    public static class ClientForgeEvents {

        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            if (Minecraft.getInstance().player != null) {
                if (KeyInit.SWITCH_TECHNIQUE_KEY.consumeClick()) {
                    JujutsuNetwork.INSTANCE.sendToServer(new net.jujutsukaisen.network.packet.SwitchMoveC2SPacket());
                }
                if (KeyInit.CAST_TECHNIQUE_KEY.consumeClick()) {
                    JujutsuNetwork.INSTANCE.sendToServer(new net.jujutsukaisen.network.packet.CastMoveC2SPacket());
                }
                if (KeyInit.DASH_KEY.consumeClick()) {
                    JujutsuNetwork.INSTANCE.sendToServer(new net.jujutsukaisen.network.packet.DashC2SPacket());
                }
            }
        }

        @SubscribeEvent
        public static void onMouseClick(InputEvent.InteractionKeyMappingTriggered event) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && event.isAttack()) {
                if (mc.player.getMainHandItem().isEmpty()) {
                    if (mc.hitResult != null && mc.hitResult.getType() == HitResult.Type.BLOCK) {
                        return;
                    }

                    ComboOverlay.registerHit();

                    JujutsuNetwork.INSTANCE.sendToServer(new net.jujutsukaisen.network.packet.MeleeComboC2SPacket());
                    mc.player.swing(net.minecraft.world.InteractionHand.MAIN_HAND);

                    event.setCanceled(true);
                    event.setSwingHand(false);
                }
            }
        }
    }

    @Mod.EventBusSubscriber(modid = JujutsuMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModBusEvents {
        @SubscribeEvent
        public static void onRegisterGuiOverlays(RegisterGuiOverlaysEvent event) {
            event.registerAboveAll("cursed_energy", CursedEnergyOverlay.HUD_OVERLAY);
            event.registerAboveAll("combo_tracker", ComboOverlay.HUD_OVERLAY);
        }

        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent event) {
            event.register(KeyInit.SWITCH_TECHNIQUE_KEY);
            event.register(KeyInit.CAST_TECHNIQUE_KEY);
            event.register(KeyInit.DASH_KEY);
        }

        @SubscribeEvent
        public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(EntityInit.CURSED_BLAST.get(), NoopRenderer::new);
            event.registerEntityRenderer(EntityInit.BLUE.get(),
                    net.jujutsukaisen.client.renderer.entity.BlueRenderer::new);
            event.registerEntityRenderer(EntityInit.RED.get(),
                    net.jujutsukaisen.client.renderer.entity.RedRenderer::new);
            event.registerEntityRenderer(EntityInit.HOLLOW_PURPLE.get(),
                    net.jujutsukaisen.client.renderer.entity.HollowPurpleRenderer::new);
            event.registerEntityRenderer(EntityInit.DIVINE_DOG.get(),
                    net.jujutsukaisen.client.renderer.entity.DivineDogRenderer::new);
        }

        @SubscribeEvent
        public static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(
                    net.jujutsukaisen.client.model.DivineDogsModel.LAYER_LOCATION,
                    net.jujutsukaisen.client.model.DivineDogsModel::createBodyLayer);
        }
    }
}