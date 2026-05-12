package net.novadev.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;

import net.novadev.entity.FlamyyEntity;

public class HoverPawItem extends Item {
	public HoverPawItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, net.minecraft.world.InteractionHand hand) {
		if (player.level().isClientSide()) return InteractionResult.SUCCESS;

		if (!(target instanceof FlamyyEntity flamy)) {
			player.sendSystemMessage(Component.literal("§cThis only works on Flamy mobs!"));
			return InteractionResult.FAIL;
		}

		if (flamy.isTame() && flamy.isOwnedBy(player)) {
			player.sendSystemMessage(Component.literal("§6This Flamy is already your pet!"));
			return InteractionResult.FAIL;
		}

		if (flamy.isTame()) {
			player.sendSystemMessage(Component.literal("§cThis Flamy belongs to another player!"));
			return InteractionResult.FAIL;
		}

		flamy.tame(player);
		flamy.setOrderedToSit(false);

		flamy.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH).setBaseValue(40.0);
		flamy.setHealth(40.0f);
		flamy.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE).setBaseValue(8.0);
		flamy.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED).setBaseValue(0.4);
		flamy.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.FLYING_SPEED).setBaseValue(0.5);

		flamy.setCustomName(Component.literal("§6" + player.getName().getString() + "'s Flamy"));
		flamy.setCustomNameVisible(true);

		player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
				SoundEvents.WOLF_HOWL, SoundSource.PLAYERS, 1.0f, 1.2f);
		player.level().playSound(null, flamy.getX(), flamy.getY(), flamy.getZ(),
				SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.7f, 1.5f);

		if (player.level() instanceof ServerLevel serverLevel) {
			serverLevel.sendParticles(ParticleTypes.HEART,
					flamy.getX(), flamy.getY() + 1.5, flamy.getZ(),
					12, 0.5, 0.5, 0.5, 0.05);
			serverLevel.sendParticles(ParticleTypes.FLAME,
					flamy.getX(), flamy.getY() + 0.5, flamy.getZ(),
					20, 0.4, 0.4, 0.4, 0.05);
			serverLevel.sendParticles(ParticleTypes.TOTEM_OF_UNDYING,
					flamy.getX(), flamy.getY() + 1.0, flamy.getZ(),
					15, 0.3, 0.5, 0.3, 0.2);
		}

		player.sendSystemMessage(Component.literal("§7Flamy has been tamed! It will follow you and fight your enemies!"));

		return InteractionResult.SUCCESS;
	}
}
