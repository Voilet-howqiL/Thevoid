package thevoid.Data;

import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import thevoid.Data.loot.ModBlockLootTabels;

import java.util.List;
import java.util.Set;

public class ModLootTableProvider {
    public static LootTableProvider create(PackOutput output){
        return new LootTableProvider(output, Set.of(), List.of(
        new LootTableProvider.SubProviderEntry(ModBlockLootTabels::new, LootContextParamSets.BLOCK)
    ));
    }
}
