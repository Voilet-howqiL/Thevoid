package thevoid.Utils;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import thevoid.Myfirst_MOd.TheVoid;
import thevoid.init.ModItems;

@Mod.EventBusSubscriber(modid = TheVoid.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ItemWaterHandler {



    /* 事件驱动 */
    @SubscribeEvent
    public static void onEntityTick(EntityEvent event) {
        if (event.getEntity() instanceof ItemEntity itemEntity) {
            handleItemConversion(itemEntity);
        }
    }
    /* 执行 */
    private static void handleItemConversion(ItemEntity entity) {
        if (isValidForConversion(entity)) {

            int tick = entity.tickCount;

            if (tick % 5 == 0){

                System.out.println("可以转化 || 计时：" + tick);

            }
            convertItem(entity);
        }
    }
    /* 检查条件 */
    private static boolean isValidForConversion(ItemEntity entity) {
        return isTargetItem(entity)
                && (isInWater(entity))
                && !isAlreadyConverted(entity);
    }

    /*三个检查点*/

    private static boolean isTargetItem(ItemEntity entity) {
        return entity.getItem().getItem() == ModItems.DUST_BLADE.get();
    }

    private static boolean isInWater(ItemEntity entity) {
        Vec3 posVec = entity.position();
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();


        for (int y = 0; y<= Math .ceil(entity.getBoundingBox().getYsize()); y ++){

            mutableBlockPos.set(
                    Mth.floor(posVec.x),
                    Mth.floor(posVec.y) + y,
                    Mth.floor(posVec.z)
            );
            BlockState state = entity.level().getBlockState(mutableBlockPos);
            FluidState fluidState = entity.level().getFluidState(mutableBlockPos);

            if (fluidState.is(FluidTags.WATER)
                && state.is(Blocks.WATER)) return true;


            if (state.hasProperty(BlockStateProperties.WATERLOGGED)
                    && state.getValue(BlockStateProperties.WATERLOGGED)) {
                return true;
            }

//            if (state.getBlock() instanceof LiquidBlock liquidBlock
//            && liquidBlock.getFluid().is(FluidTags.WATER)){
//                return true;
//            }

        }

        return entity.isInWaterRainOrBubble();
    }


    private static boolean isAlreadyConverted(ItemEntity entity) {
        return entity.getPersistentData().getBoolean("converted");
    }

    /*  转化实现  */
    private static void convertItem(ItemEntity original) {
        if (!original.level().isClientSide) {

            System.out.println("转化!!");

            original.getPersistentData().putBoolean("converted", true);

            original.level().addFreshEntity(createNewEntity(original));

            original.discard();

            spawnConversionEffects(original);
        }
    }

    private static ItemEntity createNewEntity(ItemEntity original) {
        ItemStack burntStack = new ItemStack(ModItems.BURNED_WOODEN_SWORD.get());
        burntStack.setCount(original.getItem().getCount());

        ItemEntity newEntity = new ItemEntity(
                original.level(),
                original.getX(),
                original.getY(),
                original.getZ(),
                burntStack
        );
        newEntity.setDeltaMovement(original.getDeltaMovement());
        newEntity.setPickUpDelay(40);

        return newEntity;
    }

    private static void spawnConversionEffects(ItemEntity entity) {
        if (entity.level() instanceof ServerLevel level) {
            level.sendParticles(ParticleTypes.BUBBLE_COLUMN_UP,
                    entity.getX(), entity.getY(), entity.getZ(),
                    6, 0.3, 0.3, 0.3, 0.1
            );
            level.sendParticles(
                    ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    entity.getX(),
                    entity.getY() + 0.2,
                    entity.getZ(),
                    8,
                    0.1, 0.1, 0.1,
                    0.02
            );
            level.playSound(
                    null,
                    entity.blockPosition(),
                    SoundEvents.FIRE_EXTINGUISH,
                    SoundSource.BLOCKS,
                    0.6F,
                    1.2F
            );
        }
    }
}