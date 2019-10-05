package spg.lgdev.uhc.nms.packetlistener;

import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayInUpdateSign;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedSoundEffect;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import spg.lgdev.iSpigot;
import spg.lgdev.protocol.listener.ListenPriority;
import spg.lgdev.protocol.listener.PacketController;
import spg.lgdev.protocol.listener.PacketDirection;
import spg.lgdev.protocol.listener.ProtocolVersions;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.iUHC;
import spg.lgdev.uhc.nms.NMSHandler;
import spg.lgdev.uhc.util.signgui.SignGUI;
import spg.lgdev.uhc.util.signgui.SignUpdate;

public class UHCPacketListener {

    private static PacketController<PacketPlayOutNamedSoundEffect> soundPacketListener;

    public static void register() {
        new PacketController<PacketPlayInUpdateSign>(ListenPriority.AFTER_INJECT, ProtocolVersions.V1_7_TO_1_8) {
            @Override
            public void init() {
                listen(values -> {
                    SignUpdate signUpdate = NMSHandler.getInstance().getNMSControl().getSignUpdate(values.getPacket());
                    if (signUpdate != null && SignGUI.onFinish(values.getPlayer(), signUpdate)) {
                        values.setCancel(true);
                    }
                });
            }
        }.start(PacketDirection.IN, PacketPlayInUpdateSign.class);

        soundPacketListener = new PacketController<PacketPlayOutNamedSoundEffect>(ListenPriority.AFTER_INJECT, ProtocolVersions.V1_7_TO_1_8) {
            @Override
            public void init() {
                listen(values -> {
                    if (UHCGame.getInstance().getPracticePlayers().contains(values.getPlayer().getUniqueId()))
                        return;
                    values.setCancel(true);
                });
            }
        };

        soundPacketListener.start(PacketDirection.IN, PacketPlayOutNamedSoundEffect.class);
    }

    public static void unregisterSoundPacket() {
        iSpigot.INSTANCE.getPacketListenManager().unregisterIn(soundPacketListener);
    }

}
