package net.novadev.item.model;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.novadev.item.RoboArmorItem;

public class RoboArmorItemModel extends GeoModel<RoboArmorItem> {
	@Override
	public ResourceLocation getAnimationResource(RoboArmorItem animatable) {
		return new ResourceLocation("nova_dev", "animations/roboflame.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(RoboArmorItem animatable) {
		return new ResourceLocation("nova_dev", "geo/roboflame.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(RoboArmorItem animatable) {
		return new ResourceLocation("nova_dev", "textures/item/roboflame.png");
	}
}
