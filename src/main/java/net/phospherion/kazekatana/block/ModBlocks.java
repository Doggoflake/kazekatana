package net.phospherion.kazekatana.block;

import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.phospherion.kazekatana.KazeKatana;
import net.phospherion.kazekatana.block.specialz.AppakuTataraFurnaceBlock;
import net.phospherion.kazekatana.block.specialz.TataraFurnaceBlock;
import net.phospherion.kazekatana.item.ModItems;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, KazeKatana.MOD_ID);

    public static final RegistryObject<Block> INCINDIUM_STEEL_BLOCK = registerBlock("incindium_steel_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(4f).requiresCorrectToolForDrops().sound(SoundType.METAL)));

    public static final RegistryObject<Block> CRYSTAL_STEEL_BLOCK = registerBlock("crystal_steel_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(4f).requiresCorrectToolForDrops().sound(SoundType.AMETHYST_CLUSTER)));

    public static final RegistryObject<Block> KURO_SUNA = registerBlock("kuro_suna",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(3f).requiresCorrectToolForDrops().sound(SoundType.BONE_BLOCK)));//Definitely Not Lore

    public static final RegistryObject<Block> CRIMSON_CORE = registerBlock("crimson_core",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(3f).requiresCorrectToolForDrops().sound(SoundType.ANCIENT_DEBRIS)));//Definitely Not Lore


    public static final RegistryObject<Block> INFERNITE = registerBlock("infernite",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(3f).requiresCorrectToolForDrops().sound(SoundType.SUSPICIOUS_GRAVEL)));

    //Ores or With Multiple drops and what not
    public static final RegistryObject<Block> SHINKU_KOBUTSU = registerBlock("shinku_kobutsu",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(3f).requiresCorrectToolForDrops().sound(SoundType.WEEPING_VINES)));//Definitely Not Lore

    public static final RegistryObject<Block> KOGARE_KORU = registerBlock("kogare_koru",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(3f).requiresCorrectToolForDrops().sound(SoundType.BONE_BLOCK)));//Definitely Not Lore

    //Furnaces
    public static final RegistryObject<Block> TATARA_FURNACE = registerBlock("tatara_furnace",
      () -> new TataraFurnaceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));

    public static final RegistryObject<Block> APPAKU_TATARA_FURNACE = registerBlock("appaku_tatara_furnace",
            () -> new AppakuTataraFurnaceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.NETHERITE_BLOCK)));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
