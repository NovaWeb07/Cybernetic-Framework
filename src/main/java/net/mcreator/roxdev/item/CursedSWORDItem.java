package net.mcreator.roxdev.item;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.SwordItem;
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

import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.mcreator.roxdev.entity.CursedBladeEntity;
import net.mcreator.roxdev.init.RoxDevModEntities;

public class CursedSWORDItem extends SwordItem {
	public CursedSWORDItem() {
		super(new Tier() {
			public int getUses() {
				return 100;
			}

			public float getSpeed() {
				return 4f;
			}

			public float getAttackDamageBonus() {
				return 0f;
			}

			public int getLevel() {
				return 0;
			}

			public int getEnchantmentValue() {
				return 2;
			}

			public Ingredient getRepairIngredient() {
				return Ingredient.of();
			}
		}, 3, -3f, new Item.Properties());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isFoil(ItemStack itemstack) {
		return true;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
			CursedBladeEntity blade = new CursedBladeEntity(RoxDevModEntities.CURSED_BLADE.get(), level);

			double spawnX = player.getX() + player.getLookAngle().x * 1.5;
			double spawnY = player.getY();
			double spawnZ = player.getZ() + player.getLookAngle().z * 1.5;
			blade.moveTo(spawnX, spawnY, spawnZ, player.getYRot(), 0);

			blade.setOwner(player);

			blade.setCustomName(Component.literal("\u00a74\u00a7l\u2694 Cursed Blade"));
			blade.setCustomNameVisible(true);

			serverLevel.addFreshEntity(blade);

			level.playSound(null, player.getX(), player.getY(), player.getZ(),
					SoundEvents.EVOKER_PREPARE_SUMMON, SoundSource.PLAYERS, 1.0f, 0.8f);
			level.playSound(null, spawnX, spawnY, spawnZ,
					SoundEvents.EVOKER_CAST_SPELL, SoundSource.PLAYERS, 0.8f, 1.2f);

			serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE,
					spawnX, spawnY + 1.0, spawnZ, 20, 0.3, 0.5, 0.3, 0.02);
			serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
					spawnX, spawnY + 0.5, spawnZ, 12, 0.4, 0.3, 0.4, 0.05);
			serverLevel.sendParticles(ParticleTypes.ENCHANT,
					spawnX, spawnY + 1.5, spawnZ, 25, 0.5, 0.5, 0.5, 0.2);

			player.getCooldowns().addCooldown(this, 100);

			player.sendSystemMessage(Component.literal("\u00a74\u00a7l\u2694 \u00a7r\u00a77A Cursed Blade has been summoned!"));
		}

		return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
	}
}
