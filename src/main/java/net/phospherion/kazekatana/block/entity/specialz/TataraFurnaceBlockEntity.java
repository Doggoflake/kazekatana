package net.phospherion.kazekatana.block.entity.specialz;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.phospherion.kazekatana.block.ModBlocks;
import net.phospherion.kazekatana.block.entity.ModBlockEntities;
import net.phospherion.kazekatana.item.ModItems;

/**
 * Base (tier 1) Tatara furnace. All recipes and behavior are hardcoded here.
 * This class only provides configuration and recipe lookup; the engine lives
 * in AbstractTataraFurnaceBlockEntity.
 */
public class TataraFurnaceBlockEntity extends AbstractTataraFurnaceBlockEntity {

    public TataraFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TATARAFURNACE_BE.get(), pos, state);
    }

    // ------------------------------------------------------------
    // Tier configuration
    // ------------------------------------------------------------
    @Override
    protected int getMaxHeatValue() {
        return 2500;
    }

    @Override
    protected float getThermalMass() {
        return 3.0f;
    }

    /**
     * Base tier smelt speed multiplier (multiplies the heat-based factor).
     * 1.0 = baseline speed.
     */
    @Override
    protected float getSmeltSpeedMultiplier() {
        return 1.0f;
    }

    // ------------------------------------------------------------
    // Fuel behavior (vanilla fuels)
    // ------------------------------------------------------------
    @Override
    protected boolean isFuel(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        // fuels
        if (stack.is(Items.COAL)) return true;
        if (stack.is(ModItems.MIZU_KORU.get())) return true;
        return !stack.isEmpty() && ForgeHooks.getBurnTime(stack, null) > 0;
    }

    @Override
    protected int getFuelBurnTime(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return 0;
        if (stack.is(Items.COAL)) return 400;
        if (stack.is(ModItems.MIZU_KORU.get())) return 1200;
        return Math.max(1, ForgeHooks.getBurnTime(stack, null));
    }

    /**
     * Simple fuel heat curve for base tier:
     * - early burn (burnPercent < 0.5) yields 1 heat/tick
     * - later burn yields 0 (so fuel front-loads heat)
     */
    @Override
    protected int getFuelHeatPerTick(ItemStack stack, float burnPercent) {
        if (stack == null || stack.isEmpty()) return 0;
        if (stack.is(Items.COAL)) {
            if (burnPercent < 0.6f) return 4;
            if (burnPercent < 0.9f) return 2;
            return 1;
        }
        if (stack.is(ModItems.MIZU_KORU.get())) {
            if (burnPercent < 0.2f) return 2;
            if (burnPercent < 0.5f) return 3;
            if (burnPercent < 0.9f) return 2;
            return 1;
        }

        if (burnPercent < 0.5f) return 1;
        return 0;
    }

    @Override
    protected float getFuelEfficiency(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return 1.0f;

        if (stack.is(Items.COAL)) return 0.9f; // -10% longer burn
        if (stack.is(ModItems.MIZU_KORU.get())) return 1.10f; // 10% longer burn

        return 1.0f; // default behavior
    }

    // ------------------------------------------------------------
    // Hardcoded recipes for the base tatara
    // ------------------------------------------------------------
    @Override
    protected HardcodedRecipe getRecipeFor(ItemStack input) {
        if (input == null || input.isEmpty()) return null;

        // Kuro Suna -> Incindium Steel (example)
        if (input.is(ModBlocks.KURO_SUNA.get().asItem())) {
            return new HardcodedRecipe(
                    new ItemStack(ModItems.INCINDIUM_STEEL.get(), 1),
                    800,   // required heat
                    120    // smelt time (ticks)
            );
        }

        // Add more base-tier recipes here as needed:
        // if (input.is(ModItems.SOME_ORE.get())) { ... }

        return null;
    }

    // ------------------------------------------------------------
    // Display name
    // ------------------------------------------------------------
    @Override
    protected Component getFurnaceName() {
        return Component.translatable("name.kazekatana.tatarafurnace");
    }
}
