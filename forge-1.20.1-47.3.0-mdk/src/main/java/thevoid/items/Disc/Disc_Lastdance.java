package thevoid.items.Disc;

import net.minecraft.world.item.RecordItem;
import thevoid.init.ModSounds;

public class Disc_Lastdance extends RecordItem {
    public Disc_Lastdance(){
        super(
                7,
                ()-> ModSounds.WUBAI_LASTDANCE.get(),
                new Properties().stacksTo(1),
                6000

        );
    }
}
