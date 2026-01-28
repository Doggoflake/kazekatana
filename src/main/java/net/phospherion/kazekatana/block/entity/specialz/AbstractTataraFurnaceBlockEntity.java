package net.phospherion.kazekatana.block.entity.specialz;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.phospherion.kazekatana.screen.specialz.TataraFurnaceMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractTataraFurnaceBlockEntity extends BlockEntity implements MenuProvider {

    // ------------------------------------------------------------
    // Hardcoded recipe container
    // ------------------------------------------------------------
    public static class HardcodedRecipe {
        public final ItemStack output;
        public final int requiredHeat;
        public final int smeltTime;

        public HardcodedRecipe(ItemStack output, int requiredHeat, int smeltTime) {
            this.output = output;
            this.requiredHeat = requiredHeat;
            this.smeltTime = smeltTime;
        }
    }

    // ------------------------------------------------------------
    // Inventory
    // ------------------------------------------------------------
    public final ItemStackHandler itemHandler = new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    protected static final int SLOT_INPUT  = 0;
    protected static final int SLOT_FUEL   = 1;
    protected static final int SLOT_OUTPUT = 2;
    protected static final int SLOT_MODU   = 3;

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    // ------------------------------------------------------------
    // Smelting Progress
    // ------------------------------------------------------------

    protected float progress = 0f;
    protected int maxProgress = 72;
    protected static final int DEFAULT_MAX_PROGRESS = 72;

    // ------------------------------------------------------------
    // Fuel / Burn
    // ------------------------------------------------------------

    protected int burnTime = 0;
    protected int fuelTime = 0;
    protected Item currentFuelItem = Items.AIR;

    // ------------------------------------------------------------
    // Heat System
    // ------------------------------------------------------------

    protected int heatLevel = 0;
    protected float heatAccumulator = 0f;
    protected float thermalMass;

    // ------------------------------------------------------------
    // Data Sync
    // ------------------------------------------------------------

    protected final ContainerData data;

    // ------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------

    public AbstractTataraFurnaceBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        this.thermalMass = getThermalMass();

        this.data = new SimpleContainerData(5) {
            @Override
            public int get(int i) {
                return switch (i) {
                    case 0 -> (int) progress;
                    case 1 -> maxProgress;
                    case 2 -> heatLevel;
                    case 3 -> getMaxHeatValue();
                    case 4 -> getCurrentRequiredHeat();
                    default -> 0;
                };
            }

            @Override
            public void set(int i, int value) {
                switch (i) {
                    case 0 -> progress = value;
                    case 1 -> maxProgress = value;
                    case 2 -> heatLevel = value;
                }
            }

            @Override
            public int getCount() {
                return 5;
            }
        };
    }

    // ------------------------------------------------------------
    // Abstract Tier Hooks
    // ------------------------------------------------------------
    protected abstract int getMaxHeatValue();
    protected abstract float getThermalMass();
    protected abstract float getSmeltSpeedMultiplier();

    protected abstract boolean isFuel(ItemStack stack);
    protected abstract int getFuelBurnTime(ItemStack stack);
    protected abstract int getFuelHeatPerTick(ItemStack stack, float burnPercent);
    // default returns 1.0 so existing tiers keep current behavior unless they override
    protected float getFuelEfficiency(ItemStack stack) {return 1.0f;}

    protected abstract HardcodedRecipe getRecipeFor(ItemStack input);

    protected abstract Component getFurnaceName();

    // ------------------------------------------------------------
    // Menu + Name
    // ------------------------------------------------------------
    @Override
    public Component getDisplayName() {
        return getFurnaceName();
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new TataraFurnaceMenu(id, inv, this, this.data);
    }

    // ------------------------------------------------------------
    // Capabilities
    // ------------------------------------------------------------
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    // ------------------------------------------------------------
    // NBT Save/Load
    // ------------------------------------------------------------
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("inventory", itemHandler.serializeNBT(registries));
        tag.putInt("progress", (int) progress);
        tag.putInt("burnTime", burnTime);
        tag.putInt("fuelTime", fuelTime);
        tag.putInt("heat", heatLevel);
        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        itemHandler.deserializeNBT(registries, tag.getCompound("inventory"));
        progress = tag.getInt("progress");
        burnTime = tag.getInt("burnTime");
        fuelTime = tag.getInt("fuelTime");
        heatLevel = tag.getInt("heat");
    }

    // ------------------------------------------------------------
    // Networking
    // ------------------------------------------------------------
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    // ------------------------------------------------------------
    // Main Tick Logic
    // ------------------------------------------------------------
    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level == null || level.isClientSide()) return;

        boolean dirty = false;

        // -----------------------------
        // Burning fuel
        // -----------------------------
        if (burnTime > 0) {
            float burnPercent = 1f - (burnTime / (float) fuelTime);
            burnTime--;

            int baseGain = getFuelHeatPerTick(new ItemStack(currentFuelItem), burnPercent);

            // use per-fuel efficiency for heat as well (use currentFuelItem because slot may be empty)
            float eff = Math.max(0.01f, getFuelEfficiency(new ItemStack(currentFuelItem)));
            int heatGain = Math.max(0, Math.round(baseGain * eff));

            applyHeat(heatGain);
            dirty = true;
        }



        // -----------------------------
        // Try to consume new fuel
        // -----------------------------
        // when starting a new fuel
        if (burnTime <= 0) {
            ItemStack fuelStack = itemHandler.getStackInSlot(SLOT_FUEL);
            if (!fuelStack.isEmpty() && isFuel(fuelStack)) {
                int baseBurn = getFuelBurnTime(fuelStack); // raw burn time for this item

                // per-fuel efficiency multiplier
                float efficiency = Math.max(0.01f, getFuelEfficiency(fuelStack)); // clamp to avoid zero
                fuelTime = burnTime = Math.max(1, (int) (baseBurn * efficiency));

                currentFuelItem = fuelStack.getItem();
                fuelStack.shrink(1);
                heatAccumulator = 0f;
                dirty = true;
            }
        }


        // -----------------------------
        // Smelting (hardcoded recipes)
        // -----------------------------
        ItemStack input = itemHandler.getStackInSlot(SLOT_INPUT);
        HardcodedRecipe recipe = getRecipeFor(input);

        if (recipe != null) {
            int requiredHeat = recipe.requiredHeat;
            int smeltTime = recipe.smeltTime;
            maxProgress = smeltTime;

            boolean canSmelt = heatLevel >= requiredHeat && canOutputAccept(recipe.output);

            if (canSmelt) {
                float heatFactor = Math.min(heatLevel / (float) requiredHeat, 3f);
                float speed = getSmeltSpeedMultiplier() * heatFactor;

                progress += speed;

                if (progress >= maxProgress) {
                    craftHardcoded(recipe);
                    progress = 0f;
                }

                dirty = true;
            } else if (progress != 0f) {
                progress = 0f;
                dirty = true;
            }
        } else {
            if (progress != 0f) {
                progress = 0f;
                dirty = true;
            }
            maxProgress = DEFAULT_MAX_PROGRESS;
        }

        // -----------------------------
        // Cooling
        // -----------------------------
        if (burnTime <= 0 && heatLevel > 0) {
            coolDown();
            dirty = true;
        }

        // -----------------------------
        // Sync
        // -----------------------------
        if (dirty) {
            setChanged();
            level.sendBlockUpdated(pos, state, state, 3);
        }
    }

    // ------------------------------------------------------------
    // Heat Helpers
    // ------------------------------------------------------------
    private void applyHeat(int gain) {
        if (gain <= 0) return;

        heatAccumulator += gain / thermalMass;
        int applied = (int) heatAccumulator;

        if (applied > 0) {
            heatAccumulator -= applied;
            heatLevel += applied;
            if (heatLevel > getMaxHeatValue()) heatLevel = getMaxHeatValue();
        }
    }

    private void coolDown() {
        int maxHeat = getMaxHeatValue();
        float heatPercent = maxHeat > 0 ? heatLevel / (float) maxHeat : 0f;

        int loss = (heatPercent > 0.8f) ? 3 :
                (heatPercent > 0.5f) ? 2 :
                        (heatPercent > 0.25f) ? 1 : 0;

        heatAccumulator -= loss / thermalMass;

        if (heatAccumulator <= -1f) {
            int appliedLoss = (int) -heatAccumulator;
            heatAccumulator += appliedLoss;
            heatLevel -= appliedLoss;
            if (heatLevel < 0) heatLevel = 0;
        }
    }

    // ------------------------------------------------------------
    // Crafting
    // ------------------------------------------------------------
    private void craftHardcoded(HardcodedRecipe recipe) {
        ItemStack output = itemHandler.getStackInSlot(SLOT_OUTPUT);

        if (output.isEmpty()) {
            itemHandler.setStackInSlot(SLOT_OUTPUT, recipe.output.copy());
        } else if (output.getItem() == recipe.output.getItem()) {
            output.grow(recipe.output.getCount());
        }

        itemHandler.extractItem(SLOT_INPUT, 1, false);
    }

    private boolean canOutputAccept(ItemStack result) {
        if (result.isEmpty()) return false;

        ItemStack output = itemHandler.getStackInSlot(SLOT_OUTPUT);
        return output.isEmpty() ||
                (output.getItem() == result.getItem() &&
                        output.getCount() + result.getCount() <= output.getMaxStackSize());
    }

    // ------------------------------------------------------------
    // Drops
    // ------------------------------------------------------------
    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        if (level != null) {
            Containers.dropContents(this.level, this.worldPosition, inventory);
        }
    }

    // ------------------------------------------------------------
    // GUI Helpers
    // ------------------------------------------------------------
    private int getCurrentRequiredHeat() {
        ItemStack input = itemHandler.getStackInSlot(SLOT_INPUT);
        HardcodedRecipe recipe = getRecipeFor(input);
        return recipe != null ? recipe.requiredHeat : 0;
    }

    public int getHeatLevel() {
        return heatLevel;
    }

    public int getMaxHeat() {
        return getMaxHeatValue();
    }
}
