package thevoid.events;

import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import thevoid.Myfirst_MOd.TheVoid;
import thevoid.init.ModItems;
import thevoid.items.DarkinSword;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TheVoid.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Capabilities {
    // 定义 Capability
    public static final Capability<ComboState> COMBO_STATE =
            CapabilityManager.get(new CapabilityToken<>() {});

    // ComboState 接口
    public interface ComboState {
        int getCurrentStage();
        void setCurrentStage(int stage);
        int getLastAttackTick();
        void setLastAttackTick(int tick);
    }

    public static class ComboStateImpl implements ComboState {
        private int currentStage = 0;
        private int lastAttackTick = -DarkinSword.ATTACK_COOLDOWN-1;

        @Override public int getCurrentStage() { return currentStage; }
        @Override public void setCurrentStage(int stage) { this.currentStage = stage; }
        @Override public int getLastAttackTick() { return lastAttackTick; }
        @Override public void setLastAttackTick(int tick) { this.lastAttackTick = tick; }

    }

    // 存储
    public static class ComboStateStorage implements ICapabilitySerializable<CompoundTag> {
        private final ComboStateImpl instance = new ComboStateImpl();
        private final LazyOptional<ComboState> optional = LazyOptional.of(() -> instance);

        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
            return COMBO_STATE.orEmpty(cap, optional);
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            tag.putInt("CurrentStage", instance.getCurrentStage());
            tag.putInt("LastAttackTick", instance.getLastAttackTick());
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            // 仅从 NBT 中读取数据，暂不修正时间戳
            instance.setCurrentStage(nbt.getInt("CurrentStage"));
            instance.setLastAttackTick(nbt.getInt("LastAttackTick"));
        }

    }

    // 事件监听
    // 修复时间戳异常
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        player.getCapability(COMBO_STATE).ifPresent(state -> {
            int currentTick = player.tickCount;
            int lastAttackTick = state.getLastAttackTick();


            if (lastAttackTick > currentTick) {
                state.setLastAttackTick(currentTick - DarkinSword.ATTACK_COOLDOWN);
            }
        });
    }
    //坚毅不倒！！！
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        ItemStack mainHand = player.getMainHandItem();
        if (mainHand.getItem() == ModItems.DARKIN_GREATSWORD.get() && mainHand.hasTag())  {
            int extraResilience = mainHand.getTag().getInt("ExtraResilience");
            player.getAttribute(Attributes.ARMOR_TOUGHNESS).setBaseValue(20  + extraResilience);
        }
    }

    // 绑定 Capability 到玩家实体
    @SubscribeEvent
    public static void onAttachCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(
                    new ResourceLocation(TheVoid.MODID, "combo_state"),
                    new ComboStateStorage()
            );
        }
    }

    // 玩家重生时复制数据
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            event.getOriginal().getCapability(COMBO_STATE).ifPresent(old -> {
                event.getEntity().getCapability(COMBO_STATE).ifPresent(newState -> {
                    newState.setCurrentStage(old.getCurrentStage());
                    newState.setLastAttackTick(old.getLastAttackTick());
                });
            });
        }
    }
}