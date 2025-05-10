package thevoid.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.level.GrassColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thevoid.init.ModBlocks;

@OnlyIn(Dist.CLIENT)
public class ColorHandler {
    public static void registerBlockColors() {
        BlockColors blockColors = Minecraft.getInstance().getBlockColors();

        blockColors.register((state, world, pos, tintIndex) -> world != null && pos != null ?
                BiomeColors.getAverageGrassColor(world, pos) :
                GrassColor.get(0.5D, 1.0D), ModBlocks.TALLGRASS.get());
    }
}
