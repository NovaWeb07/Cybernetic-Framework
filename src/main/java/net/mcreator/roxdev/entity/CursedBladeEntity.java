package net.mcreator.roxdev.entity;

import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.network.NetworkHooks;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;

import net.mcreator.roxdev.init.RoxDevModEntities;

import javax.annotation.Nullable;
import java.util.UUID;

public class CursedBladeEntity extends Monster {
    @Nullable
    private UUID ownerUUID;

    public CursedBladeEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(RoxDevModEntities.CURSED_BLADE.get(), world);
    }

    public CursedBladeEntity(EntityType<CursedBladeEntity> type, Level world) {
        super(type, world);
        xpReward = 0;
        setPersistenceRequired();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.4, true));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new FloatGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class,
                10, true, false,
                e -> !(e instanceof CursedBladeEntity) && !isOwner(e)));
    }

    public void setOwner(@Nullable Player player) {
        this.ownerUUID = player != null ? player.getUUID() : null;
    }

    private boolean isOwner(LivingEntity entity) {
        return ownerUUID != null && entity.getUUID().equals(ownerUUID);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
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
    public SoundEvent getAmbientSound() {
        return SoundEvents.SOUL_ESCAPE;
    }

    @Override
    public SoundEvent getHurtSound(DamageSource ds) {
        return SoundEvents.ANVIL_LAND;
    }

    @Override
    public SoundEvent getDeathSound() {
        return SoundEvents.GLASS_BREAK;
    }

    @Override
    public void tick() {
        super.tick();
        if (level() instanceof ServerLevel sl && tickCount % 4 == 0) {
            sl.sendParticles(ParticleTypes.ENCHANT,
                    getX(), getY() + 1.0, getZ(), 3, 0.2, 0.5, 0.2, 0.1);
            sl.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                    getX(), getY() + 0.5, getZ(), 1, 0.1, 0.2, 0.1, 0.01);
        }
        if (tickCount > 600) {
            if (level() instanceof ServerLevel sl) {
                sl.sendParticles(ParticleTypes.LARGE_SMOKE,
                        getX(), getY() + 1, getZ(), 15, 0.3, 0.5, 0.3, 0.05);
            }
            level().playSound(null, getX(), getY(), getZ(),
                    SoundEvents.FIRE_EXTINGUISH, getSoundSource(), 1.0f, 0.8f);
            discard();
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (ownerUUID != null) tag.putUUID("OwnerUUID", ownerUUID);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.hasUUID("OwnerUUID")) ownerUUID = tag.getUUID("OwnerUUID");
    }

    public static void init() {
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.38)
                .add(Attributes.MAX_HEALTH, 30)
                .add(Attributes.ARMOR, 2)
                .add(Attributes.ATTACK_DAMAGE, 8)
                .add(Attributes.FOLLOW_RANGE, 24)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5);
    }
}
