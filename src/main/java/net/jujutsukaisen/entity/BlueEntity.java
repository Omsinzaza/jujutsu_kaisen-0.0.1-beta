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
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.List;
import java.util.UUID;

public class BlueEntity extends Entity {
    private static final EntityDataAccessor<Boolean> LAUNCHED =
            SynchedEntityData.defineId(BlueEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> TICK_AGE =
            SynchedEntityData.defineId(BlueEntity.class, EntityDataSerializers.INT);

    private static final String ACTIVE_BLUE_KEY = "jujutsukaisen_active_blue";
    private static final double HOLD_DISTANCE = 2.0D;
    private static final double HELD_PULL_RADIUS = 5.0D;
    private static final double HELD_PULL_STRENGTH = 0.05D;
    private static final double LAUNCHED_PULL_RADIUS = 6.0D;
    private static final double LAUNCHED_PULL_STRENGTH = 0.5D;
    private static final double IMPACT_RADIUS = 6.0D;

    private LivingEntity owner;
    private UUID ownerUUID;

    public BlueEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    public BlueEntity(Level level, LivingEntity owner) {
        super(EntityInit.BLUE.get(), level);
        this.owner = owner;
        this.ownerUUID = owner.getUUID();
        this.setNoGravity(true);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(LAUNCHED, false);
        this.entityData.define(TICK_AGE, 0);
    }

    public boolean isLaunched() {
        return this.entityData.get(LAUNCHED);
    }

    public void launch(Vec3 direction) {
        this.entityData.set(LAUNCHED, true);
        this.setDeltaMovement(direction.scale(2.0D));
        this.tickCount = 0;
    }

    public int getTickAge() {
        return this.entityData.get(TICK_AGE);
    }

    @Override
    public void tick() {
        super.tick();
        this.entityData.set(TICK_AGE, this.tickCount);

        if (this.level().isClientSide()) {
            return;
        }

        LivingEntity owner = resolveOwner();
        if (shouldDiscardBecauseOwnerIsInvalid(owner)) {
            discard();
            return;
        }

        if (!isLaunched()) {
            tickHeldState(owner);
            return;
        }

        tickLaunchedState(owner);
    }

    private LivingEntity resolveOwner() {
        if (this.owner != null) {
            return this.owner;
        }

        if (this.ownerUUID == null || !(this.level() instanceof ServerLevel serverLevel)) {
            return null;
        }

        Entity entity = serverLevel.getEntity(this.ownerUUID);
        if (entity instanceof LivingEntity livingEntity) {
            this.owner = livingEntity;
            return livingEntity;
        }

        return null;
    }

    private boolean shouldDiscardBecauseOwnerIsInvalid(LivingEntity owner) {
        return owner == null || !owner.isAlive() || owner.isRemoved();
    }

    private void tickHeldState(LivingEntity owner) {
        if (!(owner instanceof Player player)) {
            discard();
            return;
        }

        if (!shouldRemainHeld(player)) {
            clearActiveBlue(player);
            discard();
            return;
        }

        followOwnerWhileHeld(owner);
        pullNearbyTargetsWhileHeld(owner);
    }

    private boolean shouldRemainHeld(Player player) {
        return player.getCapability(CursedEnergyProvider.CURSED_ENERGY)
                .map(energy -> {
                    List<TechniqueMove> moves = TechniqueMove.getMovesForTechnique(energy.getTechnique());
                    int selectedMoveIndex = energy.getSelectedMoveIndex();

                    return selectedMoveIndex >= 0
                            && selectedMoveIndex < moves.size()
                            && moves.get(selectedMoveIndex) == TechniqueMove.LIMITLESS_BLUE;
                })
                .orElse(false);
    }

    private void followOwnerWhileHeld(LivingEntity owner) {
        Vec3 look = owner.getLookAngle();
        Vec3 targetPos = owner.getEyePosition().add(look.scale(HOLD_DISTANCE));
        this.setPos(targetPos.x, targetPos.y, targetPos.z);
    }

    private void pullNearbyTargetsWhileHeld(LivingEntity owner) {
        AABB bounds = this.getBoundingBox().inflate(HELD_PULL_RADIUS);
        List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class, bounds, entity -> entity != owner);

        for (LivingEntity target : entities) {
            Vec3 pull = this.position().subtract(target.position()).normalize().scale(HELD_PULL_STRENGTH);
            target.setDeltaMovement(target.getDeltaMovement().add(pull));
            target.hurt(this.level().damageSources().drown(), 1.0F);
        }
    }

    private void tickLaunchedState(LivingEntity owner) {
        HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hitResult != null && hitResult.getType() != HitResult.Type.MISS) {
            onHit(hitResult);
            return;
        }

        Vec3 nextPos = this.position().add(this.getDeltaMovement());
        this.setPos(nextPos.x, nextPos.y, nextPos.z);
        pullNearbyTargetsWhileLaunched(owner);
    }

    private void pullNearbyTargetsWhileLaunched(LivingEntity owner) {
        AABB bounds = this.getBoundingBox().inflate(LAUNCHED_PULL_RADIUS);
        List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class, bounds, entity -> entity != owner);

        for (LivingEntity target : entities) {
            Vec3 pull = this.position().subtract(target.position()).normalize().scale(LAUNCHED_PULL_STRENGTH);
            target.setDeltaMovement(target.getDeltaMovement().add(pull));
        }
    }

    protected boolean canHitEntity(Entity entity) {
        return entity.canBeCollidedWith() && entity != this.owner;
    }

    protected void onHit(HitResult result) {
        if (this.level().isClientSide()) {
            return;
        }

        Vec3 epicenter = result.getLocation();
        playImpactEffects(epicenter);
        damageAndPullTargets(epicenter);
        breakNearbyBlocks(epicenter);
        discard();
    }

    private void playImpactEffects(Vec3 epicenter) {
        this.level().playSound(
                null,
                BlockPos.containing(epicenter),
                SoundEvents.GENERIC_EXPLODE,
                SoundSource.NEUTRAL,
                2.0F,
                0.5F
        );

        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, epicenter.x, epicenter.y, epicenter.z, 2, 0, 0, 0, 0);
            serverLevel.sendParticles(ParticleTypes.SONIC_BOOM, epicenter.x, epicenter.y, epicenter.z, 1, 0, 0, 0, 0);
        }
    }

    private void damageAndPullTargets(Vec3 epicenter) {
        AABB bounds = new AABB(
                epicenter.x - IMPACT_RADIUS, epicenter.y - IMPACT_RADIUS, epicenter.z - IMPACT_RADIUS,
                epicenter.x + IMPACT_RADIUS, epicenter.y + IMPACT_RADIUS, epicenter.z + IMPACT_RADIUS
        );

        List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class, bounds, entity -> entity != this.owner);
        for (LivingEntity target : entities) {
            double distanceSquared = target.distanceToSqr(epicenter);
            if (distanceSquared > IMPACT_RADIUS * IMPACT_RADIUS) {
                continue;
            }

            double scale = 1.0D - (Math.sqrt(distanceSquared) / IMPACT_RADIUS);
            float damage = (float) (5.0D + (15.0D * scale));
            target.hurt(this.level().damageSources().magic(), damage);

            Vec3 pull = epicenter.subtract(target.position()).normalize().scale(scale);
            target.setDeltaMovement(pull);
        }
    }

    private void breakNearbyBlocks(Vec3 epicenter) {
        BlockPos centerPos = BlockPos.containing(epicenter);

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos pos = centerPos.offset(x, y, z);
                    BlockState state = this.level().getBlockState(pos);

                    if (state.isAir() || state.getDestroySpeed(this.level(), pos) < 0 || state.getBlock() == Blocks.BEDROCK) {
                        continue;
                    }

                    FallingBlockEntity fallingBlock = FallingBlockEntity.fall(this.level(), pos, state);
                    fallingBlock.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                    fallingBlock.setDeltaMovement(0, 0.4D, 0);
                    fallingBlock.dropItem = false;
                    this.level().addFreshEntity(fallingBlock);
                }
            }
        }
    }

    private void clearActiveBlue(Player player) {
        player.getPersistentData().remove(ACTIVE_BLUE_KEY);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.hasUUID("Owner")) {
            this.ownerUUID = tag.getUUID("Owner");
        }

        if (tag.contains("Launched")) {
            this.entityData.set(LAUNCHED, tag.getBoolean("Launched"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (this.ownerUUID != null) {
            tag.putUUID("Owner", this.ownerUUID);
        }

        tag.putBoolean("Launched", this.entityData.get(LAUNCHED));
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}