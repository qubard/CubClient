package es.blueberrypancak;

public class Location {

	private int x, y, z, id;
	
	public Location(int x, int y, int z, int id) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.id = id;
	}
	
	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getZ() {
		return this.z;
	}

	public int getId() {
		return this.id;
	}
}
