package net.jujutsukaisen.move.cast.executor;

import net.jujutsukaisen.Config;
import net.jujutsukaisen.capability.TechniqueMove;
import net.jujutsukaisen.entity.RedEntity;
import net.jujutsukaisen.move.cast.MoveCastContext;
import net.jujutsukaisen.move.cast.MoveCasting;
import net.jujutsukaisen.move.cast.MoveExecutor;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

public class RedMoveExecutor implements MoveExecutor {
    @Override
    public void execute(MoveCastContext context) {
        RedEntity activeRed = MoveCasting.getTrackedEntity(
                context.player(), MoveCasting.ACTIVE_RED_KEY, RedEntity.class);

        if (activeRed != null && !activeRed.isLaunched()) {
            Vec3 look = context.player().getLookAngle();
            activeRed.launch(look);
            context.player().getPersistentData().remove(MoveCasting.ACTIVE_RED_KEY);

            context.player().level().playSound(null, context.player().blockPosition(), SoundEvents.WITHER_DEATH,
                    SoundSource.PLAYERS, 1.0F, 2.0F);
            context.player().setDeltaMovement(
                    context.player().getDeltaMovement().subtract(look.scale(Config.getRedRecoilStrength())));
            context.player().hurtMarked = true;
            return;
        }

        if (!MoveCasting.tryConsumeMove(context, TechniqueMove.LIMITLESS_RED)) {
            return;
        }

        RedEntity red = new RedEntity(context.player().level(), context.player());
        Vec3 spawnPos = context.player().getEyePosition().add(
                context.player().getLookAngle().scale(Config.getRedHoldDistance()));
        red.setPos(spawnPos.x, spawnPos.y, spawnPos.z);

        context.player().level().addFreshEntity(red);
        context.player().getPersistentData().putUUID(MoveCasting.ACTIVE_RED_KEY, red.getUUID());
        context.player().level().playSound(null, context.player().blockPosition(), SoundEvents.ILLUSIONER_PREPARE_MIRROR,
                SoundSource.PLAYERS, 1.0F, 0.5F);
    }
}
