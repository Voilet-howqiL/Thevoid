package thevoid.Data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import thevoid.Myfirst_MOd.TheVoid;
import thevoid.init.ModBlocks;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagGen extends BlockTagsProvider {
    public ModBlockTagGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, TheVoid.MODID, existingFileHelper);
    }
//似乎用于生成物品给予tag的文件，而并非注册tag
    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(BlockTags.REPLACEABLE)
                .add(ModBlocks.MASROOM.get())
                .addTag(BlockTags.MINEABLE_WITH_HOE);

    }
}
