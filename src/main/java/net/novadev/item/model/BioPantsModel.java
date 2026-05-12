package net.novadev.item.model;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.novadev.item.BioPantsItem;

public class BioPantsModel extends GeoModel<BioPantsItem> {
	@Override
	public ResourceLocation getAnimationResource(BioPantsItem object) {
		return new ResourceLocation("nova_dev", "animations/biopants.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(BioPantsItem object) {
		return new ResourceLocation("nova_dev", "geo/biopants.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(BioPantsItem object) {
		return new ResourceLocation("nova_dev", "textures/item/bio_pants.png");
	}
}
