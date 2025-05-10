package thevoid.items;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import thevoid.client.renderer.weapon.DarkinSwordRenderer;

public class DarkinSword extends SwordItem implements GeoItem {
    // 第一行：是调试信息，用来测试模型是否位于正确的位置
    private static final RawAnimation SWING_ANIMATION = RawAnimation.begin().thenPlay("swing");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
//构造函数
    public DarkinSword(Tiers tier, int attackDamage, float attackSpeed, Item.Properties properties) {
        super(tier,attackDamage,attackSpeed,properties);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);

    }
//item的渲染器注册
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private DarkinSwordRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return this.renderer == null ?
                        (this.renderer = new DarkinSwordRenderer()) :
                        this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(
                this, "SwingController", 0, state -> PlayState.STOP)
                .triggerableAnim("swing", SWING_ANIMATION));

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
    //覆写use方法
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        return InteractionResultHolder.pass(stack);
    }

    @SubscribeEvent
    public static void onSwordClick(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        ItemStack stack = event.getItemStack();
        Level level = player.level();

        System.out.println("[DEBUG] 巨剑挥舞！");
        if (stack.getItem() instanceof DarkinSword sword && !level.isClientSide()) {
            System.out.println("[DEBUG] 巨剑挥舞！");
            if (level instanceof ServerLevel serverLevel) {
                sword.triggerAnim(
                        player,
                        GeoItem.getOrAssignId(stack, serverLevel),
                        "SwingController",
                        "swing"
                );
                System.out.println("[DEBUG] 巨剑挥舞！");
            }
        }
    }

            // 连击常量
            public static final int MAX_STAGE = 3;
            public static final int STAGE_TIMEOUT = 60;
            public static final int ATTACK_COOLDOWN = 25;
            public static final float[] STAGE_DAMAGE_MULTIPLIER = {1.0f, 1.25f, 1.5f};


}
