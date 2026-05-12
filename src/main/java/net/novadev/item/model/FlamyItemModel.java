package net.novadev.item.model;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.novadev.item.FlamyItem;

public class FlamyItemModel extends GeoModel<FlamyItem> {
	@Override
	public ResourceLocation getAnimationResource(FlamyItem animatable) {
		return new ResourceLocation("nova_dev", "animations/proto.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(FlamyItem animatable) {
		return new ResourceLocation("nova_dev", "geo/proto.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(FlamyItem animatable) {
		return new ResourceLocation("nova_dev", "textures/item/proto.png");
	}
}
