package thevoid.Data.loot;

import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.RegistryObject;
import thevoid.init.ModBlocks;

import java.util.Iterator;
import java.util.Set;

public class ModBlockLootTabels extends BlockLootSubProvider {
    public ModBlockLootTabels() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }
//决定了方块掉落什么东西。可以是掉落自己。
    @Override
    protected void generate() {

        //掉落自己就这样写
        this.dropSelf(ModBlocks.OAKSTOOL.get());
        //掉落另一个物品就自定义一个下面的方法
        this.add(ModBlocks.MASROOM.get(),
               block -> createMasroomDrops(ModBlocks.MASROOM.get()));
        this.add(ModBlocks.TALLGRASS.get(),
                block -> createGrassDrops(ModBlocks.TALLGRASS.get()));
        //假如没有你喜欢的原版物品掉落，比如要掉模组物品
        //可以在这里自定义一个掉落，但是这时候是不是手作更快捷？

        }
    protected LootTable.Builder createMasroomDrops(Block pBlock) {
        return createSilkTouchDispatchTable(pBlock,
                this .applyExplosionDecay(pBlock, LootItem.lootTableItem(Items.SLIME_BALL)
                        //硬编码掉落铜矿，如果不喜欢可以再传入一个item，然后在实现里定义
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 5.0F)))
                        .apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))));
                        //掉落个数，以及是否受到时运影响。假如只需要一个就填一，到时候复制下来改一下物品即可
    }
    protected LootTable.Builder createGrassDrops(Block pBlock) {
        return createSilkTouchDispatchTable(pBlock,
                this .applyExplosionDecay(pBlock, LootItem.lootTableItem(Items.WHEAT_SEEDS)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F)))
                        .apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))));
    }






//大概意思是这个东西会报错只要你有方块没有写掉落物
    @Override
    protected Iterable<Block> getKnownBlocks(){
        return ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}
