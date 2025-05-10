package thevoid.items;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import thevoid.Myfirst_MOd.TheVoid;
import thevoid.Utils.ClientEvent.NetworkHandler;
import thevoid.Utils.ClientEvent.ReloadRequestPacket;
import thevoid.client.renderer.weapon.ChrononachtRenderer;
import thevoid.implement.ICrosshairItem;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

//注册事件总线：
@Mod.EventBusSubscriber(modid = TheVoid.MODID,bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Chrononacht extends SwordItem implements GeoItem, ICrosshairItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final RawAnimation ACTIVATE_ANIM = RawAnimation.begin().thenPlay("shooting");
    private static final RawAnimation RELOAD_ANIM = RawAnimation.begin().thenPlay("reloading");
    public static final int MAX_AMMO = 12;
    private static final int COOLDOWN_TICKS = 12;

    public Chrononacht(Tiers tiers, int attackDamage, float attackSpeed, Properties properties) {
        super(tiers, attackDamage, attackSpeed, properties);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }
    // 初始化NBT数据
    @Override
    public void onCraftedBy(ItemStack stack, Level level, Player player) {
        super.onCraftedBy(stack, level, player);
        if (!stack.getOrCreateTag().contains("Ammo")) {
            setAmmo(stack, MAX_AMMO);
        }
    }

//注册渲染器
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private ChrononachtRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return this.renderer == null ?
                        (this.renderer = new ChrononachtRenderer()) :
                        this.renderer;
            }
        });
    }
//覆写use方法，第一层保险
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        return InteractionResultHolder.pass(stack);
    }
//动画组设置
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(
                this, "Activation", 0, state -> PlayState.STOP)
                .triggerableAnim("shooting", ACTIVATE_ANIM));

        controllers.add(new AnimationController<>(
                this,"ReloadingController",0,state -> PlayState.STOP)
                .triggerableAnim("reloading",RELOAD_ANIM));
    }


    public static void consumeAmmoForReload(Player player, ItemStack weaponStack) {
        int needed = MAX_AMMO - getAmmo(weaponStack); // 计算实际需要的弹药量
        int remaining = needed;

        // 遍历背包寻找箭矢
        for (int i = 0; i < player.getInventory().getContainerSize() && remaining > 0; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(Items.ARROW)) {
                int consume = Math.min(stack.getCount(), remaining);
                stack.shrink(consume);
                remaining -= consume;
            }
        }

        // 实际补充的弹药量 = 需要的 - 剩余的（未能补充的部分）
        int actualReloaded = needed - remaining;
        setAmmo(weaponStack, getAmmo(weaponStack) + actualReloaded);
    }

    //注册事件监听器、射击逻辑
    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        ItemStack stack = event.getItemStack();
        Level level = player.level();
        if (stack.getItem() instanceof Chrononacht) {//检查武器是否对

            if (canShoot(stack)) {//检查是否有弹药（物品栏检查）

                /// 耻辱代码///
                if (level.isClientSide) {
                    //player.swing(event.getHand());
                    return;
                }
                /// 耻辱代码///
                //这里我们暂时不动了
                ((Chrononacht) stack.getItem()).triggerAnim(player, GeoItem.getOrAssignId(stack, (ServerLevel) level),
                        "Activation",
                        "shooting");

                shootArrow(level, player, stack);//射箭
                consumeAmmo(stack);//消耗弹药
                player.getCooldowns().addCooldown(stack.getItem(), COOLDOWN_TICKS);//冷却

                //播放音效
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.CROSSBOW_SHOOT,
                        SoundSource.PLAYERS,
                        1.0F,
                        0.8F + level.random.nextFloat() * 0.4F);
            }
        }
    }
    private static final KeyMapping RELOAD_KEY = new KeyMapping(
            "key.thevoid.reload",
            //这里大概设置里显示
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "category.thevoid.combat"
    );

    @SubscribeEvent
    public static void onClientTick(InputEvent event) {
        if (RELOAD_KEY.isDown()) {
            System.out.println("[DEBUG] R键被按下！");
            //发送请求到服务器！
            NetworkHandler.INSTANCE.sendToServer(new ReloadRequestPacket());
        }
    }


    //无装弹系统的弹药监测：
//判断是否有弹药
    public static boolean hasAmmo(Player player) {
        if (player.getAbilities().instabuild) return true;
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(Items.ARROW)) return true;
        }
        return false;
    }
//消耗弹药,目前射击也消耗这里的弹药
    public static void consumeAmmo(Player player) {
        if (player.getAbilities().instabuild) return;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(Items.ARROW)) {
                stack.shrink(12-getAmmo(stack));
                return;
            }
        }
    }


    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
    //装弹系统方法：
    // 获取当前弹药量
    public static int getAmmo(ItemStack stack) {
        return stack.getOrCreateTag().getInt("Ammo");
    }

    // 设置弹药量
    private static void setAmmo(ItemStack stack, int ammo) {
        stack.getOrCreateTag().putInt("Ammo", Math.max(ammo, 0));
    }

    // 检查是否可以射击
    private static boolean canShoot(ItemStack stack) {
        return getAmmo(stack) > 0 && !isReloading(stack);
    }

    // 消耗弹药
    private static void consumeAmmo(ItemStack stack) {
        setAmmo(stack, getAmmo(stack) - 1);
    }

    // 是否在装弹中
    public static boolean isReloading(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean("Reloading");
    }

    // 开始装弹
    public static void startReload(ItemStack stack) {
        stack.getOrCreateTag().putBoolean("Reloading", true);
    }

    // 完成装弹
    public static void finishReload(ItemStack stack) {
        stack.getOrCreateTag().putBoolean("Reloading", false);
        setAmmo(stack, MAX_AMMO);
    }






//关于箭矢：
    private static void shootArrow(Level level, Player player, ItemStack stack) {
        Arrow arrow = new Arrow(level, player);
        arrow.setBaseDamage(4.0D);

        int powerLevel = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.POWER_ARROWS, stack);
        if (powerLevel > 0) {
            arrow.setBaseDamage(arrow.getBaseDamage() + (double) powerLevel * 0.5D + 0.5D);
        }

        int punchLevel = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.PUNCH_ARROWS, stack);
        arrow.setKnockback(punchLevel);

        if (EnchantmentHelper.getTagEnchantmentLevel(Enchantments.FLAMING_ARROWS, stack) > 0) {
            arrow.setSecondsOnFire(100);
        }

        arrow.shootFromRotation(player, player.getXRot(), player.getYRot(),
                0.0F,
                4.0F,
                0.0F);
        level.addFreshEntity(arrow);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 0;
    }

    @Override
    public ResourceLocation getCrosshairTexture() {
        return new ResourceLocation("thevoid", "textures/ui/crosshair-chrononacht.png");
    }



//关于描述：
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        // 第一部分：物品名称（金色）
        tooltip.add(Component.translatable("item.thevoid.chrononacht.title")
                .withStyle(ChatFormatting.DARK_RED).withStyle(ChatFormatting.BOLD));

        // 第二部分：武器类型（灰色）
        tooltip.add(Component.translatable("item.thevoid.chrononacht.type")
                .withStyle(ChatFormatting.GRAY));

        // 第三部分：伤害值（红色）
        tooltip.add(Component.literal("伤害: §c8.0 ❤")
                .withStyle(ChatFormatting.DARK_GRAY));

        // 第四部分：冷却时间（蓝色）
        tooltip.add(Component.literal("射击间隔: §b0.52秒")
                .withStyle(ChatFormatting.DARK_GRAY));

        // 第五部分：使用说明（斜体灰色）
        tooltip.add(Component.translatable("item.thevoid.chrononacht.usage")
                .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));

        tooltip.add(Component.literal("弹药: " + getAmmo(stack) + "/" + MAX_AMMO)
                .withStyle(isReloading(stack) ? ChatFormatting.YELLOW : ChatFormatting.GREEN));


    }
}

