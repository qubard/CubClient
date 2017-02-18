package es.blueberrypancak.module;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import es.blueberrypancak.Client;
import es.blueberrypancak.Location;
import es.blueberrypancak.event.EventChat;
import es.blueberrypancak.event.EventEntityRender;
import es.blueberrypancak.event.EventRender;
import es.blueberrypancak.event.Subscribe;
import es.blueberrypancak.helper.InventoryHelper;
import es.blueberrypancak.hook.EntityPlayerSPHook;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ClickType;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;

@RegisterModule(key = Keyboard.KEY_J, color = 0x2FC997, listed = true)
public class Build extends Module {

	private static HashMap<Integer, Location> work = new HashMap<Integer, Location>();

	private static int distance = 3;
	
	private static int renderDistance = 100;
	
	private final int OFFHAND = 45;

	private long last = 0;
	
	private Color color = new Color(151 | (201 << 8) | (47 << 16));

	private Location start, end;
	
	private boolean equipBlock(int id) {
		if(!InventoryHelper.isItemOffhand(id)) {
			int slot = InventoryHelper.getItem(id);
			if(slot != -1) {
				EntityPlayer p = Client.getMinecraft().player;
				Minecraft mc = Client.getMinecraft();
				PlayerControllerMP controller = mc.playerController;
				if(slot <= 8) {
					slot = 45-(9-slot);
				}
				controller.windowClick(0, slot, 0, ClickType.PICKUP, p);
				controller.windowClick(0, OFFHAND, 0, ClickType.PICKUP, p);
			} else {
				return false;
			}
		}
		return true;
	}

	@Subscribe
	public void onChat(EventChat e) {
		String message = e.getValue();
		if (message.startsWith("-start")) {
			start = new Location(Client.getMinecraft().player.getPosition());
			e.setCancelled(true);
		} else if (message.startsWith("-end")) {
			end = new Location(Client.getMinecraft().player.getPosition());
			e.setCancelled(true);
		} else if (message.startsWith("-save")) {
			e.setCancelled(true);
			String[] args = message.split(" ");
			if(args.length > 1) {
				try {
					saveBounds(args[1]);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		} else if(message.startsWith("-load")) {
			e.setCancelled(true);
			String[] args = message.split(" ");
			if(start == null && args.length <= 3) {
				message("\247cSet a start position first.");
			} else {
				if(args.length > 1) {
					if(args.length > 3) {
						start = new Location(new BlockPos(Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4])), -1);
					}
					loadModel(args[1]);
				}
			}
		} else if(message.startsWith("-rotate")) {
			rotateYaw(Math.PI/2);
			message("\2479Rotated by 90 degrees on the y-axis.");
			e.setCancelled(true);
		} else if(message.startsWith("-render")) {
			String[] args = message.split(" ");
			if(args.length > 1) {
				renderDistance = Integer.parseInt(args[1]);
			}
			e.setCancelled(true);
		}
	}
	
	private void message(String s) {
		EntityPlayer p = Client.getMinecraft().player;
		p.addChatMessage(new TextComponentString(s));
	}
	
	public void rotateYaw(double angle) {
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		Iterator it = work.entrySet().iterator();

		double mx = (double)(start.getX()+(end.getX()-start.getX())/2);
		double mz = (double)(start.getZ()+(end.getZ()-start.getZ())/2);
		
		HashMap<Integer, Location> new_work = new HashMap<Integer, Location>();
		// easier to generate a new_work hashmap when rotating since the hashcodes change
		
		start = new Location(rotateYaw(start.getPos(), angle, mx, mz), start.getId());
		end = new Location(rotateYaw(end.getPos(), angle, mx, mz), end.getId());
		
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			Location l = (Location) (pair.getValue());
			BlockPos pos = l.getPos();
			BlockPos rotated = rotateYaw(pos, angle, mx, mz);
			Location newloc = new Location(rotated, l.getId());
			new_work.put(newloc.hashCode(), newloc);
		}
		work = new_work;
	}
	
	private BlockPos rotateYaw(BlockPos pos, double angle, double mx, double mz) {
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		double x = pos.getX()-mx;
		double z = pos.getZ()-mz;
		double newX = (double) (cos*x + sin*z);
		double newY = (double) pos.getY();
		double newZ = (double) (-sin*x + cos*z);
		return new BlockPos((int)(mx+newX), (int)newY, (int)(mz+newZ));
	}

	public void saveBounds(String filename) throws IOException {
		if(!filename.endsWith(".model")) {
			filename += ".model";
		}

		FileOutputStream out = null;
		
		try {
			out = new FileOutputStream(filename);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		Iterator<BlockPos> it = BlockPos.getAllInBox(start.getPos(), end.getPos()).iterator();
		
		out.write(new Location(new BlockPos(end.getX()-start.getX(), end.getY()-start.getY(), end.getZ()-start.getZ()), -1).toBytes());
		
		while (it.hasNext()) {
			BlockPos pos  = it.next();
			IBlockState state = Client.getMinecraft().world.getBlockState(pos);
			if (state.getBlock() != Blocks.AIR && state.isFullBlock()) {
				if (out != null) {
					out.write(new Location(new BlockPos(pos.getX()-start.getX(), pos.getY()-start.getY(), pos.getZ()-start.getZ()), Block.getIdFromBlock(state.getBlock())).toBytes());
				}
			}
		}

		if (out != null) {
			try {
				out.close();
				message("\247aSaved " + filename + "!");
			} catch (IOException e) {
				message("\247cError writing to " + filename + ".");
				e.printStackTrace();
			}
		}
	}

	@Subscribe
	public void onEntityRender(EventEntityRender e) {
		Minecraft mc = Client.getMinecraft();
		if (isEnabled()) {
			Iterator it = work.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				BlockPos pos = ((Location) (pair.getValue())).getPos();
				if (mc.player.getDistanceSqToCenter(pos) <= renderDistance) {
					drawBlock(pos, false);
				}
			}
			
			if (start != null & end != null) {
				drawBound();
			}
		}
	}

	private void drawBound() {
		Minecraft mc = Client.getMinecraft();
		RenderManager r = mc.getRenderManager();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glLineWidth(1.0F);

		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		double dX = end.getX()-start.getX();
		double dZ = end.getZ()-start.getZ();
		double dY = end.getY()-start.getY();
		
		if(dX == 0) {
			dX = 1;
		}
		
		if(dZ == 0) {
			dZ = 1;
		}
		
		if(dY == 0) {
			dY = 1;
		}
		
		double sx = (double)dX/Math.abs(dX);
		double sz = (double)dZ/Math.abs(dZ);
		double sy = (double)dY/Math.abs(dY);
		
		double a = 0.01;
		double b = 0.01;
		double c = 0.01;
		double d = 0.01;
		
		double e = 0.01;
		double f = 0.01;
		
		if(sx > 0 && sz > 0) {
			c = 0.99;
			d = 0.99;
		}
		
		if(sx > 0 && sz < 0) {
			b = 0.99;
			c = 0.99;
		}
		
		if(sy > 0) {
			e = 0.01;
			f = 0.99;
		} else {
			e = 0.99;
			f = 0.01;
		}
		
		if(sx < 0 && sz > 0) {
			a = 0.99;
			d = 0.99;
		}

		if(sx < 0 && sz < 0) {
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
		GL11.glEnable(GL11.GL_DEPTH_TEST);
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
		
		Location up = work.get(new Location(new BlockPos(l.getX(), l.getY() + 1, l.getZ()), 1).hashCode());
		Location down = work.get(new Location(new BlockPos(l.getX(), l.getY() - 1, l.getZ()), 1).hashCode());
		Location left = work.get(new Location(new BlockPos(l.getX() - 1, l.getY(), l.getZ()), 1).hashCode());
		Location right = work.get(new Location(new BlockPos(l.getX() + 1, l.getY(), l.getZ()), 1).hashCode());
		Location forward = work.get(new Location(new BlockPos(l.getX(), l.getY(), l.getZ() + 1), 1).hashCode());
		Location backward = work.get(new Location(new BlockPos(l.getX(), l.getY(), l.getZ() - 1), 1).hashCode());

		if (forward == null) {
			if (down == null) {
				GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY, varZ - r.renderPosZ + w);
				GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY, varZ - r.renderPosZ + w);
			}

			if (left == null) {
				GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY, varZ - r.renderPosZ + w);
				GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY + h, varZ - r.renderPosZ + w);
			}

			if (right == null) {
				GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY, varZ - r.renderPosZ + w);
				GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY + h, varZ - r.renderPosZ + w);
			}

			if (up == null) {
				GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY + h, varZ - r.renderPosZ + w);
				GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY + h, varZ - r.renderPosZ + w);
			}
		}

		if (right == null) {
			if (down == null) {
				GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY, varZ - r.renderPosZ + w);
				GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY, varZ - r.renderPosZ - w);
			}

			if (up == null) {
				GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY + h, varZ - r.renderPosZ + w);
				GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY + h, varZ - r.renderPosZ - w);
			}

			if (backward == null) {
				GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY, varZ - r.renderPosZ - w);
				GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY + h, varZ - r.renderPosZ - w);
			}
		}

		if (backward == null) {
			if (left == null) {
				GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY, varZ - r.renderPosZ - w);
				GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY + h, varZ - r.renderPosZ - w);
			}

			if (up == null) {
				GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY + h, varZ - r.renderPosZ - w);
				GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY + h, varZ - r.renderPosZ - w);
			}

			if (down == null) {
				GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY, varZ - r.renderPosZ - w);
				GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY, varZ - r.renderPosZ - w);
			}
		}

		if (left == null) {
			if (down == null) {
				GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY, varZ - r.renderPosZ - w);
				GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY, varZ - r.renderPosZ + w);
			}

			if (up == null) {
				GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY + h, varZ - r.renderPosZ - w);
				GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY + h, varZ - r.renderPosZ + w);
			}
		}

		GL11.glEnd();

		GL11.glColor4d((double) color.getRed() / 255.0, (double) color.getGreen() / 255.0,
				(double) color.getBlue() / 255.0, (double) 0.12);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glBegin(GL11.GL_QUADS);

		if (backward == null) {
			GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY + h, varZ - r.renderPosZ - w);
			GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY, varZ - r.renderPosZ - w);
			GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY, varZ - r.renderPosZ - w);
			GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY + h, varZ - r.renderPosZ - w);
		}

		if (forward == null) {
			GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY + h, varZ - r.renderPosZ + w);
			GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY, varZ - r.renderPosZ + w);
			GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY, varZ - r.renderPosZ + w);
			GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY + h, varZ - r.renderPosZ + w);
		}

		if (right == null) {
			GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY + h, varZ - r.renderPosZ - w);
			GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY, varZ - r.renderPosZ - w);
			GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY, varZ - r.renderPosZ + w);
			GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY + h, varZ - r.renderPosZ + w);
		}

		if (left == null) {
			GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY + h, varZ - r.renderPosZ - w);
			GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY, varZ - r.renderPosZ - w);
			GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY, varZ - r.renderPosZ + w);
			GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY + h, varZ - r.renderPosZ + w);
		}

		if (down == null) {
			GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY, varZ - r.renderPosZ + w);
			GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY, varZ - r.renderPosZ + w);
			GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY, varZ - r.renderPosZ - w);
			GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY, varZ - r.renderPosZ - w);
		}

		if (up == null) {
			GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY + h, varZ - r.renderPosZ + w);
			GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY + h, varZ - r.renderPosZ + w);
			GL11.glVertex3d(varX - r.renderPosX - w, varY - r.renderPosY + h, varZ - r.renderPosZ - w);
			GL11.glVertex3d(varX - r.renderPosX + w, varY - r.renderPosY + h, varZ - r.renderPosZ - w);
		}

		GL11.glEnd();
		GL11.glEnable(GL11.GL_CULL_FACE);

		if (!depth) {
			GL11.glDepthMask(true);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
		}

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glPopMatrix();
	}

	private BlockPos getMin() {
		Minecraft mc = Client.getMinecraft();
		EntityPlayer p = mc.player;
		BlockPos min = null;
		Iterator it = work.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			BlockPos pos = (BlockPos) pair.getValue();
			if (min == null || p.getDistance(pos.getX(), pos.getY(), pos.getZ()) < p.getDistance(min.getX(), min.getY(),
					min.getZ())) {
				if (mc.world.getBlockState(pos.add(0, -1, 0)).getBlock() != Blocks.AIR) {
					min = pos;
				}
			}
		}
		return min;
	}

	private int getLowestLevel(int distance) {
		Minecraft mc = Client.getMinecraft();
		EntityPlayer p = mc.player;
		int i = (int) p.posY + 1;
		Iterator it = work.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			Location l = (Location) pair.getValue();
			BlockPos pos = l.getPos();
			double dx = p.posX - pos.getX();
			double dy = p.posY - pos.getY();
			double dz = p.posZ - pos.getZ();
			if (Math.sqrt(dx*dx + dz*dz + dy*dy) <= distance) {
				if (pos.getY() < i) {
					i = pos.getY();
				}
			}
		}
		return i;
	}

	@Subscribe
	public void onRender(EventRender e) {
		if (isEnabled() && System.currentTimeMillis() > last) {
			Minecraft mc = Client.getMinecraft();
			EntityPlayer p = mc.player;

			Iterator it = work.entrySet().iterator();
			int count = 0;
			
			boolean under = false;
			
			int lowest = getLowestLevel(4);
			
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				Location l = (Location) pair.getValue();
				BlockPos pos = l.getPos();
				Block forward = mc.world.getBlockState(pos.add(0, 0, 1)).getBlock();
				Block backward = mc.world.getBlockState(pos.add(0, 0, -1)).getBlock();
				Block left = mc.world.getBlockState(pos.add(-1, 0, 0)).getBlock();
				Block right = mc.world.getBlockState(pos.add(1, 0, 0)).getBlock();
				Block down = mc.world.getBlockState(pos.add(0, -1, 0)).getBlock();
				Block up = mc.world.getBlockState(pos.add(0, 1, 0)).getBlock();
				float dist = 10;
				
				double dot = p.getLookVec().dotProduct(new Vec3d((int)p.posX-pos.getX(), (int)p.posY-pos.getY(), (int)p.posZ-pos.getZ()).normalize());
				if (lowest == pos.getY() && p.getDistance(pos.getX(), pos.getY(), pos.getZ()) <= distance && dot < 0) {
					if(down != Blocks.AIR) {
						placeBlock(l.getId(), pos.add(0, -1, 0), EnumFacing.UP);
						count++;
					} else if(left != Blocks.AIR) {
						placeBlock(l.getId(), pos.add(-1, 0, 0), EnumFacing.EAST);
						count++;
					} else if(right != Blocks.AIR) {
						placeBlock(l.getId(), pos.add(1, 0, 0), EnumFacing.WEST);
						count++;
					} else if(forward != Blocks.AIR) {
						placeBlock(l.getId(), pos.add(0, 0, 1), EnumFacing.NORTH);
						count++;
					} else if(backward != Blocks.AIR) {
						placeBlock(l.getId(), pos.add(0, 0, -1), EnumFacing.SOUTH);
						count++;
					} else if(up != Blocks.AIR) {
						placeBlock(l.getId(), pos.add(0, 1, 0), EnumFacing.DOWN);
						count++;
					}
					
					if (count > 4) {
						break;
					}
				}
			}
			last = System.currentTimeMillis() + 150;
		}
	}
	
	private void removeFilled() {
		Minecraft mc = Client.getMinecraft();
		
		Iterator it = work.entrySet().iterator();
		ArrayList<Object> remove = new ArrayList<Object>();

		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			Location pos = (Location) pair.getValue();
			if (mc.world.getBlockState(pos.getPos()).getBlock() != Blocks.AIR) {
				remove.add(pair.getKey());
			}
		}

		for (Object o : remove) {
			work.remove(o);
		}
	}

	private void loadModel(String filename) {
		if(!filename.endsWith(".model")) {
			filename += ".model";
		}
		
		try {
			FileInputStream input = new FileInputStream(filename);
			byte[] b;
			work.clear();
			try {
				b = new byte[input.available()];
				input.read(b);
				for (int i = 0; i < 7; i += 7) {
					byte[] slice = new byte[7];
					for (int k = 0; k < slice.length; k++) {
						slice[k] = b[i + k];
					}
					Location l = Location.readBytes(slice);
					end = start.add(l.getX(), l.getY(), l.getZ());
				}
				
				for (int i = 7; i < b.length; i += 7) {
					byte[] slice = new byte[7];
					for (int k = 0; k < slice.length; k++) {
						slice[k] = b[i + k];
					}
					Location l = Location.readBytes(slice);
					l = l.add(start.getX(), start.getY(), start.getZ());
					work.put(l.hashCode(), l);
				}
				
				if (input != null) {
					message("\2476Loaded " + filename + " with " + work.size() + " entries at " + start.getPos().getX() + "," + start.getPos().getY() + "," + start.getPos().getZ());
					input.close();
				}
				
			} catch (IOException e) {
				message("\247cError loading file.");
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			message("\247cFile not found.");
			e.printStackTrace();
		}
	}
	
	@Override
	public void onEnabled() {
		removeFilled();
	}

	private void placeBlock(int id, BlockPos pos, EnumFacing face) {
		if(equipBlock(id) && InventoryHelper.isItemOffhand(id)) {
			Minecraft mc = Client.getMinecraft();
			EntityPlayerSPHook p = (EntityPlayerSPHook) mc.player;
			p.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, face, EnumHand.OFF_HAND, 0, 1, 0));
			p.getConnection().sendPacket(new CPacketAnimation(EnumHand.OFF_HAND));
			removeFilled();
		}
	}

	@Override
	public void onDisabled() {
		
	}

	@Override
	public String getName() {
		int n = work.size();
		return "Build" + (n != 0 ? " " + work.size() : "");
	}

}
