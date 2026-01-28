//package net.phospherion.kazekatana.block.entity.specialz;
//
//import net.minecraft.core.BlockPos;
//import net.minecraft.core.Direction;
//import net.minecraft.core.HolderLookup;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.network.chat.Component;
//import net.minecraft.network.protocol.Packet;
//import net.minecraft.network.protocol.game.ClientGamePacketListener;
//import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
//import net.minecraft.util.Mth;
//import net.minecraft.world.Containers;
//import net.minecraft.world.MenuProvider;
//import net.minecraft.world.SimpleContainer;
//import net.minecraft.world.entity.player.Inventory;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.inventory.AbstractContainerMenu;
//import net.minecraft.world.inventory.ContainerData;
//import net.minecraft.world.inventory.SimpleContainerData;
//import net.minecraft.world.item.Item;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.Items;
//import net.minecraft.world.item.crafting.RecipeHolder;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.entity.BlockEntity;
//import net.minecraft.world.level.block.state.BlockState;
//import net.minecraftforge.common.capabilities.Capability;
//import net.minecraftforge.common.capabilities.ForgeCapabilities;
//import net.minecraftforge.common.util.LazyOptional;
//import net.minecraftforge.energy.IEnergyStorage;
//import net.minecraftforge.items.IItemHandler;
//import net.minecraftforge.items.ItemStackHandler;
//import net.phospherion.kazekatana.block.ModBlocks;
//import net.phospherion.kazekatana.block.entity.ModBlockEntities;
//import net.phospherion.kazekatana.block.entity.inventory.InventoryDirectionEntry;
//import net.phospherion.kazekatana.block.entity.inventory.InventoryDirectionWrapper;
//import net.phospherion.kazekatana.block.entity.inventory.WrappedHandler;
//import net.phospherion.kazekatana.block.specialz.TataraFurnaceBlock;
//import net.phospherion.kazekatana.item.ModItems;
//import net.phospherion.kazekatana.recipe.ModRecipes;
//import net.phospherion.kazekatana.recipe.tf.TataraFurnaceRecipe;
//import net.phospherion.kazekatana.recipe.tf.TataraFurnaceRecipeInput;
//import net.phospherion.kazekatana.screen.specialz.TataraFurnaceMenu;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//
//import java.util.Map;
//import java.util.Optional;
//
//public class ExTataraFurnaceBlockEntity extends BlockEntity implements MenuProvider {
//    // Inventory
//    public final ItemStackHandler itemHandler = new ItemStackHandler(4) {
//        @Override
//        protected void onContentsChanged(int slot) {
//            setChanged();
//            if (!level.isClientSide()) {
//                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
//            }
//        }
//    };
//
//    // Slot indices
//    private static final int TE_SLOT_INPUT  = 0;
//    private static final int TE_SLOT_FUEL   = 1;
//    private static final int TE_SLOT_OUTPUT = 2;
//    private static final int TE_SLOT_MODU   = 3;
//    private static final int TE_INVENTORY_SLOT_COUNT = 4;
//
//    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
//    private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();
//
//    protected final ContainerData data;
//
//    // Burn / progress
//    private float progress = 0f;
//    private int maxProgress = 72;
//    private final int DEFAULT_MAX_PROGRESS = 72;
//
//    private int burnTime = 0;        // ticks remaining for current fuel (float math stored in int)
//    private int fuelTime = 0;        // total burn time of the current fuel (ticks)
//
//    // Heat system (simple, stable base)
//    private int heatLevel = 0;       // current heat (0..maxHeat)
//    private int maxHeat = 2500;      // maximum heat for this furnace (tweak per tier)
//    private float thermalMass = 3f;  // higher = slower heat changes
//
//    // Fuel curve / state
//    private Item currentFuelItem = Items.AIR; // item currently burning (remember after slot shrink)
//    private float heatAccumulator = 0f;       // accumulate fractional heat
//
//    // Directional wrappers (unchanged)
//    private final Map<Direction, LazyOptional<WrappedHandler>> directionWrappedHandlerMap =
//            new InventoryDirectionWrapper(itemHandler,
//                    new InventoryDirectionEntry(Direction.DOWN, TE_SLOT_OUTPUT, false),
//                    new InventoryDirectionEntry(Direction.NORTH, TE_SLOT_INPUT, true),
//                    new InventoryDirectionEntry(Direction.SOUTH, TE_SLOT_OUTPUT, false),
//                    new InventoryDirectionEntry(Direction.EAST, TE_SLOT_INPUT, true),
//                    new InventoryDirectionEntry(Direction.WEST, TE_SLOT_OUTPUT, false),
//                    new InventoryDirectionEntry(Direction.UP, TE_SLOT_INPUT, true)).directionsMap;
//
//    public ExTataraFurnaceBlockEntity(BlockPos pPos, BlockState pBlockState) {
//        super(ModBlockEntities.TATARAFURNACE_BE.get(), pPos, pBlockState);
//
//        this.data = new SimpleContainerData(5) {
//            @Override
//            public int get(int i) {
//                return switch (i) {
//                    case 0 -> (int) progress;
//                    case 1 -> maxProgress;
//                    case 2 -> heatLevel;
//                    case 3 -> maxHeat;
//                    case 4 -> getRequiredHeatForInput(itemHandler.getStackInSlot(TE_SLOT_INPUT));
//                    default -> 0;
//                };
//            }
//
//            @Override
//            public void set(int i, int value) {
//                switch (i) {
//                    case 0 -> progress = value;
//                    case 1 -> maxProgress = value;
//                    case 2 -> heatLevel = value;
//                    case 3 -> maxHeat = value;
//                    case 4 -> {/*Server Authority*/}
//                }
//            }
//
//            @Override
//            public int getCount() {
//                return 5;
//            }
//        };
//    }
//
//    @Override
//    public Component getDisplayName() {
//        return Component.translatable("name.kazekatana.tatarafurnace");
//    }
//
//    @Nullable
//    @Override
//    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
//        return new TataraFurnaceMenu(i, inventory, this, this.data);
//    }
//
//    @Override
//    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
//        if (cap == ForgeCapabilities.ENERGY) {
//            return lazyEnergyHandler.cast();
//        }
//
//        if (cap == ForgeCapabilities.ITEM_HANDLER) {
//            if (side == null) {
//                return lazyItemHandler.cast();
//            }
//
//            if (directionWrappedHandlerMap.containsKey(side)) {
//                Direction localDir = this.getBlockState().getValue(TataraFurnaceBlock.FACING);
//
//                if (side == Direction.DOWN || side == Direction.UP) {
//                    return directionWrappedHandlerMap.get(side).cast();
//                }
//
//                return switch (localDir) {
//                    default -> directionWrappedHandlerMap.get(side).cast();
//                    case EAST -> directionWrappedHandlerMap.get(side.getCounterClockWise()).cast();
//                    case SOUTH -> directionWrappedHandlerMap.get(side.getOpposite()).cast();
//                    case WEST -> directionWrappedHandlerMap.get(side.getClockWise()).cast();
//                };
//            }
//        }
//
//        return super.getCapability(cap, side);
//    }
//
//    @Override
//    public void onLoad() {
//        super.onLoad();
//        lazyItemHandler = LazyOptional.of(() -> itemHandler);
//    }
//
//    @Override
//    public void invalidateCaps() {
//        super.invalidateCaps();
//        lazyItemHandler.invalidate();
//        lazyEnergyHandler.invalidate();
//    }
//
//    // --- NBT persistence (newer mappings with HolderLookup.Provider) ---
//    @Override
//    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
//        // If your ItemStackHandler supports the provider overload, use it; otherwise switch to serializeNBT()
//        pTag.put("inventory", itemHandler.serializeNBT(pRegistries));
//        pTag.putInt("tatarafurnace.progress", (int) progress);
//        pTag.putInt("tatarafurnace.max_progress", maxProgress);
//        pTag.putInt("tatarafurnace.heat", heatLevel);
//        pTag.putInt("tatarafurnace.max_heat", maxHeat);
//        super.saveAdditional(pTag, pRegistries);
//    }
//
//    @Override
//    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
//        super.loadAdditional(pTag, pRegistries);
//
//        if (pTag.contains("inventory")) {
//            itemHandler.deserializeNBT(pRegistries, pTag.getCompound("inventory"));
//        }
//        progress = pTag.getInt("tatarafurnace.progress");
//        maxProgress = pTag.getInt("tatarafurnace.max_progress");
//        heatLevel = pTag.contains("tatarafurnace.heat") ? pTag.getInt("tatarafurnace.heat") : 0;
//        maxHeat = pTag.contains("tatarafurnace.max_heat") ? pTag.getInt("tatarafurnace.max_heat") : maxHeat;
//    }
//
//    // --- Drops ---
//    public void drops() {
//        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
//        for (int i = 0; i < itemHandler.getSlots(); i++) {
//            inventory.setItem(i, itemHandler.getStackInSlot(i));
//        }
//        Containers.dropContents(this.level, this.worldPosition, inventory);
//    }
//
//    // --- Main tick loop (fixed and stable) ---
//    public void tick(Level level, BlockPos pos, BlockState state) {
//        boolean dirty = false;
//
//        // clamp
//        if (burnTime < 0) burnTime = 0;
//
//        // --- Burning block: decrement burnTime and apply heat from current fuel curve ---
//        if (burnTime > 0) {
//            float heatPercent = maxHeat > 0 ? heatLevel / (float) maxHeat : 0f;
//            float burnRate = 1.0f + (heatPercent * 0.75f);
//            burnTime -= burnRate;
//            if (burnTime < 0) burnTime = 0;
//
//            // recompute heat gain based on the currently burning fuel item and current burnPercent
//            ItemStack burningStack = (currentFuelItem == null || currentFuelItem == Items.AIR) ? ItemStack.EMPTY : new ItemStack(currentFuelItem);
//            int gain = getFuelHeatPerTick(burningStack); // uses burnTime/fuelTime internally
//
//            if (gain > 0) {
//                // accumulate fractional heat so small gains are not lost
//                float rawGain = gain / thermalMass;
//                heatAccumulator += rawGain;
//                int applied = (int) heatAccumulator;
//                if (applied > 0) {
//                    heatAccumulator -= applied;
//                    int before = heatLevel;
//                    heatLevel += applied;
//                    if (heatLevel > maxHeat) heatLevel = maxHeat;
//                    if (heatLevel != before) dirty = true;
//                }
//            }
//        }
//
//        ItemStack fuelStack = itemHandler.getStackInSlot(TE_SLOT_FUEL);
//        ItemStack inputStack = itemHandler.getStackInSlot(TE_SLOT_INPUT);
//
//        boolean hasValidRecipe = hasRecipe();
//        boolean canSmelt = hasValidRecipe && isOutputSlotEmptyOrReceivable();
//
//        // --- Consume fuel when needed ---
//        if (burnTime == 0 && !fuelStack.isEmpty()) {
//            int baseBurn = getFuelBurnTime(fuelStack);
//
//            float heatPercent = maxHeat > 0 ? heatLevel / (float) maxHeat : 0f;
//            float efficiency = 1.0f - (heatPercent * 0.4f);
//            if (efficiency < 0.1f) efficiency = 0.1f; // avoid zero-length fuel
//
//            fuelTime = burnTime = Math.max(1, (int) (baseBurn * efficiency));
//
//            // remember what item is burning so we can compute curve while slot is empty
//            currentFuelItem = fuelStack.getItem();
//
//            // shrink the slot
//            fuelStack.shrink(1);
//
//            // reset accumulator so curve starts fresh for this fuel
//            heatAccumulator = 0f;
//
//            dirty = true;
//        }
//
//        // --- Smelting progress (server-side) ---
//
//        int requiredHeat = getRequiredHeatForInput(inputStack);
//        boolean heatReady = heatLevel >= requiredHeat;
//// Only allow smelting if we have fuel, a valid recipe, output space, and heat meets requirement
//        if (hasFuel() && canSmelt && heatReady) {
//            float heatPercent = maxHeat > 0 ? heatLevel / (float) maxHeat : 0f;
//            float speedMultiplier = 1.0f + heatPercent;
//
//            float beforeProgress = progress;
//            progress += speedMultiplier;
//
//            // mark dirty when integer progress changes so client updates
//            if ((int) beforeProgress != (int) progress) dirty = true;
//
//            if (progress >= maxProgress) {
//                craftItem();
//                progress = 0f;
//                dirty = true;
//            }
//
//            if (!state.getValue(TataraFurnaceBlock.LIT)) {
//                state = state.setValue(TataraFurnaceBlock.LIT, true);
//                level.setBlock(pos, state, 3);
//            }
//        } else {
//            // Not allowed to smelt (missing heat or other condition) — reset progress and unlit state
//            if (progress != 0f) {
//                progress = 0f;
//                dirty = true;
//            }
//            if (state.getValue(TataraFurnaceBlock.LIT)) {
//                state = state.setValue(TataraFurnaceBlock.LIT, false);
//                level.setBlock(pos, state, 3);
//            }
//        }
//
//
//        // --- Heat cooldown when not burning ---
//        if (burnTime == 0 && heatLevel > 0) {
//            int loss;
//            float heatPercent = maxHeat > 0 ? heatLevel / (float) maxHeat : 0f;
//
//            if (heatPercent > 0.8f) loss = 3;
//            else if (heatPercent > 0.50f) loss = 2;
//            else if (heatPercent > 0.25f) loss = 1;
//            else loss = 0;
//
//            float rawLoss = loss / thermalMass;
//            heatAccumulator -= rawLoss; // allow accumulator to go negative slightly
//            int appliedLoss = 0;
//            if (heatAccumulator <= -1f) {
//                appliedLoss = (int) -heatAccumulator;
//                heatAccumulator += appliedLoss; // remove applied loss from accumulator
//            }
//
//            // also apply integer loss based on rawLoss if it's >= 1
//            int intLoss = (int) rawLoss;
//            appliedLoss += intLoss;
//
//            if (appliedLoss > 0) {
//                int before = heatLevel;
//                heatLevel -= appliedLoss;
//                if (heatLevel < 0) heatLevel = 0;
//                if (heatLevel != before) dirty = true;
//            }
//        }
//
//        // --- Sync if anything changed ---
//        if (dirty) {
//            // mark changed and send block update so client receives ContainerData updates
//            setChanged();
//            if (!level.isClientSide()) {
//                level.sendBlockUpdated(pos, state, state, 3);
//            }
//        }
//    }
//
//    private boolean hasFuel() {
//        return burnTime > 0;
//    }
//
//    // --- Fuel burn times (simple overrides) ---
//    private int getFuelBurnTime(ItemStack stack) {
//        if (stack.isEmpty()) return 0;
//
//        if (stack.is(ModItems.MIZU_KORU.get())) {
//            return 3200; // custom long burn
//        }
//
//        if (stack.is(Items.COAL)) {
//            return 400; // vanilla-ish
//        }
//
//        // fallback to Forge/vanilla
//        return net.minecraftforge.common.ForgeHooks.getBurnTime(stack, null);
//    }
//
//    // --- Fuel heat curve (called every tick for the currently burning item) ---
//    private int getFuelHeatPerTick(ItemStack stack) {
//        if (stack.isEmpty()) return 0;
//        if (fuelTime <= 0) return 0; // avoid divide by zero
//
//        float burnPercent = 1f - (burnTime / (float) fuelTime);
//        burnPercent = Mth.clamp(burnPercent, 0f, 1f);
//
//        // Example curves (tune as desired)
//        if (stack.is(ModItems.MIZU_KORU.get())) {
//            if (burnPercent < 0.2f) return 1;
//            if (burnPercent < 0.7f) return 4;
//            return 2;
//        }
//
//        if (stack.is(Items.COAL)) {
//            if (burnPercent < 0.2f) return 0; // startup
//            if (burnPercent < 0.6f) return 1; // small steady heat early
//            if (burnPercent < 0.9f) return 2; // peak
//            return 0;
//        }
//
//        // default weak fuel
//        int burn = net.minecraftforge.common.ForgeHooks.getBurnTime(stack, null);
//        if (burn <= 0) return 0;
//        if (burnPercent < 0.5f) return 1;
//        return 0;
//    }
//
//    // Hard-coded required heat per input item
//    private int getRequiredHeatForInput(ItemStack input) {
//        if (input.isEmpty()) return 0;
//
//        // Example: Kuro Suna -> Incindium requires significant preheat
//        if (input.is(ModBlocks.KURO_SUNA.get().asItem())) {
//            return 800; // tune this (800 of 2500 is moderate)
//        }
//
//        // Default: no special requirement
//        return 0;
//    }
//
//    private void resetProgress() {
//        progress = 0f;
//        maxProgress = DEFAULT_MAX_PROGRESS;
//    }
//
//    private void craftItem() {
//        ItemStack input = itemHandler.getStackInSlot(TE_SLOT_INPUT);
//        ItemStack result = getHardcodedOutput(input);
//
//        if (result.isEmpty()) return;
//
//        ItemStack outputSlot = itemHandler.getStackInSlot(TE_SLOT_OUTPUT);
//
//        if (outputSlot.isEmpty()) {
//            itemHandler.setStackInSlot(TE_SLOT_OUTPUT, result.copy());
//        } else if (outputSlot.getItem() == result.getItem()) {
//            outputSlot.grow(result.getCount());
//        }
//
//        itemHandler.extractItem(TE_SLOT_INPUT, 1, false);
//    }
//
//    private boolean isOutputSlotEmptyOrReceivable() {
//        return this.itemHandler.getStackInSlot(TE_SLOT_OUTPUT).isEmpty() ||
//                this.itemHandler.getStackInSlot(TE_SLOT_OUTPUT).getCount() < this.itemHandler.getStackInSlot(TE_SLOT_OUTPUT).getMaxStackSize();
//    }
//
//    private boolean hasRecipe() {
//        ItemStack input = this.itemHandler.getStackInSlot(TE_SLOT_INPUT);
//        return input.is(ModBlocks.KURO_SUNA.get().asItem());
//    }
//
//    private Optional<RecipeHolder<TataraFurnaceRecipe>> getCurrentRecipe() {
//        return this.level.getRecipeManager()
//                .getRecipeFor(ModRecipes.BASE_TATARA_TYPE.get(), new TataraFurnaceRecipeInput(itemHandler.getStackInSlot(TE_SLOT_INPUT)), level);
//    }
//
//    private boolean canInsertItemIntoOutputSlot(ItemStack output) {
//        return itemHandler.getStackInSlot(TE_SLOT_OUTPUT).isEmpty() || this.itemHandler.getStackInSlot(TE_SLOT_OUTPUT).getItem() == output.getItem();
//    }
//
//    private boolean canInsertAmountIntoOutputSlot(int count) {
//        int maxCount = itemHandler.getStackInSlot(TE_SLOT_OUTPUT).isEmpty() ? 64 : itemHandler.getStackInSlot(TE_SLOT_OUTPUT).getMaxStackSize();
//        int currentCount = itemHandler.getStackInSlot(TE_SLOT_OUTPUT).getCount();
//        return maxCount >= currentCount + count;
//    }
//
//    private ItemStack getHardcodedOutput(ItemStack input) {
//        if (input.is(ModBlocks.KURO_SUNA.get().asItem())) {
//            return new ItemStack(ModItems.INCINDIUM_STEEL.get(), 1);
//        }
//        return ItemStack.EMPTY;
//    }
//
//    @Override
//    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
//        return saveWithoutMetadata(pRegistries);
//    }
//
//    @Nullable
//    @Override
//    public Packet<ClientGamePacketListener> getUpdatePacket() {
//        return ClientboundBlockEntityDataPacket.create(this);
//    }
//
//    // Getters for client-side use (GUI)
//    public int getHeatLevel() {
//        return heatLevel;
//    }
//
//    public int getMaxHeat() {
//        return maxHeat;
//    }
//
//
//
//    // Allow changing maxHeat for tiers
//    public void setMaxHeat(int maxHeat) {
//        this.maxHeat = maxHeat;
//        if (this.heatLevel > this.maxHeat) this.heatLevel = this.maxHeat;
//        setChanged();
//    }
//}
