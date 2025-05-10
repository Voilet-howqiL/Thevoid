package thevoid.Data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import thevoid.Myfirst_MOd.TheVoid;


import java.util.concurrent.CompletableFuture;


@Mod.EventBusSubscriber(modid = TheVoid.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper helper=event.getExistingFileHelper();

        // 添加配方生成器

        generator.addProvider(event.includeServer(), new ModRecipes(output));
        generator.addProvider(event.includeServer(), ModLootTableProvider.create(output));

        generator.addProvider(event.includeClient(), new ModBlockStateGen(output,helper));
        generator.addProvider(event.includeClient(), new ModItemModelGen(output,helper));

        ModBlockTagGen blockTagGen = generator.addProvider(event.includeServer(),
                new ModBlockTagGen(output,lookupProvider,helper));

        generator.addProvider(event.includeServer(), new ModItemTagGen(output,lookupProvider,blockTagGen.contentsGetter(),helper));

    }
}