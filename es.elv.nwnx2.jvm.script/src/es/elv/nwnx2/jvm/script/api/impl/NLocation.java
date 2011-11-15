package es.elv.nwnx2.jvm.script.api.impl;

import es.elv.nwnx2.jvm.script.api.IArea;
import es.elv.nwnx2.jvm.script.api.ILocation;
import es.elv.nwnx2.jvm.script.api.IVector3;

public class NLocation implements ILocation {
	private final IArea area;
	private final IVector3 position;
	private final float facing;
	
	public NLocation(IArea area, IVector3 position, float facing) {
		this.area = area;
		this.position = position;
		this.facing = facing;
	}
	
	public NLocation(IArea area, float x, float y, float z, float facing) {
		this(area, new NVector3(x, y, z), facing);
	}
	
	@Override
	public IArea getArea() {
		return area;
	}

	@Override
	public IVector3 getPosition() {
		return position;
	}

	@Override
	public float getFacing() {
		return facing;
	}

	@Override
	public String toString() {
		return "ILocation [area=" + area + ", position=" + position + ", facing=" + facing + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((area == null) ? 0 : area.hashCode());
		result = prime * result + Float.floatToIntBits(facing);
		result = prime * result + ((position == null) ? 0 : position.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NLocation other = (NLocation) obj;
		if (area == null) {
			if (other.area != null)
				return false;
		} else if (!area.equals(other.area))
			return false;
		if (Float.floatToIntBits(facing) != Float.floatToIntBits(other.facing))
			return false;
		if (position == null) {
			if (other.position != null)
				return false;
		} else if (!position.equals(other.position))
			return false;
		return true;
	}
	
	
}