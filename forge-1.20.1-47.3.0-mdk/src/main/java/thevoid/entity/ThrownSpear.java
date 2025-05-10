package thevoid.entity;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import thevoid.init.ModEntity;
import thevoid.init.ModItems;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ThrownSpear extends AbstractArrow  {
    private AbstractArrow.Pickup pickup;
    private ItemStack thrownStack;
    private final Set<Integer> hitEntities = ConcurrentHashMap.newKeySet();



    public ThrownSpear(EntityType<ThrownSpear> thrownSpearEntityType, Level level) {
    super(thrownSpearEntityType,level);
    this.thrownStack = ItemStack.EMPTY;
    this.setPierceLevel((byte) 0);
    }
    //每次都需要先access数据才行！
    private static final EntityDataAccessor<Boolean> SPEAR =
            SynchedEntityData.defineId(ThrownSpear.class, EntityDataSerializers.BOOLEAN);
    //同步定义数据！
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SPEAR,false);
    }

    ///
    ///
    ///
    ///最好都写到前面，免得在调用时但是却没有define
    ///
    ///
    ///

    public static ThrownSpear create(Level level, LivingEntity shooter, ItemStack stack) {
        ThrownSpear spear = new ThrownSpear(ModEntity.THROWN_SPEAR.get(), level);
        spear.setOwner(shooter);
        spear.setBaseDamage(12);
        spear.pickup = AbstractArrow.Pickup.ALLOWED;//可以拾取.
        spear.setNoGravity(false);//原版箭矢的重力系数是0.05，三叉戟为0.
        spear.thrownStack = stack.copy();
        int pierceLevel = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.PIERCING, stack);
        spear.setPierceLevel((byte) pierceLevel);
        return spear;
    }


    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity target = result.getEntity();

        // 终极空检查链
        if (target == null || target.isRemoved() || this.isRemoved()) return;
        if (!hitEntities.add(target.getId())) return; // 原子性添加

        // 2. 安全获取owner（防止中间态）
        Entity owner = this.getOwner();
        if (owner == null) {
            this.discard();
            return;
        }
        float damage = (float) this.getBaseDamage();
        float velocityDamage = Math.min((float) this.getDeltaMovement().length() * 3.0F, 30.0F);
        DamageSource damageSource = this.damageSources().thrown(this, owner);
        hitEntities.add(target.getId());
        target.hurt(this.damageSources().thrown(
                        this, this.getOwner()),
                damage + velocityDamage);

        // 触发击退效果.并且加一层保险对抗发光物品展示框
        //仅对于生物进行击退
        if (!this.level().isClientSide) {
            ((ServerLevel) this.level()).getServer().submit(() -> {
                if (!target.isRemoved()) { // 二次校验
                    target.hurt(damageSource, damage + velocityDamage);
                    postHitEffects(target, owner);
                }
            });
        }



        int pierceLevel = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.PIERCING, thrownStack);
        float speedScale = 1.0f - (0.4f - pierceLevel * 0.1f);
        this.setDeltaMovement(this.getDeltaMovement().scale(speedScale));


    }

    private float setHitDamage() {
        return (float)this.getBaseDamage(); //设置伤害，不知道行不行.
    }
    @Override
    public void tick() {
        super.tick();

        // 根据速度动态调整旋转（模拟真实飞行效果）
//        if (!this.inGround) {
//            Vec3 deltaMovement = this.getDeltaMovement();
//            this.setYRot((float) (Mth.atan2(deltaMovement.x, deltaMovement.z) * (180F / Math.PI)));
//            this.setXRot((float) (Mth.atan2(deltaMovement.y, deltaMovement.horizontalDistance()) * (180F / Math.PI)));
//        }
    }


    @Override
    protected @NotNull ItemStack getPickupItem() {
        return thrownStack.copy(); // 返回保存的堆栈副本
    }


    public ItemStack getItem() {
        return new ItemStack(ModItems.DESERT_SPEAR.get());//给渲染器传递参数
    }
    private void postHitEffects(Entity target, Entity owner) {
        if (target instanceof LivingEntity livingTarget && owner instanceof LivingEntity attacker) {
            EnchantmentHelper.doPostHurtEffects(livingTarget, attacker);
            EnchantmentHelper.doPostDamageEffects(attacker, livingTarget);
        }
    }


}
