package net.mcreator.roxdev.entity.model;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.mcreator.roxdev.entity.FlamyyEntity;

public class FlamyyModel extends GeoModel<FlamyyEntity> {
	@Override
	public ResourceLocation getAnimationResource(FlamyyEntity entity) {
		return new ResourceLocation("rox_dev", "animations/proto.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(FlamyyEntity entity) {
		return new ResourceLocation("rox_dev", "geo/proto.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(FlamyyEntity entity) {
		return new ResourceLocation("rox_dev", "textures/entities/" + entity.getTexture() + ".png");
	}

}
