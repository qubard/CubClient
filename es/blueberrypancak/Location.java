package es.blueberrypancak;

import java.awt.Color;

public class Location {

	private int x, y, z, id;
	
	private Color color;

	public Location(int x, int y, int z, String color, int id) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.color = Color.decode(color);
		this.id = id;
	}
	
	public void setColor(String color) {
		this.color = Color.decode(color);
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

	public Color getColor() {
		return this.color;
	}

	public int getId() {
		return this.id;
	}
}
