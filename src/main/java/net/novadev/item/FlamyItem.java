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
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;

import net.novadev.item.renderer.FlamyItemRenderer;
import net.novadev.entity.FlamyyEntity;
import net.novadev.init.NovaDevModEntities;

import java.util.function.Consumer;

public class FlamyItem extends Item implements GeoItem {
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	public String animationprocedure = "empty";

	public FlamyItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
			FlamyyEntity flamy = new FlamyyEntity(NovaDevModEntities.FLAMYY.get(), level);
			double spawnX = player.getX() + player.getLookAngle().x * 1.5;
			double spawnY = player.getY() + 0.5;
			double spawnZ = player.getZ() + player.getLookAngle().z * 1.5;
			flamy.moveTo(spawnX, spawnY, spawnZ, player.getYRot(), 0);
			flamy.setPersistenceRequired();

			serverLevel.addFreshEntity(flamy);

			level.playSound(null, spawnX, spawnY, spawnZ,
					SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 0.8f, 1.2f);
			level.playSound(null, spawnX, spawnY, spawnZ,
					SoundEvents.BLAZE_AMBIENT, SoundSource.PLAYERS, 0.5f, 1.5f);

			serverLevel.sendParticles(ParticleTypes.FLAME,
					spawnX, spawnY + 0.5, spawnZ, 20, 0.4, 0.3, 0.4, 0.05);
			serverLevel.sendParticles(ParticleTypes.SMOKE,
					spawnX, spawnY + 1.0, spawnZ, 10, 0.3, 0.4, 0.3, 0.02);

			if (!player.getAbilities().instabuild) {
				stack.shrink(1);
			}

			player.sendSystemMessage(Component.literal("\u00a77A Flamy has been summoned! Use \u00a76Hover Paw\u00a77 to tame it."));
			player.getCooldowns().addCooldown(this, 40);
		}

		return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		super.initializeClient(consumer);
		consumer.accept(new IClientItemExtensions() {
			private final BlockEntityWithoutLevelRenderer renderer = new FlamyItemRenderer();

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
