package es.blueberrypancak;

import net.minecraft.util.math.BlockPos;

public class Location {

	private int x, y, z, id;
	
	private BlockPos pos;
	
	public Location(int x, int y, int z, int id) {
		this.pos = new BlockPos(x, y, z);
		this.id = id;
	}
	
	public Location(BlockPos pos){ 
		this(pos, -1);
	}
	
	public Location(BlockPos pos, int id) {
		this(pos.getX(), pos.getY(), pos.getZ(), id);
		this.pos = pos;
		this.id = id;
	}
	
	@Override
	public int hashCode() {
		return (pos.getY() + pos.getZ() * 859433) * 859433 + pos.getX(); // choose a huge fucking prime
	}
	
	public BlockPos getPos() {
		return this.pos;
	}
	
	public int getX() {
		return pos.getX();
	}

	public int getY() {
		return pos.getY();
	}

	public int getZ() {
		return pos.getZ();
	}

	public int getId() {
		return this.id;
	}
	
	public static Location readBytes(byte[] b) {
		byte id = b[0];
		int x = (b[1] & 255) | ((b[2] << 8) & 32767);
		if((b[2] & 128) == 128) {
			x = -x;
		}
		int y = (b[3] & 255) | ((b[4] << 8) & 32767);
		if((b[4] & 128) == 128) {
			y = -y;
		}
		int z = (b[5] & 255) | ((b[6] << 8) & 32767);
		if((b[6] & 128) == 128) {
			z = -z;
		}
		return new Location(x, y, z, id);
	}
	
	public Location add(int x, int y, int z) {
		return new Location(new BlockPos(pos.getX()+x, pos.getY()+y, pos.getZ()+z), id);
	}
	
	public void setPos(BlockPos p) {
		this.pos = p;
	}
	
	public byte[] toBytes() {
		byte[] b = new byte[7];
		int x = getX();
		int y = getY();
		int z = getZ();
		b[0] = (byte)(this.id&255);
		b[1] = (byte) (Math.abs(x) & 255);
		b[2] = (byte)(((Math.abs(x) >> 8) & 255) | (x < 0 ? 128 : 0));
		b[3] = (byte) (Math.abs(y) & 255);
		b[4] = (byte)((Math.abs(y) >> 8) & 127 | (y < 0 ? 128 : 0));
		b[5] = (byte) (Math.abs(z) & 255);
		b[6] = (byte)((Math.abs(z) >> 8) & 127 | (z < 0 ? 128 : 0));
		return b;
	}
}
