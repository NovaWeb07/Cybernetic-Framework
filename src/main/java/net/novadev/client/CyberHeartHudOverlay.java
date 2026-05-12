package net.novadev.client;

import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import net.novadev.NovaDevMod;
import net.novadev.item.CyberHeartItem;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = NovaDevMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class CyberHeartHudOverlay {

    private static final ResourceLocation CYBER_HEART_TEX =
            new ResourceLocation(NovaDevMod.MODID, "textures/gui/cyber_heart.png");

    private static final int SPRITE = 9;
    private static final int HEARTS_PER_ROW = 10;
    private static final int H_SPACING = 8;
    private static final int ROW_HEIGHT = 10;

    @SubscribeEvent
    public static void onRenderGuiOverlayPost(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay() != VanillaGuiOverlay.PLAYER_HEALTH.type()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;

        Player player = mc.player;
        int cyberHearts = CyberHeartItem.getCyberHearts(player);
        if (cyberHearts <= 0) return;

        GuiGraphics gfx = event.getGuiGraphics();
        int screenWidth  = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        int maxHealthHearts = Mth.ceil(player.getMaxHealth() / 2.0f);
        int healthRows = Mth.ceil(maxHealthHearts / 10.0f);

        float absorption = player.getAbsorptionAmount();
        int absHearts = Mth.ceil(absorption / 2.0f);
        int absRows = absHearts > 0 ? Mth.ceil(absHearts / 10.0f) : 0;

        int totalVanillaRows = healthRows + absRows;

        int barLeft = screenWidth / 2 - 91;
        int baseRowY = screenHeight - 39 - totalVanillaRows * ROW_HEIGHT;

        int count = Math.min(cyberHearts, CyberHeartItem.MAX_CYBER_HEARTS);

        gfx.setColor(1f, 1f, 1f, 1f);

        for (int i = 0; i < count; i++) {
            int col = i % HEARTS_PER_ROW;
            int row = i / HEARTS_PER_ROW;

            int x = barLeft + col * H_SPACING;
            int y = baseRowY - row * ROW_HEIGHT;

            gfx.blit(CYBER_HEART_TEX, x, y, 0, 0, SPRITE, SPRITE, SPRITE, SPRITE);
        }

        gfx.setColor(1f, 1f, 1f, 1f);
    }
}
