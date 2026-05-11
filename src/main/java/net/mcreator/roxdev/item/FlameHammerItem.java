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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;

import net.mcreator.roxdev.item.renderer.FlameHammerItemRenderer;

import java.util.function.Consumer;

public class FlameHammerItem extends Item implements GeoItem {
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	public String animationprocedure = "empty";

	private static final int USE_DURATION = 72000;
	private static final double BOOST_SPEED = 0.55;
	private static final int COOLDOWN_TICKS = 60;
	private static final int SHOCK_DURATION_TICKS = 200;
	private static final float SHOCK_DAMAGE_PER_TICK = 0.5f;
	private static final int SHOCK_INTERVAL = 4;

	public FlameHammerItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		player.startUsingItem(hand);

		if (!level.isClientSide()) {
			player.setDeltaMovement(player.getDeltaMovement().add(0, 1.2, 0));
			player.hurtMarked = true;
			player.fallDistance = 0;

			level.playSound(null, player.getX(), player.getY(), player.getZ(),
					SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.PLAYERS, 1.0f, 0.8f);

			if (level instanceof ServerLevel sl) {
				sl.sendParticles(ParticleTypes.FLAME,
						player.getX(), player.getY(), player.getZ(),
						25, 0.3, 0.1, 0.3, 0.15);
				sl.sendParticles(ParticleTypes.LAVA,
						player.getX(), player.getY(), player.getZ(),
						10, 0.2, 0.05, 0.2, 0.0);
			}
		}

		return InteractionResultHolder.consume(stack);
	}

	@Override
	public void onUseTick(Level level, LivingEntity user, ItemStack stack, int remainingUseDuration) {
		if (!(user instanceof Player player)) return;

		if (!level.isClientSide()) {
			var motion = player.getDeltaMovement();
			double newY = Math.min(motion.y + 0.15, BOOST_SPEED);
			player.setDeltaMovement(motion.x * 0.98, newY, motion.z * 0.98);
			player.hurtMarked = true;
			player.fallDistance = 0;

			if (player.tickCount % 2 == 0 && level instanceof ServerLevel sl) {
				sl.sendParticles(ParticleTypes.FLAME,
						player.getX(), player.getY() - 0.5, player.getZ(),
						4, 0.15, 0.0, 0.15, 0.02);
				sl.sendParticles(ParticleTypes.SMOKE,
						player.getX(), player.getY() - 0.3, player.getZ(),
						2, 0.1, 0.0, 0.1, 0.01);
			}

			if (player.tickCount % 10 == 0) {
				level.playSound(null, player.getX(), player.getY(), player.getZ(),
						SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 0.4f, 1.5f);
			}
		}
	}

	@Override
	public void releaseUsing(ItemStack stack, Level level, LivingEntity user, int timeChargeUp) {
		if (user instanceof Player player && !level.isClientSide()) {
			player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);

			if (level instanceof ServerLevel sl) {
				sl.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
						player.getX(), player.getY(), player.getZ(),
						8, 0.4, 0.1, 0.4, 0.02);
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
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		if (!attacker.level().isClientSide() && attacker instanceof Player player) {
			target.getPersistentData().putInt("FlameHammerShockTicks", SHOCK_DURATION_TICKS);
			target.getPersistentData().putInt("FlameHammerShockTimer", 0);

			target.getPersistentData().putUUID("FlameHammerShockAttacker", player.getUUID());

			if (attacker.level() instanceof ServerLevel sl) {
				sl.sendParticles(ParticleTypes.ELECTRIC_SPARK,
						target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ(),
						20, 0.4, 0.5, 0.4, 0.1);
				sl.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
						target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ(),
						10, 0.3, 0.4, 0.3, 0.05);
			}

			attacker.level().playSound(null, target.getX(), target.getY(), target.getZ(),
					SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.PLAYERS, 0.8f, 1.8f);
			attacker.level().playSound(null, target.getX(), target.getY(), target.getZ(),
					SoundEvents.TRIDENT_THUNDER, SoundSource.PLAYERS, 0.3f, 2.0f);

			player.displayClientMessage(Component.literal("§e⚡ Electric Shock applied for 10 seconds!"), true);
		}

		return true;
	}

	@Mod.EventBusSubscriber(modid = "rox_dev")
	public static class ElectricShockHandler {

		@SubscribeEvent
		public static void onLivingTick(LivingEvent.LivingTickEvent event) {
			LivingEntity entity = event.getEntity();
			if (entity.level().isClientSide()) return;

			var data = entity.getPersistentData();
			if (!data.contains("FlameHammerShockTicks")) return;

			int remaining = data.getInt("FlameHammerShockTicks");
			if (remaining <= 0) {
				data.remove("FlameHammerShockTicks");
				data.remove("FlameHammerShockTimer");
				data.remove("FlameHammerShockAttacker");
				return;
			}

			data.putInt("FlameHammerShockTicks", remaining - 1);
			int timer = data.getInt("FlameHammerShockTimer") + 1;
			data.putInt("FlameHammerShockTimer", timer);

			if (timer % 4 == 0 && entity.level() instanceof ServerLevel sl) {
				sl.sendParticles(ParticleTypes.ELECTRIC_SPARK,
						entity.getX(), entity.getY() + entity.getBbHeight() * 0.5, entity.getZ(),
						5, 0.3, 0.4, 0.3, 0.08);
			}

			if (timer % 8 == 0 && entity.level() instanceof ServerLevel sl) {
				sl.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
						entity.getX(), entity.getY() + entity.getBbHeight() * 0.7, entity.getZ(),
						3, 0.2, 0.3, 0.2, 0.02);
			}

			if (timer % SHOCK_INTERVAL == 0) {
				DamageSource source;
				if (data.hasUUID("FlameHammerShockAttacker")) {
					java.util.UUID attackerUUID = data.getUUID("FlameHammerShockAttacker");
					Player attacker = entity.level().getPlayerByUUID(attackerUUID);
					if (attacker != null) {
						source = entity.damageSources().playerAttack(attacker);
					} else {
						source = entity.damageSources().magic();
					}
				} else {
					source = entity.damageSources().magic();
				}

				entity.hurt(source, SHOCK_DAMAGE_PER_TICK);

				entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
						SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.HOSTILE, 0.3f, 2.0f);
			}
		}
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return false;
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		super.initializeClient(consumer);
		consumer.accept(new IClientItemExtensions() {
			private final BlockEntityWithoutLevelRenderer renderer = new FlameHammerItemRenderer();

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
