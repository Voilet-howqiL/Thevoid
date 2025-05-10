package thevoid.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;


import static thevoid.Myfirst_MOd.TheVoid.MODID;

public class ModCreativeModeTab {

    //每次我们都需要获取注册表
    public static final DeferredRegister<CreativeModeTab>CREATIVE_MODE_TABS= DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

//武器
    public static final RegistryObject<CreativeModeTab> THEVOID_TAB = CREATIVE_MODE_TABS.register("thevoid-tab", () ->
            CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.thevoid")) // 名称（语言文件定义）
            .icon(() -> new ItemStack(ModItems.CHRONONACHT.get())) // 图标（用你的模组物品）
            .displayItems((params, output) -> {
                // 在这里添加你的模组物品
                output.accept(ModItems.DESERT_SPEAR.get());
                output.accept(ModItems.DARKIN_GREATSWORD.get());
                output.accept(ModItems.CHRONONACHT.get());
                output.accept(ModItems.BONEHANDLE_SPEAR.get());
                output.accept(ModItems.ZEPHYRGRAVEN.get());
                output.accept(ModItems.TEST.get());
                output.accept(ModItems.HUM.get());
                output.accept(ModItems.MODELTEST.get());
                output.accept(ModItems.DUST_BLADE.get());
                output.accept(ModItems.BURNED_WOODEN_SWORD.get());
                // 也可以添加其他物品（如原版物品或别的模组物品）
            })
            .build()
    );
//唱片
    public static final RegistryObject<CreativeModeTab> THEVOID_DISC_TAB = CREATIVE_MODE_TABS.register("thevoid-disc", () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.thevoid-DISC")) // 名称（语言文件定义）
                    .icon(() -> new ItemStack(ModItems.DISC_LEIQIAO.get())) // 图标（用你的模组物品）
                    .displayItems((params, output) -> {


                       /// 在这里添加唱片组物品
                        output.accept(ModItems.DISC_LEIQIAO.get());
                        output.accept(ModItems.DISC_LANGRENQINGGE.get());
                        output.accept(ModItems.DISC_LASTDANCE.get());
                        output.accept(ModItems.JAMIE_WIN.get());



                    })
                    .build()
    );
    //物品与方块
    public static final RegistryObject<CreativeModeTab>THEVOID_ITEMANDBLOCK_TAB  = CREATIVE_MODE_TABS.register("thevoid-itemandblock",()->
            CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.thevoid-item"))
                    .icon(()->new ItemStack(ModItems.ZEPHYRGRAVEN.get()))
                    .displayItems((itemDisplayParameters, output) ->{


                        output.accept(ModItems.ZEPHYRGRAVEN.get());
                        output.accept(ModBlocks.MASROOM.get());
                        output.accept(ModBlocks.OAKSTOOL.get());
                        output.accept(ModItems.EASTERNHUMBURGER.get());
                        output.accept(ModItems.UNMIXED_DOUGH.get());
                        output.accept(ModItems.DOUGH.get());
                        output.accept(ModItems.FLATBREAD.get());
                        output.accept(ModItems.EASTERN_FLATBREAD.get());
                        output.accept(ModBlocks.TALLGRASS.get());





                    } )
                    .build()
    );

}
