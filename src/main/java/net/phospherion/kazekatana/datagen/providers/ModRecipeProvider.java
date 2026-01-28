package net.phospherion.kazekatana.datagen.providers;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.phospherion.kazekatana.KazeKatana;
import net.phospherion.kazekatana.block.ModBlocks;
import net.phospherion.kazekatana.item.ModItems;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pRegistries) {
        super(pOutput, pRegistries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        /*
        List<ItemLike> AZURITE_SMELTABLES = List.of(ModItems.RAW_AZURITE.get(),
                ModBlocks.AZURITE_ORE.get(), ModBlocks.AZURITE_DEEPSLATE_ORE.get(), ModBlocks.AZURITE_END_ORE.get(), ModBlocks.AZURITE_NETHER_ORE.get());

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.AZURITE_BLOCK.get())
                .pattern("AAA")
                .pattern("AAA")
                .pattern("AAA")
                .define('A', ModItems.AZURITE.get())
                .unlockedBy("has_azurite", has(ModItems.AZURITE.get())).save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.AZURITE.get(), 9)
                .requires(ModBlocks.AZURITE_BLOCK.get())
                .unlockedBy("has_azurite_block", has(ModBlocks.AZURITE_BLOCK.get())).save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.AZURITE.get(), 9)
                .requires(ModBlocks.MAGIC_BLOCK.get())
                .unlockedBy("has_magic_block", has(ModBlocks.MAGIC_BLOCK.get())).save(recipeOutput, "mccourse:azurite_2");


        oreSmelting(recipeOutput, AZURITE_SMELTABLES, RecipeCategory.MISC, ModItems.AZURITE.get(), 0.25f, 200, "azurite");
        oreBlasting(recipeOutput, AZURITE_SMELTABLES, RecipeCategory.MISC, ModItems.AZURITE.get(), 0.25f, 100, "azurite");

        stairBuilder(ModBlocks.AZURITE_STAIRS.get(), Ingredient.of(ModItems.AZURITE.get())).group("azurite")
                .unlockedBy("has_azurite", has(ModItems.AZURITE.get())).save(recipeOutput);
        slab(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ModBlocks.AZURITE_SLAB.get(), ModItems.AZURITE.get());

        buttonBuilder(ModBlocks.AZURITE_BUTTON.get(), Ingredient.of(ModItems.AZURITE.get())).group("azurite")
                .unlockedBy("has_azurite", has(ModItems.AZURITE.get())).save(recipeOutput);
        pressurePlate(recipeOutput, ModBlocks.AZURITE_PRESSURE_PLATE.get(), ModItems.AZURITE.get());

        fenceBuilder(ModBlocks.AZURITE_FENCE.get(), Ingredient.of(ModItems.AZURITE.get())).group("azurite")
                .unlockedBy("has_azurite", has(ModItems.AZURITE.get())).save(recipeOutput);
        fenceGateBuilder(ModBlocks.AZURITE_FENCE_GATE.get(), Ingredient.of(ModItems.AZURITE.get())).group("azurite")
                .unlockedBy("has_azurite", has(ModItems.AZURITE.get())).save(recipeOutput);
        wall(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ModBlocks.AZURITE_WALL.get(), ModItems.AZURITE.get());

        doorBuilder(ModBlocks.AZURITE_DOOR.get(), Ingredient.of(ModItems.AZURITE.get())).group("azurite")
                .unlockedBy("has_azurite", has(ModItems.AZURITE.get())).save(recipeOutput);
        trapdoorBuilder(ModBlocks.AZURITE_TRAPDOOR.get(), Ingredient.of(ModItems.AZURITE.get())).group("azurite")
                .unlockedBy("has_azurite", has(ModItems.AZURITE.get())).save(recipeOutput);
        */

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.CRYSTAL_STEEL.get(), 9)
                .requires(ModBlocks.CRYSTAL_STEEL_BLOCK.get())
                .unlockedBy("has_crystal_steel", has(ModItems.CRYSTAL_STEEL.get())).save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.INCINDIUM_STEEL.get(), 9)
                .requires(ModBlocks.INCINDIUM_STEEL_BLOCK.get())
                .unlockedBy("has_incindium", has(ModItems.INCINDIUM_STEEL.get())).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.TATARA_FURNACE.get())
                .pattern("OIO")
                .pattern("DMD")
                .pattern("OIO")
                .define('D', Items.DEEPSLATE)
                .define('I', Items.IRON_INGOT)
                .define('O', Items.OBSIDIAN)
                .define('M', Items.MAGMA_BLOCK)
                .unlockedBy("has_kuro_suna", has(ModBlocks.KURO_SUNA.get())).save(recipeOutput);
    }

    protected static void oreSmelting(RecipeOutput recipeOutput, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult,
                                      float pExperience, int pCookingTIme, String pGroup) {
        oreCooking(recipeOutput, RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new, pIngredients, pCategory, pResult,
                pExperience, pCookingTIme, pGroup, "_from_smelting");
    }

    protected static void oreBlasting(RecipeOutput recipeOutput, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult,
                                      float pExperience, int pCookingTime, String pGroup) {
        oreCooking(recipeOutput, RecipeSerializer.BLASTING_RECIPE, BlastingRecipe::new, pIngredients, pCategory, pResult,
                pExperience, pCookingTime, pGroup, "_from_blasting");
    }

    protected static <T extends AbstractCookingRecipe> void oreCooking(RecipeOutput recipeOutput, RecipeSerializer<T> pCookingSerializer, AbstractCookingRecipe.Factory<T> factory,
                                                                       List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup, String pRecipeName) {
        for(ItemLike itemlike : pIngredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), pCategory, pResult, pExperience, pCookingTime, pCookingSerializer, factory).group(pGroup).unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(recipeOutput, KazeKatana.MOD_ID + ":" + getItemName(pResult) + pRecipeName + "_" + getItemName(itemlike));
        }
    }
}