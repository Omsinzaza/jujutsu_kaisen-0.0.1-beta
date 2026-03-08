package net.jujutsukaisen.move.cast.executor;

import net.jujutsukaisen.capability.TechniqueMove;
import net.jujutsukaisen.entity.HollowPurpleEntity;
import net.jujutsukaisen.init.SoundInit;
import net.jujutsukaisen.move.cast.MoveCastContext;
import net.jujutsukaisen.move.cast.MoveCasting;
import net.jujutsukaisen.move.cast.MoveExecutor;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

public class HollowPurpleMoveExecutor implements MoveExecutor {
    @Override
    public void execute(MoveCastContext context) {
        HollowPurpleEntity activePurple = MoveCasting.getTrackedEntity(
                context.player(), MoveCasting.ACTIVE_HOLLOW_PURPLE_KEY, HollowPurpleEntity.class);

        if (activePurple != null) {
            if (activePurple.getEntityState() == HollowPurpleEntity.STATE_READY) {
                activePurple.startShootSequence();
                context.player().level().playSound(null, context.player().blockPosition(),
                        SoundInit.HOLLOW_PURPLE_SHOOT.get(), SoundSource.PLAYERS, 4.0F, 1.0F);
                context.player().getPersistentData().remove(MoveCasting.ACTIVE_HOLLOW_PURPLE_KEY);
            }
            return;
        }

        if (!MoveCasting.tryConsumeMove(context, TechniqueMove.LIMITLESS_HOLLOW_PURPLE)) {
            return;
        }

        HollowPurpleEntity purple = new HollowPurpleEntity(context.player().level(), context.player());
        Vec3 spawnPos = context.player().getEyePosition().add(context.player().getLookAngle().scale(1.5D));
        purple.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
        context.player().level().addFreshEntity(purple);
        context.player().getPersistentData().putUUID(MoveCasting.ACTIVE_HOLLOW_PURPLE_KEY, purple.getUUID());

        context.player().level().playSound(null, context.player().blockPosition(), SoundInit.HOLLOW_PURPLE_START.get(),
                SoundSource.PLAYERS, 4.0F, 1.0F);
    }
}
