package net.mcreator.roxdev.item.model;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.mcreator.roxdev.item.DrillItem;

public class DrillItemModel extends GeoModel<DrillItem> {
	@Override
	public ResourceLocation getAnimationResource(DrillItem animatable) {
		return new ResourceLocation("rox_dev", "animations/drill.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(DrillItem animatable) {
		return new ResourceLocation("rox_dev", "geo/drill.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(DrillItem animatable) {
		return new ResourceLocation("rox_dev", "textures/item/drill.png");
	}
}
