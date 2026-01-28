package net.phospherion.kazekatana.datagen;



import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.phospherion.kazekatana.KazeKatana;
import net.phospherion.kazekatana.block.ModBlocks;
import net.phospherion.kazekatana.item.ModItems;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, KazeKatana.MOD_ID);


   /* public static final RegistryObject<CreativeModeTab> KATANA_ITEMS_TAB = CREATIVE_MODE_TABS.register("katana_items_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.WIND_KATANA.get()))
                    .title(Component.translatable("creativetab.katana"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModItems.WIND_KATANA.get());

                    }).build());
*/

    public static final RegistryObject<CreativeModeTab> KATANA_MATERIALS_ITEMS_TAB = CREATIVE_MODE_TABS.register("katana_materials_items_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.INCINDIUM_STEEL.get()))/*.withTabsBefore(KATANA_ITEMS_TAB.getId())*/
                    .title(Component.translatable("creativetab.katana_materials"))
                    .displayItems((itemDisplayParameters, output) -> {

                        output.accept(ModItems.INCINDIUM_STEEL.get());

                        output.accept(ModItems.INCINDIUM_TOSHIN.get());

                        output.accept(ModItems.INCINDIUM_KOSHIRAE.get());

                        output.accept(ModItems.ZERO_STEEL.get());

                        output.accept(ModItems.ZERO_TOSHIN.get());

                        output.accept(ModItems.ZERO_KOSHIRAE.get());

                        output.accept(ModItems.CRYSTAL_STEEL.get());

                        output.accept(ModItems.CRYSTAL_TOSHIN.get());

                        output.accept(ModItems.CRYSTAL_KOSHIRAE.get());

                        output.accept(ModItems.CARDINAL_STEEL.get());

                        output.accept(ModItems.CARDINAL_TOSHIN.get());

                        output.accept(ModItems.CARDINAL_KOSHIRAE.get());


                        output.accept(ModItems.MIZU_KORU.get());

                        output.accept(ModItems.INFERNAL_KORU.get());

                    }).build());



    public static final RegistryObject<CreativeModeTab> KAZE_BLOCKS_TAB = CREATIVE_MODE_TABS.register("kaze_blocks_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.INCINDIUM_STEEL_BLOCK.get())).withTabsBefore(KATANA_MATERIALS_ITEMS_TAB.getId())
                    .title(Component.translatable("creativetab.blocks"))
                    .displayItems((itemDisplayParameters, output) -> {

                        output.accept(ModBlocks.KURO_SUNA.get());

                        output.accept(ModBlocks.CRIMSON_CORE.get());

                        output.accept(ModBlocks.INCINDIUM_STEEL_BLOCK.get());
                        output.accept(ModBlocks.CRYSTAL_STEEL_BLOCK.get());

                        output.accept(ModBlocks.KOGARE_KORU.get());

                        output.accept(ModBlocks.SHINKU_KOBUTSU.get());

                        output.accept(ModBlocks.TATARA_FURNACE.get());

                        output.accept(ModBlocks.APPAKU_TATARA_FURNACE.get());


                        output.accept(ModBlocks.INFERNITE.get());
                    }).build());


    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
