package net.mcreator.roxdev.item;

import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.GeoItem;

import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;

import net.mcreator.roxdev.item.renderer.FlamePearlItemRenderer;

import java.util.function.Consumer;
import java.util.List;
import java.util.Optional;

public class FlamePearlItem extends Item implements GeoItem {
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	public String animationprocedure = "empty";

	private static final double REACH = 13.0;
	private static final int COOLDOWN_TICKS = 40;

	public FlamePearlItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON));
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return false;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (!level.isClientSide()) {
			Vec3 eyePos = player.getEyePosition(1.0f);
			Vec3 lookVec = player.getLookAngle();
			Vec3 reachEnd = eyePos.add(lookVec.scale(REACH));

			AABB searchBox = player.getBoundingBox()
					.expandTowards(lookVec.scale(REACH))
					.inflate(1.0);

			List<Entity> candidates = level.getEntities(player, searchBox,
					e -> e != player && e instanceof LivingEntity && e.isAlive());

			Entity target = null;
			double closestDist = Double.MAX_VALUE;

			for (Entity e : candidates) {
				AABB entityBox = e.getBoundingBox().inflate(0.3);
				Optional<Vec3> hitResult = entityBox.clip(eyePos, reachEnd);

				if (hitResult.isPresent()) {
					double dist = eyePos.distanceTo(hitResult.get());
					if (dist < closestDist) {
						closestDist = dist;
						target = e;
					}
				} else if (entityBox.contains(eyePos)) {
					closestDist = 0;
					target = e;
				}
			}

			if (target == null) {
				player.sendSystemMessage(Component.literal("§cNo target in range!"));
				return InteractionResultHolder.fail(stack);
			}

			Vec3 playerPos = player.position();
			Vec3 targetPos = target.position();
			Vec3 pullDir = playerPos.subtract(targetPos).normalize();

			double dist = playerPos.distanceTo(targetPos);
			double speed = Math.min(2.5, 0.8 + dist * 0.15);

			target.setDeltaMovement(
					pullDir.x * speed,
					pullDir.y * speed + 0.25,
					pullDir.z * speed
			);
			target.hurtMarked = true;

			level.playSound(null, player.getX(), player.getY(), player.getZ(),
					SoundEvents.ENDER_PEARL_THROW, SoundSource.PLAYERS, 0.8f, 0.6f);
			level.playSound(null, target.getX(), target.getY(), target.getZ(),
					SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.7f, 1.4f);

			if (level instanceof ServerLevel serverLevel) {
				serverLevel.sendParticles(ParticleTypes.FLAME,
						target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ(),
						15, 0.3, 0.3, 0.3, 0.05);

				int steps = (int) (dist * 2);
				for (int i = 0; i < steps; i++) {
					double t = (double) i / steps;
					double px = targetPos.x + (playerPos.x - targetPos.x) * t;
					double py = targetPos.y + (playerPos.y - targetPos.y) * t + 0.5;
					double pz = targetPos.z + (playerPos.z - targetPos.z) * t;
					serverLevel.sendParticles(ParticleTypes.FLAME,
							px, py, pz, 1, 0.05, 0.05, 0.05, 0.01);
				}

				serverLevel.sendParticles(ParticleTypes.SMOKE,
						player.getX(), player.getY() + 1.0, player.getZ(),
						8, 0.2, 0.3, 0.2, 0.02);
			}

			player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
		}

		return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		super.initializeClient(consumer);
		consumer.accept(new IClientItemExtensions() {
			private final BlockEntityWithoutLevelRenderer renderer = new FlamePearlItemRenderer();

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return renderer;
			}
		});
	}

	private PlayState idlePredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			event.getController().setAnimation(RawAnimation.begin().thenLoop("idle"));
			return PlayState.CONTINUE;
		}
		return PlayState.STOP;
	}

	String prevAnim = "empty";

	private PlayState procedurePredicate(AnimationState event) {
		if (!this.animationprocedure.equals("empty") && event.getController().getAnimationState() == AnimationController.State.STOPPED || (!this.animationprocedure.equals(prevAnim) && !this.animationprocedure.equals("empty"))) {
			if (!this.animationprocedure.equals(prevAnim))
				event.getController().forceAnimationReset();
			event.getController().setAnimation(RawAnimation.begin().thenPlay(this.animationprocedure));
			if (event.getController().getAnimationState() == AnimationController.State.STOPPED) {
				this.animationprocedure = "empty";
				event.getController().forceAnimationReset();
			}
		} else if (this.animationprocedure.equals("empty")) {
			prevAnim = "empty";
			return PlayState.STOP;
		}
		prevAnim = this.animationprocedure;
		return PlayState.CONTINUE;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar data) {
		AnimationController procedureController = new AnimationController(this, "procedureController", 0, this::procedurePredicate);
		data.add(procedureController);
		AnimationController idleController = new AnimationController(this, "idleController", 0, this::idlePredicate);
		data.add(idleController);
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}
}
