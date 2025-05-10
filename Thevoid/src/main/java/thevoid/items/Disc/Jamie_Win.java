package thevoid.items.Disc;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.RecordItem;
import thevoid.init.ModSounds;

public class Jamie_Win extends RecordItem {
    public Jamie_Win() {
        super(7,
                ()-> ModSounds.MR_TOP_PLAYER.get(),
                new Properties().stacksTo(1),
                4200);
    }
}
