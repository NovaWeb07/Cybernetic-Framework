package net.mcreator.roxdev.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.common.ForgeSpawnEggItem;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ArmorItem;

import net.mcreator.roxdev.item.SupersonicPantsItem;
import net.mcreator.roxdev.item.RoboFlameItem;
import net.mcreator.roxdev.item.RoboArmorItem;
import net.mcreator.roxdev.item.HoverPawItem;
import net.mcreator.roxdev.item.HologramDecoyItem;
import net.mcreator.roxdev.item.FlamyItem;
import net.mcreator.roxdev.item.FlameSushiItem;
import net.mcreator.roxdev.item.FlamePearlItem;
import net.mcreator.roxdev.item.FlameHammerItem;
import net.mcreator.roxdev.item.FlameDroneItem;
import net.mcreator.roxdev.item.DrillItem;
import net.mcreator.roxdev.item.CyberHeartItem;
import net.mcreator.roxdev.item.CursedSWORDItem;
import net.mcreator.roxdev.item.CannonItem;
import net.mcreator.roxdev.item.BioPantsItem;
import net.mcreator.roxdev.RoxDevMod;

public class RoxDevModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, RoxDevMod.MODID);
	public static final RegistryObject<Item> FLAME_PEARL = REGISTRY.register("flame_pearl", () -> new FlamePearlItem());
	public static final RegistryObject<Item> CYBER_HEART = REGISTRY.register("cyber_heart", () -> new CyberHeartItem());
	public static final RegistryObject<Item> CURSED_SWORD = REGISTRY.register("cursed_sword", () -> new CursedSWORDItem());
	public static final RegistryObject<Item> FLAME_SUSHI = REGISTRY.register("flame_sushi", () -> new FlameSushiItem());
	public static final RegistryObject<Item> HOVER_PAW = REGISTRY.register("hover_paw", () -> new HoverPawItem());
	public static final RegistryObject<Item> FLAME_HAMMER = REGISTRY.register("flame_hammer", () -> new FlameHammerItem());
	public static final RegistryObject<Item> HOLOGRAM_DECOY = REGISTRY.register("hologram_decoy", () -> new HologramDecoyItem());
	public static final RegistryObject<Item> FLAME_DRONE = REGISTRY.register("flame_drone", () -> new FlameDroneItem());
	public static final RegistryObject<Item> FLAMDRONE_SPAWN_EGG = REGISTRY.register("flamdrone_spawn_egg", () -> new ForgeSpawnEggItem(RoxDevModEntities.FLAMDRONE, -1, -1, new Item.Properties()));
	public static final RegistryObject<Item> CANNON = REGISTRY.register("cannon", () -> new CannonItem());
	public static final RegistryObject<Item> DRILL = REGISTRY.register("drill", () -> new DrillItem());
	public static final RegistryObject<Item> FLAMYY_SPAWN_EGG = REGISTRY.register("flamyy_spawn_egg", () -> new ForgeSpawnEggItem(RoxDevModEntities.FLAMYY, -1, -1, new Item.Properties()));
	public static final RegistryObject<Item> FLAMY = REGISTRY.register("flamy", () -> new FlamyItem());
	public static final RegistryObject<RoboFlameItem> ROBO_FLAME_HELMET = REGISTRY.register("robo_flame_helmet", () -> new RoboFlameItem(ArmorItem.Type.HELMET, new Item.Properties()));
	public static final RegistryObject<RoboFlameItem> ROBO_FLAME_CHESTPLATE = REGISTRY.register("robo_flame_chestplate", () -> new RoboFlameItem(ArmorItem.Type.CHESTPLATE, new Item.Properties()));
	public static final RegistryObject<Item> ROBO_ARMOR = REGISTRY.register("robo_armor", () -> new RoboArmorItem());
	public static final RegistryObject<BioPantsItem> BIO_PANTS_CHESTPLATE = REGISTRY.register("bio_pants_chestplate", () -> new BioPantsItem(ArmorItem.Type.CHESTPLATE, new Item.Properties()));
	public static final RegistryObject<BioPantsItem> BIO_PANTS_LEGGINGS = REGISTRY.register("bio_pants_leggings", () -> new BioPantsItem(ArmorItem.Type.LEGGINGS, new Item.Properties()));
	public static final RegistryObject<Item> SUPERSONIC_PANTS = REGISTRY.register("supersonic_pants", () -> new SupersonicPantsItem());
}
