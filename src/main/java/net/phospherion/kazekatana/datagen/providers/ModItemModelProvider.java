package net.phospherion.kazekatana.datagen.providers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.phospherion.kazekatana.KazeKatana;
import net.phospherion.kazekatana.block.ModBlocks;
import net.phospherion.kazekatana.item.ModItems;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, KazeKatana.MOD_ID, existingFileHelper);
    }

    public record AnimationData(int frames, int frametime, boolean interpolate) {}

    @Override
    protected void registerModels() {
       /* basicItem(ModItems.AZURITE.get());
        basicItem(ModItems.RAW_AZURITE.get());

        basicItem(ModItems.CHAINSAW.get());
        basicItem(ModItems.ONION.get());
        basicItem(ModItems.AURORA_ASHES.get());

        buttonItem(ModBlocks.AZURITE_BUTTON, ModBlocks.AZURITE_BLOCK);
        fenceItem(ModBlocks.AZURITE_FENCE, ModBlocks.AZURITE_BLOCK);
        wallItem(ModBlocks.AZURITE_WALL, ModBlocks.AZURITE_BLOCK);

        simpleBlockItem(ModBlocks.AZURITE_DOOR);
        */

        //basicItem(ModItems.LOW_PURITY_INCINDIUM.get());
        //basicItem(ModItems.HIGH_PURITY_INCINDIUM.get());

      //  simpleBlockItem(ModBlocks.TATARA_FURNACE);
       // withExistingParent(ModBlocks.TATARA_FURNACE.getId().getPath(),
       //         modLoc("block/tatarafurnace"));

        //simpleBlockItem(ModBlocks.KURO_SUNA);

        animatedItem(ModItems.INCINDIUM_STEEL, 8, 10, true);
        animatedItem(ModItems.ZERO_STEEL, 8, 10, true);
        animatedItem(ModItems.CRYSTAL_STEEL, 8, 6, true);
        animatedItem(ModItems.CARDINAL_STEEL, 8, 5, true);

        animatedItem(ModItems.MIZU_KORU, 4, 7, true);
        animatedItem(ModItems.INFERNAL_KORU, 4, 7, true);


    }

    public void buttonItem(RegistryObject<Block> block, RegistryObject<Block> baseBlock) {
        this.withExistingParent(ForgeRegistries.BLOCKS.getKey(block.get()).getPath(), mcLoc("block/button_inventory"))
                .texture("texture",  ResourceLocation.fromNamespaceAndPath(KazeKatana.MOD_ID,
                        "block/" + ForgeRegistries.BLOCKS.getKey(baseBlock.get()).getPath()));
    }

    public void fenceItem(RegistryObject<Block> block, RegistryObject<Block> baseBlock) {
        this.withExistingParent(ForgeRegistries.BLOCKS.getKey(block.get()).getPath(), mcLoc("block/fence_inventory"))
                .texture("texture",  ResourceLocation.fromNamespaceAndPath(KazeKatana.MOD_ID,
                        "block/" + ForgeRegistries.BLOCKS.getKey(baseBlock.get()).getPath()));
    }

    public void wallItem(RegistryObject<Block> block, RegistryObject<Block> baseBlock) {
        this.withExistingParent(ForgeRegistries.BLOCKS.getKey(block.get()).getPath(), mcLoc("block/wall_inventory"))
                .texture("wall",  ResourceLocation.fromNamespaceAndPath(KazeKatana.MOD_ID,
                        "block/" + ForgeRegistries.BLOCKS.getKey(baseBlock.get()).getPath()));
    }

    private ItemModelBuilder simpleBlockItem(RegistryObject<Block> item) {
        return withExistingParent(item.getId().getPath(),
                ResourceLocation.parse("item/generated")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(KazeKatana.MOD_ID,"item/" + item.getId().getPath()));
    }

    private final Map<String, AnimationData> animatedItems = new HashMap<>();

    public void animatedItem(RegistryObject<Item> item, int frames, int frametime, boolean interpolate) {
        String name = item.getId().getPath();

        withExistingParent(name, mcLoc("item/generated"))
                .texture("layer0", modLoc("item/" + name));

        animatedItems.put(name, new AnimationData(frames, frametime, interpolate));
    }


    public Map<String, ModItemModelProvider.AnimationData>
    getAnimatedItems() {
        return animatedItems;
    }

}