package thevoid.Data;

import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import thevoid.Myfirst_MOd.TheVoid;
import thevoid.init.ModBlocks;

public class ModBlockStateGen extends BlockStateProvider {


    public ModBlockStateGen(PackOutput output,  ExistingFileHelper exFileHelper) {
        super(output, TheVoid.MODID, exFileHelper);
    }
//放在这里面自动生成重复劳动的states.json文件
    //但是如果我有特殊的需求怎么办？比如我的蘑菇，我手动改写了，到时候我们再看
    //果然，只能生成最基本的，而且，资源名必须与方块注册名一致。这没什么，我们自己写就好了
    @Override
    protected void registerStatesAndModels() {

    }
    public void BlockWithItem(RegistryObject<Block> blockRegisterObject){
        simpleBlockWithItem(blockRegisterObject.get(),cubeAll(blockRegisterObject.get()));
    }
}
