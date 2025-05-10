package thevoid.items;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import thevoid.Myfirst_MOd.TheVoid;
import thevoid.entity.EntityFallingBlock;
import thevoid.init.ModEntity;
import thevoid.init.ModItems;
import thevoid.init.ModSounds;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Mod.EventBusSubscriber(modid = TheVoid.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ResonanceBlade extends SwordItem {
    private static final int SHAKE_RADIUS = 6;  // 震动水平半径
    private static final int SHAKE_HEIGHT = 1; // 垂直影响层数（玩家所在层+Y+1）
    private static final int COOLDOWN_TICKS = 60;
    private static final float BASE_DAMAGE = 8.0f;
    private static final float MAX_RANGE = 6.0f;
    private static final double INNER_RADIUS = 3;
    private static int currentWave = 0;
    private static BlockPos waveCenter;
    private static final int MAX_WAVE = 6;

    public ResonanceBlade(Tiers tier, int attackDamage, float attackSpeed, Properties properties) {
        super(tier, attackDamage, attackSpeed, properties);
    }

    @SubscribeEvent
    public static void onEntityHurt(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            if (isHoldingVoidBlade(player)) {
                handleSpecialAttackEffects(player, event.getEntity());
            }
        }
    }

    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        ItemStack stack = event.getItemStack();

        if (stack.getItem() instanceof ResonanceBlade) {
            if(player.isShiftKeyDown()){
                executeAoeAttack(player, stack);
                 event.setCanceled(true);
            }
        }
    }
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && waveCenter != null) {
            ServerLevel serverLevel = event.getServer().overworld();
            if (serverLevel.getGameTime() % 2 == 0){
                generateWaveLayer(serverLevel,currentWave); // 生成当前层
                if (++currentWave > MAX_WAVE) {  // 推进到下一层
                    waveCenter = null;           // 重置状态
                }
            }
        }
    }

    private static void generateWaveLayer(ServerLevel level, int layer) {
        if (level == null) return;

        final int radius = layer;
        final double ringWidth = 0.5; // 波浪环宽度
        //改了这个就没重复生成的内圈了，为什么呢？
        // 计算当前层的最小和最大半径（严格从内圈外开始）
        BlockPos.betweenClosedStream(
                        waveCenter.offset(-radius, -1, -radius),  // 区域左下角
                        waveCenter.offset(radius, 1, radius))     // 区域右上角
                .filter(pos -> {
                    double squaredDist = pos.distSqr(waveCenter);  // 使用平方距离避免开方

                    // 预计算关键阈值
                    double minOuterSquared = Math.pow(radius - ringWidth, 2);
                    double maxOuterSquared = Math.pow(radius + ringWidth, 2);
                    double innerSquared = INNER_RADIUS * INNER_RADIUS;

                    return squaredDist >= minOuterSquared      // 外环边界
                            && squaredDist <= maxOuterSquared      // 内环边界
                            && squaredDist > innerSquared          // 排除内部空洞
                            && pos.getY() >= waveCenter.getY() - 1 // Y轴下限
                            && pos.getY() <= waveCenter.getY() + 1; // Y轴上限
                })
                .forEach(pos -> {
                    BlockState state = level.getBlockState(pos);
                    BlockPos abovePos = new BlockPos(pos).above();
                     BlockState blockAbove = level.getBlockState(abovePos);

                    if (!state.isAir() &&
                            state.isRedstoneConductor(level, pos) &&
                            !state.hasBlockEntity() &&
                            !blockAbove.blocksMotion()){
                        if (!state.isAir() && isFallingable(state) && level.random.nextFloat() < 0.7f) {
                            BlockPos spawnPos = pos.above(); // 生成位置在原始位置上方
                            BlockState belowState = level.getBlockState(pos); // 当前pos就是下方方块

                            EntityFallingBlock fallingBlock = new EntityFallingBlock(
                                    ModEntity.FALLING_BLOCK.get(),
                                    level,
                                    spawnPos,       // 实体的生成位置
                                    belowState      // 使用原始pos位置的方块作为材质
                            );
                            fallingBlock.setPos(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
                            level.addFreshEntity(fallingBlock);

                        }
                    }
                });
    }



    private static boolean isHoldingVoidBlade(Player player) {
        return player.getMainHandItem().getItem() == ModItems.TEST.get();
    }

    private static void handleSpecialAttackEffects(Player player, LivingEntity target) {
        Level level = player.level();

        if (!level.isClientSide) {
            target.addEffect(new MobEffectInstance(
                    MobEffects.MOVEMENT_SLOWDOWN,
                    100,
                    2,
                    false,
                    true
            ));

            spawnAttackParticles((ServerLevel) level, target);
        }
    }

    private static void executeAoeAttack(Player player, ItemStack stack) {
        Level level = player.level();
        initiateCooldown(player, stack);
        consumeDurability(player, stack);
        playAttackSound(level, player);

        if (!level.isClientSide) {
            // 设置波浪起始点
            waveCenter = player.blockPosition();
            currentWave = 0;
            clearEnvironment(player);
            affectNearbyEntities(player);
            spawnAoeParticles((ServerLevel) level, player);
        }
    }

    private static void initiateCooldown(Player player, ItemStack stack) {
        player.getCooldowns().addCooldown(stack.getItem(), COOLDOWN_TICKS);
        player.swing(InteractionHand.MAIN_HAND);
    }

    private static void consumeDurability(Player player, ItemStack stack) {
        if (!player.isCreative()) {
            if (stack.hurt(5, player.getRandom(), null)) {
                stack.shrink(1);
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.TNT_PRIMED, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
        }
    }

    private static void playAttackSound(Level level, Player player) {
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                ModSounds.BUZZZZ_HEAVYHIT.get(), SoundSource.PLAYERS, 1.0F,
                0.4F + level.random.nextFloat() * 0.4F);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                ModSounds.BUZZZZ_SHAKE.get(), SoundSource.PLAYERS, 1.0F,
                0.8F + level.random.nextFloat() * 0.4F);

    }

    private static void clearEnvironment(Player player) {
        BlockPos center = BlockPos.containing(
                player.getX(),
                player.getEyeY()-5,
                player.getZ()
        );

        BlockPos destroyCenter = BlockPos.containing(player.getX(), player.getEyeY(), player.getZ());
        clearDestructibleBlocks(player.level(), destroyCenter, (int)MAX_RANGE);

        // 震动效果使用新范围
        BlockPos shakeCenter = player.blockPosition(); // 获取玩家脚下方块位置
        handleFallingBlocks(player.level(), shakeCenter);
    }

    private static void affectNearbyEntities(Player player) {
        List<LivingEntity> targets = player.level().getEntitiesOfClass(
                LivingEntity.class,
                new AABB(player.blockPosition()).inflate(MAX_RANGE),
                entity -> entity != player
        );

        for (LivingEntity target : targets) {
            applyEntityEffects(player, target);
        }
    }

    private static void applyEntityEffects(Player player, LivingEntity target) {
        float distance = (float) player.distanceTo(target);
        float damage = calculateDistanceDamage(distance);

        target.hurt(player.damageSources().playerAttack(player), damage);
        target.knockback(1.5f,
                player.getX() - target.getX(),
                player.getZ() - target.getZ());
    }

    private static float calculateDistanceDamage(float distance) {
        return BASE_DAMAGE * (1 - (float) Math.pow(distance / MAX_RANGE, 2));
    }

    private static void clearDestructibleBlocks(Level level, BlockPos center, int radius) {
        BlockPos.betweenClosedStream(center.offset(-radius, -radius, -radius),
                        center.offset(radius, radius, radius))
                .filter(pos -> isDestructible(level.getBlockState(pos)))
                .forEach(pos -> {
                    level.destroyBlock(pos, true);
                    handleDripstone(level, pos);
                });
    }

    private static boolean isDestructible(BlockState state) {
        return state.is(BlockTags.FLOWERS)
                ||state.is(BlockTags.REPLACEABLE)
                ||state.is(BlockTags.CLIMBABLE)
                ||state.is(BlockTags.CAVE_VINES)
                ||state.is(BlockTags.UNDERWATER_BONEMEALS)
                ||state.is(BlockTags.WOOL_CARPETS)
                ||state.is(BlockTags.CROPS)
                ||state.is(BlockTags.DOORS)
                ||state.is(BlockTags.TRAPDOORS)
                ||state.is(BlockTags.ICE)
                ||state.is(BlockTags.FIRE)
                ||state.is(BlockTags.BANNERS)
                ||state.is(BlockTags.CAMPFIRES)
                ||state.is(BlockTags.SIGNS)
                ||state.is(BlockTags.SAPLINGS)
                ||state.is(BlockTags.CRYSTAL_SOUND_BLOCKS)
                ||state.getBlock()== Blocks.TORCH
                ||state.getBlock()== Blocks.LANTERN;
    }
    private static boolean isFallingable(BlockState state) {
        return state.is(BlockTags.DIRT) ||
                state.is(BlockTags.MINEABLE_WITH_AXE) ||
                state.is(BlockTags.MINEABLE_WITH_HOE) ||
                state.is(BlockTags.MINEABLE_WITH_PICKAXE)||
                state.is(BlockTags.MINEABLE_WITH_SHOVEL);
    }

    private static void handleDripstone(Level level, BlockPos pos) {
        if (level.getBlockState(pos).getBlock() == Blocks.POINTED_DRIPSTONE) {
            List<BlockPos> dripstones = findConnectedDripstone(level, pos);
            Collections.reverse(dripstones);

            dripstones.forEach(dripPos -> {
                level.destroyBlock(dripPos, false);
                FallingBlockEntity.fall(level, dripPos, level.getBlockState(dripPos))
                        .setHurtsEntities(2.0f, 40);
            });
        }
    }

    private static List<BlockPos> findConnectedDripstone(Level level, BlockPos start) {
        List<BlockPos> result = new ArrayList<>();
        BlockPos.MutableBlockPos current = start.mutable();

        while (level.getBlockState(current).getBlock() == Blocks.POINTED_DRIPSTONE) {
            result.add(current.immutable());
            current.move(Direction.UP);
        }
        return result;
    }

    private static void spawnAttackParticles(ServerLevel level, LivingEntity target) {
        level.sendParticles(ParticleTypes.CRIT,
                target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ(),
                15, 0.2, 0.2, 0.2, 0.4);
    }

    private static void spawnAoeParticles(ServerLevel level, Player player) {
        level.sendParticles(ParticleTypes.ENCHANT,
                player.getX(), player.getY() + 1.0, player.getZ(),
                30, 0.5, 0.5, 0.5, 0.1);
    }

//    private static void handleFallingBlocks(Level level, BlockPos center) {
//        int centerY = center.getY();
//
//        BlockPos.betweenClosedStream(
//                        center.offset(-SHAKE_RADIUS, -1, -SHAKE_RADIUS),
//                        center.offset(SHAKE_RADIUS, SHAKE_HEIGHT, SHAKE_RADIUS))
//                .filter(pos -> pos.getY() >= centerY && pos.getY() <= centerY + SHAKE_HEIGHT)
//                .forEach(pos -> {
//                    BlockState state = level.getBlockState(pos);
//
//                    if (isFallingable(state)) {
//                        // 生成方块碎裂粒子（客户端可见）
//                        if (level instanceof ServerLevel serverLevel) {
//                            serverLevel.sendParticles(
//                                    new BlockParticleOption(ParticleTypes.BLOCK, state),
//                                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
//                                    5, // 粒子数量
//                                    0.2, 0.2, 0.2, 0.05
//                            );
//                        }
//
//                        // 播放方块被击中的音效
//                        level.playSound(null, pos, SoundEvents.NETHER_BRICKS_BREAK,
//                                SoundSource.PLAYERS, 0.8F, 0.5F + level.random.nextFloat() * 0.4F);
//                    }
//                });
//    }

    private static void handleFallingBlocks(Level level, BlockPos center) {
        final int INNER_RADIUS = 1; // 中心不生成区域半径
        int centerX = center.getX();
        int centerZ = center.getZ();

//        BlockPos.betweenClosedStream(
//                        center.offset(-SHAKE_RADIUS, -1, -SHAKE_RADIUS),
//                        center.offset(SHAKE_RADIUS, SHAKE_HEIGHT, SHAKE_RADIUS))
//                .filter(pos -> {
//                // 排除中心区域：X和Z轴都在INNER_RADIUS范围内
//                int dx = Math.abs(pos.getX() - centerX);
//                int dz = Math.abs(pos.getZ() - centerZ);
//                return !(dx <= INNER_RADIUS && dz <= INNER_RADIUS);})
//                .forEach(pos -> {
//                    BlockState state = level.getBlockState(pos);
//                    BlockPos abovePos = new BlockPos(pos).above();
//                    BlockState blockAbove = level.getBlockState(abovePos);
//
////                   if(isFallingable(state)){
////                     level.destroyBlock(pos, false);
////                    }
//
//                    if (!state.isAir() &&
//                            state.isRedstoneConductor(level, pos) &&
//                            !state.hasBlockEntity() &&
//                            !blockAbove.blocksMotion()){
//                    if (!level.isClientSide && isFallingable(state)) {
//                        if (level.random.nextBoolean()) {
//                            // 自定义的下落方块实体
//                            EntityFallingBlock fallingBlock = new EntityFallingBlock(
//                                    ModEntity.FALLING_BLOCK.get(),
//                                    level,
//                                    state,
//                                    0.3f
//                                    //最后传了一个没用的参数，所以修改这里没用
//
//                            );
//                            // 配置实体属性
//                            fallingBlock.setPos(pos.getX() + 0.5, pos.getY()+ 1, pos.getZ() + 0.5);
//
//                            // 禁用掉落物和伤害
//                            fallingBlock.noCulling = true;
//                            // 设置向上的初始速度（Y轴速度需足够大）
//
//
//                            level.addFreshEntity(fallingBlock);
//                        }
//                    }

                        // 播放音效

//                    }
//                });
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.translatable("item.thevoid.test.tip")
                .withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, level, tooltip, flag);
    }


}
