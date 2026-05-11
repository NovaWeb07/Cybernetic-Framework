package net.mcreator.roxdev.entity;

import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.GeoEntity;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.network.NetworkHooks;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

import net.mcreator.roxdev.init.RoxDevModEntities;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;

public class FlamdroneEntity extends PathfinderMob implements GeoEntity {
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(FlamdroneEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(FlamdroneEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(FlamdroneEntity.class, EntityDataSerializers.STRING);
	private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID =
			SynchedEntityData.defineId(FlamdroneEntity.class, EntityDataSerializers.OPTIONAL_UUID);

	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private boolean swinging;
	private boolean lastloop;
	private long lastSwing;
	public String animationprocedure = "empty";

	private int shootCooldown = 0;
	private static final int SHOOT_COOLDOWN_TICKS = 30;
	private static final double FOLLOW_DISTANCE = 4.0;
	private static final double HOVER_HEIGHT = 3.0;

	public FlamdroneEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(RoxDevModEntities.FLAMDRONE.get(), world);
	}

	public FlamdroneEntity(EntityType<FlamdroneEntity> type, Level world) {
		super(type, world);
		xpReward = 0;
		setNoAi(false);
		setMaxUpStep(0.6f);
		setPersistenceRequired();
		this.moveControl = new FlyingMoveControl(this, 10, true);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SHOOT, false);
		this.entityData.define(ANIMATION, "undefined");
		this.entityData.define(TEXTURE, "flamedrone");
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

	public void setTexture(String texture) {
		this.entityData.set(TEXTURE, texture);
	}

	public String getTexture() {
		return this.entityData.get(TEXTURE);
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	protected PathNavigation createNavigation(Level world) {
		return new FlyingPathNavigation(this, world);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new DroneFollowOwnerGoal(this));
	}

	@Override
	public void tick() {
		super.tick();

		if (shootCooldown > 0) shootCooldown--;

		if (!this.level().isClientSide()) {
			Player owner = getOwner();

			if (this.tickCount % 5 == 0 && this.level() instanceof ServerLevel sl) {
				sl.sendParticles(ParticleTypes.FLAME,
						this.getX(), this.getY() - 0.3, this.getZ(),
						2, 0.1, 0.0, 0.1, 0.01);
				sl.sendParticles(ParticleTypes.SMOKE,
						this.getX(), this.getY() - 0.2, this.getZ(),
						1, 0.05, 0.0, 0.05, 0.005);
			}

			if (owner != null && shootCooldown <= 0) {
				LivingEntity ownerTarget = owner.getLastHurtMob();
				int timestamp = owner.getLastHurtMobTimestamp();

				if (ownerTarget != null && ownerTarget.isAlive()
						&& ownerTarget != this
						&& (this.tickCount - timestamp) < 20) {
					shootSwordAt(ownerTarget);
					shootCooldown = SHOOT_COOLDOWN_TICKS;
				}
			}

			if (this.tickCount > 3600) {
				if (this.level() instanceof ServerLevel sl) {
					sl.sendParticles(ParticleTypes.LARGE_SMOKE,
							getX(), getY() + 0.5, getZ(), 15, 0.3, 0.5, 0.3, 0.05);
				}
				this.playSound(SoundEvents.FIRE_EXTINGUISH, 0.8f, 1.2f);
				if (owner != null) {
					owner.displayClientMessage(
							net.minecraft.network.chat.Component.literal("§cFlame Drone expired!"), true);
				}
				this.discard();
			}
		}
	}

	private void shootSwordAt(LivingEntity target) {
		if (!(this.level() instanceof ServerLevel sl)) return;

		DroneSwordProjectile sword = new DroneSwordProjectile(
				RoxDevModEntities.DRONE_SWORD.get(), this.level());
		sword.setPos(this.getX(), this.getY() + 0.5, this.getZ());
		sword.setOwner(this);

		double dx = target.getX() - this.getX();
		double dy = (target.getY() + target.getBbHeight() / 2) - (this.getY() + 0.5);
		double dz = target.getZ() - this.getZ();
		double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

		if (dist > 0) {
			double speed = 1.8;
			sword.setDeltaMovement(
					(dx / dist) * speed,
					(dy / dist) * speed + 0.05,
					(dz / dist) * speed
			);
		}

		sl.addFreshEntity(sword);

		this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
				SoundEvents.TRIDENT_THROW, SoundSource.NEUTRAL, 0.8f, 1.5f);

		sl.sendParticles(ParticleTypes.FLAME,
				this.getX(), this.getY() + 0.5, this.getZ(),
				8, 0.15, 0.1, 0.15, 0.05);
		sl.sendParticles(ParticleTypes.ELECTRIC_SPARK,
				this.getX(), this.getY() + 0.5, this.getZ(),
				5, 0.1, 0.1, 0.1, 0.1);
	}

	@Override
	public boolean isAlliedTo(net.minecraft.world.entity.Entity other) {
		if (other instanceof Player p && p.getUUID().equals(getOwnerUUID())) return true;
		if (other instanceof FlamdroneEntity fd) {
			UUID myOwner = getOwnerUUID();
			UUID theirOwner = fd.getOwnerUUID();
			return myOwner != null && myOwner.equals(theirOwner);
		}
		return super.isAlliedTo(other);
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.getEntity() instanceof Player p && p.getUUID().equals(getOwnerUUID())) {
			return false;
		}
		return super.hurt(source, amount);
	}

	@Override
	public MobType getMobType() {
		return MobType.UNDEFINED;
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.hurt"));
	}

	@Override
	public SoundEvent getDeathSound() {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.death"));
	}

	@Override
	public boolean causeFallDamage(float l, float d, DamageSource source) {
		return false;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putString("Texture", this.getTexture());
		UUID ownerUUID = getOwnerUUID();
		if (ownerUUID != null) compound.putUUID("OwnerUUID", ownerUUID);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Texture"))
			this.setTexture(compound.getString("Texture"));
		if (compound.hasUUID("OwnerUUID"))
			setOwnerUUID(compound.getUUID("OwnerUUID"));
	}

	@Override
	public void baseTick() {
		super.baseTick();
		this.refreshDimensions();
	}

	@Override
	public EntityDimensions getDimensions(Pose p_33597_) {
		return super.getDimensions(p_33597_).scale((float) 1);
	}

	@Override
	protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
	}

	@Override
	public void setNoGravity(boolean ignored) {
		super.setNoGravity(true);
	}

	public void aiStep() {
		super.aiStep();
		this.setNoGravity(true);
	}

	public static void init() {
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.35);
		builder = builder.add(Attributes.MAX_HEALTH, 20);
		builder = builder.add(Attributes.ARMOR, 4);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 3);
		builder = builder.add(Attributes.FOLLOW_RANGE, 32);
		builder = builder.add(Attributes.FLYING_SPEED, 0.4);
		return builder;
	}

	static class DroneFollowOwnerGoal extends Goal {
		private final FlamdroneEntity drone;

		DroneFollowOwnerGoal(FlamdroneEntity drone) {
			this.drone = drone;
			this.setFlags(EnumSet.of(Goal.Flag.MOVE));
		}

		@Override
		public boolean canUse() {
			return drone.getOwner() != null;
		}

		@Override
		public void tick() {
			Player owner = drone.getOwner();
			if (owner == null) return;

			double targetX = owner.getX();
			double targetY = owner.getY() + HOVER_HEIGHT;
			double targetZ = owner.getZ();

			double dx = targetX - drone.getX();
			double dy = targetY - drone.getY();
			double dz = targetZ - drone.getZ();
			double distSq = dx * dx + dy * dy + dz * dz;

			double angle = (drone.tickCount * 0.05) % (Math.PI * 2);
			double orbitRadius = 2.0;
			targetX += Math.cos(angle) * orbitRadius;
			targetZ += Math.sin(angle) * orbitRadius;

			if (distSq > 2.0) {
				drone.getNavigation().moveTo(targetX, targetY, targetZ, 1.2);
			}

			if (distSq > 400) {
				drone.teleportTo(owner.getX(), owner.getY() + HOVER_HEIGHT, owner.getZ());
			}

			drone.getLookControl().setLookAt(owner, 30.0f, 30.0f);
		}
	}

	@Override
	public net.minecraft.network.chat.Component getName() {
		return net.minecraft.network.chat.Component.literal("Flame Drone");
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			return event.setAndContinue(RawAnimation.begin().thenLoop("idle"));
		}
		return PlayState.STOP;
	}

	String prevAnim = "empty";

	private PlayState procedurePredicate(AnimationState event) {
		if (!animationprocedure.equals("empty") && event.getController().getAnimationState() == AnimationController.State.STOPPED || (!this.animationprocedure.equals(prevAnim) && !this.animationprocedure.equals("empty"))) {
			if (!this.animationprocedure.equals(prevAnim))
				event.getController().forceAnimationReset();
			event.getController().setAnimation(RawAnimation.begin().thenPlay(this.animationprocedure));
			if (event.getController().getAnimationState() == AnimationController.State.STOPPED) {
				this.animationprocedure = "empty";
				event.getController().forceAnimationReset();
			}
		} else if (animationprocedure.equals("empty")) {
			prevAnim = "empty";
			return PlayState.STOP;
		}
		prevAnim = this.animationprocedure;
		return PlayState.CONTINUE;
	}

	@Override
	protected void tickDeath() {
		++this.deathTime;
		if (this.deathTime == 20) {
			this.remove(FlamdroneEntity.RemovalReason.KILLED);
			this.dropExperience();
		}
	}

	public String getSyncedAnimation() {
		return this.entityData.get(ANIMATION);
	}

	public void setAnimation(String animation) {
		this.entityData.set(ANIMATION, animation);
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar data) {
		data.add(new AnimationController<>(this, "movement", 4, this::movementPredicate));
		data.add(new AnimationController<>(this, "procedure", 4, this::procedurePredicate));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}
}
