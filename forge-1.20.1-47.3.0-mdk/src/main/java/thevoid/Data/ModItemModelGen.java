package thevoid.Data;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;
import thevoid.Myfirst_MOd.TheVoid;
import thevoid.init.ModItems;

public class ModItemModelGen extends ItemModelProvider {
    public ModItemModelGen(PackOutput output,  ExistingFileHelper existingFileHelper) {
        super(output, TheVoid.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem(ModItems.ZEPHYRGRAVEN);
        simpleItem(ModItems.EASTERNHUMBURGER);
        simpleItem(ModItems.UNMIXED_DOUGH);
        simpleItem(ModItems.FLATBREAD);
        simpleItem(ModItems.DOUGH);
        simpleItem(ModItems.EASTERN_FLATBREAD);
        simpleItem(ModItems.JAMIE_WIN);
        toolItem(ModItems.TEST);
        toolItem(ModItems.HUM);
        toolItem(ModItems.BURNED_WOODEN_SWORD);
    }

    //自定义工具
    //只给普通的物品使用，其他的物品比如有特殊设计还是要手作
    public ItemModelBuilder simpleItem(RegistryObject<Item> item){
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(TheVoid.MODID,"item/" + item.getId().getPath()));
    }
    public ItemModelBuilder toolItem(RegistryObject<Item> item){
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/handheld")).texture("layer0",
                new ResourceLocation(TheVoid.MODID,"item/" + item.getId().getPath()));
    }
}
