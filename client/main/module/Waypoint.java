package client.main.module;

import org.lwjgl.opengl.GL11;

import client.main.Client;
import client.main.event.EventChat;
import client.main.event.EventEntityRender;
import client.main.event.EventRespawn;
import client.main.event.Subscribe;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;

@RegisterModule
public class Waypoint extends Module {

	private Marker marker;

	@Subscribe
	public void onEntityRender(EventEntityRender e) {
		if (marker != null) {
			renderMarker(marker);
		}
	}

	@Subscribe
	public void onChat(EventChat e) {
		String message = e.getValue();
		if (message.startsWith("-f")) {
			if (message.split(" ").length == 4) {
				int x = Integer.parseInt(message.split(" ")[1]);
				int y = Integer.parseInt(message.split(" ")[2]);
				int z = Integer.parseInt(message.split(" ")[3]);
				this.marker = new Marker(x, y, z);
			} else if (message.split(" ").length == 3) {
				int x = Integer.parseInt(message.split(" ")[1]);
				int z = Integer.parseInt(message.split(" ")[2]);
				this.marker = new Marker(x, (int) Client.getMinecraft().player.posY, z);
			} else {
				this.marker = null;
			}
			e.setCancelled(true);
		}
	}

	@Subscribe
	public void onRespawn(EventRespawn e) {
		EntityPlayer p = Client.getMinecraft().player;
		int posX = (int) p.posX;
		int posY = (int) p.posY;
		int posZ = (int) p.posZ;
		p.addChatMessage(new TextComponentString(
				"You died at " + (posX >= 0 ? "\247a+" : "\247c") + (int) posX + ", " + (posY >= 0 ? "\247a+" : "\247c")
						+ (int) posY + ", " + (posZ >= 0 ? "\247a+" : "\247c") + (int) posZ));
	}

	@Override
	public void onEnabled() {

	}

	@Override
	public void onDisabled() {

	}

	@Override
	public String getName() {
		return null;
	}

	private void renderMarker(Marker m) {
		RenderManager r = Client.getMinecraft().getRenderManager();

		int par3 = m.getX();
		int par5 = m.getY();
		int par7 = m.getZ();

		float var10 = (int) r.renderViewEntity.getDistance(par3, par5, par7);
		FontRenderer var12 = Client.getMinecraft().fontRendererObj;
		String par2Str = "\2477" + (int) r.renderViewEntity.getDistance(par3, par5, par7) + "m";

		par3 -= r.renderPosX;
		par5 -= r.renderPosY;
		par7 -= r.renderPosZ;
		int d = Client.getMinecraft().gameSettings.renderDistanceChunks * 16;
		if (var10 > d) {
			par3 *= d / var10;
			par5 *= d / var10;
			par7 *= d / var10;
			var10 = d;
		}
		float var14 = 0.00896667F * var10;
		GL11.glDisable(GL11.GL_FOG);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glPushMatrix();
		GL11.glDepthMask(false);
		GL11.glTranslated((float) par3 + 0.0F, (float) par5, (float) par7);
		GL11.glNormal3f(0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-r.playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(Client.getMinecraft().gameSettings.thirdPersonView == 2 ? -r.playerViewX : r.playerViewX, 1.0F,
				0.0F, 0.0F);
		GL11.glScalef(-var14, -var14, var14);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		byte var16 = 0;
		var16 -= 0;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		int var17 = var12.getStringWidth(par2Str) / 2;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		vertexbuffer.pos((double) (-var17 - 2), (double) (-2), 0.0D).color(0.0F, 0.0F, 0.0F, 0.20F).endVertex();
		vertexbuffer.pos((double) (-var17 - 2), (double) (9), 0.0D).color(0.0F, 0.0F, 0.0F, 0.20F).endVertex();
		vertexbuffer.pos((double) (var17 + 2), (double) (9), 0.0D).color(0.0F, 0.0F, 0.0F, 0.20F).endVertex();
		vertexbuffer.pos((double) (var17 + 2), (double) (-2), 0.0D).color(0.0F, 0.0F, 0.0F, 0.20F).endVertex();
		tessellator.draw();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		var12.drawStringWithShadow(par2Str, -var12.getStringWidth(par2Str) / 2, var16, 0xFFFFFF);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	}

	class Marker {
		private int x, y, z;

		public Marker(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public int getX() {
			return this.x;
		}

		public int getY() {
			return this.y;
		}

		public int getZ() {
			return this.z;
		}
	}
}
