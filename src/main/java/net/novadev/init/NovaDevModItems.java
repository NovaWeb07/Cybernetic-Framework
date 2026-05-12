package net.novadev.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.common.ForgeSpawnEggItem;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ArmorItem;

import net.novadev.item.SupersonicPantsItem;
import net.novadev.item.RoboFlameItem;
import net.novadev.item.RoboArmorItem;
import net.novadev.item.HoverPawItem;
import net.novadev.item.HologramDecoyItem;
import net.novadev.item.FlamyItem;
import net.novadev.item.FlameSushiItem;
import net.novadev.item.FlamePearlItem;
import net.novadev.item.FlameHammerItem;
import net.novadev.item.FlameDroneItem;
import net.novadev.item.DrillItem;
import net.novadev.item.CyberHeartItem;
import net.novadev.item.CursedSWORDItem;
import net.novadev.item.CannonItem;
import net.novadev.item.BioPantsItem;
import net.novadev.NovaDevMod;

public class NovaDevModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, NovaDevMod.MODID);
	public static final RegistryObject<Item> FLAME_PEARL = REGISTRY.register("flame_pearl", () -> new FlamePearlItem());
	public static final RegistryObject<Item> CYBER_HEART = REGISTRY.register("cyber_heart", () -> new CyberHeartItem());
	public static final RegistryObject<Item> CURSED_SWORD = REGISTRY.register("cursed_sword", () -> new CursedSWORDItem());
	public static final RegistryObject<Item> FLAME_SUSHI = REGISTRY.register("flame_sushi", () -> new FlameSushiItem());
	public static final RegistryObject<Item> HOVER_PAW = REGISTRY.register("hover_paw", () -> new HoverPawItem());
	public static final RegistryObject<Item> FLAME_HAMMER = REGISTRY.register("flame_hammer", () -> new FlameHammerItem());
	public static final RegistryObject<Item> HOLOGRAM_DECOY = REGISTRY.register("hologram_decoy", () -> new HologramDecoyItem());
	public static final RegistryObject<Item> FLAME_DRONE = REGISTRY.register("flame_drone", () -> new FlameDroneItem());
	public static final RegistryObject<Item> FLAMDRONE_SPAWN_EGG = REGISTRY.register("flamdrone_spawn_egg", () -> new ForgeSpawnEggItem(NovaDevModEntities.FLAMDRONE, -1, -1, new Item.Properties()));
	public static final RegistryObject<Item> CANNON = REGISTRY.register("cannon", () -> new CannonItem());
	public static final RegistryObject<Item> DRILL = REGISTRY.register("drill", () -> new DrillItem());
	public static final RegistryObject<Item> FLAMYY_SPAWN_EGG = REGISTRY.register("flamyy_spawn_egg", () -> new ForgeSpawnEggItem(NovaDevModEntities.FLAMYY, -1, -1, new Item.Properties()));
	public static final RegistryObject<Item> FLAMY = REGISTRY.register("flamy", () -> new FlamyItem());
	public static final RegistryObject<RoboFlameItem> ROBO_FLAME_HELMET = REGISTRY.register("robo_flame_helmet", () -> new RoboFlameItem(ArmorItem.Type.HELMET, new Item.Properties()));
	public static final RegistryObject<RoboFlameItem> ROBO_FLAME_CHESTPLATE = REGISTRY.register("robo_flame_chestplate", () -> new RoboFlameItem(ArmorItem.Type.CHESTPLATE, new Item.Properties()));
	public static final RegistryObject<Item> ROBO_ARMOR = REGISTRY.register("robo_armor", () -> new RoboArmorItem());
	public static final RegistryObject<BioPantsItem> BIO_PANTS_CHESTPLATE = REGISTRY.register("bio_pants_chestplate", () -> new BioPantsItem(ArmorItem.Type.CHESTPLATE, new Item.Properties()));
	public static final RegistryObject<BioPantsItem> BIO_PANTS_LEGGINGS = REGISTRY.register("bio_pants_leggings", () -> new BioPantsItem(ArmorItem.Type.LEGGINGS, new Item.Properties()));
	public static final RegistryObject<Item> SUPERSONIC_PANTS = REGISTRY.register("supersonic_pants", () -> new SupersonicPantsItem());
}
