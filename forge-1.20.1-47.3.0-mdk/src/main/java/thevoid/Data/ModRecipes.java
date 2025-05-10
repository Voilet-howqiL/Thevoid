package thevoid.Data;

import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import thevoid.Myfirst_MOd.TheVoid;
import thevoid.init.ModBlocks;
import thevoid.init.ModItems;


import java.util.List;
import java.util.function.Consumer;

public class ModRecipes extends RecipeProvider {
private static final List<ItemLike> MASROOM = List.of(ModBlocks.MASROOM.get());
    public ModRecipes(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.DESERT_SPEAR.get())
                .pattern("III")
                .pattern("F#F")
                .pattern(" # ")
                .define('#', Items.STICK) // 使用木棍作为中间材料
                .define('I', Items.IRON_INGOT) // 定义'I'为铁锭
                .define('F', ModItems.ZEPHYRGRAVEN.get()) // 定义'F'为羽毛
                .group("thevoid")
                .unlockedBy("has_iron", InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_INGOT))
                .save(pWriter);


        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ZEPHYRGRAVEN.get())
                .pattern(" L ")
                .pattern(" F ")
                .pattern(" G ")
                .define('L', Items.LAPIS_LAZULI)
                .define('G', Items.GOLD_NUGGET)
                .define('F', Items.FEATHER)
                .group("thevoid")
                .unlockedBy("has_feather", InventoryChangeTrigger.TriggerInstance.hasItems(Items.FEATHER))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.HUM.get())
                .pattern("I I")
                .pattern("LSL")
                .pattern(" I ")
                .define('L', Items.LEATHER)
                .define('S', Items.STICK)
                .define('I', Items.IRON_INGOT)
                .group("thevoid")
                .unlockedBy("has_Iron_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_INGOT))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.MODELTEST.get())
                .pattern(" II")
                .pattern(" SI")
                .pattern("S  ")
                .define('S', Items.STICK)
                .define('I', Items.IRON_INGOT)
                .group("thevoid")
                .unlockedBy("has_Iron_ingot1", InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_INGOT))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, ModItems.EASTERNHUMBURGER.get())
                .pattern(" W ")
                .pattern(" P ")
                .pattern(" W ")
                .define('W', Items.BREAD)
                .define('P', Items.COOKED_PORKCHOP)
                .group("thevoid.Food")
                .unlockedBy("has_wheat", InventoryChangeTrigger.TriggerInstance.hasItems(Items.WHEAT))
                .save(pWriter);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.UNMIXED_DOUGH.get(), 1)
                .requires(Items.WHEAT, 4)
                .requires(Items.WATER_BUCKET)
                .unlockedBy("has_wheat", has(Items.WHEAT))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.EASTERN_FLATBREAD.get(), 1)
                .requires(ModItems.FLATBREAD.get(), 1)
                .requires(Items.COOKED_PORKCHOP)
                .unlockedBy("has_flatbread", has(ModItems.FLATBREAD.get()))
                .save(pWriter);

        //矿物合成表：
        oreSmelting(pWriter, MASROOM,RecipeCategory.MISC,Items.SLIME_BALL,0.25f,100,"masroom");

        //食物合成表：
        /// 营火版本
        SimpleCookingRecipeBuilder.campfireCooking(
                Ingredient.of(ModItems.DOUGH.get()),
                RecipeCategory.FOOD,
                ModItems.FLATBREAD.get(),
                0.2f,
                600)
                .unlockedBy("has_dough",has(ModItems.DOUGH.get()))
                .save(pWriter,TheVoid.MODID + ":flatbread_campfire");

        SimpleCookingRecipeBuilder.campfireCooking(
                    Ingredient.of(ModItems.UNMIXED_DOUGH.get()),
                        RecipeCategory.FOOD,
                        ModItems.DOUGH.get(),
                        0.2f,
                        600)
                .unlockedBy("has_dough",has(ModItems.UNMIXED_DOUGH.get()))
                .save(pWriter,TheVoid.MODID + ":dough_campfire");

        SimpleCookingRecipeBuilder.campfireCooking(
                        Ingredient.of(Items.WOODEN_SWORD),
                        RecipeCategory.MISC,
                        ModItems.DUST_BLADE.get(),
                        0f,
                        100)
                .unlockedBy("has_woodenBlade",has(Items.WOODEN_SWORD))
                .save(pWriter,TheVoid.MODID + ":dust_blade_campfire");


        /// 烟熏炉
        SimpleCookingRecipeBuilder.smoking(
                        Ingredient.of(ModItems.DOUGH.get()),
                        RecipeCategory.FOOD,
                        ModItems.FLATBREAD.get(),
                        0.2f,
                        300)
                .unlockedBy("has_dough",has(ModItems.DOUGH.get()))
                .save(pWriter,TheVoid.MODID + ":flatbread_smoking");
        /// 熔炉
        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(ModItems.DOUGH.get()),
                        RecipeCategory.FOOD,
                        ModItems.FLATBREAD.get(),
                        0.2f,
                        400)
                .unlockedBy("has_dough",has(ModItems.DOUGH.get()))
                .save(pWriter,TheVoid.MODID + ":flatbread_smelting");

        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(ModItems.UNMIXED_DOUGH.get()),
                        RecipeCategory.FOOD,
                        ModItems.DOUGH.get(),
                        0.2f,
                        200)
                .unlockedBy("has_dough",has(ModItems.UNMIXED_DOUGH.get()))
                .save(pWriter,TheVoid.MODID + ":dough_smelting");

    }


    protected static void oreSmelting(Consumer<FinishedRecipe> p_250654_, List<ItemLike> p_250172_, RecipeCategory p_250588_, ItemLike p_251868_, float p_250789_, int p_252144_, String p_251687_) {
        oreCooking(p_250654_, RecipeSerializer.SMELTING_RECIPE, p_250172_, p_250588_, p_251868_, p_250789_, p_252144_, p_251687_, "_from_smelting");
    }

    protected static void oreBlasting(Consumer<FinishedRecipe> p_248775_, List<ItemLike> p_251504_, RecipeCategory p_248846_, ItemLike p_249735_, float p_248783_, int p_250303_, String p_251984_) {
        oreCooking(p_248775_, RecipeSerializer.BLASTING_RECIPE, p_251504_, p_248846_, p_249735_, p_248783_, p_250303_, p_251984_, "_from_blasting");
    }

    protected static void oreCooking(Consumer<FinishedRecipe> p_250791_, RecipeSerializer<? extends AbstractCookingRecipe> p_251817_, List<ItemLike> p_249619_, RecipeCategory p_251154_, ItemLike p_250066_, float p_251871_, int p_251316_, String p_251450_, String p_249236_) {
        for(ItemLike itemlike : p_249619_) {
            SimpleCookingRecipeBuilder
                    .generic(Ingredient.of(new ItemLike[]{itemlike}), p_251154_, p_250066_, p_251871_, p_251316_, p_251817_)
                    .group(p_251450_)
                    .unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(p_250791_,  TheVoid.MODID + ":"+ getItemName(p_250066_) + p_249236_ + "_" + getItemName(itemlike));
        }
    }
}