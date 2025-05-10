package thevoid.items.Disc;

import net.minecraft.world.item.RecordItem;
import thevoid.init.ModSounds;

public class Disc_Leiqiao extends RecordItem {
    public Disc_Leiqiao(){
        super(
                7,
                ()->ModSounds.WUBAI_LEIQIAO.get(),
                new Properties().stacksTo(1),
                4600
        );
        //上至下分别是：红石信号强度，
        //播放的声音（声音时间的提供者）
         //物品属性
         //长度

    }
}
