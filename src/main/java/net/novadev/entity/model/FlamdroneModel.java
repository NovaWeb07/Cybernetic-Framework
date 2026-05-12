package net.novadev.entity.model;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.novadev.entity.FlamdroneEntity;

public class FlamdroneModel extends GeoModel<FlamdroneEntity> {
	@Override
	public ResourceLocation getAnimationResource(FlamdroneEntity entity) {
		return new ResourceLocation("nova_dev", "animations/flamedrone.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(FlamdroneEntity entity) {
		return new ResourceLocation("nova_dev", "geo/flamedrone.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(FlamdroneEntity entity) {
		return new ResourceLocation("nova_dev", "textures/entities/" + entity.getTexture() + ".png");
	}

}
