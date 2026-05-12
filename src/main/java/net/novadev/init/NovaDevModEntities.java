package net.novadev.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;

import net.novadev.entity.HologramCloneEntity;
import net.novadev.entity.FlamyyEntity;
import net.novadev.entity.FlamdroneEntity;
import net.novadev.entity.DroneSwordProjectile;
import net.novadev.entity.CursedBladeEntity;
import net.novadev.entity.CannonIceProjectileEntity;
import net.novadev.NovaDevMod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class NovaDevModEntities {
	public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, NovaDevMod.MODID);
	public static final RegistryObject<EntityType<FlamdroneEntity>> FLAMDRONE = register("flamdrone",
			EntityType.Builder.<FlamdroneEntity>of(FlamdroneEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(FlamdroneEntity::new)

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<FlamyyEntity>> FLAMYY = register("flamyy",
			EntityType.Builder.<FlamyyEntity>of(FlamyyEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(FlamyyEntity::new)

					.sized(0.6f, 1.8f));

	public static final RegistryObject<EntityType<CursedBladeEntity>> CURSED_BLADE = register("cursed_blade",
			EntityType.Builder.<CursedBladeEntity>of(CursedBladeEntity::new, MobCategory.MONSTER)
					.setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3)
					.setCustomClientFactory(CursedBladeEntity::new)
					.sized(0.5f, 2.0f));
	public static final RegistryObject<EntityType<HologramCloneEntity>> HOLOGRAM_CLONE = register("hologram_clone",
			EntityType.Builder.<HologramCloneEntity>of(HologramCloneEntity::new, MobCategory.CREATURE)
					.setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3)
					.setCustomClientFactory(HologramCloneEntity::new)
					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<DroneSwordProjectile>> DRONE_SWORD = register("drone_sword",
			EntityType.Builder.<DroneSwordProjectile>of(DroneSwordProjectile::new, MobCategory.MISC)
					.setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1)
					.sized(0.3f, 0.3f));
	public static final RegistryObject<EntityType<CannonIceProjectileEntity>> CANNON_ICE_PROJECTILE = register("cannon_ice_projectile",
			EntityType.Builder.<CannonIceProjectileEntity>of(CannonIceProjectileEntity::new, MobCategory.MISC)
					.setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1)
					.sized(0.5f, 0.5f));
	private static <T extends Entity> RegistryObject<EntityType<T>> register(String registryname, EntityType.Builder<T> entityTypeBuilder) {
		return REGISTRY.register(registryname, () -> (EntityType<T>) entityTypeBuilder.build(registryname));
	}

	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			FlamdroneEntity.init();
			FlamyyEntity.init();
		});
	}

	@SubscribeEvent
	public static void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(FLAMDRONE.get(), FlamdroneEntity.createAttributes().build());
		event.put(FLAMYY.get(), FlamyyEntity.createAttributes().build());
		event.put(CURSED_BLADE.get(), CursedBladeEntity.createAttributes().build());
		event.put(HOLOGRAM_CLONE.get(), HologramCloneEntity.createAttributes().build());
	}
}
