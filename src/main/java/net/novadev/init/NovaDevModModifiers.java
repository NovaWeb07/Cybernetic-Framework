package net.novadev.init;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import com.mojang.serialization.Codec;
import net.novadev.NovaDevMod;
import net.novadev.world.loot.BlockRandomizerModifier;

public class NovaDevModModifiers {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> REGISTRY = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, NovaDevMod.MODID);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> BLOCK_RANDOMIZER = REGISTRY.register("block_randomizer", () -> BlockRandomizerModifier.CODEC);
}
