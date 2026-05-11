package net.mcreator.roxdev.init;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.core.registries.Registries;

import net.mcreator.roxdev.RoxDevMod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class RoxDevModTabs {
	public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RoxDevMod.MODID);

	@SubscribeEvent
	public static void buildTabContentsVanilla(BuildCreativeModeTabContentsEvent tabData) {
		if (tabData.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
			tabData.accept(RoxDevModItems.FLAME_PEARL.get());
			tabData.accept(RoxDevModItems.CYBER_HEART.get());
			tabData.accept(RoxDevModItems.CURSED_SWORD.get());
			tabData.accept(RoxDevModItems.FLAME_SUSHI.get());
			tabData.accept(RoxDevModItems.HOVER_PAW.get());
			tabData.accept(RoxDevModItems.FLAME_HAMMER.get());
			tabData.accept(RoxDevModItems.HOLOGRAM_DECOY.get());
			tabData.accept(RoxDevModItems.FLAME_DRONE.get());
			tabData.accept(RoxDevModItems.FLAMDRONE_SPAWN_EGG.get());
			tabData.accept(RoxDevModItems.CANNON.get());
			tabData.accept(RoxDevModItems.DRILL.get());
			tabData.accept(RoxDevModItems.FLAMYY_SPAWN_EGG.get());
			tabData.accept(RoxDevModItems.FLAMY.get());
			tabData.accept(RoxDevModItems.ROBO_FLAME_HELMET.get());
			tabData.accept(RoxDevModItems.ROBO_FLAME_CHESTPLATE.get());
			tabData.accept(RoxDevModItems.ROBO_ARMOR.get());
			tabData.accept(RoxDevModItems.BIO_PANTS_CHESTPLATE.get());
			tabData.accept(RoxDevModItems.BIO_PANTS_LEGGINGS.get());
			tabData.accept(RoxDevModItems.SUPERSONIC_PANTS.get());
		}
	}
}
