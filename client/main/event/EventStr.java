package client.main.event;

import net.minecraft.block.state.IBlockState;

public class EventStr extends EventValue<Float> {

	private IBlockState block;
	
	public EventStr(float f, IBlockState block) {
		super(f);
		this.block = block;
	}

	public IBlockState getBlock() {
		return this.block;
	}
}
