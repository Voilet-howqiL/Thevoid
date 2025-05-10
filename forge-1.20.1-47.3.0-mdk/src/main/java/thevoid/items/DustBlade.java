package thevoid.items;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import thevoid.Myfirst_MOd.TheVoid;
import thevoid.init.ModItems;

public class DustBlade extends SwordItem {
    public DustBlade(Tiers tiers, int i, float v, Properties properties) {
        super(tiers,i,v, properties);
    }
    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // 调用父类方法处理基础攻击逻辑
        boolean result = super.hurtEnemy(stack, target, attacker);

        if (!attacker.level().isClientSide()) { // 确保在服务器端执行
            // 30%概率点燃目标3秒
            if (attacker.getRandom().nextFloat() < 0.3F) {
                target.setSecondsOnFire(3);
            }

            // 在目标位置生成ASH粒子
            ServerLevel serverLevel = (ServerLevel) attacker.level();
            serverLevel.sendParticles(
                    ParticleTypes.ASH,
                    target.getX(),                          // X坐标
                    target.getY() + target.getBbHeight()/2, // Y坐标（实体中心）
                    target.getZ(),                          // Z坐标
                    12,                                     // 粒子数量
                    0.5D,                                   // X散布范围
                    0.5D,                                   // Y散布范围
                    0.5D,                                   // Z散布范围
                    0.1D                                    // 额外速度
            );
            serverLevel.sendParticles(
                    ParticleTypes.SMOKE,
                    target.getX(),
                    target.getY() + target.getBbHeight()/2,
                    target.getZ(),
                    4,
                    0.1D,
                    0.1D,
                    0.1D,
                    0D
            );
        }

        return result;
    }

}
