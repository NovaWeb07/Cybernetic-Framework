package net.novadev.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;

import net.novadev.entity.CursedBladeEntity;

public class CursedBladeRenderer extends EntityRenderer<CursedBladeEntity> {

    private static final ItemStack SWORD_STACK;

    static {
        SWORD_STACK = new ItemStack(Items.DIAMOND_SWORD);
        SWORD_STACK.enchant(Enchantments.SHARPNESS, 5);
    }

    public CursedBladeRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.3f;
    }

    @Override
    public void render(CursedBladeEntity entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        poseStack.translate(0, 1.0, 0);

        float bob = (float) Math.sin((entity.tickCount + partialTicks) * 0.12) * 0.06f;
        poseStack.translate(0, bob, 0);

        poseStack.mulPose(Axis.YP.rotationDegrees(180 - entityYaw));

        poseStack.scale(3.5f, 3.5f, 3.5f);

        Minecraft.getInstance().getItemRenderer().renderStatic(
                SWORD_STACK, ItemDisplayContext.FIXED,
                packedLight, OverlayTexture.NO_OVERLAY,
                poseStack, buffer, entity.level(), entity.getId());

        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(CursedBladeEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
