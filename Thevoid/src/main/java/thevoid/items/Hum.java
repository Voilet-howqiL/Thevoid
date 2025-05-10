package thevoid.items;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import thevoid.Myfirst_MOd.TheVoid;
import thevoid.init.ModItems;
import thevoid.init.ModSounds;

import java.util.List;


@Mod.EventBusSubscriber(modid = TheVoid.MODID,bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Hum extends SwordItem {
    private static final int COOLDOWN_TICKS = 60;
    public Hum(Tiers tiers,int attackdamage,float attackspeed, Properties properties){
        super(tiers,attackdamage,attackspeed,properties);
    }

    @SubscribeEvent
    public static void onEntityAttack(LivingAttackEvent event) {
        //确保攻击来源是玩家

        if (event.getSource().getEntity() instanceof Player player) {
            ItemStack heldItem = player.getMainHandItem();
            Level level = player.level();
            // 检查物品是否为Hum且事件未被取消
            if (heldItem.getItem() == ModItems.HUM.get() && !event.isCanceled()) {
                //仅服务端触发音效（自动同步到客户端）

                if (level instanceof ServerLevel serverLevel) {

                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            ModSounds.HUM_HIT.get(),
                            SoundSource.PLAYERS,
                            0.5F+ event.getAmount() * 0.2F,
                            0.7F + level.random.nextFloat() * 0.2F
                    );
                }
            }
        }
    }
    //注册事件监听器
    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickItem event) {


        Player player = event.getEntity();
        ItemStack stack = event.getItemStack();
        Level level = player.level();

        BlockPos pos = BlockPos.containing(player.position());

        if (stack.getItem() instanceof Hum) {
            //检查武器是否对

            player.getCooldowns().addCooldown(stack.getItem(), COOLDOWN_TICKS);//冷却
            //摆动手臂
            player.swing(event.getHand());
            //消耗耐久
            if(!player.isCreative()){
                stack.setDamageValue(stack.getDamageValue() + 10);
            }
            //耐久为1时直接销毁
            if(stack.getDamageValue() >= stack.getMaxDamage()){
                stack.shrink(1);
                level.playSound(null,player.getX(),player.getY(),player.getZ(),
                        SoundEvents.TNT_PRIMED,
                        SoundSource.PLAYERS,
                        2.0F,
                        1.0F);


            }

            //播放音效
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    ModSounds.BUZZZZ_HIT.get(),
                    SoundSource.PLAYERS,
                    2.0F,
                    0.3F + level.random.nextFloat() * 0.4F);

            // 只在服务端执行伤害逻辑
            if (!level.isClientSide()) {
                //在这一步再进行转换
                ServerLevel serverLevel = (ServerLevel) level;
                serverLevel.gameEvent(
                        player,
                        GameEvent.EXPLODE,
                        pos
                );
                // 定义伤害范围参数
                float damageRadius = 3.5F; // 3.5格半径
                float baseDamage = 4.0F; // 基础伤害值
                //传入玩家眼部的位置
                BlockPos center = BlockPos.containing(
                        player.getX(),
                        player.getEyeY(),
                        player.getZ()
                );
                //调用函数
                clearPlantsInRange(level,center,(int) damageRadius);

                // 获取范围内所有生物
                List<LivingEntity> entities = level.getEntitiesOfClass(
                        LivingEntity.class,
                        new AABB(player.getX() - damageRadius,
                                player.getY() - damageRadius,
                                player.getZ() - damageRadius,
                                player.getX() + damageRadius,
                                player.getY() + damageRadius,
                                player.getZ() + damageRadius),
                        entity -> entity != player // 排除玩家自己
                );

                // 对每个生物造成伤害
                for (LivingEntity target : entities) {
                    // 计算伤害（可以根据距离衰减）
                    float distance = (float) player.distanceTo(target);
                    float damage = baseDamage * (1 - (distance / damageRadius));

                    if (damage > 0) {
                        target.hurt(level.damageSources().playerAttack(player), damage);
                        //施加减速效果
                        target.addEffect(new MobEffectInstance(
                                MobEffects.MOVEMENT_SLOWDOWN,
                                60,
                                1
                        ));

                        // 添加击退效果
                        target.knockback(1.0F,
                                player.getX() - target.getX(),
                                player.getZ() - target.getZ());

                        // 在服务端生成粒子效果（会自动同步到客户端）
                        ((ServerLevel) level).sendParticles(
                                ParticleTypes.CRIT,
                                target.getX(),
                                target.getY() + target.getBbHeight() / 2,
                                target.getZ(),
                                10,
                                0.2, 0.2, 0.2,
                                0.4
                        );
                        //生成位置
                        // 粒子数量
                        //随机偏移
                        //初速度
                    }
                }

                // 在玩家位置生成冲击波粒子
                ((ServerLevel) level).sendParticles(
                        ParticleTypes.POOF,
                        player.getX(),
                        player.getY() + 1.0,
                        player.getZ(),
                        20,
                        0.2, 0.2, 0.2,
                        0.2
                );

            }

            event.setCanceled(true);

        }

    }

    //除草效果
    public static void clearPlantsInRange(Level level, BlockPos center, int radius) {
        // 获取范围内的所有方块
        BlockPos.betweenClosedStream(
                center.offset(-radius, -radius, -radius),
                center.offset(radius, radius, radius)
        ).forEach(pos -> {
            BlockState state = level.getBlockState(pos);

            // 检查是否为植物（或自定义判断）
            if (state.is(BlockTags.TALL_FLOWERS)
                    ||state.is(BlockTags.REPLACEABLE)
                    ||state.is(BlockTags.CLIMBABLE)
                    || state.is(BlockTags.CAVE_VINES)
                    ||state.is(BlockTags.UNDERWATER_BONEMEALS)
                    ||state.is(BlockTags.CANDLES)
                    ||state.is(BlockTags.FIRE)
                    || state.is(BlockTags.CRYSTAL_SOUND_BLOCKS)
                    ||state.getBlock()== Blocks.TORCH) {
                level.destroyBlock(pos, true);
                System.out.println("CUTCUTCUT");// false表示不掉落物品
            }
        });
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pIsAdvanced) {
        pTooltip.add(Component.translatable("item.thevoid.hum.tip")
                .withStyle(ChatFormatting.GRAY));
        super.appendHoverText(pStack, pLevel, pTooltip, pIsAdvanced);

    }
}

