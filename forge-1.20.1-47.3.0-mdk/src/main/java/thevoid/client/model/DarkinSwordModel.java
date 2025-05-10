package thevoid.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import thevoid.Myfirst_MOd.TheVoid;
import thevoid.items.DarkinSword;

public class DarkinSwordModel extends DefaultedItemGeoModel<DarkinSword> {
    public DarkinSwordModel() {
        super(new ResourceLocation(TheVoid.MODID, "darkin_greatsword"));
    }
}
