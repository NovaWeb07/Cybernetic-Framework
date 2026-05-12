package net.novadev.init;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.novadev.client.renderer.FlamyyRenderer;
import net.novadev.client.renderer.FlamdroneRenderer;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class NovaDevModEntityRenderers {
	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(NovaDevModEntities.FLAMDRONE.get(), FlamdroneRenderer::new);
		event.registerEntityRenderer(NovaDevModEntities.FLAMYY.get(), FlamyyRenderer::new);
		event.registerEntityRenderer(NovaDevModEntities.CANNON_ICE_PROJECTILE.get(), net.novadev.client.renderer.CannonIceProjectileRenderer::new);
	}
}
