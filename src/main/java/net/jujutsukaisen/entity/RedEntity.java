package net.jujutsukaisen.entity;

import net.jujutsukaisen.capability.CursedEnergyProvider;
import net.jujutsukaisen.capability.TechniqueMove;
import net.jujutsukaisen.init.EntityInit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.List;

public class RedEntity extends Projectile {
    private static final EntityDataAccessor<Boolean> LAUNCHED =
            SynchedEntityData.defineId(RedEntity.class, EntityDataSerializers.BOOLEAN);

    private static final String ACTIVE_RED_KEY = "jujutsukaisen_active_red";
    private static final double HOLD_DISTANCE = 2.0D;
    private static final int MAX_FLIGHT_TICKS = 100;
    private static final double IMPACT_RADIUS = 10.0D;

    public RedEntity(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
    }

    public RedEntity(Level level, LivingEntity owner) {
        super(EntityInit.RED.get(), level);
        this.setOwner(owner);
        this.setNoGravity(true);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(LAUNCHED, false);
    }

    public boolean isLaunched() {
        return this.entityData.get(LAUNCHED);
    }

    public void launch(Vec3 direction) {
        this.entityData.set(LAUNCHED, true);
        this.setDeltaMovement(direction.scale(3.5D));
        this.tickCount = 0;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            return;
        }

        Entity owner = this.getOwner();
        if (shouldDiscardBecauseOwnerIsInvalid(owner)) {
            discard();
            return;
        }

        if (!isLaunched()) {
            tickHeldState(owner);
            return;
        }

        tickLaunchedState();
    }

    private boolean shouldDiscardBecauseOwnerIsInvalid(Entity owner) {
        return owner == null || !owner.isAlive() || owner.isRemoved();
    }

    private void tickHeldState(Entity owner) {
        if (!(owner instanceof Player player)) {
            discard();
            return;
        }

        if (!shouldRemainHeld(player)) {
            clearActiveRed(player);
            discard();
            return;
        }

        followOwnerWhileHeld(owner);
    }

    private boolean shouldRemainHeld(Player player) {
        return player.getCapability(CursedEnergyProvider.CURSED_ENERGY)
                .map(energy -> {
                    List<TechniqueMove> moves = TechniqueMove.getMovesForTechnique(energy.getTechnique());
                    int selectedMoveIndex = energy.getSelectedMoveIndex();

                    return selectedMoveIndex >= 0
                            && selectedMoveIndex < moves.size()
                            && moves.get(selectedMoveIndex) == TechniqueMove.LIMITLESS_RED;
                })
                .orElse(false);
    }

    private void followOwnerWhileHeld(Entity owner) {
        Vec3 look = owner.getLookAngle();
        Vec3 targetPos = owner.getEyePosition().add(look.scale(HOLD_DISTANCE));
        this.setPos(targetPos.x, targetPos.y, targetPos.z);
    }

    private void tickLaunchedState() {
        HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hitResult != null && hitResult.getType() != HitResult.Type.MISS) {
            onHit(hitResult);
            return;
        }

        this.checkInsideBlocks();

        Vec3 movement = this.getDeltaMovement();
        double nextX = this.getX() + movement.x;
        double nextY = this.getY() + movement.y;
        double nextZ = this.getZ() + movement.z;

        this.updateRotation();

        if (this.tickCount > MAX_FLIGHT_TICKS) {
            discard();
            return;
        }

        this.setPos(nextX, nextY, nextZ);
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);

        if (!this.level().isClientSide()) {
            detonate(result.getLocation());
            discard();
        }
    }

    private void detonate(Vec3 epicenter) {
        Level level = this.level();
        Entity owner = this.getOwner();

        playImpactEffects(level, epicenter);
        damageAndPushTargets(level, owner, epicenter);
        breakNearbyBlocks(level, epicenter);
    }

    private void playImpactEffects(Level level, Vec3 epicenter) {
        level.playSound(
                null,
                BlockPos.containing(epicenter),
                SoundEvents.GENERIC_EXPLODE,
                SoundSource.NEUTRAL,
                3.0F,
                0.5F
        );

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, epicenter.x, epicenter.y, epicenter.z, 2, 0, 0, 0, 0);
            serverLevel.sendParticles(ParticleTypes.SONIC_BOOM, epicenter.x, epicenter.y, epicenter.z, 2, 0, 0, 0, 0);
            serverLevel.sendParticles(ParticleTypes.FLAME, epicenter.x, epicenter.y, epicenter.z, 50, 2.0D, 2.0D, 2.0D, 0.5D);
            serverLevel.sendParticles(ParticleTypes.LAVA, epicenter.x, epicenter.y, epicenter.z, 20, 1.0D, 1.0D, 1.0D, 0.5D);
        }
    }

    private void damageAndPushTargets(Level level, Entity owner, Vec3 epicenter) {
        AABB bounds = new AABB(
                epicenter.x - IMPACT_RADIUS, epicenter.y - IMPACT_RADIUS, epicenter.z - IMPACT_RADIUS,
                epicenter.x + IMPACT_RADIUS, epicenter.y + IMPACT_RADIUS, epicenter.z + IMPACT_RADIUS
        );

        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, bounds, entity -> entity != owner);
        for (LivingEntity target : entities) {
            double distanceSquared = target.distanceToSqr(epicenter);
            if (distanceSquared > IMPACT_RADIUS * IMPACT_RADIUS) {
                continue;
            }

            double scale = 1.0D - (Math.sqrt(distanceSquared) / IMPACT_RADIUS);
            float damage = (float) (10.0D + (30.0D * scale));
            target.hurt(level.damageSources().magic(), damage);

            Vec3 push = target.position().subtract(epicenter).normalize().scale(3.0D * scale);
            target.setDeltaMovement(target.getDeltaMovement().add(push.x, push.y + scale, push.z));
        }
    }

    private void breakNearbyBlocks(Level level, Vec3 epicenter) {
        BlockPos centerPos = BlockPos.containing(epicenter);

        for (int x = -2; x <= 2; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -2; z <= 2; z++) {
                    if (x * x + y * y + z * z > 5) {
                        continue;
                    }

                    BlockPos pos = centerPos.offset(x, y, z);
                    BlockState state = level.getBlockState(pos);

                    if (state.isAir()
                            || state.getDestroySpeed(level, pos) < 0
                            || state.getBlock() == Blocks.BEDROCK
                            || state.getBlock() == Blocks.OBSIDIAN) {
                        continue;
                    }

                    FallingBlockEntity fallingBlock = FallingBlockEntity.fall(level, pos, state);
                    fallingBlock.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

                    Vec3 blockPush = new Vec3(
                            pos.getX() - epicenter.x,
                            pos.getY() - epicenter.y,
                            pos.getZ() - epicenter.z
                    ).normalize().scale(1.5D);

                    fallingBlock.setDeltaMovement(blockPush);
                    fallingBlock.dropItem = false;
                    level.addFreshEntity(fallingBlock);
                }
            }
        }
    }

    private void clearActiveRed(Player player) {
        player.getPersistentData().remove(ACTIVE_RED_KEY);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.contains("Launched")) {
            this.entityData.set(LAUNCHED, tag.getBoolean("Launched"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Launched", this.entityData.get(LAUNCHED));
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}