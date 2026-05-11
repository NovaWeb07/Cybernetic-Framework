package net.mcreator.roxdev.item.model;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.mcreator.roxdev.item.FlamePearlItem;

public class FlamePearlItemModel extends GeoModel<FlamePearlItem> {
	@Override
	public ResourceLocation getAnimationResource(FlamePearlItem animatable) {
		return new ResourceLocation("rox_dev", "animations/cybernetic_recall_pearl_-_converted.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(FlamePearlItem animatable) {
		return new ResourceLocation("rox_dev", "geo/cybernetic_recall_pearl_-_converted.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(FlamePearlItem animatable) {
		return new ResourceLocation("rox_dev", "textures/item/cyber_pearl.png");
	}
}
