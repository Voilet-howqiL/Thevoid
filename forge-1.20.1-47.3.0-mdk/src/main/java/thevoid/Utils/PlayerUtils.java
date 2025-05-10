package thevoid.Utils;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerUtils {
    private static final Logger LOGGER = LogManager.getLogger();  // 移除错误类型转换

    public static boolean safeClientCheck(Player player) {
        try {
            if (player == null) {
                LOGGER.warn("Null  player instance detected");
                return false;
            }

            Level world = player.level();
            if (world == null) {
                String playerName = safeGetPlayerName(player);
                LOGGER.warn("Player  {} has no world context", playerName);
                return false;
            }

            return world.isClientSide();
        } catch (Exception e) {
            LOGGER.warn("Client check failed", e);
            return false;
        }
    }

    private static String safeGetPlayerName(Player player) {
        try {
            return player.getScoreboardName();
        } catch (Exception e) {
            LOGGER.warn("Failed  to get player name", e);
            return "[Unknown]";
        }
    }
}