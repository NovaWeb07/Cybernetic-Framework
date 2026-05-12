package net.novadev.entity;

import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;

import net.novadev.init.NovaDevModEntities;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class HologramCloneEntity extends PathfinderMob {

    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID =
            SynchedEntityData.defineId(HologramCloneEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    private int lifespan = 1200;

    public HologramCloneEntity(EntityType<HologramCloneEntity> type, Level level) {
        super(type, level);
        xpReward = 0;
        setPersistenceRequired();
    }

    public HologramCloneEntity(PlayMessages.SpawnEntity packet, Level level) {
        this(NovaDevModEntities.HOLOGRAM_CLONE.get(), level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(OWNER_UUID, Optional.empty());
    }

    public void setOwnerUUID(@Nullable UUID uuid) {
        this.entityData.set(OWNER_UUID, Optional.ofNullable(uuid));
    }

    @Nullable
    public UUID getOwnerUUID() {
        return this.entityData.get(OWNER_UUID).orElse(null);
    }

    @Nullable
    public Player getOwner() {
        UUID uuid = getOwnerUUID();
        return uuid != null ? this.level().getPlayerByUUID(uuid) : null;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.4, true));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 0.8));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtByGoal(this));
        this.targetSelector.addGoal(3, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Monster.class, false));
    }

    @Override
    public void tick() {
        super.tick();
        lifespan--;

        if (this.level() instanceof ServerLevel sl && this.tickCount % 10 == 0) {
            sl.sendParticles(ParticleTypes.END_ROD,
                    this.getX(), this.getY() + 1.0, this.getZ(),
                    2, 0.2, 0.5, 0.2, 0.01);
        }

        if (lifespan <= 0 && !this.level().isClientSide()) {
            if (this.level() instanceof ServerLevel sl) {
                sl.sendParticles(ParticleTypes.CLOUD,
                        this.getX(), this.getY() + 0.5, this.getZ(),
                        15, 0.3, 0.5, 0.3, 0.05);
            }
            this.playSound(SoundEvents.ENDERMAN_TELEPORT, 0.6f, 1.4f);
            this.discard();
        }
    }

    @Override
    public net.minecraft.network.chat.Component getName() {
        return net.minecraft.network.chat.Component.literal("Clone");
    }

    @Override
    public net.minecraft.network.chat.Component getDisplayName() {
        return net.minecraft.network.chat.Component.literal("Clone");
    }

    @Override
    public void die(DamageSource cause) {
        super.die(cause);
        if (!this.level().isClientSide()) {
            Player owner = this.getOwner();
            if (owner != null) {
                owner.displayClientMessage(net.minecraft.network.chat.Component.literal("§cClones dead"), true);
            }
        }
    }

    @Override
    public boolean isAlliedTo(net.minecraft.world.entity.Entity other) {
        if (other instanceof Player p && p.getUUID().equals(getOwnerUUID())) return true;
        if (other instanceof HologramCloneEntity hc) {
            UUID myOwner = getOwnerUUID();
            UUID theirOwner = hc.getOwnerUUID();
            return myOwner != null && myOwner.equals(theirOwner);
        }
        return super.isAlliedTo(other);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() instanceof HologramCloneEntity hc) {
            UUID myOwner = getOwnerUUID();
            if (myOwner != null && myOwner.equals(hc.getOwnerUUID())) return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEFINED;
    }

    @Override
    public boolean removeWhenFarAway(double dist) {
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        UUID ownerUUID = getOwnerUUID();
        if (ownerUUID != null) tag.putUUID("OwnerUUID", ownerUUID);
        tag.putInt("CloneLifespan", lifespan);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.hasUUID("OwnerUUID")) setOwnerUUID(tag.getUUID("OwnerUUID"));
        if (tag.contains("CloneLifespan")) lifespan = tag.getInt("CloneLifespan");
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30.0)
                .add(Attributes.MOVEMENT_SPEED, 0.45)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.ARMOR, 4.0)
                .add(Attributes.FOLLOW_RANGE, 28.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.3);
    }

    static class OwnerHurtByGoal extends net.minecraft.world.entity.ai.goal.target.TargetGoal {
        private final HologramCloneEntity clone;
        private LivingEntity attacker;
        private int timestamp;

        public OwnerHurtByGoal(HologramCloneEntity clone) {
            super(clone, false);
            this.clone = clone;
        }

        @Override
        public boolean canUse() {
            Player owner = clone.getOwner();
            if (owner == null) return false;
            this.attacker = owner.getLastHurtByMob();
            int i = owner.getLastHurtByMobTimestamp();
            if (i == this.timestamp || this.attacker == null) return false;
            if (attacker instanceof HologramCloneEntity hc) {
                UUID myOwner = clone.getOwnerUUID();
                if (myOwner != null && myOwner.equals(hc.getOwnerUUID())) return false;
            }
            if (attacker instanceof Player p && p.getUUID().equals(clone.getOwnerUUID())) return false;
            return true;
        }

        @Override
        public void start() {
            clone.setTarget(this.attacker);
            Player owner = clone.getOwner();
            if (owner != null) this.timestamp = owner.getLastHurtByMobTimestamp();
            super.start();
        }
    }

    static class OwnerHurtTargetGoal extends net.minecraft.world.entity.ai.goal.target.TargetGoal {
        private final HologramCloneEntity clone;
        private LivingEntity target;
        private int timestamp;

        public OwnerHurtTargetGoal(HologramCloneEntity clone) {
            super(clone, false);
            this.clone = clone;
        }

        @Override
        public boolean canUse() {
            Player owner = clone.getOwner();
            if (owner == null) return false;
            this.target = owner.getLastHurtMob();
            int i = owner.getLastHurtMobTimestamp();
            if (i == this.timestamp || this.target == null) return false;
            if (target instanceof HologramCloneEntity hc) {
                UUID myOwner = clone.getOwnerUUID();
                if (myOwner != null && myOwner.equals(hc.getOwnerUUID())) return false;
            }
            if (target instanceof Player p && p.getUUID().equals(clone.getOwnerUUID())) return false;
            return true;
        }

        @Override
        public void start() {
            clone.setTarget(this.target);
            Player owner = clone.getOwner();
            if (owner != null) this.timestamp = owner.getLastHurtMobTimestamp();
            super.start();
        }
    }
}
