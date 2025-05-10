package thevoid.init;

import net.minecraft.world.food.FoodProperties;

public class ModFoodItem {
    public static final FoodProperties EASTERNHUMBURGER =  new FoodProperties.Builder()
            .nutrition(8)
            .saturationMod(0.5f)
            .alwaysEat()
            .fast()
            .build();

    public static final FoodProperties FLATBREAD = new FoodProperties.Builder()
            .nutrition(2)
            .saturationMod(0.1f)
            .alwaysEat()
            .build();

    public static final FoodProperties EASTERN_FLATBREAD = new FoodProperties.Builder()
            .nutrition(9)
            .saturationMod(1.0f)
            .alwaysEat()
            .build();
}
