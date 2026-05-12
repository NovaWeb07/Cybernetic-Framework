package net.novadev.item;

import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;

import net.novadev.network.CyberHeartNetworking;

public class CyberHeartItem extends Item {

    public static final String CYBER_HEARTS_KEY = "CyberHeartsCount";
    public static final int MAX_CYBER_HEARTS = 100;

    public CyberHeartItem() {
        super(new Item.Properties().stacksTo(64).rarity(Rarity.EPIC));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean isFoil(ItemStack itemstack) {
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide()) {
            CompoundTag data = player.getPersistentData();
            int cyberHearts = data.getInt(CYBER_HEARTS_KEY);

            if (cyberHearts >= MAX_CYBER_HEARTS) {
                player.sendSystemMessage(Component.literal("§bCyber Hearts§r §7are already maxed! §b(10/10)"));
                return InteractionResultHolder.fail(stack);
            }

            cyberHearts++;
            data.putInt(CYBER_HEARTS_KEY, cyberHearts);

            player.sendSystemMessage(Component.literal(
                    "§bCyber Heart §r§7implanted! §b(" + cyberHearts + "/" + MAX_CYBER_HEARTS + ")"));

            CyberHeartNetworking.syncToPlayer(player);

            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 0.55f, 1.5f);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.45f, 1.9f);

            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.HEART,
                        player.getX(), player.getY() + 2.1, player.getZ(),
                        10, 0.45, 0.2, 0.45, 0.04);

                for (int i = 0; i < 32; i++) {
                    double angle = (2.0 * Math.PI / 32.0) * i;
                    double r = 0.85;
                    double px = player.getX() + Math.cos(angle) * r;
                    double pz = player.getZ() + Math.sin(angle) * r;
                    serverLevel.sendParticles(ParticleTypes.END_ROD,
                            px, player.getY() + 0.9, pz,
                            1, 0.0, 0.06, 0.0, 0.015);
                }

                serverLevel.sendParticles(ParticleTypes.FALLING_OBSIDIAN_TEAR,
                        player.getX(), player.getY() + 2.6, player.getZ(),
                        18, 0.28, 0.1, 0.28, 0.012);
            }

            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    public static int getCyberHearts(Player player) {
        return player.getPersistentData().getInt(CYBER_HEARTS_KEY);
    }
}
