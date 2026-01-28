package net.phospherion.kazekatana.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.phospherion.kazekatana.KazeKatana;
import net.phospherion.kazekatana.item.specialz.SkySplitItem;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, KazeKatana.MOD_ID);


    public static final RegistryObject<Item> INCINDIUM_STEEL = ITEMS.register("incindium_steel",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> INCINDIUM_TOSHIN = ITEMS.register("incindium_toshin",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> INCINDIUM_KOSHIRAE = ITEMS.register("incindium_koshirae",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> ZERO_STEEL = ITEMS.register("zero_steel",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ZERO_TOSHIN = ITEMS.register("zero_toshin",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ZERO_KOSHIRAE = ITEMS.register("zero_koshirae",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> CRYSTAL_STEEL = ITEMS.register("crystal_steel",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CRYSTAL_TOSHIN = ITEMS.register("crystal_toshin",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CRYSTAL_KOSHIRAE = ITEMS.register("crystal_koshirae",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> CARDINAL_STEEL = ITEMS.register("cardinal_steel",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CARDINAL_TOSHIN = ITEMS.register("cardinal_toshin",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CARDINAL_KOSHIRAE = ITEMS.register("cardinal_koshirae",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> MIZU_KORU = ITEMS.register("mizu_koru",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> INFERNAL_KORU = ITEMS.register("infernal_koru",
            () -> new Item(new Item.Properties()));

    //public static final RegistryObject<Item> SKY_SPLIT = ITEMS.register("sky_split",
      //      () -> new SkySplitItem(new Item.Properties()));


    /*public static final RegistryObject<Item> WIND_KATANA = ITEMS.register("wind_katana",
            () -> new WindKatana(new Item.Properties()));
                    */


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

}