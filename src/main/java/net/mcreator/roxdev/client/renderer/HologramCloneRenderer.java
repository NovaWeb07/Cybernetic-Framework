package net.mcreator.roxdev.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;

import net.mcreator.roxdev.entity.HologramCloneEntity;

import java.util.UUID;

public class HologramCloneRenderer extends LivingEntityRenderer<HologramCloneEntity, PlayerModel<HologramCloneEntity>> {

    public HologramCloneRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new PlayerModel<>(ctx.bakeLayer(ModelLayers.PLAYER), false), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(HologramCloneEntity entity) {
        UUID ownerUUID = entity.getOwnerUUID();
        if (ownerUUID != null) {
            var connection = Minecraft.getInstance().getConnection();
            if (connection != null) {
                var playerInfo = connection.getPlayerInfo(ownerUUID);
                if (playerInfo != null) {
                    return playerInfo.getSkinLocation();
                }
            }
        }
        return DefaultPlayerSkin.getDefaultSkin();
    }

    @Override
    public void render(HologramCloneEntity entity, float yaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource buffer, int light) {
        poseStack.pushPose();

        float bob = (float) Math.sin((entity.tickCount + partialTick) * 0.08) * 0.04f;
        poseStack.translate(0.0, bob, 0.0);

        super.render(entity, yaw, partialTick, poseStack, buffer, light);

        poseStack.popPose();
    }

    @Override
    protected boolean isBodyVisible(HologramCloneEntity entity) {
        return true;
    }

    @Override
    protected boolean shouldShowName(HologramCloneEntity entity) {
        return false;
    }
}
