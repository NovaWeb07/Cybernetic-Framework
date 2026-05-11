package net.mcreator.roxdev.network;

import net.minecraftforge.network.PacketDistributor;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import net.mcreator.roxdev.RoxDevMod;
import net.mcreator.roxdev.item.CyberHeartItem;

public class CyberHeartNetworking {

    public static void syncToPlayer(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        int count = CyberHeartItem.getCyberHearts(player);
        RoxDevMod.PACKET_HANDLER.send(
                PacketDistributor.PLAYER.with(() -> serverPlayer),
                new SyncCyberHeartsPacket(count)
        );
    }
}
