package es.blueberrypancak.event;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class EventPlayerDamageBlock implements Event {
	
	private BlockPos loc;
	private EnumFacing face;
	
	public EventPlayerDamageBlock(BlockPos loc, EnumFacing face) {
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
