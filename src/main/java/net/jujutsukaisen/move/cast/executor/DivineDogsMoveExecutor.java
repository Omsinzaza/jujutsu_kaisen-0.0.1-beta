package net.jujutsukaisen.move.cast.executor;

import net.jujutsukaisen.Config;
import net.jujutsukaisen.capability.TechniqueMove;
import net.jujutsukaisen.entity.DivineDogEntity;
import net.jujutsukaisen.move.cast.MoveCastContext;
import net.jujutsukaisen.move.cast.MoveCasting;
import net.jujutsukaisen.move.cast.MoveExecutor;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class DivineDogsMoveExecutor implements MoveExecutor {
    @Override
    public void execute(MoveCastContext context) {
        List<DivineDogEntity> existingDogs = context.player().level().getEntitiesOfClass(
                DivineDogEntity.class,
                context.player().getBoundingBox().inflate(Config.getDivineDogRecallRange()),
                dog -> dog.getOwner() != null && dog.getOwner().equals(context.player()) && !dog.isSummoning());

        if (!existingDogs.isEmpty()) {
            for (DivineDogEntity dog : existingDogs) {
                if (context.player().level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, dog.getX(),
                            dog.getY() + 0.5, dog.getZ(), 10, 0.3, 0.3, 0.3, 0.02);
                    serverLevel.sendParticles(ParticleTypes.SOUL, dog.getX(), dog.getY() + 0.5,
                            dog.getZ(), 5, 0.2, 0.1, 0.2, 0.01);
                }
                dog.discard();
            }
            context.player().level().playSound(null, context.player().blockPosition(), SoundEvents.WOLF_WHINE,
                    SoundSource.PLAYERS, 1.0F, 1.0F);
            return;
        }

        if (!MoveCasting.tryConsumeMove(context, TechniqueMove.TEN_SHADOWS_DIVINE_DOGS)) {
            return;
        }

        Vec3 look = context.player().getLookAngle();
        Vec3 side = new Vec3(-look.z, 0, look.x).normalize();
        Vec3 forward = context.player().position().add(look.x * Config.getDivineDogSummonForwardOffset(), 0,
                look.z * Config.getDivineDogSummonForwardOffset());

        DivineDogEntity blackDog = DivineDogEntity.summon(context.player().level(), context.player(), 0);
        Vec3 leftPos = forward.add(side.scale(-Config.getDivineDogSummonSideOffset()));
        blackDog.setPos(leftPos.x, leftPos.y, leftPos.z);
        context.player().level().addFreshEntity(blackDog);

        DivineDogEntity whiteDog = DivineDogEntity.summon(context.player().level(), context.player(), 1);
        Vec3 rightPos = forward.add(side.scale(Config.getDivineDogSummonSideOffset()));
        whiteDog.setPos(rightPos.x, rightPos.y, rightPos.z);
        context.player().level().addFreshEntity(whiteDog);

        context.player().level().playSound(null, context.player().blockPosition(), SoundEvents.WOLF_HOWL,
                SoundSource.PLAYERS, 1.0F, 0.8F);
        if (context.player().level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, leftPos.x, leftPos.y + 0.5,
                    leftPos.z, 10, 0.3, 0.3, 0.3, 0.02);
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, rightPos.x, rightPos.y + 0.5,
                    rightPos.z, 10, 0.3, 0.3, 0.3, 0.02);
        }
    }
}
