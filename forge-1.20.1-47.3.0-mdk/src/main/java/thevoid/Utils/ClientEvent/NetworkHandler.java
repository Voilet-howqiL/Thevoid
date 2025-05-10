package thevoid.Utils.ClientEvent;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

// 创建网络通道类
public class NetworkHandler {
    private static final String PROTOCOL = "1.0";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("modid", "main"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals
    );

    public static void register() {
        int packetId = 0;
        // 注册装填请求包
        INSTANCE.registerMessage(packetId++,
                ReloadRequestPacket.class,
                ReloadRequestPacket::encode,
                ReloadRequestPacket::decode,
                ReloadRequestPacket::handle
        );
    }
}

