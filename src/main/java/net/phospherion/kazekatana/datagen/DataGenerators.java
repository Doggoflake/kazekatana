package net.phospherion.kazekatana.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.phospherion.kazekatana.KazeKatana;
import net.phospherion.kazekatana.datagen.providers.*;
import net.phospherion.kazekatana.datagen.taggen.ModBlockTagGenerator;
import net.phospherion.kazekatana.datagen.taggen.ModItemTagGenerator;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = KazeKatana.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeServer(), new ModRecipeProvider(packOutput, lookupProvider));
        generator.addProvider(event.includeServer(), ModLootTableProvider.create(packOutput, lookupProvider));

        BlockTagsProvider blockTagsProvider = new ModBlockTagGenerator(packOutput, lookupProvider, existingFileHelper);
        generator.addProvider(event.includeServer(), blockTagsProvider);
        generator.addProvider(event.includeServer(), new ModItemTagGenerator(packOutput, lookupProvider, blockTagsProvider.contentsGetter(), existingFileHelper));

        ModItemModelProvider itemModels = new ModItemModelProvider(packOutput, existingFileHelper);
        generator.addProvider(event.includeClient(), itemModels);

        // Register MCMetaProvider immediately after itemModels so it can consume getAnimatedItems()
        generator.addProvider(event.includeClient(), new MCMetaProvider(packOutput, itemModels.getAnimatedItems()));

        generator.addProvider(event.includeClient(), new ModBlockStateProvider(packOutput, existingFileHelper));

    }
}
