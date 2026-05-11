package net.mcreator.roxdev.item.model;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.mcreator.roxdev.item.SupersonicPantsItem;

public class SupersonicPantsItemModel extends GeoModel<SupersonicPantsItem> {
	@Override
	public ResourceLocation getAnimationResource(SupersonicPantsItem animatable) {
		return new ResourceLocation("rox_dev", "animations/biopants.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(SupersonicPantsItem animatable) {
		return new ResourceLocation("rox_dev", "geo/biopants.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(SupersonicPantsItem animatable) {
		return new ResourceLocation("rox_dev", "textures/item/bio_pants.png");
	}
}
