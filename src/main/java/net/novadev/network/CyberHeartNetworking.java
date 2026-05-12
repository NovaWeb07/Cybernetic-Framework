package net.novadev.network;

import net.minecraftforge.network.PacketDistributor;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import net.novadev.NovaDevMod;
import net.novadev.item.CyberHeartItem;

public class CyberHeartNetworking {

    public static void syncToPlayer(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        int count = CyberHeartItem.getCyberHearts(player);
        NovaDevMod.PACKET_HANDLER.send(
                PacketDistributor.PLAYER.with(() -> serverPlayer),
                new SyncCyberHeartsPacket(count)
        );
    }
}
