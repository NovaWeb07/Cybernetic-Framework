package net.mcreator.roxdev.item.model;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.mcreator.roxdev.item.FlameHammerItem;

public class FlameHammerItemModel extends GeoModel<FlameHammerItem> {
	@Override
	public ResourceLocation getAnimationResource(FlameHammerItem animatable) {
		return new ResourceLocation("rox_dev", "animations/hammer.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(FlameHammerItem animatable) {
		return new ResourceLocation("rox_dev", "geo/hammer.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(FlameHammerItem animatable) {
		return new ResourceLocation("rox_dev", "textures/item/flamehammer.png");
	}
}
