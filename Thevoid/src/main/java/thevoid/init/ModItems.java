package thevoid.init;

import net.minecraft.world.item.Rarity;
import net.minecraftforge.fml.common.Mod;
import thevoid.Myfirst_MOd.TheVoid;
import thevoid.items.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import thevoid.items.Disc.Disc_Langrenqingge;
import thevoid.items.Disc.Disc_Lastdance;
import thevoid.items.Disc.Disc_Leiqiao;
import thevoid.items.Disc.Jamie_Win;
import thevoid.items.Foods.*;
import thevoid.items.custom.Zephyrgraven;

//          此页是声明|注册
public class ModItems{
    public static final DeferredRegister<Item> ITEMS =DeferredRegister.create(ForgeRegistries.ITEMS, TheVoid.MODID);

    /// 武器类

    public static final
    //注册的暗裔巨剑物品，名称什么的都在下面：
    RegistryObject<Item> DARKIN_GREATSWORD =ITEMS.register("darkin_greatsword",()-> {
        return new DarkinSword
                (Tiers.NETHERITE,
                        8,
                        -2.4F,
                        new Item.Properties()
                                .rarity(Rarity.EPIC)
                                .fireResistant()


                );
    });
    public  static final
            //注册的骨头手柄短矛
    RegistryObject<Item> BONEHANDLE_SPEAR = ITEMS.register("bonehandle_spear",()->{
       return  new BoneHandleSpear(
               Tiers.IRON,
               2,
               -2.0F,
               new Item.Properties()
                       .rarity(Rarity.COMMON)

       );
    });
    public static final
        //注册的沙漠长枪
    RegistryObject<Item> DESERT_SPEAR = ITEMS.register("desert_spear",()->{
        return new DesertSpear(
                Tiers.IRON,
                3,
                -3.0f,
                new Item.Properties()
                        .rarity(Rarity.COMMON)
                        .durability(670)

        );
    });

    public static final
    RegistryObject<Item> CHRONONACHT = ITEMS.register("chrononacht",()->{
        return new Chrononacht(
                Tiers.IRON,
                2,
                -3.0f,
                new Item.Properties()
                    .rarity(Rarity.COMMON)
                    .durability( 70)
         );

    });
    public static final
    RegistryObject<Item>TEST =ITEMS.register("test",()->{
        return new ResonanceBlade(Tiers.WOOD,
                5,
                -3.0f,
                new Item.Properties()
                        .durability(170));
    });
    public static final
    RegistryObject<Item>HUM =ITEMS.register("hum",()->{
        return new Hum(Tiers.IRON,
                2,
                -2.4f,
                new Item.Properties()
                        .durability(170));
    });
    public static final
    RegistryObject<Item>MODELTEST =ITEMS.register("model_test",()->{
        return new ModelTest(Tiers.IRON,
                2,
                -2.4f,
                new Item.Properties()
                        .durability(160));
    });

    public static final
    RegistryObject<Item>DUST_BLADE =ITEMS.register("dust_blade",()->{
        return new DustBlade(Tiers.WOOD,
                4,
                -2.4f,
                new Item.Properties()
                        .durability(30));
    });
    public static final
    RegistryObject<Item>BURNED_WOODEN_SWORD =ITEMS.register("burned_wooden_sword",()->{
        return new BurnedSword(Tiers.WOOD,
                1,
                -2.0f,
                new Item.Properties()
                        .durability(10));
    });


    /// 常规物品类


    public static  final
    RegistryObject<Item> ZEPHYRGRAVEN =ITEMS.register("zephyrgraven", Zephyrgraven::new);

    public static final
    RegistryObject<Item>DISC_LEIQIAO=ITEMS.register("disc_leiqiao", Disc_Leiqiao::new);

    public static final
    RegistryObject<Item> DISC_LANGRENQINGGE =ITEMS.register("disc_langrenqingge", Disc_Langrenqingge::new);

    public static final
    RegistryObject<Item> DISC_LASTDANCE = ITEMS.register("disc_lastdance", Disc_Lastdance::new);

    public static final
    RegistryObject<Item> JAMIE_WIN = ITEMS.register("jamie_win", Jamie_Win::new);


    /// 食物类


    public static final
    RegistryObject<Item>EASTERNHUMBURGER = ITEMS.register("roujiamo",
            ()->new Roujiamo(new Item.Properties().food(ModFoodItem.EASTERNHUMBURGER).stacksTo(16)));

    public static final
    RegistryObject<Item>FLATBREAD = ITEMS.register("baked_flatbread",
            ()->new Flatbread(new Item.Properties().food(ModFoodItem.FLATBREAD).stacksTo(4)));

    public static final
    RegistryObject<Item>DOUGH = ITEMS.register("dough",
            ()->new Dough(new Item.Properties().stacksTo(4)));

    public static final
    RegistryObject<Item>UNMIXED_DOUGH = ITEMS.register("unmixed_dough",
            ()->new Unmixed_Dough(new Item.Properties().stacksTo(4)));

    public static final
    RegistryObject<Item>EASTERN_FLATBREAD = ITEMS.register("eastern_flatbread",
            ()-> new East_Flatbread(new Item.Properties().food(ModFoodItem.EASTERN_FLATBREAD).stacksTo(4)));

}
