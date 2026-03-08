package net.jujutsukaisen.event;

import net.jujutsukaisen.Config;
import net.jujutsukaisen.JujutsuMod;
import net.jujutsukaisen.capability.CursedEnergy;
import net.jujutsukaisen.capability.CursedEnergyProvider;
import net.jujutsukaisen.network.JujutsuNetwork;
import net.jujutsukaisen.network.packet.SyncCursedEnergyS2CPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = JujutsuMod.MODID)
public class ModEvents {

    public static final TagKey<Item> BYPASSES_INFINITY = ItemTags.create(
            ResourceLocation.fromNamespaceAndPath("jujutsukaisen", "bypasses_infinity"));

    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            if (!event.getObject().getCapability(CursedEnergyProvider.CURSED_ENERGY).isPresent()) {
                event.addCapability(ResourceLocation.fromNamespaceAndPath(JujutsuMod.MODID, "cursed_energy"),
                        new CursedEnergyProvider());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            event.getOriginal().getCapability(CursedEnergyProvider.CURSED_ENERGY).ifPresent(oldStore -> {
                event.getEntity().getCapability(CursedEnergyProvider.CURSED_ENERGY).ifPresent(newStore -> {
                    newStore.copyFrom(oldStore);
                });
            });
        }
    }

    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (player.getPersistentData().getBoolean("jujutsukaisen_negate_fall")) {
                event.setDistance(0.0f);
                event.setDamageMultiplier(0.0f);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (event.getSource().is(net.minecraft.world.damagesource.DamageTypes.FALL)) {
                if (player.getPersistentData().getBoolean("jujutsukaisen_negate_fall")) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side == net.minecraftforge.fml.LogicalSide.SERVER && event.phase == TickEvent.Phase.END) {
            ServerPlayer player = (ServerPlayer) event.player;

            boolean isUsingPurple = player.getPersistentData().hasUUID("jujutsukaisen_active_hollow_purple");
            int sustainTicks = player.getPersistentData().getInt("jujutsukaisen_purple_sustain");

            if (isUsingPurple || sustainTicks > 0) {
                player.fallDistance = 0;

                double liftSpeed;
                if (isUsingPurple) {
                    player.getPersistentData().putInt("jujutsukaisen_purple_sustain", 60);
                    liftSpeed = player.onGround() ? 0.25D : 0.08D;
                } else {
                    liftSpeed = 0.02D;
                    player.getPersistentData().putInt("jujutsukaisen_purple_sustain", sustainTicks - 1);
                }

                player.setDeltaMovement(player.getDeltaMovement().x, liftSpeed, player.getDeltaMovement().z);
                player.hurtMarked = true;

                player.getPersistentData().putBoolean("jujutsukaisen_negate_fall", true);
            } else {
                if (player.onGround() || player.isInWater()) {
                    if (player.getPersistentData().getBoolean("jujutsukaisen_negate_fall")) {
                        int landingTicks = player.getPersistentData().getInt("jujutsukaisen_landing_grace");
                        if (landingTicks >= 10) {
                            player.getPersistentData().remove("jujutsukaisen_negate_fall");
                            player.getPersistentData().remove("jujutsukaisen_landing_grace");
                        } else {
                            player.getPersistentData().putInt("jujutsukaisen_landing_grace", landingTicks + 1);
                        }
                    }
                }
            }

            player.getCapability(CursedEnergyProvider.CURSED_ENERGY).ifPresent(energy -> {
                boolean shouldSync = energy.tickCooldowns();

                int regenInterval = Math.max(1, Config.getCeRegenIntervalTicks());
                if (player.tickCount % regenInterval == 0) {
                    if (!energy.isInfinityActive() && energy.getEnergy() < energy.getMaxEnergy()) {
                        energy.addEnergy(Config.getCeRegenAmount());
                        shouldSync = true;
                    }
                }

                if (energy.isInfinityActive()) {
                    shouldSync |= handleInfinity(player, energy);
                }

                if (shouldSync) {
                    syncCE(player, energy);
                }
            });
        }
    }

    private static boolean handleInfinity(ServerPlayer player, CursedEnergy energy) {
        boolean changed = false;
        double radius = Config.getInfinityRadius();

        int drainInterval = Math.max(1, Config.getInfinityDrainIntervalTicks());
        if (player.tickCount % drainInterval == 0) {
            int drainAmount = Config.getInfinityDrainAmount();
            if (energy.getEnergy() >= drainAmount) {
                energy.consumeEnergy(drainAmount);
                changed = true;
            } else {
                energy.setInfinityActive(false);
                return true;
            }
        }

        net.minecraft.world.phys.AABB bb = player.getBoundingBox().inflate(radius);
        player.level().getEntitiesOfClass(net.minecraft.world.entity.projectile.Projectile.class, bb, p -> p.getOwner() != player)
                .forEach(p -> {
                    p.setDeltaMovement(p.getDeltaMovement().scale(Config.getInfinityProjectileSlowFactor()));
                    p.setNoGravity(true);
                });

        player.level().getEntitiesOfClass(net.minecraft.world.entity.LivingEntity.class, bb, e -> e != player)
                .forEach(e -> {
                    if (!e.getMainHandItem().is(BYPASSES_INFINITY)) {
                        e.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                                net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN,
                                Config.getInfinitySlownessDurationTicks(),
                                Config.getInfinitySlownessAmplifier(),
                                false, false));
                        if (e.distanceTo(player) < Config.getInfinityRepelRadius()) {
                            e.setDeltaMovement(e.position().subtract(player.position()).normalize()
                                    .scale(Config.getInfinityRepelStrength()));
                        }
                    }
                });

        return changed;
    }

    private static void syncCE(ServerPlayer player, CursedEnergy energy) {
        JujutsuNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                new SyncCursedEnergyS2CPacket(energy.getEnergy(), energy.getMaxEnergy(),
                        energy.isInfinityActive(), energy.getTechnique(),
                        energy.getSelectedMoveIndex(), energy.createCooldownArray()));
    }

    @Mod.EventBusSubscriber(modid = JujutsuMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEventBusEvents {
        @SubscribeEvent
        public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
            event.register(net.jujutsukaisen.capability.CursedEnergy.class);
        }

        @SubscribeEvent
        public static void onEntityAttributeCreation(net.minecraftforge.event.entity.EntityAttributeCreationEvent event) {
            event.put(net.jujutsukaisen.init.EntityInit.DIVINE_DOG.get(),
                    net.jujutsukaisen.entity.DivineDogEntity.createAttributes().build());
        }
    }

    @SubscribeEvent
    public static void onRegisterCommands(net.minecraftforge.event.RegisterCommandsEvent event) {
        net.jujutsukaisen.command.SetTechniqueCommand.register(event.getDispatcher());
    }
}
