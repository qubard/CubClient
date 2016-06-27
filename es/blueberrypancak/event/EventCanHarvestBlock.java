package es.blueberrypancak.event;

import net.minecraft.block.state.IBlockState;

public class EventCanHarvestBlock extends EventValue<Boolean> {

	private IBlockState state;
	
	public EventCanHarvestBlock(Boolean value, IBlockState state) {
		super(value);
		this.state = state;
	}
	
	public IBlockState getBlock() {
		return this.state;
	}
}
