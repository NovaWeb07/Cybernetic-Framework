package net.novadev.event;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ParticleTypes;

import net.novadev.NovaDevMod;
import net.novadev.item.CyberHeartItem;
import net.novadev.network.CyberHeartNetworking;

@Mod.EventBusSubscriber(modid = NovaDevMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CyberHeartDamageHandler {

    @SubscribeEvent
    public static void onPlayerHurt(LivingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        CompoundTag data = player.getPersistentData();
        int cyberHearts = data.getInt(CyberHeartItem.CYBER_HEARTS_KEY);
        if (cyberHearts <= 0) return;

        float damage = event.getAmount();

        float cyberHp = cyberHearts * 2.0f;

        if (damage <= cyberHp) {
            int heartsRemoved = (int) Math.ceil(damage / 2.0f);
            int newCyberHearts = Math.max(0, cyberHearts - heartsRemoved);
            data.putInt(CyberHeartItem.CYBER_HEARTS_KEY, newCyberHearts);
            event.setCanceled(true);

            CyberHeartNetworking.syncToPlayer(player);

            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.PLAYERS, 0.7f, 1.6f);

            if (player.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.END_ROD,
                        player.getX(), player.getY() + 1.0, player.getZ(),
                        14, 0.4, 0.5, 0.4, 0.09);
            }

        } else {
            float remainingDamage = damage - cyberHp;
            data.putInt(CyberHeartItem.CYBER_HEARTS_KEY, 0);
            event.setAmount(remainingDamage);

            CyberHeartNetworking.syncToPlayer(player);

            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.AMETHYST_CLUSTER_BREAK, SoundSource.PLAYERS, 1.0f, 0.7f);

            if (player.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.EXPLOSION,
                        player.getX(), player.getY() + 1.0, player.getZ(),
                        3, 0.25, 0.3, 0.25, 0.0);
                serverLevel.sendParticles(ParticleTypes.END_ROD,
                        player.getX(), player.getY() + 1.0, player.getZ(),
                        24, 0.55, 0.65, 0.55, 0.12);
            }
        }
    }
}
