package es.blueberrypancak.event;

import net.minecraft.util.math.BlockPos;

public class EventBlockBreak implements Event {

	private BlockPos pos;
	
	public EventBlockBreak(BlockPos pos) {
		this.pos = pos;
	}

	public BlockPos getBlockPos() {
		return this.pos;
	}
}
