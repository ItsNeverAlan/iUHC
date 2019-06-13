package spg.lgdev.uhc.nms.v1_8_R3;

import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

import spg.lgdev.uhc.nms.common.IFastBlockSet;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.IBlockData;

public class FastBlockSet1_8 implements IFastBlockSet {

	@Override
	public void setBlockFast(final World world, final int x, final int y, final int z, final int blockId,
			final byte data, final boolean applyPhysics) {
		try {
			final net.minecraft.server.v1_8_R3.World w = ((CraftWorld) world).getHandle();
			final net.minecraft.server.v1_8_R3.Chunk chunk = w.getChunkAt(x >> 4, z >> 4);
			final BlockPosition bp = new BlockPosition(x, y, z);
			final int combined = blockId + (data << 12);
			final IBlockData ibd = net.minecraft.server.v1_8_R3.Block.getByCombinedId(combined);
			w.setTypeAndData(bp, ibd, applyPhysics ? 3 : 2);
			chunk.a(bp, ibd);
		} catch (final Throwable throwable) {
			throwable.printStackTrace();
		}
	}

}
