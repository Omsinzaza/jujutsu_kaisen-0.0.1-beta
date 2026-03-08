package net.jujutsukaisen.move.cast.executor;

import net.jujutsukaisen.Config;
import net.jujutsukaisen.capability.TechniqueMove;
import net.jujutsukaisen.entity.CursedBlastEntity;
import net.jujutsukaisen.move.cast.MoveCastContext;
import net.jujutsukaisen.move.cast.MoveCasting;
import net.jujutsukaisen.move.cast.MoveExecutor;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class CursedBlastMoveExecutor implements MoveExecutor {
    @Override
    public void execute(MoveCastContext context) {
        if (!MoveCasting.tryConsumeMove(context, TechniqueMove.CURSED_BLAST)) {
            return;
        }

        CursedBlastEntity projectile = new CursedBlastEntity(context.player().level(), context.player());
        projectile.shootFromRotation(context.player(), context.player().getXRot(), context.player().getYRot(), 0.0F,
                Config.getCursedBlastVelocity(), Config.getCursedBlastInaccuracy());
        context.player().level().addFreshEntity(projectile);

        context.player().level().playSound(null, context.player().blockPosition(), SoundEvents.WITHER_SHOOT,
                SoundSource.PLAYERS, 0.5F, 1.0F);
        if (context.player().level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.ENCHANT, context.player().getX(), context.player().getY() + 1.0,
                    context.player().getZ(), 20, 0.5, 0.5, 0.5, 0.1);
        }
    }
}
