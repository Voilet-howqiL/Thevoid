package thevoid.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import thevoid.Myfirst_MOd.TheVoid;
import thevoid.items.Chrononacht;

public class ChrononachtModel extends DefaultedItemGeoModel<Chrononacht> {
    public ChrononachtModel(){
        super(new ResourceLocation(TheVoid.MODID,"chrononacht"));
    }
}
