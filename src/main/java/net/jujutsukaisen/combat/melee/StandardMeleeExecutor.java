package net.jujutsukaisen.combat.melee;

import net.jujutsukaisen.Config;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class StandardMeleeExecutor implements MeleeExecutor {

    @Override
    public void execute(MeleeContext context) {
        long currentTime = System.currentTimeMillis();

        if (currentTime - context.energy().getLastMeleeTime() > Config.getMeleeComboResetMs()) {
            context.energy().setComboCount(0);
        }

        if (currentTime - context.energy().getLastMeleeTime() < Config.getMeleeSwingCooldownMs()) {
            return;
        }

        context.energy().setComboCount(context.energy().getComboCount() + 1);
        context.energy().setLastMeleeTime(currentTime);

        boolean isComboFinisher = context.energy().getComboCount() >= Config.getMeleeFinisherHits();

        Vec3 look = context.player().getLookAngle();
        Vec3 attackPos = context.player().getEyePosition().add(look.scale(Config.getMeleeRange()));
        double range = Config.getMeleeRange();
        AABB hitbox = new AABB(
                attackPos.x - range,
                attackPos.y - range,
                attackPos.z - range,
                attackPos.x + range,
                attackPos.y + range,
                attackPos.z + range
        );

        List<LivingEntity> targets = context.player().level().getEntitiesOfClass(
                LivingEntity.class,
                hitbox,
                entity -> entity != context.player() && entity.isAttackable()
        );

        if (!(context.player().level() instanceof ServerLevel level)) {
            return;
        }

        if (targets.isEmpty()) {
            level.playSound(
                    null,
                    context.player().blockPosition(),
                    SoundEvents.PLAYER_ATTACK_SWEEP,
                    SoundSource.PLAYERS,
                    0.5F,
                    1.5F
            );
        } else {
            for (LivingEntity target : targets) {
                float damage = isComboFinisher
                        ? Config.getMeleeFinisherDamage()
                        : Config.getMeleeNormalDamage();
                target.hurt(level.damageSources().playerAttack(context.player()), damage);

                if (isComboFinisher) {
                    target.knockback(Config.getMeleeFinisherKnockback(), -look.x, -look.z);
                    level.sendParticles(
                            ParticleTypes.EXPLOSION,
                            target.getX(),
                            target.getY() + 1,
                            target.getZ(),
                            1,
                            0,
                            0,
                            0,
                            0
                    );
                    level.playSound(
                            null,
                            context.player().blockPosition(),
                            SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR,
                            SoundSource.PLAYERS,
                            0.5F,
                            1.2F
                    );
                    context.energy().setComboCount(0);
                } else {
                    target.knockback(Config.getMeleeNormalKnockback(), -look.x, -look.z);
                    level.sendParticles(
                            ParticleTypes.CRIT,
                            target.getX(),
                            target.getY() + 1,
                            target.getZ(),
                            5,
                            0.2,
                            0.2,
                            0.2,
                            0.1
                    );
                    level.playSound(
                            null,
                            context.player().blockPosition(),
                            SoundEvents.PLAYER_ATTACK_CRIT,
                            SoundSource.PLAYERS,
                            0.5F,
                            1.0F
                    );
                }
            }
        }

        context.player().swing(InteractionHand.MAIN_HAND, true);
    }
}
