package thevoid.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import thevoid.Myfirst_MOd.TheVoid;


public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS =DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, TheVoid.MODID);

    public static final
    RegistryObject<SoundEvent>WUBAI_LEIQIAO = SOUNDS.register("leiqiao_wubai",()->
            SoundEvent.createFixedRangeEvent(new ResourceLocation(TheVoid.MODID,"leiqiao_wubai"),
                    16.0f
                    //原版使用的是此方法，所以我们也要这样做。以免错误
            )
    );
    public static final
    RegistryObject<SoundEvent>WUBAI_LANGRENQINGGE = SOUNDS.register("langrenqingge-wubai",()->
            SoundEvent.createFixedRangeEvent(new ResourceLocation(TheVoid.MODID,"langrenqingge-wubai"),
                    16.0f

            )
    );

    public static final
    RegistryObject<SoundEvent>WUBAI_LASTDANCE = SOUNDS.register("lastdance-wubai",()->
            SoundEvent.createFixedRangeEvent(new ResourceLocation(TheVoid.MODID,"lastdance-wubai"),
                    16.0f
            )
    );
    public static final
    RegistryObject<SoundEvent> MR_TOP_PLAYER = SOUNDS.register("mr_top_player",()->
            SoundEvent.createFixedRangeEvent(new ResourceLocation(TheVoid.MODID,"mr_top_player"),
                    16.0f
            )
    );
    public  static final
    RegistryObject<SoundEvent>BUZZZZ_HIT = SOUNDS.register("buzzzz_hit",()->
            SoundEvent.createVariableRangeEvent(new ResourceLocation(TheVoid.MODID,"buzzzz_hit")));
    public  static final
    RegistryObject<SoundEvent>HUM_HIT = SOUNDS.register("hum_hit",()->
            SoundEvent.createVariableRangeEvent(new ResourceLocation(TheVoid.MODID,"hum_hit")));
    public  static final
    RegistryObject<SoundEvent>BUZZZZ_HEAVYHIT = SOUNDS.register("buzzzz_heavyhit",()->
            SoundEvent.createVariableRangeEvent(new ResourceLocation(TheVoid.MODID,"buzzzz_heavyhit")));
    public  static final
    RegistryObject<SoundEvent>BUZZZZ_SHAKE = SOUNDS.register("buzzzz_shake",()->
            SoundEvent.createVariableRangeEvent(new ResourceLocation(TheVoid.MODID,"buzzzz_shake")));


}
