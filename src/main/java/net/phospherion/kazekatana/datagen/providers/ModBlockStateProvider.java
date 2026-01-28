package net.phospherion.kazekatana.datagen.providers;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.phospherion.kazekatana.KazeKatana;
import net.phospherion.kazekatana.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static net.minecraft.data.models.model.TextureMapping.cubeBottomTop;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, KazeKatana.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        //  blockWithItem(ModBlocks.MAGIC_BLOCK);
        // blockWithItem(ModBlocks.CRYSTALLIZER);
        // horizontalBlock(ModBlocks.CRYSTALLIZER.get(), models().orientable("mccourse:crystallizer",
        //         mcLoc("block/blast_furnace_side"),
        //         modLoc("block/crystallizer_front"),
        //         mcLoc("block/blast_furnace_top")));

        simpleBlockWithItem(
                ModBlocks.KOGARE_KORU.get(),
                models().cubeBottomTop(
                        "kogare_koru",
                        modLoc("block/kogare_koru_side"),
                        modLoc("block/kogare_koru_bottom"),
                        modLoc("block/kogare_koru_top")
                )
        );

        simpleBlockWithItem(
                ModBlocks.CRIMSON_CORE.get(),
                models().cubeBottomTop(
                        "crimson_core",
                        modLoc("block/crimson_core_side"),
                        modLoc("block/crimson_core_bottom"),
                        modLoc("block/crimson_core_top")
                )
        );

        simpleBlockWithItem(
                ModBlocks.CRYSTAL_STEEL_BLOCK.get(),
                models().cubeBottomTop(
                        "crystal_steel_block",
                        modLoc("block/crystal_steel_block_alt1"),
                        modLoc("block/crystal_steel_block_bottom"),
                        modLoc("block/crystal_steel_block_top")
                )
        );



        blockWithItem(ModBlocks.KURO_SUNA);
        blockWithItem(ModBlocks.INFERNITE);
        blockWithItem(ModBlocks.SHINKU_KOBUTSU);
        blockWithItem(ModBlocks.INCINDIUM_STEEL_BLOCK);


        // Base tatara furnace (uses default prefix "tatarafurnace")
        tataraFurnace(ModBlocks.TATARA_FURNACE, "tatara_furnace",
                mcLoc("block/blast_furnace_side"),
                modLoc("block/tatarafurnace"),
                modLoc("block/tatarafurnace_front_on"),
                mcLoc("block/blast_furnace_top"),
                mcLoc("block/blast_furnace_top")
                );

        tataraFurnace( ModBlocks.APPAKU_TATARA_FURNACE, "appaku_tatara_furnace",
                modLoc("block/appaku_tatara_furnace_side"), // side uses vanilla blast furnace side
                modLoc("block/appaku_tatara_furnace_front"), // front (unlit) from mod
                modLoc("block/appaku_tatara_furnace_front_on"), // front when lit
                modLoc("block/appaku_tatara_furnace_top"), // top from mod
                modLoc("block/appaku_tatara_furnace_bottom")
        );

    }

    private void blockWithItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }

    private void blockItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockItem(blockRegistryObject.get(), new ModelFile.UncheckedModelFile("kazekatana:block/" +
                ForgeRegistries.BLOCKS.getKey(blockRegistryObject.get()).getPath()));
    }

    private void blockItem(RegistryObject<Block> blockRegistryObject, String appendix) {
        simpleBlockItem(blockRegistryObject.get(), new ModelFile.UncheckedModelFile("kazekatana:block/" +
                ForgeRegistries.BLOCKS.getKey(blockRegistryObject.get()).getPath() + appendix));
    }

    // Place these two methods in your ModBlockStateProvider class, replacing the old tataraFurnace().

    private void tataraFurnace() {
        // default call for the base tatara furnace using the "tatarafurnace" prefix
        tataraFurnace(ModBlocks.TATARA_FURNACE, "tatarafurnace");
    }

    // Place these methods inside ModBlockStateProvider

    /**
     * Full customization: provide explicit ResourceLocation for each face texture.
     *
     * @param furnaceBlock RegistryObject of the furnace block
     * @param modelPrefix  base model name (e.g., "appaku_tatara_furnace")
     * @param sideTex      texture for the sides (use mcLoc or modLoc)
     * @param frontTex     texture for the front when unlit
     * @param frontOnTex   texture for the front when lit
     * @param topTex       texture for the top
     * @param bottomTex    texture for the bottom
     */
    private void tataraFurnace(RegistryObject<Block> furnaceBlock,
                               String modelPrefix,
                               ResourceLocation sideTex,
                               ResourceLocation frontTex,
                               ResourceLocation frontOnTex,
                               ResourceLocation topTex,
                               ResourceLocation bottomTex) {
        Block block = furnaceBlock.get();

        getVariantBuilder(block).forAllStates(state -> {
            Direction facing = state.getValue(HorizontalDirectionalBlock.FACING);
            boolean lit = state.getValue(BlockStateProperties.LIT);

            String modelName = modelPrefix + (lit ? "_on" : "");

            // Use vanilla orientable parent and map each texture explicitly.
            ModelFile model = models().withExistingParent(modelName, mcLoc("block/orientable"))
                    .texture("side", sideTex)
                    .texture("front", lit ? frontOnTex : frontTex)
                    .texture("top", topTex)
                    .texture("bottom", bottomTex);

            int yRot = (int) facing.getOpposite().toYRot();

            return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationY(yRot)
                    .build();
        });

        // Item model points to the unlit block model
        simpleBlockItem(furnaceBlock.get(), new ModelFile.UncheckedModelFile(modLoc("block/" + modelPrefix)));
    }

    /**
     * Convenience overload: only modelPrefix provided. Uses defaults:
     *  - side  -> mcLoc("block/blast_furnace_side")
     *  - front -> modLoc("block/{modelPrefix}_front")
     *  - front_on -> modLoc("block/{modelPrefix}_front_on")
     *  - top   -> mcLoc("block/blast_furnace_top")
     *  - bottom-> modLoc("block/{modelPrefix}_bottom")
     */
    private void tataraFurnace(RegistryObject<Block> furnaceBlock, String modelPrefix) {
        tataraFurnace(
                furnaceBlock,
                modelPrefix,
                mcLoc("block/blast_furnace_side"),
                modLoc("block/" + modelPrefix + "_front"),
                modLoc("block/" + modelPrefix + "_front_on"),
                mcLoc("block/blast_furnace_top"),
                modLoc("block/" + modelPrefix + "_bottom")
        );
    }




}
