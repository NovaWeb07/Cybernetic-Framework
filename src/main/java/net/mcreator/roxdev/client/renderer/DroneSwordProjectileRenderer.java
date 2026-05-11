package net.mcreator.roxdev.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import net.mcreator.roxdev.entity.DroneSwordProjectile;

public class DroneSwordProjectileRenderer extends EntityRenderer<DroneSwordProjectile> {

    private static final ItemStack SWORD_STACK = new ItemStack(Items.IRON_SWORD);

    public DroneSwordProjectileRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(DroneSwordProjectile entity, float yaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource buffer, int light) {
        poseStack.pushPose();

        float spin = (entity.tickCount + partialTick) * 30.0f;
        poseStack.mulPose(Axis.YP.rotationDegrees(spin));
        poseStack.mulPose(Axis.ZP.rotationDegrees(45.0f));

        poseStack.scale(1.5f, 1.5f, 1.5f);

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        itemRenderer.renderStatic(SWORD_STACK, ItemDisplayContext.FIXED,
                light, OverlayTexture.NO_OVERLAY, poseStack, buffer, entity.level(), entity.getId());

        poseStack.popPose();

        super.render(entity, yaw, partialTick, poseStack, buffer, light);
    }

    @Override
    public ResourceLocation getTextureLocation(DroneSwordProjectile entity) {
        return new ResourceLocation("minecraft", "textures/item/iron_sword.png");
    }
}
