package net.phospherion.kazekatana.block.entity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.phospherion.kazekatana.KazeKatana;
import net.phospherion.kazekatana.block.ModBlocks;
import net.phospherion.kazekatana.block.entity.specialz.AppakuTataraFurnaceBlockEntity;
import net.phospherion.kazekatana.block.entity.specialz.TataraFurnaceBlockEntity;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, KazeKatana.MOD_ID);

    public static final RegistryObject<BlockEntityType<TataraFurnaceBlockEntity>> TATARAFURNACE_BE =
            BLOCK_ENTITIES.register("tatarafurnace_be", () -> BlockEntityType.Builder.of(
                    TataraFurnaceBlockEntity::new, ModBlocks.TATARA_FURNACE.get()).build(null));

    public static final RegistryObject<BlockEntityType<AppakuTataraFurnaceBlockEntity>> APPAKU_TATARA_FURNACE_BE =
            BLOCK_ENTITIES.register("appaku_tatara_furnace_be", () -> BlockEntityType.Builder.of(
                    AppakuTataraFurnaceBlockEntity::new, ModBlocks.APPAKU_TATARA_FURNACE.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}