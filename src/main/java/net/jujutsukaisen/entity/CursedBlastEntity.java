package net.jujutsukaisen.entity;

import net.jujutsukaisen.Config;
import net.jujutsukaisen.init.EntityInit;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class CursedBlastEntity extends ThrowableProjectile {

    public CursedBlastEntity(EntityType<? extends ThrowableProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public CursedBlastEntity(Level pLevel, LivingEntity pShooter) {
        super(EntityInit.CURSED_BLAST.get(), pShooter, pLevel);
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            this.level().addParticle(ParticleTypes.ENCHANT, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        if (!this.level().isClientSide) {
            pResult.getEntity().hurt(this.damageSources().magic(), Config.getCursedBlastDamage());
        }
    }

    @Override
    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte) 3);
            this.discard();
        }
    }
}
