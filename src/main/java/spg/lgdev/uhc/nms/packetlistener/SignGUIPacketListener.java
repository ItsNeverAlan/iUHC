package spg.lgdev.uhc.nms.packetlistener;

import spg.lgdev.uhc.nms.NMSHandler;
import spg.lgdev.uhc.util.signgui.SignGUI;
import spg.lgdev.uhc.util.signgui.SignUpdate;
import net.development.mitw.packetlistener.PacketEvent;
import net.development.mitw.packetlistener.PacketListener;

public class SignGUIPacketListener implements PacketListener {

	@Override
	public void out(final PacketEvent packetEvent) {
	}

	@Override
	public void in(final PacketEvent packetEvent) {
		if (packetEvent.getPacketName().equals("PacketPlayInUpdateSign")) {
			final SignUpdate signUpdate = NMSHandler.getInstance().getNMSControl().getSignUpdate(packetEvent.getPacket());
			if (signUpdate != null && SignGUI.onFinish(packetEvent.getPlayer(), signUpdate)) {
				packetEvent.setCancelled(true);
			}
		}
	}

}
