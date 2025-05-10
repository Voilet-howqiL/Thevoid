package thevoid.Utils;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import thevoid.Myfirst_MOd.TheVoid;

public class ModTags {

    public static class Items{


        public static final TagKey<EntityType<?>> YEZHELE =
                TagKey.create(Registries.ENTITY_TYPE,
                        new ResourceLocation(TheVoid.MODID, "eat_too_fast"));
    }
    
    public static class Blocks{

        public static final TagKey<Block> SITTABLE_BLOCKS = tag("sittable_blocks");

        private static TagKey<Block>tag(String name){
            return BlockTags.create(new ResourceLocation(TheVoid.MODID,name));
        }

    }


}
