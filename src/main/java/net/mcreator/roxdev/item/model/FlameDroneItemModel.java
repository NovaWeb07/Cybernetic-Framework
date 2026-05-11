package net.mcreator.roxdev.item.model;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.mcreator.roxdev.item.FlameDroneItem;

public class FlameDroneItemModel extends GeoModel<FlameDroneItem> {
	@Override
	public ResourceLocation getAnimationResource(FlameDroneItem animatable) {
		return new ResourceLocation("rox_dev", "animations/flamedrone.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(FlameDroneItem animatable) {
		return new ResourceLocation("rox_dev", "geo/flamedrone.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(FlameDroneItem animatable) {
		return new ResourceLocation("rox_dev", "textures/item/flamedrone.png");
	}
}
