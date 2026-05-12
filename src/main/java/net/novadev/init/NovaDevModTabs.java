package net.novadev.init;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.core.registries.Registries;

import net.novadev.NovaDevMod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class NovaDevModTabs {
	public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, NovaDevMod.MODID);

	@SubscribeEvent
	public static void buildTabContentsVanilla(BuildCreativeModeTabContentsEvent tabData) {
		if (tabData.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
			tabData.accept(NovaDevModItems.FLAME_PEARL.get());
			tabData.accept(NovaDevModItems.CYBER_HEART.get());
			tabData.accept(NovaDevModItems.CURSED_SWORD.get());
			tabData.accept(NovaDevModItems.FLAME_SUSHI.get());
			tabData.accept(NovaDevModItems.HOVER_PAW.get());
			tabData.accept(NovaDevModItems.FLAME_HAMMER.get());
			tabData.accept(NovaDevModItems.HOLOGRAM_DECOY.get());
			tabData.accept(NovaDevModItems.FLAME_DRONE.get());
			tabData.accept(NovaDevModItems.FLAMDRONE_SPAWN_EGG.get());
			tabData.accept(NovaDevModItems.CANNON.get());
			tabData.accept(NovaDevModItems.DRILL.get());
			tabData.accept(NovaDevModItems.FLAMYY_SPAWN_EGG.get());
			tabData.accept(NovaDevModItems.FLAMY.get());
			tabData.accept(NovaDevModItems.ROBO_FLAME_HELMET.get());
			tabData.accept(NovaDevModItems.ROBO_FLAME_CHESTPLATE.get());
			tabData.accept(NovaDevModItems.ROBO_ARMOR.get());
			tabData.accept(NovaDevModItems.BIO_PANTS_CHESTPLATE.get());
			tabData.accept(NovaDevModItems.BIO_PANTS_LEGGINGS.get());
			tabData.accept(NovaDevModItems.SUPERSONIC_PANTS.get());
		}
	}
}
