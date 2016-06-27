package es.blueberrypancak.module;

import java.awt.Color;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import es.blueberrypancak.Client;
import es.blueberrypancak.Location;
import es.blueberrypancak.event.EventEntityRender;
import es.blueberrypancak.event.EventLoadBlock;
import es.blueberrypancak.event.Subscribe;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

@RegisterModule(key=45,color=32526,listed=true)
public class Wallhack extends Module {

	private ArrayList<Location> blocks = new ArrayList<Location>();

	@Subscribe
	public void onEntityRender(EventEntityRender e) {
		Minecraft mc = Client.getMinecraft();
		if (isEnabled()) {
			for(Location l : blocks) {
				Block block = mc.theWorld.getBlockState(new BlockPos((double)l.getX(), (double)l.getY(), (double)l.getZ())).getBlock();
				if(mc.getRenderViewEntity().getDistance(l.getX(), l.getY(), l.getZ()) >= Client.getMinecraft().gameSettings.renderDistanceChunks*16 || Block.getIdFromBlock(block) != l.getId())
				{
					blocks.remove(l);
					continue;
				}
				drawBlock(l);
			}
		}
	}

	private void drawBlock(Location l) {
		RenderManager r = Client.getMinecraft().getRenderManager();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glLineWidth(1.0F);
		Color c = l.getColor();
		GL11.glPushMatrix();
		GL11.glDepthMask(false);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4d((double) c.getRed() / 255.0, (double) c.getGreen() / 255.0, (double) c.getBlue() / 255.0, 1);
		
		AxisAlignedBB ax = Block.getBlockById(l.getId()).getSelectedBoundingBox(Block.getStateById(l.getId()), Client.getMinecraft().theWorld, new BlockPos((double)l.getX(), (double)l.getY(), (double)l.getZ()));
		System.out.println(ax.maxX-ax.minX);
		double varX = l.getX() + 0.5;
		double varY = l.getY();
		double varZ = l.getZ() + 0.5;
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		double w = (ax.maxX-ax.minX)/2;
		double h = ax.maxY-ax.minY;
		GL11.glBegin(GL11.GL_LINES);

		GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY, varZ - r.renderPosZ + w);
		GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY, varZ - r.renderPosZ + w);
		GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY, varZ - r.renderPosZ - w);
		GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY, varZ - r.renderPosZ + w);
		GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY, varZ - r.renderPosZ - w);
		GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY, varZ - r.renderPosZ + w);
		GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY, varZ - r.renderPosZ - w);
		GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY, varZ - r.renderPosZ - w);
		GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY, varZ - r.renderPosZ + w);
		GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY, varZ - r.renderPosZ + w);
		GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY + h, varZ - r.renderPosZ - w);
		GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY + h, varZ - r.renderPosZ + w);

		GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY + h, varZ - r.renderPosZ - w);
		GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY + h, varZ - r.renderPosZ + w);

		GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY + h, varZ - r.renderPosZ - w);
		GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY + h, varZ - r.renderPosZ - w);
		GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY + h, varZ - r.renderPosZ + w);
		GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY + h, varZ - r.renderPosZ + w);
		GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY, varZ - r.renderPosZ + w);
		GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY + h, varZ - r.renderPosZ + w);
		GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY, varZ - r.renderPosZ + w);
		GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY + h, varZ - r.renderPosZ + w);
		GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY, varZ - r.renderPosZ - w);
		GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY + h, varZ - r.renderPosZ - w);
		GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY, varZ - r.renderPosZ - w);
		GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY + h, varZ - r.renderPosZ - w);

		GL11.glEnd();

		GL11.glColor4d((double) c.getRed() / 255.0, (double) c.getGreen() / 255.0, (double) c.getBlue() / 255.0, 0.15);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY + h, varZ - r.renderPosZ - w);
		GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY, varZ - r.renderPosZ - w);
		GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY, varZ - r.renderPosZ - w);
		GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY + h, varZ - r.renderPosZ - w);

		GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY + h, varZ - r.renderPosZ + w);
		GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY, varZ - r.renderPosZ + w);
		GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY, varZ - r.renderPosZ + w);
		GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY + h, varZ - r.renderPosZ + w);

		GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY + h, varZ - r.renderPosZ - w);
		GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY, varZ - r.renderPosZ - w);
		GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY, varZ - r.renderPosZ + w);
		GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY + h, varZ - r.renderPosZ + w);

		GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY + h, varZ - r.renderPosZ - w);
		GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY, varZ - r.renderPosZ - w);
		GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY, varZ - r.renderPosZ + w);
		GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY + h, varZ - r.renderPosZ + w);

		GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY, varZ - r.renderPosZ + w);
		GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY, varZ - r.renderPosZ + w);
		GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY, varZ - r.renderPosZ - w);
		GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY, varZ - r.renderPosZ - w);

		GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY + h, varZ - r.renderPosZ + w);
		GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY + h, varZ - r.renderPosZ + w);
		GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY + h, varZ - r.renderPosZ - w);
		GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY + h, varZ - r.renderPosZ - w);
		GL11.glEnd();
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glPopMatrix();
	}

	@Subscribe
	public void onLoadBlock(EventLoadBlock e) {
		Location pos = e.getValue();
		if (pos.getId() != 54)
			return;
		for (Location loc : blocks) {
			if (loc.getX() == pos.getX() && loc.getY() == pos.getY() && loc.getZ() == pos.getZ()) {
				return;
			}
		}
		blocks.add(e.getValue());
	}

	@Override
	public void onEnabled() {
	}

	@Override
	public void onDisabled() {
	}

	@Override
	public String getName() {
		return "Wallhack\u00a72" + (blocks.size() > 0 ? " " + blocks.size() : "");
	}
}
