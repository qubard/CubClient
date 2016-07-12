package es.blueberrypancak.module;

import java.awt.Color;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import es.blueberrypancak.Client;
import es.blueberrypancak.Location;
import es.blueberrypancak.event.EventChat;
import es.blueberrypancak.event.EventEntityRender;
import es.blueberrypancak.event.EventLoadBlock;
import es.blueberrypancak.event.Subscribe;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

@RegisterModule(key=45,color=32526,listed=true)
public class Wallhack extends Module {

	private ArrayList<Location> blocks = new ArrayList<Location>();
	
	private ArrayList<String> tempList = new ArrayList<String>();

	@Subscribe
	public void onEntityRender(EventEntityRender e) {
		Minecraft mc = Client.getMinecraft();
		if (isEnabled()) {
			for(Location l : blocks) {
				Block block = mc.theWorld.getBlockState(new BlockPos((double)l.getX(), (double)l.getY(), (double)l.getZ())).getBlock();
				double dist = mc.getRenderViewEntity().getDistance(l.getX(), l.getY(), l.getZ());
				if(dist >= mc.gameSettings.renderDistanceChunks*16 || Block.getIdFromBlock(block) != l.getId()) {
					blocks.remove(l);
					continue;
				}
				drawBlock(l);
			}
		}
	}
	
	@Subscribe
	public void onChat(EventChat e) {
		String message = e.getValue();
		if(message.startsWith("-w")) {
			String k = message.split(" ")[1];
			boolean found = false;
			for(String s: tempList) {
				found |= s.equals(k);
			}
			if(!found) {
				tempList.add(k); 
			}
			refresh();
			e.setCancelled(true);
		} else if(message.equals("-clear")) {
			blocks.clear();
			tempList.clear();
			e.setCancelled(true);
		} else if(message.startsWith("-c")) {
			String k = message.split(" ")[1];
			boolean found = false;
			for(String s: tempList) {
				found |= s.equals(k);
				if(found) {
					tempList.remove(s);
					break;
				}
			}
			ArrayList<Integer> removed = new ArrayList<Integer>();
			int z = 0;
			for(Location loc : blocks) {
				if(loc.getId() == Integer.parseInt(k)) {
					removed.add(blocks.indexOf(loc)-z);
					z++;
				}
			}
			for(Integer remove : removed) {
				blocks.remove(blocks.get(remove));
			}
			e.setCancelled(true);
		}
	}
	
	@Subscribe
	public void onLoadBlock(EventLoadBlock e) {
		Location pos = e.getValue();
		boolean found = false;
		for(String s : tempList) {
			found |= Integer.parseInt(s.split(":")[0]) == pos.getId();
			if(found) {
				break;
			}
		}
		if(!found) return;
		for (Location loc : blocks) {
			if (loc.getX() == pos.getX() && loc.getY() == pos.getY() && loc.getZ() == pos.getZ()) {
				return;
			}
		}
		boolean max = countBlocks(pos.getId()) <= 200;
		double dist = Client.getMinecraft().thePlayer.getDistanceSqToCenter(new BlockPos((double)pos.getX(), (double)pos.getY(), (double)pos.getZ()));
		if(max || (!max && dist <= 100)) { 
			blocks.add(e.getValue());
		}
	}
	
	private int countBlocks(int id) {
		int i = 0;
		for(Location l : blocks) {
			if(l.getId() == id) {
				i++;
			}
		}
		return i;
	}
	
	private void refresh() {
		Minecraft mc = Client.getMinecraft();
		EntityPlayer p = mc.thePlayer;
		int d = mc.gameSettings.renderDistanceChunks*16;
		mc.theWorld.markBlockRangeForRenderUpdate(new BlockPos(p.posX-d, p.posY-d, p.posZ-d), new BlockPos(p.posX+d, p.posY+d, p.posZ+d));
	}

	private void drawBlock(Location l) {
		Minecraft mc = Client.getMinecraft();
		IBlockState state = mc.theWorld.getBlockState(new BlockPos((double)l.getX(), (double)l.getY(), (double)l.getZ()));
		RenderManager r = mc.getRenderManager();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glLineWidth(1.0F);
		Color c = new Color(state.getMapColor().colorValue);
		GL11.glPushMatrix();
		GL11.glDepthMask(false);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4d((double) c.getRed() / 255.0, (double) c.getGreen() / 255.0, (double) c.getBlue() / 255.0, 1);
		
		double varX = l.getX() + 0.5;
		double varY = l.getY();
		double varZ = l.getZ() + 0.5;
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		double w = 0.5;
		double h = 1.0;
		GL11.glBegin(GL11.GL_LINES);

		Block current = state.getBlock();
		Block up = mc.theWorld.getBlockState(new BlockPos((double)l.getX(), (double)l.getY()+1, (double)l.getZ())).getBlock();
		Block down = mc.theWorld.getBlockState(new BlockPos((double)l.getX(), (double)l.getY()-1, (double)l.getZ())).getBlock();
		Block left = mc.theWorld.getBlockState(new BlockPos((double)l.getX()-1, (double)l.getY(), (double)l.getZ())).getBlock();		
		Block right = mc.theWorld.getBlockState(new BlockPos((double)l.getX()+1, (double)l.getY(), (double)l.getZ())).getBlock(); // gonna use adjacent later
		Block forward = mc.theWorld.getBlockState(new BlockPos((double)l.getX(), (double)l.getY(), (double)l.getZ()+1)).getBlock();
		Block backward = mc.theWorld.getBlockState(new BlockPos((double)l.getX(), (double)l.getY(), (double)l.getZ()-1)).getBlock();
		
		if(forward != current) {
			if(down != current) { 
				GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY, varZ - r.renderPosZ + w);
				GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY, varZ - r.renderPosZ + w);
			}
			
			if(left != current) {
				GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY, varZ - r.renderPosZ + w);
				GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY + h, varZ - r.renderPosZ + w);
			}
			
			if(right != current) {
				GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY, varZ - r.renderPosZ + w);
				GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY + h, varZ - r.renderPosZ + w);
			}
			
			if(up != current) { 
				GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY + h, varZ - r.renderPosZ + w);
				GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY + h, varZ - r.renderPosZ + w);
			}
		}
		
		if(right != current) {
			if(down != current) {
				GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY, varZ - r.renderPosZ + w);
				GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY, varZ - r.renderPosZ - w);
			}
			
			if(up != current) {
				GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY + h, varZ - r.renderPosZ + w);
				GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY + h, varZ - r.renderPosZ - w);
			}
			
			if(backward != current) {
				GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY, varZ - r.renderPosZ - w);
				GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY + h, varZ - r.renderPosZ - w);
			}
		}
		
		if(backward != current) {
			if(left != current) {
				GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY, varZ - r.renderPosZ - w);
				GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY + h, varZ - r.renderPosZ - w);
			}
			
			if(up != current) {
				GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY + h, varZ - r.renderPosZ - w);
				GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY + h, varZ - r.renderPosZ - w);
			}
			
			if(down != current) {
				GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY, varZ - r.renderPosZ - w);
				GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY, varZ - r.renderPosZ - w);
			}
		}
		
		if(left != current) {
			if(down != current) {
				GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY, varZ - r.renderPosZ - w);
				GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY, varZ - r.renderPosZ + w);
			}
			
			if(up != current) {
				GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY + h, varZ - r.renderPosZ - w);
				GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY + h	, varZ - r.renderPosZ + w);
			}
		}
		
		GL11.glEnd();

		GL11.glColor4d((double) c.getRed() / 255.0, (double) c.getGreen() / 255.0, (double) c.getBlue() / 255.0, 0.15);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glBegin(GL11.GL_QUADS);
		
		if(current != backward) {
			GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY + h, varZ - r.renderPosZ - w);
			GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY, varZ - r.renderPosZ - w);
			GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY, varZ - r.renderPosZ - w);
			GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY + h, varZ - r.renderPosZ - w);
		}
		
		if(current != forward) { 
			GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY + h, varZ - r.renderPosZ + w);
			GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY, varZ - r.renderPosZ + w);
			GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY, varZ - r.renderPosZ + w);
			GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY + h, varZ - r.renderPosZ + w);
		}
		
		if(current != right) {
			GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY + h, varZ - r.renderPosZ - w);
			GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY, varZ - r.renderPosZ - w);
			GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY, varZ - r.renderPosZ + w);
			GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY + h, varZ - r.renderPosZ + w);
		}
		
		if(current != left) { 
			GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY + h, varZ - r.renderPosZ - w);
			GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY, varZ - r.renderPosZ - w);
			GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY, varZ - r.renderPosZ + w);
			GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY + h, varZ - r.renderPosZ + w);
		}
		
		if(current != down) { 
			GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY, varZ - r.renderPosZ + w);
			GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY, varZ - r.renderPosZ + w);
			GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY, varZ - r.renderPosZ - w);
			GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY, varZ - r.renderPosZ - w);
		}
		
		if(current != up) {
			GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY + h, varZ - r.renderPosZ + w);
			GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY + h, varZ - r.renderPosZ + w);
			GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY + h, varZ - r.renderPosZ - w);
			GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY + h, varZ - r.renderPosZ - w);
		}
		
		GL11.glEnd();
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glPopMatrix();
	}

	@Override
	public void onEnabled() {
		refresh();
	}

	@Override
	public void onDisabled() {
		
	}

	@Override
	public String getName() {
		return "Wallhack\u00a72" + (blocks.size() > 0 ? " " + blocks.size() : "");
	}
}
