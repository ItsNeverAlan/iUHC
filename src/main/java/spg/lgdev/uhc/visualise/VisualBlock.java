package spg.lgdev.uhc.visualise;

import lombok.Getter;
import net.minecraft.server.v1_8_R3.BlockPosition;

@Getter
public class VisualBlock {

	private final VisualBlockData blockData;
	private final BlockPosition location;

	public VisualBlock(final VisualBlockData blockData, final BlockPosition location) {
		this.blockData = blockData;
		this.location = location;
	}
}
