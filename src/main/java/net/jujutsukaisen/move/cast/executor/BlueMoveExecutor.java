package net.jujutsukaisen.move.cast.executor;

import net.jujutsukaisen.Config;
import net.jujutsukaisen.capability.TechniqueMove;
import net.jujutsukaisen.entity.BlueEntity;
import net.jujutsukaisen.move.cast.MoveCastContext;
import net.jujutsukaisen.move.cast.MoveCasting;
import net.jujutsukaisen.move.cast.MoveExecutor;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

public class BlueMoveExecutor implements MoveExecutor {
    @Override
    public void execute(MoveCastContext context) {
        BlueEntity activeBlue = MoveCasting.getTrackedEntity(
                context.player(), MoveCasting.ACTIVE_BLUE_KEY, BlueEntity.class);

        if (activeBlue != null && !activeBlue.isLaunched()) {
            activeBlue.launch(context.player().getLookAngle());
            context.player().getPersistentData().remove(MoveCasting.ACTIVE_BLUE_KEY);
            context.player().level().playSound(null, context.player().blockPosition(), SoundEvents.EVOKER_CAST_SPELL,
                    SoundSource.PLAYERS, 1.0F, 1.5F);
            return;
        }

        if (!MoveCasting.tryConsumeMove(context, TechniqueMove.LIMITLESS_BLUE)) {
            return;
        }

        BlueEntity blue = new BlueEntity(context.player().level(), context.player());
        Vec3 look = context.player().getLookAngle();
        Vec3 targetPos = context.player().getEyePosition().add(look.scale(Config.getBlueHoldDistance()));
        blue.setPos(targetPos.x, targetPos.y, targetPos.z);

        context.player().level().addFreshEntity(blue);
        context.player().getPersistentData().putUUID(MoveCasting.ACTIVE_BLUE_KEY, blue.getUUID());
        context.player().level().playSound(null, context.player().blockPosition(),
                SoundEvents.ILLUSIONER_PREPARE_BLINDNESS, SoundSource.PLAYERS, 1.0F, 0.5F);
    }
}
