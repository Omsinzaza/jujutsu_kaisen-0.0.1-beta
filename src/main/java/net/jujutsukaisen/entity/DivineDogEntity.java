package net.jujutsukaisen.entity;

import net.jujutsukaisen.Config;
import net.jujutsukaisen.init.EntityInit;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class DivineDogEntity extends TamableAnimal {

    private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(
            DivineDogEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Boolean> DATA_SUMMONING = SynchedEntityData.defineId(
            DivineDogEntity.class, EntityDataSerializers.BOOLEAN);

    private static final int DEFAULT_LIFETIME_TICKS = 1200;
    private static final int DEFAULT_SUMMONING_TICKS = 15;
    private static final double DEFAULT_MAX_HEALTH = 30.0D;
    private static final double DEFAULT_MOVEMENT_SPEED = 0.35D;
    private static final double DEFAULT_ATTACK_DAMAGE = 8.0D;

    private int lifeTicks = DEFAULT_LIFETIME_TICKS;
    private int summoningTicks = DEFAULT_SUMMONING_TICKS;
    private Vec3 summonerPos = null;

    public DivineDogEntity(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
        this.setTame(true);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_VARIANT, 0);
        this.entityData.define(DATA_SUMMONING, true);
    }

    public int getVariant() {
        return this.entityData.get(DATA_VARIANT);
    }

    public void setVariant(int variant) {
        this.entityData.set(DATA_VARIANT, variant);
    }

    public boolean isWhite() {
        return getVariant() == 1;
    }

    public boolean isSummoning() {
        return this.entityData.get(DATA_SUMMONING);
    }

    private void setSummoning(boolean summoning) {
        this.entityData.set(DATA_SUMMONING, summoning);
    }

    public void setSummonerPos(Vec3 pos) {
        this.summonerPos = pos;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("DogVariant", this.getVariant());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setVariant(tag.getInt("DogVariant"));
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(4, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Monster.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, DEFAULT_MAX_HEALTH)
                .add(Attributes.MOVEMENT_SPEED, DEFAULT_MOVEMENT_SPEED)
                .add(Attributes.ATTACK_DAMAGE, DEFAULT_ATTACK_DAMAGE);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        Entity attacker = source.getEntity();
        if (attacker != null) {
            if (attacker.equals(this.getOwner())) {
                return false;
            }

            if (attacker instanceof DivineDogEntity otherDog) {
                if (this.getOwner() != null && this.getOwner().equals(otherDog.getOwner())) {
                    return false;
                }
            }
        }
        return super.hurt(source, amount);
    }

    @Override
    public boolean wantsToAttack(LivingEntity target, LivingEntity owner) {
        if (target.equals(owner)) {
            return false;
        }

        if (target instanceof DivineDogEntity otherDog) {
            if (owner != null && owner.equals(otherDog.getOwner())) {
                return false;
            }
        }
        return super.wantsToAttack(target, owner);
    }

    @Override
    public boolean isAlliedTo(Entity other) {
        if (other instanceof DivineDogEntity otherDog) {
            if (this.getOwner() != null && this.getOwner().equals(otherDog.getOwner())) {
                return true;
            }
        }
        if (other.equals(this.getOwner())) {
            return true;
        }
        return super.isAlliedTo(other);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            if (summoningTicks > 0) {
                this.setInvisible(true);
                this.setInvulnerable(true);
                this.setNoAi(true);
                this.noPhysics = true;

                if (this.level() instanceof ServerLevel serverLevel && summonerPos != null) {
                    float denominator = Math.max(1.0F, (float) Config.getDivineDogSummoningTicks());
                    float progress = 1.0F - ((float) summoningTicks / denominator);
                    Vec3 targetPos = this.position();
                    double px = summonerPos.x + (targetPos.x - summonerPos.x) * progress;
                    double pz = summonerPos.z + (targetPos.z - summonerPos.z) * progress;
                    double py = summonerPos.y + 0.1;

                    serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE,
                            px, py, pz, 3, 0.1, 0.05, 0.1, 0.01);
                    serverLevel.sendParticles(ParticleTypes.SOUL,
                            px, py, pz, 1, 0.1, 0.05, 0.1, 0.0);
                }

                summoningTicks--;

                if (summoningTicks <= 0) {
                    this.setSummoning(false);
                    this.setInvisible(false);
                    this.setInvulnerable(false);
                    this.setNoAi(false);
                    this.noPhysics = false;

                    if (this.level() instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE,
                                this.getX(), this.getY() + 0.3, this.getZ(),
                                8, 0.3, 0.2, 0.3, 0.02);
                        serverLevel.sendParticles(ParticleTypes.SOUL,
                                this.getX(), this.getY() + 0.3, this.getZ(),
                                5, 0.2, 0.1, 0.2, 0.01);
                    }
                }
                return;
            }

            lifeTicks--;
            if (lifeTicks <= 0) {
                this.discard();
                return;
            }

            LivingEntity owner = this.getOwner();
            if (owner == null || !owner.isAlive() || owner.isRemoved()) {
                this.discard();
            }
        }
    }

    @Override
    public boolean isPickable() {
        return !isSummoning();
    }

    @Override
    public boolean isPushable() {
        return !isSummoning();
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    public static DivineDogEntity summon(Level level, Player summoner, int variant) {
        DivineDogEntity dog = new DivineDogEntity(EntityInit.DIVINE_DOG.get(), level);
        dog.tame(summoner);
        dog.setOrderedToSit(false);
        dog.setVariant(variant);
        dog.setSummonerPos(summoner.position());
        return dog;
    }
}
