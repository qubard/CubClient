package es.blueberrypancak.event;

import net.minecraft.block.state.IBlockState;

public class EventOnStr implements Event {
	
	private float f;
	
	private IBlockState block;
	
	public EventOnStr(float f, IBlockState block) {
		this.f = f;
		this.block = block;
	}
	
	public float getValue() {
		return this.f;
	}
	
	public void setValue(float f) {
		this.f = f;
	}

	public IBlockState getBlock() {
		return this.block;
	}
}
