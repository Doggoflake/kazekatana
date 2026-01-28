package net.phospherion.kazekatana.screen.specialz;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;
import net.phospherion.kazekatana.block.ModBlocks;
import net.phospherion.kazekatana.block.entity.specialz.TataraFurnaceBlockEntity;
import net.phospherion.kazekatana.item.ModItems;
import net.phospherion.kazekatana.screen.ModMenuTypes;
import org.jetbrains.annotations.NotNull;

public class TataraFurnaceMenu extends AbstractContainerMenu {
    public final TataraFurnaceBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    // TE slot indices (must match block entity)
    private static final int TE_SLOT_INPUT  = 0;
    private static final int TE_SLOT_FUEL   = 1;
    private static final int TE_SLOT_OUTPUT = 2;
    private static final int TE_SLOT_MODU   = 3;
    private static final int TE_INVENTORY_SLOT_COUNT = 4;

    public TataraFurnaceMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(5));
    }

    public TataraFurnaceMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.TATARA_FURNACE_MENU.get(), pContainerId);
        this.blockEntity = (TataraFurnaceBlockEntity) entity;
        this.level = inv.player.level();
        this.data = data;

        // Add player inventory/hotbar first so TE slots are after vanilla slots
        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        // Add TE slots
        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(itemHandler -> {
            // Input slot (44, 30)
            this.addSlot(new SlotItemHandler(itemHandler, TE_SLOT_INPUT, 44, 30) {
                @Override
                public boolean mayPlace(@NotNull ItemStack stack) {
                    return stack.is(ModBlocks.KURO_SUNA.get().asItem());
                }
            });

            // Fuel slot (80, 57)
            this.addSlot(new SlotItemHandler(itemHandler, TE_SLOT_FUEL, 80, 57) {
                @Override
                public boolean mayPlace(@NotNull ItemStack stack) {
                    return stack.is(ModItems.MIZU_KORU.get()) || ForgeHooks.getBurnTime(stack, null) > 0;
                }
            });

            // Output slot (116, 30) - output only
            this.addSlot(new SlotItemHandler(itemHandler, TE_SLOT_OUTPUT, 116, 30) {
                @Override
                public boolean mayPlace(@NotNull ItemStack stack) {
                    return false;
                }

                @Override
                public void onTake(Player thePlayer, ItemStack stack) {
                    super.onTake(thePlayer, stack);
                }
            });

            // Modu slot (152, 57)
            this.addSlot(new SlotItemHandler(itemHandler, TE_SLOT_MODU, 152, 57) {
                @Override
                public boolean mayPlace(@NotNull ItemStack stack) {
                    // placeholder check; replace with your auto-feed core item
                    return stack.is(ModItems.ZERO_STEEL.get());
                }
            });
        });

        // Register data so client receives progress/heat values
        this.addDataSlots(data);
    }

    public boolean isCrafting() {
        return data.get(0) > 0;
    }

    /** Current smelt progress (server-synced via ContainerData) */
    public int getProgress() {
        return this.data.get(0);
    }

    /** Max smelt progress (server-synced via ContainerData) */
    public int getMaxProgress() {
        return this.data.get(1);
    }

    /** Current heat level (server-synced via ContainerData) */
    public int getHeat() {
        return this.data.get(2);
    }

    /** Max heat (server-synced via ContainerData) */
    public int getMaxHeat() {
        return this.data.get(3);
    }

    public int getRequiredHeat() {
        // index 4 is requiredHeat
        return this.data.get(4);
    }

    public int getScaledArrowProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        int arrowPixelSize = 24;
        return maxProgress != 0 && progress != 0 ? progress * arrowPixelSize / maxProgress : 0;
    }

    public int getScaledCrystalProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        int crystalPixelSize = 16;
        return maxProgress != 0 && progress != 0 ? progress * crystalPixelSize / maxProgress : 0;
    }

    // Slot indexing constants for quickMoveStack
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;
    private static final int TE_INVENTORY_SLOT_COUNT_TOTAL = TE_INVENTORY_SLOT_COUNT;

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        Slot sourceSlot = this.slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // If the clicked slot is in the player inventory
        if (index < TE_INVENTORY_FIRST_SLOT_INDEX) {
            // Try to move into TE input, fuel, or modu in that order
            if (sourceStack.is(ModBlocks.KURO_SUNA.get().asItem())) {
                if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX + TE_SLOT_INPUT, TE_INVENTORY_FIRST_SLOT_INDEX + TE_SLOT_INPUT + 1, false))
                    return ItemStack.EMPTY;
            } else if (sourceStack.is(ModItems.MIZU_KORU.get()) || ForgeHooks.getBurnTime(sourceStack, null) > 0) {
                if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX + TE_SLOT_FUEL, TE_INVENTORY_FIRST_SLOT_INDEX + TE_SLOT_FUEL + 1, false))
                    return ItemStack.EMPTY;
            } else if (sourceStack.is(ModItems.ZERO_STEEL.get())) {
                if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX + TE_SLOT_MODU, TE_INVENTORY_FIRST_SLOT_INDEX + TE_SLOT_MODU + 1, false))
                    return ItemStack.EMPTY;
            } else {
                // fallback: try to put into hotbar or main inventory
                if (index < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT - HOTBAR_SLOT_COUNT) {
                    if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT - HOTBAR_SLOT_COUNT, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false))
                        return ItemStack.EMPTY;
                } else {
                    if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT - HOTBAR_SLOT_COUNT, false))
                        return ItemStack.EMPTY;
                }
            }
        } else if (index < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT_TOTAL) {
            // If the clicked slot is in the TE, move to player inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, true))
                return ItemStack.EMPTY;
        } else {
            return ItemStack.EMPTY;
        }

        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }

        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                pPlayer, ModBlocks.TATARA_FURNACE.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}
