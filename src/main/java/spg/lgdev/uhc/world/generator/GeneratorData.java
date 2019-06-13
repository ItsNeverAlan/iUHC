package spg.lgdev.uhc.world.generator;

import org.bukkit.Location;

import spg.lgdev.uhc.world.CoordXZ;


public class GeneratorData
{
	// the main data interacted with
	private double x = 0;
	private double z = 0;
	private int radiusX = 0;
	private int radiusZ = 0;
	private boolean wrapping = false;

	// some extra data kept handy for faster border checks
	private double maxX;
	private double minX;
	private double maxZ;
	private double minZ;

	public GeneratorData(final double x, final double z, final int radiusX, final int radiusZ, final boolean wrap)
	{
		setData(x, z, radiusX, radiusZ, wrap);
	}
	public GeneratorData(final double x, final double z, final int radiusX, final int radiusZ)
	{
		setData(x, z, radiusX, radiusZ);
	}
	public GeneratorData(final double x, final double z, final int radius)
	{
		setData(x, z, radius, null);
	}
	public GeneratorData(final double x, final double z, final int radius, final Boolean shapeRound)
	{
		setData(x, z, radius, shapeRound);
	}

	public final void setData(final double x, final double z, final int radiusX, final int radiusZ, final boolean wrap)
	{
		this.x = x;
		this.z = z;
		this.wrapping = wrap;
		this.setRadiusX(radiusX);
		this.setRadiusZ(radiusZ);
	}
	public final void setData(final double x, final double z, final int radiusX, final int radiusZ)
	{
		setData(x, z, radiusX, radiusZ, false);
	}
	public final void setData(final double x, final double z, final int radius, final Boolean shapeRound)
	{
		setData(x, z, radius, radius, false);
	}

	public GeneratorData copy()
	{
		return new GeneratorData(x, z, radiusX, radiusZ, wrapping);
	}

	public double getX()
	{
		return x;
	}
	public void setX(final double x)
	{
		this.x = x;
		this.maxX = x + radiusX;
		this.minX = x - radiusX;
	}
	public double getZ()
	{
		return z;
	}
	public void setZ(final double z)
	{
		this.z = z;
		this.maxZ = z + radiusZ;
		this.minZ = z - radiusZ;
	}
	public int getRadiusX()
	{
		return radiusX;
	}
	public int getRadiusZ()
	{
		return radiusZ;
	}
	public void setRadiusX(final int radiusX)
	{
		this.radiusX = radiusX;
		this.maxX = x + radiusX;
		this.minX = x - radiusX;
	}
	public void setRadiusZ(final int radiusZ)
	{
		this.radiusZ = radiusZ;
		this.maxZ = z + radiusZ;
		this.minZ = z - radiusZ;
	}

	@Deprecated
	public int getRadius()
	{
		return (radiusX + radiusZ) / 2;  // average radius; not great, but probably best for backwards compatibility
	}
	public void setRadius(final int radius)
	{
		setRadiusX(radius);
		setRadiusZ(radius);
	}

	public boolean getWrapping()
	{
		return wrapping;
	}
	public void setWrapping(final boolean wrap)
	{
		this.wrapping = wrap;
	}


	@Override
	public String toString()
	{
		return "radius " + ((radiusX == radiusZ) ? radiusX : radiusX + "x" + radiusZ) + " at X: " + GeneratorManager.coord.format(x) + " Z: " + GeneratorManager.coord.format(z) + (wrapping ? (" (wrapping)") : "");
	}

	public boolean insideBorder(final double xLoc, final double zLoc)
	{
		return !(xLoc < minX || xLoc > maxX || zLoc < minZ || zLoc > maxZ);
	}
	public boolean insideBorder(final Location loc)
	{
		return insideBorder(loc.getX(), loc.getZ());
	}
	public boolean insideBorder(final CoordXZ coord)
	{
		return insideBorder(coord.x, coord.z);
	}


	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
			return true;
		else if (obj == null || obj.getClass() != this.getClass())
			return false;

		final GeneratorData test = (GeneratorData)obj;
		return test.x == this.x && test.z == this.z && test.radiusX == this.radiusX && test.radiusZ == this.radiusZ;
	}

	@Override
	public int hashCode()
	{
		return (((int)(this.x * 10) << 4) + (int)this.z + (this.radiusX << 2) + (this.radiusZ << 3));
	}
}
