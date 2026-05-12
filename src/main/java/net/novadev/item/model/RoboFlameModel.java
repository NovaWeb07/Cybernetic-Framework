package net.novadev.item.model;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.novadev.item.RoboFlameItem;

public class RoboFlameModel extends GeoModel<RoboFlameItem> {
	@Override
	public ResourceLocation getAnimationResource(RoboFlameItem object) {
		return new ResourceLocation("nova_dev", "animations/roboflame.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(RoboFlameItem object) {
		return new ResourceLocation("nova_dev", "geo/roboflame.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(RoboFlameItem object) {
		return new ResourceLocation("nova_dev", "textures/item/roboflame.png");
	}
}
