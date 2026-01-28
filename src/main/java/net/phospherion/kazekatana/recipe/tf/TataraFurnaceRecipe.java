package net.phospherion.kazekatana.recipe.tf;

import net.minecraft.world.item.ItemStack;

public class TataraFurnaceRecipe {

    private final ItemStack input;
    private final ItemStack output;
    private final int requiredHeat;
    private final int smeltTime;

    public TataraFurnaceRecipe(ItemStack input, ItemStack output, int requiredHeat, int smeltTime) {
        this.input = input;
        this.output = output;
        this.requiredHeat = requiredHeat;
        this.smeltTime = smeltTime;
    }

    public ItemStack getInput() {
        return input;
    }

    public ItemStack getOutput() {
        return output.copy();
    }

    public int getRequiredHeat() {
        return requiredHeat;
    }

    public int getSmeltTime() {
        return smeltTime;
    }
}
