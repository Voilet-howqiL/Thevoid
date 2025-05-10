package thevoid.client.renderer.weapon;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import thevoid.Myfirst_MOd.TheVoid;
import thevoid.items.DarkinSword;

public class DarkinSwordRenderer extends GeoItemRenderer<DarkinSword> {
    public DarkinSwordRenderer() {
        super(new DefaultedItemGeoModel<>(new ResourceLocation(TheVoid.MODID, "darkin_greatsword")));
    }
}
