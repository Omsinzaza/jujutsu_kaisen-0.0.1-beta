package net.jujutsukaisen.entity;

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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.List;

public class HollowPurpleEntity extends Projectile {
    private static final EntityDataAccessor<Integer> STATE =
            SynchedEntityData.defineId(HollowPurpleEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> INTERNAL_TICK =
            SynchedEntityData.defineId(HollowPurpleEntity.class, EntityDataSerializers.INT);

    public static final int STATE_CHARGING = 0;
    public static final int STATE_READY = 1;
    public static final int STATE_SHOOTING = 2;
    public static final int STATE_LAUNCHED = 3;

    private static final int FORM_END_TICK = 324;
    private static final int SHOOT_DELAY_TICKS = 35;
    private static final int MAX_FLIGHT_TICKS = 200;

    private static final double HOLD_DISTANCE = 4.0D;
    private static final double OWNER_HOVER_LIFT_LOW = 0.08D;
    private static final double OWNER_HOVER_LIFT_HIGH = 0.02D;
    private static final double LAUNCH_SPEED = 3.0D;

    private static final double DESTRUCTION_RADIUS = 10.0D;
    private static final float DESTRUCTION_DAMAGE = 500.0F;

    private int flightTicks = 0;

    public HollowPurpleEntity(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
    }

    public HollowPurpleEntity(Level level, LivingEntity owner) {
        super(EntityInit.HOLLOW_PURPLE.get(), level);
        this.setOwner(owner);
        this.setNoGravity(true);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(STATE, STATE_CHARGING);
        this.entityData.define(INTERNAL_TICK, 0);
    }

    public int getEntityState() {
        return this.entityData.get(STATE);
    }

    public int getInternalTick() {
        return this.entityData.get(INTERNAL_TICK);
    }

    public void startShootSequence() {
        if (getEntityState() != STATE_READY) {
            return;
        }

        setState(STATE_SHOOTING);
        setInternalTick(0);
    }

    @Override
    public void tick() {
        updateBaseTickBehavior();
        incrementInternalTick();

        if (this.level().isClientSide()) {
            return;
        }

        Entity owner = this.getOwner();
        if (shouldDiscardBecauseOwnerIsInvalid(owner)) {
            discard();
            return;
        }

        if (isLaunchedState()) {
            tickLaunchedState();
            return;
        }

        tickHeldState(owner);
    }

    private void updateBaseTickBehavior() {
        if (!isLaunchedState()) {
            super.tick();
            return;
        }

        this.xOld = this.getX();
        this.yOld = this.getY();
        this.zOld = this.getZ();
    }

    private void incrementInternalTick() {
        setInternalTick(getInternalTick() + 1);
    }

    private boolean shouldDiscardBecauseOwnerIsInvalid(Entity owner) {
        return owner == null || !owner.isAlive() || owner.isRemoved();
    }

    private boolean isLaunchedState() {
        return getEntityState() == STATE_LAUNCHED;
    }

    private void tickHeldState(Entity owner) {
        anchorInFrontOfOwner(owner);
        liftOwnerWhileCharging(owner);
        updateHeldState();
    }

    private void anchorInFrontOfOwner(Entity owner) {
        Vec3 look = owner.getLookAngle();
        Vec3 targetPos = owner.getEyePosition().add(look.scale(HOLD_DISTANCE));
        this.setPos(targetPos.x, targetPos.y, targetPos.z);
    }

    private void liftOwnerWhileCharging(Entity owner) {
        int state = getEntityState();
        if (state != STATE_CHARGING && state != STATE_READY) {
            return;
        }

        Vec3 currentVelocity = owner.getDeltaMovement();
        double lift = currentVelocity.y < 0.05D ? OWNER_HOVER_LIFT_LOW : OWNER_HOVER_LIFT_HIGH;
        owner.setDeltaMovement(currentVelocity.x, lift, currentVelocity.z);
        owner.hurtMarked = true;
    }

    private void updateHeldState() {
        int state = getEntityState();
        int ticks = getInternalTick();

        if (state == STATE_CHARGING && ticks >= FORM_END_TICK) {
            setState(STATE_READY);
            return;
        }

        if (state == STATE_SHOOTING && ticks >= SHOOT_DELAY_TICKS) {
            launch();
        }
    }

    private void launch() {
        setState(STATE_LAUNCHED);

        Entity owner = this.getOwner();
        if (owner != null) {
            this.setDeltaMovement(owner.getLookAngle().scale(LAUNCH_SPEED));
        }

        this.flightTicks = 0;
    }

    private void tickLaunchedState() {
        this.flightTicks++;
        moveLaunchedProjectile();
        processDestruction();

        if (this.flightTicks > MAX_FLIGHT_TICKS) {
            discard();
        }
    }

    private void moveLaunchedProjectile() {
        Vec3 velocity = this.getDeltaMovement();
        this.setPos(this.getX() + velocity.x, this.getY() + velocity.y, this.getZ() + velocity.z);
    }

    private void processDestruction() {
        Level level = this.level();
        Vec3 center = this.position();

        damageNearbyTargets(level, center);
        destroyNearbyBlocks(level, center);
        spawnDestructionParticles(level, center);
    }

    private void damageNearbyTargets(Level level, Vec3 center) {
        AABB bounds = this.getBoundingBox().inflate(DESTRUCTION_RADIUS);
        List<LivingEntity> targets = level.getEntitiesOfClass(
                LivingEntity.class,
                bounds,
                entity -> entity != getOwner()
        );

        for (LivingEntity target : targets) {
            target.hurt(level.damageSources().magic(), DESTRUCTION_DAMAGE);
        }
    }

    private void destroyNearbyBlocks(Level level, Vec3 center) {
        BlockPos entityPos = this.blockPosition();
        int radius = (int) DESTRUCTION_RADIUS;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + y * y + z * z > radius * radius) {
                        continue;
                    }

                    BlockPos blockPos = entityPos.offset(x, y, z);
                    BlockState state = level.getBlockState(blockPos);

                    if (!canDestroyBlock(level, blockPos, state)) {
                        continue;
                    }

                    destroyBlock(level, blockPos, state, center);
                }
            }
        }
    }

    private boolean canDestroyBlock(Level level, BlockPos pos, BlockState state) {
        return !state.isAir()
                && state.getDestroySpeed(level, pos) >= 0
                && state.getBlock() != Blocks.BEDROCK;
    }

    private void destroyBlock(Level level, BlockPos pos, BlockState state, Vec3 center) {
        FallingBlockEntity fallingBlock = FallingBlockEntity.fall(level, pos, state);
        fallingBlock.setPos(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);

        Vec3 push = new Vec3(
                pos.getX() - center.x,
                pos.getY() - center.y,
                pos.getZ() - center.z
        ).normalize().scale(1.5D);

        fallingBlock.setDeltaMovement(push);
        fallingBlock.dropItem = false;
        level.addFreshEntity(fallingBlock);
    }

    private void spawnDestructionParticles(Level level, Vec3 center) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        serverLevel.sendParticles(
                ParticleTypes.PORTAL,
                center.x, center.y, center.z,
                20,
                1.0D, 1.0D, 1.0D,
                0.2D
        );

        serverLevel.sendParticles(
                ParticleTypes.DRAGON_BREATH,
                center.x, center.y, center.z,
                10,
                0.5D, 0.5D, 0.5D,
                0.05D
        );
    }

    private void setState(int state) {
        this.entityData.set(STATE, state);
    }

    private void setInternalTick(int tick) {
        this.entityData.set(INTERNAL_TICK, tick);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("State", getEntityState());
        tag.putInt("InternalTick", getInternalTick());
        tag.putInt("FlightTicks", this.flightTicks);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.contains("State")) {
            setState(tag.getInt("State"));
        }

        if (tag.contains("InternalTick")) {
            setInternalTick(tag.getInt("InternalTick"));
        }

        if (tag.contains("FlightTicks")) {
            this.flightTicks = tag.getInt("FlightTicks");
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}