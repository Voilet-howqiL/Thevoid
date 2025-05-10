package thevoid.init;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import thevoid.entity.EntityFallingBlock;
import thevoid.entity.SeatEntity;
import thevoid.entity.ThrownCrudeSpear;
import thevoid.entity.ThrownSpear;

import static thevoid.Myfirst_MOd.TheVoid.MODID;

public class ModEntity {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID);

    public static final RegistryObject<EntityType<ThrownSpear>> THROWN_SPEAR = ENTITIES.register("thrown_spear",
            ()-> EntityType.Builder.<ThrownSpear>of(ThrownSpear::new, MobCategory.MISC)
                    .sized(0.5f,0.5f)
                    .clientTrackingRange(4)
                    .updateInterval(20)
                    .build("thrown_spear")
    );

    public static final RegistryObject<EntityType<ThrownCrudeSpear>> THROWN_CRUDE_SPEAR = ENTITIES.register("thrown_crude_spear",
            ()-> EntityType.Builder.<ThrownCrudeSpear>of(ThrownCrudeSpear::new, MobCategory.MISC)
                    .sized(0.5f,0.5f)
                    .clientTrackingRange(4)
                    .updateInterval(20)
                    .build("thrown_crude_spear")
    );
    public static final RegistryObject<EntityType<SeatEntity>> SEAT = ENTITIES.register("seat",
            () -> EntityType.Builder.<SeatEntity>of(SeatEntity::new, MobCategory.MISC)
                    .sized(0.01f, 0.01f)
                    .clientTrackingRange(10)
                    .build("seat"));

    public static final RegistryObject<EntityType<EntityFallingBlock>> FALLING_BLOCK = ENTITIES.register("falling_block",
            () -> EntityType.Builder.<EntityFallingBlock>of(EntityFallingBlock::new, MobCategory.MISC)
                    .sized(0.98f, 0.98f)
                    .clientTrackingRange(10)
                    .updateInterval(20)
                    .build("falling_block"));
}



