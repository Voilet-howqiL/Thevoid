package thevoid.implement;

import net.minecraft.resources.ResourceLocation;
import thevoid.Myfirst_MOd.TheVoid;

public interface ICrosshairItem {
    /** 是否启用自定义准心 */
    default boolean hasCustomCrosshair() {
        return true;
    }

    /** 获取准心材质路径（可为不同武器设置不同准心） */
    default ResourceLocation getCrosshairTexture() {
        return new ResourceLocation(TheVoid.MODID, "textures/ui/crosshair-spear.png");
    }


    //以后还可以加入不透明度之类的。
}
