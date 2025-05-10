package thevoid.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import thevoid.Myfirst_MOd.TheVoid;

public class ModRecipesI {


    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, TheVoid.MODID);

    // 注册自定义配方序列化器
    public static final RegistryObject<RecipeSerializer<NonConsumingRecipe>> NON_CONSUMING_RECIPE =
            RECIPE_SERIALIZERS.register("non_consuming", () -> NonConsumingRecipe.Serializer.INSTANCE);


}
