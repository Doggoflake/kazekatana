//package net.phospherion.kazekatana.recipe;
//
//import net.minecraft.world.item.crafting.RecipeSerializer;
//import net.minecraft.world.item.crafting.RecipeType;
//import net.minecraftforge.eventbus.api.IEventBus;
//import net.minecraftforge.registries.DeferredRegister;
//import net.minecraftforge.registries.ForgeRegistries;
//import net.minecraftforge.registries.RegistryObject;
//import net.phospherion.kazekatana.KazeKatana;
//import net.phospherion.kazekatana.recipe.tf.TataraFurnaceRecipe;
//
//public class ModRecipes {
//
//    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
//            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, KazeKatana.MOD_ID);
//
//    public static final DeferredRegister<RecipeType<?>> TYPES =
//            DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, KazeKatana.MOD_ID);
//
//    // Base-tier Tatara Furnace recipe type
//    public static final RegistryObject<RecipeSerializer<TataraFurnaceRecipe>> BASE_TATARA_SERIALIZER =
//            SERIALIZERS.register("basetatarasmelting", TataraFurnaceRecipe.Serializer::new);
//
//    public static final RegistryObject<RecipeType<TataraFurnaceRecipe>> BASE_TATARA_TYPE =
//            TYPES.register("basetatarasmelting", () -> new RecipeType<TataraFurnaceRecipe>() {
//                @Override
//                public String toString() {
//                    return "basetatarasmelting";
//                }
//            });
//
//    public static void register(IEventBus eventBus) {
//        SERIALIZERS.register(eventBus);
//        TYPES.register(eventBus);
//    }
//}
