package es.blueberrypancak.event;

import net.minecraft.block.state.IBlockState;

public class EventOnStr extends EventValue<Float> {

	private IBlockState block;
	
	public EventOnStr(float f, IBlockState block) {
		super(f);
		this.block = block;
	}

	public IBlockState getBlock() {
		return this.block;
	}
}
