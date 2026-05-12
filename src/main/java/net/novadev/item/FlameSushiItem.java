package net.novadev.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;

public class FlameSushiItem extends Item {
	public FlameSushiItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.EPIC)
				.food((new FoodProperties.Builder())
						.nutrition(10).saturationMod(1.2f).alwaysEat().meat().build()));
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
		ItemStack result = super.finishUsingItem(stack, level, entity);

		if (!level.isClientSide() && entity instanceof Player player) {
			int duration = 3600;

			player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, duration, 2, false, true));
			player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, duration, 0, false, true));
			player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, duration, 2, false, true));
			player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, duration, 12, false, true));

			level.playSound(null, player.getX(), player.getY(), player.getZ(),
					SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 0.8f, 1.0f);
			level.playSound(null, player.getX(), player.getY(), player.getZ(),
					SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.6f, 1.3f);

			if (player instanceof ServerPlayer serverPlayer) {
				serverPlayer.connection.send(
						new ClientboundGameEventPacket(ClientboundGameEventPacket.GUARDIAN_ELDER_EFFECT, 0));
			}

			if (level instanceof ServerLevel serverLevel) {
				for (int i = 0; i < 24; i++) {
					double angle = (2.0 * Math.PI / 24.0) * i;
					double px = player.getX() + Math.cos(angle) * 1.0;
					double pz = player.getZ() + Math.sin(angle) * 1.0;
					serverLevel.sendParticles(ParticleTypes.FLAME,
							px, player.getY() + 0.5, pz, 1, 0, 0.1, 0, 0.02);
				}
				serverLevel.sendParticles(ParticleTypes.TOTEM_OF_UNDYING,
						player.getX(), player.getY() + 1.0, player.getZ(),
						40, 0.5, 0.8, 0.5, 0.3);
				serverLevel.sendParticles(ParticleTypes.HEART,
						player.getX(), player.getY() + 2.0, player.getZ(),
						10, 0.4, 0.3, 0.4, 0.05);
			}
		}

		return result;
	}
}
