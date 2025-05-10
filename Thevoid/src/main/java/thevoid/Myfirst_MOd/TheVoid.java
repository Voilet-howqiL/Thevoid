package thevoid.Myfirst_MOd;


import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.RegisterEvent;
import software.bernie.geckolib.GeckoLib;
import thevoid.Utils.ClientEvent.NetworkHandler;
import thevoid.events.AttackHandler;
import thevoid.events.Capabilities;
import thevoid.init.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import thevoid.items.DustBlade;


// 主类
@Mod(TheVoid.MODID)
public class TheVoid {

    public static final String MODID = "thevoid";

    public TheVoid() {
        // 初始化 GeckoLib
        GeckoLib.initialize();

        //获取主线事件，通常是原版的东西
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        //获取Forge主线事件，比如按键响应
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;

        // //注册模组内容// //添加到主线
        ModItems.ITEMS.register(modBus);
        ModEntity.ENTITIES.register(modBus);
        ModCreativeModeTab.CREATIVE_MODE_TABS.register(modBus);
        ModSounds.SOUNDS.register(modBus);
        ModBlocks.BLOCKS.register(modBus);
        ModRecipesI.RECIPE_SERIALIZERS.register(modBus);


        // //注册事件处理器// //
        forgeBus.register(Capabilities.class);
        forgeBus.register(AttackHandler.class); // 注册攻击事件
        NetworkHandler.register();
        modBus.addListener(this::registerRecipeTypes);


    }

    private void registerRecipeTypes(RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.RECIPE_TYPE)) {
            event.register(
                    Registries.RECIPE_TYPE,
                    new ResourceLocation("thevoid", "non_consuming"),
                    () -> NonConsumingRecipe.TYPE
            );
        }


    }
}

