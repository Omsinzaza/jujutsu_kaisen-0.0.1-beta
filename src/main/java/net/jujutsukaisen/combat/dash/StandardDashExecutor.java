package net.jujutsukaisen.combat.dash;

import net.jujutsukaisen.Config;
import net.jujutsukaisen.combat.CombatSync;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

public class StandardDashExecutor implements DashExecutor {

    @Override
    public void execute(DashContext context) {
        long currentTime = System.currentTimeMillis();

        if (currentTime - context.energy().getLastDashTime() < Config.getDashCooldownMs()) {
            return;
        }

        int dashCost = Config.getDashCost();
        if (context.energy().getEnergy() < dashCost) {
            return;
        }

        context.energy().consumeEnergy(dashCost);
        context.energy().setLastDashTime(currentTime);

        Vec3 look = context.player().getLookAngle();
        context.player().setDeltaMovement(
                look.x * Config.getDashHorizontalStrength(),
                Config.getDashVerticalStrength(),
                look.z * Config.getDashHorizontalStrength()
        );
        context.player().hurtMarked = true;

        if (context.player().level() instanceof ServerLevel level) {
            level.sendParticles(
                    ParticleTypes.POOF,
                    context.player().getX(),
                    context.player().getY(),
                    context.player().getZ(),
                    10,
                    0.5,
                    0.1,
                    0.5,
                    0.05
            );
            level.playSound(
                    null,
                    context.player().blockPosition(),
                    SoundEvents.ENDER_DRAGON_FLAP,
                    SoundSource.PLAYERS,
                    0.5F,
                    1.5F
            );
        }

        CombatSync.syncEnergy(context.player(), context.energy());
    }
}
