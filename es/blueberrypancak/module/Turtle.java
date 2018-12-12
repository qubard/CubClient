package es.blueberrypancak.module;

import java.awt.Color;
import java.util.LinkedList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import es.blueberrypancak.Client;
import es.blueberrypancak.event.EventBlockBreak;
import es.blueberrypancak.event.EventChat;
import es.blueberrypancak.event.EventEntityRender;
import es.blueberrypancak.event.EventUpdateEntity;
import es.blueberrypancak.event.Subscribe;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

@RegisterModule(key = Keyboard.KEY_M, color = 0xBBAAFF, listed = true, pressed = false)
public class Turtle extends Module {

	private Color color = new Color(255 | (170 << 8) | (187 << 16));

	private BlockPos start, end;

	private long nextBuild = 0;

	private boolean running = false;

	private LinkedList<BlockPos> queue = new LinkedList<BlockPos>();

	@Override
	public void onEnabled() {
		build();
	}

	@Override
	public void onDisabled() {
		if (running) {
			running = false;
			message(running ? "\247aTurtle mode enabled." : "\247cTurtle mode disabled.");
		}
	}

	@Subscribe
	public void onChat(EventChat e) {
		String message = e.getValue();
		if (message.startsWith("-tstart")) {
			start = new BlockPos(Client.getMinecraft().player.getPosition());
			e.setCancelled(true);
		} else if (message.startsWith("-tend")) {
			end = new BlockPos(Client.getMinecraft().player.getPosition());
			e.setCancelled(true);
		} else if (message.startsWith("-turtle")) {
			running = !running;
			message(running ? "\247aTurtle mode enabled." : "\247cTurtle mode disabled.");
			e.setCancelled(true);
		} else if (message.startsWith("-tclear")) {
			start = end = null;
			queue.clear();
			e.setCancelled(true);
		}

		if (message.startsWith("-tend") || message.startsWith("-tstart")) {
			build();
		}
	}

	// all of these occurrences should be static at some point in time
	private void message(String s) {
		EntityPlayer p = Client.getMinecraft().player;
		p.addChatMessage(new TextComponentString(s));
	}

	// build the queue by which blocks are dug out
	private void build() {
		queue.clear();

		int dx = end.getX() - start.getX();
		int dy = end.getY() - start.getY();
		int dz = end.getZ() - start.getZ();

		int adx = Math.abs(dx);
		int ady = Math.abs(dy);
		int adz = Math.abs(dz);
		for (int i = 0; i <= ady; i++) {
			for (int j = 0; j <= adx; j++) {
				for (int k = j % 2 == 0 ? 0 : adz; j % 2 == 0 ? k <= adz : k >= 0; k = k + (j % 2 == 0 ? 1 : -1)) {
					BlockPos pos = new BlockPos(start.getX() + j * dx / Math.abs(dx),
							start.getY() + i * dy / Math.abs(dy), start.getZ() + k * dz / Math.abs(dz));
					IBlockState state = Client.getMinecraft().world.getBlockState(pos);
					if (state.getBlock() != Blocks.AIR) {
						queue.add(pos);
					}
				}
			}
		}
	}

	private void drawBound() {
		Minecraft mc = Client.getMinecraft();
		RenderManager r = mc.getRenderManager();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glLineWidth(1.0F);

		GL11.glDepthMask(false);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		double dX = end.getX() - start.getX();
		double dZ = end.getZ() - start.getZ();
		double dY = end.getY() - start.getY();

		if (dX == 0) {
			dX = 1;
		}

		if (dZ == 0) {
			dZ = 1;
		}

		if (dY == 0) {
			dY = 1;
		}

		double sx = (double) dX / Math.abs(dX);
		double sz = (double) dZ / Math.abs(dZ);
		double sy = (double) dY / Math.abs(dY);

		double a = 0.01;
		double b = 0.01;
		double c = 0.01;
		double d = 0.01;

		double e = 0.01;
		double f = 0.01;

		if (sx > 0 && sz > 0) {
			c = 0.99;
			d = 0.99;
		}

		if (sx > 0 && sz < 0) {
			b = 0.99;
			c = 0.99;
		}

		if (sy > 0) {
			e = 0.01;
			f = 0.99;
		} else {
			e = 0.99;
			f = 0.01;
		}

		if (sx < 0 && sz > 0) {
			a = 0.99;
			d = 0.99;
		}

		if (sx < 0 && sz < 0) {
			a = 0.99;
			b = 0.99;
		}

		double startX = start.getX() + a;
		double startY = start.getY() + e;
		double startZ = start.getZ() + b;

		double endX = end.getX() + c;
		double endY = end.getY() + f;
		double endZ = end.getZ() + d;

		GL11.glDisable(GL11.GL_TEXTURE_2D);

		GL11.glBegin(GL11.GL_LINES);

		GL11.glColor4d((double) color.getRed() / 255.0, (double) color.getGreen() / 255.0,
				(double) color.getBlue() / 255.0, 0.5);

		GL11.glVertex3d(endX - r.renderPosX, endY - r.renderPosY, endZ - r.renderPosZ);
		GL11.glVertex3d(endX - r.renderPosX + (startX - endX), endY - r.renderPosY, endZ - r.renderPosZ);

		GL11.glVertex3d(startX - r.renderPosX, endY - r.renderPosY, endZ - r.renderPosZ);
		GL11.glVertex3d(startX - r.renderPosX, endY - r.renderPosY, startZ - r.renderPosZ);

		GL11.glVertex3d(startX - r.renderPosX, endY - r.renderPosY, startZ - r.renderPosZ);
		GL11.glVertex3d(startX - r.renderPosX, startY - r.renderPosY, startZ - r.renderPosZ);

		GL11.glVertex3d(startX - r.renderPosX, startY - r.renderPosY, startZ - r.renderPosZ);
		GL11.glVertex3d(startX - r.renderPosX, startY - r.renderPosY, endZ - r.renderPosZ);

		GL11.glVertex3d(startX - r.renderPosX, startY - r.renderPosY, endZ - r.renderPosZ);
		GL11.glVertex3d(startX - r.renderPosX, endY - r.renderPosY, endZ - r.renderPosZ);

		GL11.glVertex3d(startX - r.renderPosX, startY - r.renderPosY, endZ - r.renderPosZ);
		GL11.glVertex3d(endX - r.renderPosX, startY - r.renderPosY, endZ - r.renderPosZ);

		GL11.glVertex3d(endX - r.renderPosX, startY - r.renderPosY, endZ - r.renderPosZ);
		GL11.glVertex3d(endX - r.renderPosX, endY - r.renderPosY, endZ - r.renderPosZ);

		GL11.glVertex3d(endX - r.renderPosX, startY - r.renderPosY, endZ - r.renderPosZ);
		GL11.glVertex3d(endX - r.renderPosX, startY - r.renderPosY, startZ - r.renderPosZ);

		GL11.glVertex3d(endX - r.renderPosX, startY - r.renderPosY, startZ - r.renderPosZ);
		GL11.glVertex3d(endX - r.renderPosX, endY - r.renderPosY, startZ - r.renderPosZ);

		GL11.glVertex3d(endX - r.renderPosX, endY - r.renderPosY, startZ - r.renderPosZ);
		GL11.glVertex3d(endX - r.renderPosX, endY - r.renderPosY, endZ - r.renderPosZ);

		GL11.glVertex3d(endX - r.renderPosX, endY - r.renderPosY, startZ - r.renderPosZ);
		GL11.glVertex3d(startX - r.renderPosX, endY - r.renderPosY, startZ - r.renderPosZ);

		GL11.glVertex3d(endX - r.renderPosX, startY - r.renderPosY, startZ - r.renderPosZ);
		GL11.glVertex3d(startX - r.renderPosX, startY - r.renderPosY, startZ - r.renderPosZ);

		GL11.glEnd();

		GL11.glDisable(GL11.GL_CULL_FACE);

		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glBegin(GL11.GL_QUADS);

		GL11.glColor4d((double) color.getRed() / 255.0, (double) color.getGreen() / 255.0,
				(double) color.getBlue() / 255.0, (double) 0.1011);

		GL11.glVertex3d(startX - r.renderPosX, endY - r.renderPosY, startZ - r.renderPosZ);
		GL11.glVertex3d(endX - r.renderPosX, endY - r.renderPosY, startZ - r.renderPosZ);
		GL11.glVertex3d(endX - r.renderPosX, endY - r.renderPosY, endZ - r.renderPosZ);
		GL11.glVertex3d(startX - r.renderPosX, endY - r.renderPosY, endZ - r.renderPosZ);

		GL11.glVertex3d(startX - r.renderPosX, startY - r.renderPosY, startZ - r.renderPosZ);
		GL11.glVertex3d(endX - r.renderPosX, startY - r.renderPosY, startZ - r.renderPosZ);
		GL11.glVertex3d(endX - r.renderPosX, startY - r.renderPosY, endZ - r.renderPosZ);
		GL11.glVertex3d(startX - r.renderPosX, startY - r.renderPosY, endZ - r.renderPosZ);

		GL11.glVertex3d(startX - r.renderPosX, startY - r.renderPosY, startZ - r.renderPosZ);
		GL11.glVertex3d(startX - r.renderPosX, startY - r.renderPosY, endZ - r.renderPosZ);
		GL11.glVertex3d(startX - r.renderPosX, endY - r.renderPosY, endZ - r.renderPosZ);
		GL11.glVertex3d(startX - r.renderPosX, endY - r.renderPosY, startZ - r.renderPosZ);

		GL11.glVertex3d(endX - r.renderPosX, startY - r.renderPosY, startZ - r.renderPosZ);
		GL11.glVertex3d(endX - r.renderPosX, startY - r.renderPosY, endZ - r.renderPosZ);
		GL11.glVertex3d(endX - r.renderPosX, endY - r.renderPosY, endZ - r.renderPosZ);
		GL11.glVertex3d(endX - r.renderPosX, endY - r.renderPosY, startZ - r.renderPosZ);

		GL11.glVertex3d(startX - r.renderPosX, startY - r.renderPosY, endZ - r.renderPosZ);
		GL11.glVertex3d(endX - r.renderPosX, startY - r.renderPosY, endZ - r.renderPosZ);
		GL11.glVertex3d(endX - r.renderPosX, endY - r.renderPosY, endZ - r.renderPosZ);
		GL11.glVertex3d(startX - r.renderPosX, endY - r.renderPosY, endZ - r.renderPosZ);

		GL11.glVertex3d(startX - r.renderPosX, startY - r.renderPosY, startZ - r.renderPosZ);
		GL11.glVertex3d(endX - r.renderPosX, startY - r.renderPosY, startZ - r.renderPosZ);
		GL11.glVertex3d(endX - r.renderPosX, endY - r.renderPosY, startZ - r.renderPosZ);
		GL11.glVertex3d(startX - r.renderPosX, endY - r.renderPosY, startZ - r.renderPosZ);

		GL11.glEnd();

		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glPopMatrix();
	}

	private void drawBlock(BlockPos l, boolean depth) {
		Minecraft mc = Client.getMinecraft();
		IBlockState state = mc.world.getBlockState(l);
		RenderManager r = mc.getRenderManager();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glLineWidth(1.0F);

		if (!depth) {
			GL11.glDepthMask(false);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
		}

		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		double varX = l.getX() + 0.5;
		double varY = l.getY();
		double varZ = l.getZ() + 0.5;

		GL11.glDisable(GL11.GL_TEXTURE_2D);

		double w = 0.5;
		double h = 1.0;

		GL11.glBegin(GL11.GL_LINES);

		GL11.glColor4d((double) color.getRed() / 255.0, (double) color.getGreen() / 255.0,
				(double) color.getBlue() / 255.0, 0.5);

		GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY, varZ - r.renderPosZ + w);
		GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY, varZ - r.renderPosZ + w);

		GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY, varZ - r.renderPosZ + w);
		GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY + h, varZ - r.renderPosZ + w);

		GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY, varZ - r.renderPosZ + w);
		GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY + h, varZ - r.renderPosZ + w);

		GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY + h, varZ - r.renderPosZ + w);
		GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY + h, varZ - r.renderPosZ + w);

		GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY, varZ - r.renderPosZ + w);
		GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY, varZ - r.renderPosZ - w);

		GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY + h, varZ - r.renderPosZ + w);
		GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY + h, varZ - r.renderPosZ - w);

		GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY, varZ - r.renderPosZ - w);
		GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY + h, varZ - r.renderPosZ - w);

		GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY, varZ - r.renderPosZ - w);
		GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY + h, varZ - r.renderPosZ - w);

		GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY + h, varZ - r.renderPosZ - w);
		GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY + h, varZ - r.renderPosZ - w);

		GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY, varZ - r.renderPosZ - w);
		GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY, varZ - r.renderPosZ - w);

		GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY, varZ - r.renderPosZ - w);
		GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY, varZ - r.renderPosZ + w);

		GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY + h, varZ - r.renderPosZ - w);
		GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY + h, varZ - r.renderPosZ + w);

		GL11.glEnd();

		GL11.glColor4d((double) color.getRed() / 255.0, (double) color.getGreen() / 255.0,
				(double) color.getBlue() / 255.0, (double) 0.12);
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

		if (!depth) {
			GL11.glDepthMask(true);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
		}

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glPopMatrix();
	}

	@Subscribe
	public void onUpdateEntity(EventUpdateEntity e) {
		if (isEnabled() && running) {
			Minecraft mc = Client.getMinecraft();
			EntityPlayerSP p = mc.player;
			BlockPos next = queue.peek();
			if (next != null) {
				double dx = p.posX - (double) (next.getX() + 0.5);
				double dy = p.posY - (double) (next.getY() + 0.5);
				double dz = p.posZ - (double) (next.getZ() + 0.5);
				float dist = (float) Math.sqrt(dx * dx + dz * dz);
				if (dist > 0.2) { // approximation of sqrt(2)
					float r = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
					float yaw = (float) (Math.atan2(dx, dz) * 180f / Math.PI);
					yaw = 180 - yaw;
					mc.gameSettings.keyBindForward.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);
					p.rotationYaw = yaw;
					if ((int) p.posY > next.getY()) {
						p.rotationPitch = 90f;
					} else {
						p.rotationPitch = 45f;
					}
				}

				if (p.getDistanceSqToCenter(next) < 3) {
					if (mc.world.getBlockState(next).getMaterial() != Material.AIR
							&& mc.playerController.onPlayerDamageBlock(next, EnumFacing.UP)) {
						mc.effectRenderer.addBlockHitEffects(next, EnumFacing.UP);
						mc.player.swingArm(EnumHand.MAIN_HAND);
					} else {
						queue.removeFirst();
					}
				}
			}
		}
	}

	@Subscribe
	public void onEntityRender(EventEntityRender e) {
		if (isEnabled()) {
			if (queue.size() > 0) {
				drawBlock(queue.peek(), false);
			}

			if (start != null && end != null) {
				drawBound();
			} else {
				if (start != null) {
					drawBlock(start, false);
				}

				if (end != null) {
					drawBlock(end, false);
				}
			}

			Minecraft mc = Client.getMinecraft();
			EntityPlayer p = mc.player;

			if (System.currentTimeMillis() > nextBuild) {
				build();
				nextBuild = System.currentTimeMillis() + 5000;
			}
		}
	}

	@Subscribe
	public void onBlockBreak(EventBlockBreak e) {
		if (isEnabled()) {
			Minecraft mc = Client.getMinecraft();
			EntityPlayerSP p = mc.player;
			mc.playerController.processRightClickBlock(p, mc.world, e.getBlockPos(), EnumFacing.DOWN, p.getLookVec(),
					EnumHand.MAIN_HAND);
		}
	}

	@Override
	public String getName() {
		int n = queue.size();
		return "Turtle" + (n != 0 ? " " + n : "");
	}

}
