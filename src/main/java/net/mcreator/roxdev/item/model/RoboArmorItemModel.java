package net.mcreator.roxdev.item.model;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.mcreator.roxdev.item.RoboArmorItem;

public class RoboArmorItemModel extends GeoModel<RoboArmorItem> {
	@Override
	public ResourceLocation getAnimationResource(RoboArmorItem animatable) {
		return new ResourceLocation("rox_dev", "animations/roboflame.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(RoboArmorItem animatable) {
		return new ResourceLocation("rox_dev", "geo/roboflame.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(RoboArmorItem animatable) {
		return new ResourceLocation("rox_dev", "textures/item/roboflame.png");
	}
}
