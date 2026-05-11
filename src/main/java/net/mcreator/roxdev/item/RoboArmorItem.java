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
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;

import net.mcreator.roxdev.item.renderer.RoboArmorItemRenderer;
import net.mcreator.roxdev.init.RoxDevModItems;

import java.util.function.Consumer;

public class RoboArmorItem extends Item implements GeoItem {
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	public String animationprocedure = "empty";

	private static final int USE_DURATION = 72000;
	private static final double BOOST_SPEED = 0.6;

	public RoboArmorItem() {
		super(new Item.Properties().stacksTo(1).durability(250).rarity(Rarity.EPIC));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (!level.isClientSide()) {
			boolean wearingHelmet = isRoboFlameArmor(player.getItemBySlot(EquipmentSlot.HEAD));
			boolean wearingChest = isRoboFlameArmor(player.getItemBySlot(EquipmentSlot.CHEST));

			if (!wearingHelmet || !wearingChest) {
				equipArmor(player, level);

				if (!player.getAbilities().instabuild) {
					stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
				}
				player.getCooldowns().addCooldown(this, 20);
				return InteractionResultHolder.success(stack);
			} else {
				player.startUsingItem(hand);

				player.setDeltaMovement(player.getDeltaMovement().add(0, 1.0, 0));
				player.hurtMarked = true;
				player.fallDistance = 0;

				level.playSound(null, player.getX(), player.getY(), player.getZ(),
						SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.PLAYERS, 0.8f, 1.2f);

				if (level instanceof ServerLevel sl) {
					sl.sendParticles(ParticleTypes.FLAME,
							player.getX(), player.getY(), player.getZ(),
							20, 0.3, 0.1, 0.3, 0.12);
					sl.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
							player.getX(), player.getY(), player.getZ(),
							10, 0.2, 0.05, 0.2, 0.08);
				}

				return InteractionResultHolder.consume(stack);
			}
		}

		player.startUsingItem(hand);
		return InteractionResultHolder.consume(stack);
	}

	private void equipArmor(Player player, Level level) {
		ItemStack oldHelmet = player.getItemBySlot(EquipmentSlot.HEAD);
		ItemStack oldChest = player.getItemBySlot(EquipmentSlot.CHEST);

		if (!oldHelmet.isEmpty()) {
			player.drop(oldHelmet.copy(), false);
		}
		if (!oldChest.isEmpty()) {
			player.drop(oldChest.copy(), false);
		}

		player.setItemSlot(EquipmentSlot.HEAD,
				new ItemStack(RoxDevModItems.ROBO_FLAME_HELMET.get()));
		player.setItemSlot(EquipmentSlot.CHEST,
				new ItemStack(RoxDevModItems.ROBO_FLAME_CHESTPLATE.get()));

		level.playSound(null, player.getX(), player.getY(), player.getZ(),
				SoundEvents.ARMOR_EQUIP_IRON, SoundSource.PLAYERS, 1.0f, 1.0f);
		level.playSound(null, player.getX(), player.getY(), player.getZ(),
				SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 0.6f, 1.8f);

		if (level instanceof ServerLevel sl) {
			sl.sendParticles(ParticleTypes.ELECTRIC_SPARK,
					player.getX(), player.getY() + 1.0, player.getZ(),
					25, 0.4, 0.6, 0.4, 0.15);
			sl.sendParticles(ParticleTypes.END_ROD,
					player.getX(), player.getY() + 1.0, player.getZ(),
					15, 0.3, 0.5, 0.3, 0.05);
		}

		player.displayClientMessage(
				Component.literal("§b⚙ Robo Flame Armor equipped! §7Hold right-click to fly!"), true);
	}

	private boolean isRoboFlameArmor(ItemStack stack) {
		return !stack.isEmpty() && stack.getItem() instanceof RoboFlameItem;
	}

	@Override
	public void onUseTick(Level level, LivingEntity user, ItemStack stack, int remainingUseDuration) {
		if (!(user instanceof Player player)) return;

		if (!level.isClientSide()) {
			boolean wearingHelmet = isRoboFlameArmor(player.getItemBySlot(EquipmentSlot.HEAD));
			boolean wearingChest = isRoboFlameArmor(player.getItemBySlot(EquipmentSlot.CHEST));

			if (!wearingHelmet && !wearingChest) {
				player.stopUsingItem();
				return;
			}

			var motion = player.getDeltaMovement();
			double newY = Math.min(motion.y + 0.12, BOOST_SPEED);
			player.setDeltaMovement(motion.x * 0.98, newY, motion.z * 0.98);
			player.hurtMarked = true;
			player.fallDistance = 0;

			if (player.tickCount % 2 == 0 && level instanceof ServerLevel sl) {
				double lx = player.getX() + Math.cos(Math.toRadians(player.getYRot() + 90)) * 0.3;
				double lz = player.getZ() + Math.sin(Math.toRadians(player.getYRot() + 90)) * 0.3;
				double rx = player.getX() + Math.cos(Math.toRadians(player.getYRot() - 90)) * 0.3;
				double rz = player.getZ() + Math.sin(Math.toRadians(player.getYRot() - 90)) * 0.3;

				sl.sendParticles(ParticleTypes.FLAME,
						lx, player.getY() - 0.3, lz,
						3, 0.05, 0.0, 0.05, 0.03);
				sl.sendParticles(ParticleTypes.FLAME,
						rx, player.getY() - 0.3, rz,
						3, 0.05, 0.0, 0.05, 0.03);

				sl.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
						player.getX(), player.getY() - 0.4, player.getZ(),
						2, 0.1, 0.0, 0.1, 0.02);

				sl.sendParticles(ParticleTypes.SMOKE,
						player.getX(), player.getY() - 0.5, player.getZ(),
						2, 0.15, 0.0, 0.15, 0.01);
			}

			if (player.tickCount % 6 == 0 && level instanceof ServerLevel sl) {
				sl.sendParticles(ParticleTypes.ELECTRIC_SPARK,
						player.getX(), player.getY() - 0.2, player.getZ(),
						3, 0.2, 0.1, 0.2, 0.08);
			}

			if (player.tickCount % 20 == 0 && !player.getAbilities().instabuild) {
				stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(player.getUsedItemHand()));
			}

			if (player.tickCount % 8 == 0) {
				level.playSound(null, player.getX(), player.getY(), player.getZ(),
						SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 0.3f, 1.8f);
			}
		}
	}

	@Override
	public void releaseUsing(ItemStack stack, Level level, LivingEntity user, int timeChargeUp) {
		if (user instanceof Player player && !level.isClientSide()) {
			player.getCooldowns().addCooldown(this, 10);

			if (level instanceof ServerLevel sl) {
				sl.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
						player.getX(), player.getY(), player.getZ(),
						6, 0.3, 0.1, 0.3, 0.02);
			}
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
			private final BlockEntityWithoutLevelRenderer renderer = new RoboArmorItemRenderer();

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
