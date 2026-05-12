package net.novadev.world.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.novadev.init.NovaDevModItems;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class BlockRandomizerModifier extends LootModifier {
    public static final Codec<BlockRandomizerModifier> CODEC = RecordCodecBuilder.create(inst -> codecStart(inst).apply(inst, BlockRandomizerModifier::new));

    private static final List<Item> globalPool = new ArrayList<>();

    public BlockRandomizerModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    private static void initPool() {
        if (!globalPool.isEmpty()) return;

        List<Item> customItems = new ArrayList<>();
        List<Item> goldenFoodsItems = new ArrayList<>();
        List<Item> vanillaBlocks = new ArrayList<>();
        List<Item> vanillaItems = new ArrayList<>();
        List<Item> opItems = new ArrayList<>();

        opItems.add(Items.DIAMOND_SWORD);
        opItems.add(Items.DIAMOND_PICKAXE);
        opItems.add(Items.DIAMOND_AXE);
        opItems.add(Items.DIAMOND_SHOVEL);
        opItems.add(Items.DIAMOND_HELMET);
        opItems.add(Items.DIAMOND_CHESTPLATE);
        opItems.add(Items.DIAMOND_LEGGINGS);
        opItems.add(Items.DIAMOND_BOOTS);
        opItems.add(Items.NETHERITE_SWORD);
        opItems.add(Items.NETHERITE_PICKAXE);
        opItems.add(Items.NETHERITE_AXE);
        opItems.add(Items.NETHERITE_HELMET);
        opItems.add(Items.NETHERITE_CHESTPLATE);
        opItems.add(Items.NETHERITE_LEGGINGS);
        opItems.add(Items.NETHERITE_BOOTS);
        opItems.add(Items.IRON_SWORD);
        opItems.add(Items.IRON_PICKAXE);
        opItems.add(Items.IRON_CHESTPLATE);
        opItems.add(Items.TOTEM_OF_UNDYING);
        opItems.add(Items.ENCHANTED_GOLDEN_APPLE);
        opItems.add(Items.GOLDEN_APPLE);
        opItems.add(Items.DIAMOND);
        opItems.add(Items.EMERALD);
        opItems.add(Items.IRON_INGOT);
        opItems.add(Items.GOLD_INGOT);
        opItems.add(Items.NETHERITE_INGOT);
        opItems.add(Items.DIAMOND_BLOCK);
        opItems.add(Items.EMERALD_BLOCK);
        opItems.add(Items.GOLD_BLOCK);
        opItems.add(Items.IRON_BLOCK);
        opItems.add(Items.NETHERITE_BLOCK);
        opItems.add(Items.OBSIDIAN);
        opItems.add(Items.ENDER_PEARL);
        opItems.add(Items.BLAZE_ROD);
        opItems.add(Items.EXPERIENCE_BOTTLE);
        opItems.add(Items.CREEPER_SPAWN_EGG);
        opItems.add(Items.ZOMBIE_SPAWN_EGG);
        opItems.add(Items.SKELETON_SPAWN_EGG);
        opItems.add(Items.WITHER_SKELETON_SPAWN_EGG);
        opItems.add(Items.GHAST_SPAWN_EGG);
        opItems.add(Items.RAVAGER_SPAWN_EGG);
        opItems.add(Items.WARDEN_SPAWN_EGG);

        for (Item item : ForgeRegistries.ITEMS.getValues()) {
            ResourceLocation regName = ForgeRegistries.ITEMS.getKey(item);
            if (regName != null) {
                String namespace = regName.getNamespace();
                String path = regName.getPath();

                if (namespace.equals("minecraft")) {
                    if (item instanceof net.minecraft.world.item.BlockItem) {
                        vanillaBlocks.add(item);
                    } else {
                        vanillaItems.add(item);
                    }
                } else if (namespace.equals("golden_foods")) {
                    goldenFoodsItems.add(item);
                } else if (namespace.equals("nova_dev")) {
                    if (!path.startsWith("robo_flame_") && !path.startsWith("bio_pants_") && !path.endsWith("_spawn_egg")) {
                        customItems.add(item);
                    }
                }
            }
        }

        for (int i = 0; i < 5; i++) {
            globalPool.addAll(vanillaBlocks);
        }

        globalPool.addAll(vanillaItems);

        for (int i = 0; i < 7; i++) {
            globalPool.addAll(goldenFoodsItems);
        }

        for (int i = 0; i < 15; i++) {
            globalPool.addAll(customItems);
        }

        for (int i = 0; i < 4; i++) {
            globalPool.addAll(opItems);
        }
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (!context.hasParam(LootContextParams.BLOCK_STATE)) {
            return generatedLoot;
        }

        BlockState state = context.getParam(LootContextParams.BLOCK_STATE);
        Block block = state.getBlock();
        ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(block);

        if (block == net.minecraft.world.level.block.Blocks.TORCH || block == net.minecraft.world.level.block.Blocks.WALL_TORCH || block == net.minecraft.world.level.block.Blocks.SOUL_TORCH || block == net.minecraft.world.level.block.Blocks.SOUL_WALL_TORCH) {
            return ObjectArrayList.wrap(new ItemStack[]{new ItemStack(NovaDevModItems.FLAME_PEARL.get())});
        }
        if (block == net.minecraft.world.level.block.Blocks.HAY_BLOCK) {
            return ObjectArrayList.wrap(new ItemStack[]{new ItemStack(NovaDevModItems.FLAME_SUSHI.get())});
        }
        if (block == net.minecraft.world.level.block.Blocks.GRASS || block == net.minecraft.world.level.block.Blocks.TALL_GRASS || block == net.minecraft.world.level.block.Blocks.FERN || block == net.minecraft.world.level.block.Blocks.LARGE_FERN) {
            return ObjectArrayList.wrap(new ItemStack[]{new ItemStack(NovaDevModItems.CYBER_HEART.get())});
        }

        ServerLevel level = context.getLevel();
        long seed = level.getSeed();
        long blockHash = blockId != null ? blockId.toString().hashCode() : 0;

        Random random = new Random(seed ^ blockHash);

        initPool();
        if (globalPool.isEmpty()) {
            return generatedLoot;
        }

        Item randomItem = globalPool.get(random.nextInt(globalPool.size()));
        return ObjectArrayList.wrap(new ItemStack[]{new ItemStack(randomItem)});
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
