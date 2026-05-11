package net.mcreator.roxdev.init;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import com.mojang.serialization.Codec;
import net.mcreator.roxdev.RoxDevMod;
import net.mcreator.roxdev.world.loot.BlockRandomizerModifier;

public class RoxDevModModifiers {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> REGISTRY = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, RoxDevMod.MODID);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> BLOCK_RANDOMIZER = REGISTRY.register("block_randomizer", () -> BlockRandomizerModifier.CODEC);
}
