package thevoid.items.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class Zephyrgraven extends Item {
    public Zephyrgraven(){
        super(new Item.Properties()
                .rarity(Rarity.UNCOMMON) // 可选：设置稀有度
                .stacksTo(64)           // 可选：堆叠数量
        );
    }
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        // 第一部分：物品名称（金色）
        tooltip.add(Component.translatable("item.thevoid.Zephyrgraven.title")
                .withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.BOLD));


    }
}
