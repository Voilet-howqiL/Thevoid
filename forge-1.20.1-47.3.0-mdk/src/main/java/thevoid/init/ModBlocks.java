package thevoid.init;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import thevoid.Myfirst_MOd.TheVoid;
import thevoid.blocks.Masroom;
import thevoid.blocks.Oka_Stool;
import thevoid.blocks.Tall_Grass;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, TheVoid.MODID);

    public static final RegistryObject<Block>MASROOM =registerBlock("masroom",()->
            new Masroom(BlockBehaviour.Properties.copy(Blocks.RED_MUSHROOM)
                    .sound(SoundType.SLIME_BLOCK)
                    .randomTicks()
                    .noOcclusion()
                    .offsetType(BlockBehaviour.OffsetType.XZ)
                    .lightLevel(state ->3)),
            new Item.Properties().stacksTo(16),

                    Component.translatable("theVoid.masroom.desc").withStyle(ChatFormatting.GRAY)
            );


    public static final RegistryObject<Block>OAKSTOOL =registerBlock("oak_stool",()->
                    new Oka_Stool(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)
                            .sound(SoundType.WOOL)
                            .randomTicks()
                            .dynamicShape()
                            .noOcclusion()),
            new Item.Properties().stacksTo(4),

            Component.translatable("theVoid.oak_stool.desc").withStyle(ChatFormatting.GRAY)
    );
    //后面再加上.sounds(SoundsType.XXX)就可以自定义声音了，这里我们回头再加。
    //复习知识：SoundType与SoundEvent。
    //在这里加更多的块并 同时注册方块物品

    public static final RegistryObject<Block>TALLGRASS =registerBlock("tall_grass",()->
                    new Tall_Grass(BlockBehaviour.Properties.copy(Blocks.GRASS)
                            .randomTicks()
                            .noOcclusion()
                            .offsetType(BlockBehaviour.OffsetType.XZ)),
            new Item.Properties().stacksTo(64),

            Component.translatable("theVoid.tall_grass.desc").withStyle(ChatFormatting.GRAY)
    );

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block,Item.Properties properties,Component tooltip){
        RegistryObject<T> toReturn = BLOCKS.register(name ,block);
        registryBlockItem(name,toReturn,properties,tooltip);
        return toReturn;
    }
//这里是模组物品注册，按道理不应该这样写在这里，大概率会导致之后的全部物品都这样。
    private static <T extends Block> RegistryObject<Item> registryBlockItem(String name,RegistryObject<T> block,Item.Properties itemProperties, Component tooltip){
        return ModItems.ITEMS.register(name,()->new BlockItem(block.get(), itemProperties));
    }

    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }

}
