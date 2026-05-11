package net.mcreator.roxdev.init;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.mcreator.roxdev.client.renderer.FlamyyRenderer;
import net.mcreator.roxdev.client.renderer.FlamdroneRenderer;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class RoxDevModEntityRenderers {
	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(RoxDevModEntities.FLAMDRONE.get(), FlamdroneRenderer::new);
		event.registerEntityRenderer(RoxDevModEntities.FLAMYY.get(), FlamyyRenderer::new);
		event.registerEntityRenderer(RoxDevModEntities.CANNON_ICE_PROJECTILE.get(), net.mcreator.roxdev.client.renderer.CannonIceProjectileRenderer::new);
	}
}
