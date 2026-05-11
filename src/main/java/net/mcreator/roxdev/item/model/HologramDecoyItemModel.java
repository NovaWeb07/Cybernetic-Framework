package net.mcreator.roxdev.item.model;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.mcreator.roxdev.item.HologramDecoyItem;

public class HologramDecoyItemModel extends GeoModel<HologramDecoyItem> {
	@Override
	public ResourceLocation getAnimationResource(HologramDecoyItem animatable) {
		return new ResourceLocation("rox_dev", "animations/hologramdecoy.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(HologramDecoyItem animatable) {
		return new ResourceLocation("rox_dev", "geo/hologramdecoy.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(HologramDecoyItem animatable) {
		return new ResourceLocation("rox_dev", "textures/item/hologram_decoy.png");
	}
}
