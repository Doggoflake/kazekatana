package net.phospherion.kazekatana.recipe.tf;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public class TataraFurnaceRecipeInput implements RecipeInput {

    private final ItemStack stack;

    public TataraFurnaceRecipeInput(ItemStack stack) {
        this.stack = stack;
    }

    public ItemStack getItem() {
        return stack;
    }

    @Override
    public ItemStack getItem(int index) {
        return stack;
    }

    @Override
    public int size() {
        return 1;
    }
}
