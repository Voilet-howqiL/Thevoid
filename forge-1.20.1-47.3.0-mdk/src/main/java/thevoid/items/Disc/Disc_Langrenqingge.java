package thevoid.items.Disc;

import net.minecraft.world.item.RecordItem;
import thevoid.init.ModSounds;

public class Disc_Langrenqingge extends RecordItem {
    public Disc_Langrenqingge(){
        super(
                7,
                ()->ModSounds.WUBAI_LANGRENQINGGE.get(),
                new Properties().stacksTo(1),
                6000
        );
    }
}
