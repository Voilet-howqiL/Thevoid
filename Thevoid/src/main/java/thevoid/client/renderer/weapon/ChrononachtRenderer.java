package thevoid.client.renderer.weapon;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import thevoid.Myfirst_MOd.TheVoid;
import thevoid.items.Chrononacht;

public class ChrononachtRenderer extends GeoItemRenderer<Chrononacht> {
    public ChrononachtRenderer(){
        super(new DefaultedItemGeoModel<>(new ResourceLocation(TheVoid.MODID,"chrononacht")));
    }
}
