package es.blueberrypancak.event;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class EventBlockSwing implements Event  {
	
	private BlockPos loc;
	private EnumFacing face;
	
	public EventBlockSwing(BlockPos loc, EnumFacing face) {
		this.loc = loc;
		this.face = face;
	}
	
	public BlockPos getBlockPos() {
		return this.loc;
	}
	
	public EnumFacing getFace() {
		return this.face;
	}
}
