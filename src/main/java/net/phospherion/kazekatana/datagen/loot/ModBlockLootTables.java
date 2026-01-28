package net.phospherion.kazekatana.datagen.loot;

import net.phospherion.kazekatana.block.ModBlocks;
import net.phospherion.kazekatana.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;

public class ModBlockLootTables extends BlockLootSubProvider {
    public ModBlockLootTables(HolderLookup.Provider provider) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
    }

    @Override
    protected void generate() {
        /*dropSelf(ModBlocks.AZURITE_BLOCK.get());
        // dropSelf(ModBlocks.MAGIC_BLOCK.get());

        this.add(ModBlocks.AZURITE_ORE.get(),
                block -> createOreDrop(ModBlocks.AZURITE_ORE.get(), ModItems.RAW_AZURITE.get()));
        this.add(ModBlocks.AZURITE_DEEPSLATE_ORE.get(),
                block -> createMultipleOreDrops(ModBlocks.AZURITE_DEEPSLATE_ORE.get(), ModItems.RAW_AZURITE.get(), 2, 5));
        this.add(ModBlocks.AZURITE_END_ORE.get(),
                block -> createMultipleOreDrops(ModBlocks.AZURITE_END_ORE.get(), ModItems.RAW_AZURITE.get(), 4, 8));
        this.add(ModBlocks.AZURITE_NETHER_ORE.get(),
                block -> createMultipleOreDrops(ModBlocks.AZURITE_NETHER_ORE.get(), ModItems.RAW_AZURITE.get(), 1, 6));

        this.dropSelf(ModBlocks.AZURITE_STAIRS.get());
        this.add(ModBlocks.AZURITE_SLAB.get(),
                block -> createSlabItemTable(ModBlocks.AZURITE_SLAB.get()));

        this.dropSelf(ModBlocks.AZURITE_PRESSURE_PLATE.get());
        this.dropSelf(ModBlocks.AZURITE_BUTTON.get());

        this.dropSelf(ModBlocks.AZURITE_FENCE.get());
        this.dropSelf(ModBlocks.AZURITE_FENCE_GATE.get());
        this.dropSelf(ModBlocks.AZURITE_WALL.get());
        this.dropSelf(ModBlocks.AZURITE_TRAPDOOR.get());

        this.add(ModBlocks.AZURITE_DOOR.get(),
                block -> createDoorTable(ModBlocks.AZURITE_DOOR.get()));

        this.dropSelf(ModBlocks.PEDESTAL.get());
        this.dropSelf(ModBlocks.CRYSTALLIZER.get());
        this.dropSelf(ModBlocks.COAL_GENERATOR.get());
        */

        this.dropSelf(ModBlocks.TATARA_FURNACE.get());
        this.dropSelf(ModBlocks.APPAKU_TATARA_FURNACE.get());


        this.dropSelf(ModBlocks.KURO_SUNA.get());

        this.dropSelf(ModBlocks.CRIMSON_CORE.get());


        this.dropSelf(ModBlocks.INFERNITE.get());

        this.dropSelf(ModBlocks.INCINDIUM_STEEL_BLOCK.get());
        this.dropSelf(ModBlocks.CRYSTAL_STEEL_BLOCK.get());

        this.add(ModBlocks.SHINKU_KOBUTSU.get(),
                block -> createMultipleOreDrops(ModBlocks.SHINKU_KOBUTSU.get(), ModItems.INFERNAL_KORU.get(), 1, 3));

        this.add(ModBlocks.KOGARE_KORU.get(),
                block -> createMultipleOreDrops(ModBlocks.KOGARE_KORU.get(), ModItems.MIZU_KORU.get(), 1, 3));

    }

    protected LootTable.Builder createMultipleOreDrops(Block pBlock, Item item, float minDrops, float maxDrops) {
        HolderLookup.RegistryLookup<Enchantment> registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return this.createSilkTouchDispatchTable(pBlock,
                this.applyExplosionDecay(pBlock, LootItem.lootTableItem(item)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(minDrops, maxDrops)))
                        .apply(ApplyBonusCount.addOreBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}
