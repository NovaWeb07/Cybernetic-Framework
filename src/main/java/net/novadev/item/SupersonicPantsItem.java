package net.novadev.item;

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
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.phys.Vec3;

import net.novadev.item.renderer.SupersonicPantsItemRenderer;
import net.novadev.init.NovaDevModItems;

import java.util.function.Consumer;

public class SupersonicPantsItem extends Item implements GeoItem {
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	public String animationprocedure = "empty";

	private static final int USE_DURATION = 72000;
	private static final double FLY_SPEED = 1.5;

	public SupersonicPantsItem() {
		super(new Item.Properties().stacksTo(1).durability(250).rarity(Rarity.EPIC));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (!level.isClientSide()) {
			boolean wearingLeggings = isBioPants(player.getItemBySlot(EquipmentSlot.LEGS));

			if (!wearingLeggings) {
				equipArmor(player, level);

				if (!player.getAbilities().instabuild) {
					stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
				}
				player.getCooldowns().addCooldown(this, 20);
				return InteractionResultHolder.success(stack);
			} else {
				player.startUsingItem(hand);

				Vec3 look = player.getLookAngle();
				player.setDeltaMovement(look.scale(1.2));
				player.hurtMarked = true;
				player.fallDistance = 0;

				level.playSound(null, player.getX(), player.getY(), player.getZ(),
						SoundEvents.TRIDENT_RIPTIDE_1, SoundSource.PLAYERS, 1.0f, 0.8f);

				return InteractionResultHolder.consume(stack);
			}
		}

		player.startUsingItem(hand);
		return InteractionResultHolder.consume(stack);
	}

	private void equipArmor(Player player, Level level) {
		ItemStack oldLegs = player.getItemBySlot(EquipmentSlot.LEGS);

		if (!oldLegs.isEmpty()) {
			player.drop(oldLegs.copy(), false);
		}

		player.setItemSlot(EquipmentSlot.LEGS, new ItemStack(NovaDevModItems.BIO_PANTS_LEGGINGS.get()));

		level.playSound(null, player.getX(), player.getY(), player.getZ(),
				SoundEvents.ARMOR_EQUIP_IRON, SoundSource.PLAYERS, 1.0f, 1.0f);

		if (level instanceof ServerLevel sl) {
			sl.sendParticles(ParticleTypes.SOUL,
					player.getX(), player.getY() + 0.5, player.getZ(),
					20, 0.4, 0.3, 0.4, 0.1);
		}

		player.displayClientMessage(
				Component.literal("§b⚡ Supersonic Pants equipped! §7Hold right-click to dash!"), true);
	}

	private boolean isBioPants(ItemStack stack) {
		return !stack.isEmpty() && stack.getItem() instanceof BioPantsItem;
	}

	@Override
	public void onUseTick(Level level, LivingEntity user, ItemStack stack, int remainingUseDuration) {
		if (!(user instanceof Player player)) return;

		if (!level.isClientSide()) {
			boolean wearingLeggings = isBioPants(player.getItemBySlot(EquipmentSlot.LEGS));

			if (!wearingLeggings) {
				player.stopUsingItem();
				return;
			}

			Vec3 look = player.getLookAngle();
			player.setDeltaMovement(look.scale(FLY_SPEED));
			player.hurtMarked = true;
			player.fallDistance = 0;

			player.setPose(Pose.FALL_FLYING);

			if (level instanceof ServerLevel sl) {
				Vec3 offset = player.getLookAngle().reverse().scale(0.5);
				double px = player.getX() + offset.x;
				double py = player.getY() + 0.5 + offset.y;
				double pz = player.getZ() + offset.z;

				sl.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
						px, py, pz,
						4, 0.1, 0.1, 0.1, 0.05);
				sl.sendParticles(ParticleTypes.FIREWORK,
						px, py, pz,
						2, 0.1, 0.1, 0.1, 0.05);
				sl.sendParticles(ParticleTypes.GLOW,
						px, py, pz,
						2, 0.1, 0.1, 0.1, 0.02);
			}

			if (player.tickCount % 20 == 0 && !player.getAbilities().instabuild) {
				stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(player.getUsedItemHand()));
			}

			if (player.tickCount % 10 == 0) {
				level.playSound(null, player.getX(), player.getY(), player.getZ(),
						SoundEvents.ELYTRA_FLYING, SoundSource.PLAYERS, 0.6f, 1.2f);
			}
		} else {
			player.setPose(Pose.FALL_FLYING);
		}
	}

	@Override
	public void releaseUsing(ItemStack stack, Level level, LivingEntity user, int timeChargeUp) {
		if (user instanceof Player player && !level.isClientSide()) {
			player.setPose(Pose.STANDING);
			player.getCooldowns().addCooldown(this, 10);
		}
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return USE_DURATION;
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.BOW;
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return false;
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		super.initializeClient(consumer);
		consumer.accept(new IClientItemExtensions() {
			private final BlockEntityWithoutLevelRenderer renderer = new SupersonicPantsItemRenderer();

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
