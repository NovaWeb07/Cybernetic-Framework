package net.novadev.network;

import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.nbt.CompoundTag;

import net.novadev.item.CyberHeartItem;

import java.util.function.Supplier;

public class SyncCyberHeartsPacket {

    private final int cyberHearts;

    public SyncCyberHeartsPacket(int cyberHearts) {
        this.cyberHearts = cyberHearts;
    }

    public static void encode(SyncCyberHeartsPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.cyberHearts);
    }

    public static SyncCyberHeartsPacket decode(FriendlyByteBuf buf) {
        return new SyncCyberHeartsPacket(buf.readInt());
    }

    public static void handle(SyncCyberHeartsPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> handleOnClient(msg));
        ctx.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleOnClient(SyncCyberHeartsPacket msg) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        CompoundTag data = mc.player.getPersistentData();
        data.putInt(CyberHeartItem.CYBER_HEARTS_KEY, msg.cyberHearts);
    }
}
