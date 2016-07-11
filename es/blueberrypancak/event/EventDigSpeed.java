package es.blueberrypancak.event;

import net.minecraft.block.state.IBlockState;

public class EventDigSpeed extends EventValue<Float> {

	private IBlockState state;
	
	public EventDigSpeed(IBlockState state, Float value) {
		super(value);
		this.state = state;
	}
	
	public IBlockState getBlock() {
		return this.state;
	}
}
