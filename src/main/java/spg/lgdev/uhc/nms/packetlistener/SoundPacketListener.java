package spg.lgdev.uhc.nms.packetlistener;

import spg.lgdev.uhc.handler.game.UHCGame;
import net.development.mitw.packetlistener.PacketEvent;
import net.development.mitw.packetlistener.PacketListener;

public class SoundPacketListener implements PacketListener {

	@Override
	public void out(final PacketEvent packetEvent) {
		if (packetEvent.hasPlayer() && packetEvent.getPacketName().contains("PacketPlayOutNamedSoundEffect")) {
			if (UHCGame.getInstance().getPracticePlayers().contains(packetEvent.getPlayer().getUniqueId()))
				return;
			packetEvent.setCancelled(true);
			return;
		}
	}

	@Override
	public void in(final PacketEvent packetEvent) {
	}

}
