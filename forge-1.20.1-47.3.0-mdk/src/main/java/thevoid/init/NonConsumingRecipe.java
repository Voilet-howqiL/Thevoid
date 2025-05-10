package thevoid.init;

import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;


// CustomRecipe.java
public class NonConsumingRecipe implements Recipe<CraftingContainer> {
    private final ResourceLocation id;
    private final Ingredient tool; // 剑的标签
    private final ItemStack result; // 成品（肉夹馍）
    public static final RecipeType<NonConsumingRecipe> TYPE = new RecipeType<>() {
        @Override
        public String toString() { return "thevoid:non_consuming"; }
    };


    public NonConsumingRecipe(ResourceLocation id, Ingredient tool, ItemStack result) {
        this.id = id;
        this.tool = tool;
        this.result = result;
    }


    @Override
    public boolean matches(CraftingContainer inv, Level level) {
        boolean hasTool = false;
        for(int i=0; i<inv.getContainerSize(); i++){
            ItemStack stack = inv.getItem(i);
            if(tool.test(stack)) hasTool = true;
        }
        return hasTool; // 只要有任意剑即可匹配
    }

    @Override
    public ItemStack assemble(CraftingContainer craftingContainer, RegistryAccess registryAccess) {
        return result.copy(); // 返回肉夹馍
    }

    //是否可以在任意大小工作台上合成
    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return result.copy();
    }


    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv) {
        NonNullList<ItemStack> remains = Recipe.super.getRemainingItems(inv);
        // 找到剑并返还
        for(int i=0; i<inv.getContainerSize(); i++){
            ItemStack stack = inv.getItem(i);
            if(tool.test(stack)){
                remains.set(i, stack.copy()); // 返还剑
            }
        }
        return remains;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return TYPE;
    }



    public static class Serializer implements RecipeSerializer<NonConsumingRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        public ResourceLocation getId() {
            return new ResourceLocation("thevoid:non_consuming");
        }


        @Override
        public NonConsumingRecipe fromJson(ResourceLocation resourceLocation, com.google.gson.JsonObject json) {
            // 从JSON读取配方
            Ingredient tool = Ingredient.fromJson(json.get("tool"));
            ItemStack result = ShapedRecipe.itemStackFromJson(json.getAsJsonObject("result"));
            return new NonConsumingRecipe(resourceLocation, tool, result);
        }

        @Override
        public NonConsumingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            Ingredient tool = Ingredient.fromNetwork(buf);
            ItemStack result = buf.readItem();
            return new NonConsumingRecipe(id, tool, result);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, NonConsumingRecipe recipe) {
            recipe.tool.toNetwork(buf);
            buf.writeItem(recipe.result);
        }

    }

    // 其他必要方法（序列化等）
}
