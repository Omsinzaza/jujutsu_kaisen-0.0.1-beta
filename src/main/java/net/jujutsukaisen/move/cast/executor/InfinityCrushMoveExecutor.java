package net.jujutsukaisen.move.cast.executor;

import net.jujutsukaisen.Config;
import net.jujutsukaisen.capability.TechniqueMove;
import net.jujutsukaisen.move.cast.MoveCastContext;
import net.jujutsukaisen.move.cast.MoveCasting;
import net.jujutsukaisen.move.cast.MoveExecutor;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class InfinityCrushMoveExecutor implements MoveExecutor {
    @Override
    public void execute(MoveCastContext context) {
        TechniqueMove move = TechniqueMove.LIMITLESS_INFINITY_CRUSH;
        if (!context.energy().isInfinityActive()) {
            context.player().sendSystemMessage(Component.literal("§eInfinity must be active to use Infinity Crush"));
            return;
        }

        if (!MoveCasting.tryConsumeMove(context, move)) {
            return;
        }

        ServerLevel level = context.serverLevel();
        double radius = Config.getInfinityCrushRadius();
        AABB bounds = context.player().getBoundingBox().inflate(radius, radius, radius);

        level.sendParticles(ParticleTypes.SONIC_BOOM, context.player().getX(), context.player().getY() + 1.0,
                context.player().getZ(), 1, 0, 0, 0, 0);
        level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, context.player().getX(), context.player().getY() + 1.0,
                context.player().getZ(), 2, 0, 0, 0, 0);
        level.playSound(null, context.player().blockPosition(), SoundEvents.WARDEN_SONIC_BOOM,
                SoundSource.PLAYERS, 2.0F, 1.0F);

        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, bounds,
                entity -> entity != context.player() && entity.distanceTo(context.player()) <= radius);
        for (LivingEntity entity : entities) {
            entity.hurt(level.damageSources().magic(), Config.getInfinityCrushDamage());
            double d0 = entity.getX() - context.player().getX();
            double d1 = entity.getZ() - context.player().getZ();
            entity.knockback(Config.getInfinityCrushKnockback(), -d0, -d1);
        }
    }
}
