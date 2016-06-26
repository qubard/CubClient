package es.blueberrypancak.module;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import es.blueberrypancak.Client;
import es.blueberrypancak.event.EventEntityRender;
import es.blueberrypancak.event.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

@RegisterModule
public class TraceESP extends Module {

	@Subscribe
	public void onEntityRender(EventEntityRender e) {
		Minecraft mc = Client.getMinecraft();
		for (Object o : mc.theWorld.loadedEntityList) {
			if (o instanceof EntityItem) {
				trace(mc, (EntityItem) o, e.getValue(), "#FFD105");
			} else if (o instanceof EntityEnderPearl) {
				trace(mc, (EntityEnderPearl) o, e.getValue(), "#00FF90");
			} else if (o instanceof EntityOtherPlayerMP) {
				EntityOtherPlayerMP g = (EntityOtherPlayerMP) o;
				EntityPlayer p = mc.thePlayer;
				box(mc, g, e.getValue(), 1.0);
				trace(mc, g, e.getValue(), 1.0);
				RenderManager r = mc.getRenderManager();
				double d0 = g.lastTickPosX + (g.posX - g.lastTickPosX) * (double) e.getValue();
				double d1 = g.lastTickPosY + (g.posY - g.lastTickPosY) * (double) e.getValue();
				double d2 = g.lastTickPosZ + (g.posZ - g.lastTickPosZ) * (double) e.getValue();
				renderLivingLabel(g, r.getFontRenderer(), g.getName(), (float) (d0 - r.renderPosX), (float) (d1 - r.renderPosY) + g.getEyeHeight() * 1.5F, (float) (d2 - r.renderPosZ), 0, e.getValue(), r.options.thirdPersonView == 2, false);
			}
		}
	}

	private void box(Minecraft mc, EntityOtherPlayerMP e, float par1, double opacity) {
		/*
		 * if(CubAimbot.get().isFriend(par1Entity.getName())) { opacity = 0.4; }
		 */
		RenderManager r = mc.getRenderManager();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glLineWidth(1.0F);
		float hp = e.getHealth();
		Color c = Color.decode("#" + (hp / 2 > 7 ? "55FF55" : hp / 2 >= 4 ? "FFFF55" : "FF5555"));
		GL11.glPushMatrix();
		GL11.glDepthMask(false);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4d((double) c.getRed() / 255.0, (double) c.getGreen() / 255.0, (double) c.getBlue() / 255.0, opacity);
		double var3 = e.lastTickPosX + (e.posX - e.lastTickPosX) * (double) par1;
		double var5 = e.lastTickPosY + (e.posY - e.lastTickPosY) * (double) par1;
		double var7 = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * (double) par1;
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBegin(GL11.GL_LINES);

		double w = e.width - 0.2;
		double h = e.height + 0.3;

		GL11.glVertex3d(var3 - r.renderPosX + w, var5 - r.renderPosY, var7 - r.renderPosZ + w);
		GL11.glVertex3d(var3 - r.renderPosX - w, var5 - r.renderPosY, var7 - r.renderPosZ + w);
		GL11.glVertex3d(var3 - r.renderPosX + w, var5 - r.renderPosY, var7 - r.renderPosZ - w);
		GL11.glVertex3d(var3 - r.renderPosX + w, var5 - r.renderPosY, var7 - r.renderPosZ + w);
		GL11.glVertex3d(var3 - r.renderPosX - w, var5 - r.renderPosY, var7 - r.renderPosZ - w);
		GL11.glVertex3d(var3 - r.renderPosX - w, var5 - r.renderPosY, var7 - r.renderPosZ + w);
		GL11.glVertex3d(var3 - r.renderPosX + w, var5 - r.renderPosY, var7 - r.renderPosZ - w);
		GL11.glVertex3d(var3 - r.renderPosX - w, var5 - r.renderPosY, var7 - r.renderPosZ - w);
		GL11.glVertex3d(var3 - r.renderPosX + w, var5 - r.renderPosY, var7 - r.renderPosZ + w);
		GL11.glVertex3d(var3 - r.renderPosX - w, var5 - r.renderPosY, var7 - r.renderPosZ + w);
		GL11.glVertex3d(var3 - r.renderPosX + w, var5 - r.renderPosY + h, var7 - r.renderPosZ - w);
		GL11.glVertex3d(var3 - r.renderPosX + w, var5 - r.renderPosY + h, var7 - r.renderPosZ + w);

		GL11.glVertex3d(var3 - r.renderPosX - w, var5 - r.renderPosY + h, var7 - r.renderPosZ - w);
		GL11.glVertex3d(var3 - r.renderPosX - w, var5 - r.renderPosY + h, var7 - r.renderPosZ + w);

		GL11.glVertex3d(var3 - r.renderPosX + w, var5 - r.renderPosY + h, var7 - r.renderPosZ - w);
		GL11.glVertex3d(var3 - r.renderPosX - w, var5 - r.renderPosY + h, var7 - r.renderPosZ - w);
		GL11.glVertex3d(var3 - r.renderPosX + w, var5 - r.renderPosY + h, var7 - r.renderPosZ + w);
		GL11.glVertex3d(var3 - r.renderPosX - w, var5 - r.renderPosY + h, var7 - r.renderPosZ + w);
		GL11.glVertex3d(var3 - r.renderPosX + w, var5 - r.renderPosY, var7 - r.renderPosZ + w);
		GL11.glVertex3d(var3 - r.renderPosX + w, var5 - r.renderPosY + h, var7 - r.renderPosZ + w);
		GL11.glVertex3d(var3 - r.renderPosX - w, var5 - r.renderPosY, var7 - r.renderPosZ + w);
		GL11.glVertex3d(var3 - r.renderPosX - w, var5 - r.renderPosY + h, var7 - r.renderPosZ + w);
		GL11.glVertex3d(var3 - r.renderPosX - w, var5 - r.renderPosY, var7 - r.renderPosZ - w);
		GL11.glVertex3d(var3 - r.renderPosX - w, var5 - r.renderPosY + h, var7 - r.renderPosZ - w);
		GL11.glVertex3d(var3 - r.renderPosX + w, var5 - r.renderPosY, var7 - r.renderPosZ - w);
		GL11.glVertex3d(var3 - r.renderPosX + w, var5 - r.renderPosY + h, var7 - r.renderPosZ - w);

		GL11.glEnd();
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glPopMatrix();
	}

	private void trace(Minecraft mc, Entity e, float par1, String color) {
		if (mc.getRenderManager().renderViewEntity != null) {
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glLineWidth(1F);
			Color c = Color.decode(color);
			GL11.glPushMatrix();
			GL11.glDepthMask(false);
			GL11.glColor3d((double) c.getRed() / 255.0, (double) c.getGreen() / 255.0, (double) c.getBlue() / 255.0);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glBegin(GL11.GL_LINES);
			RenderManager r = mc.getRenderManager();
			Vec3d v = new Vec3d(0.0D, 0.0D, 1.0D).rotatePitch(-((float) Math.toRadians((double) mc.thePlayer.rotationPitch))).rotateYaw(-((float) Math.toRadians((double) mc.thePlayer.rotationYaw)));
			GL11.glVertex3d(v.xCoord, mc.thePlayer.getEyeHeight() + v.yCoord, v.zCoord);
			double var3 = e.lastTickPosX + (e.posX - e.lastTickPosX) * (double) par1;
			double var5 = e.lastTickPosY + (e.posY - e.lastTickPosY) * (double) par1;
			double var7 = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * (double) par1;
			GL11.glVertex3d(var3 - r.renderPosX, var5 - r.renderPosY + 0.25, var7 - r.renderPosZ);
			GL11.glEnd();
			GL11.glDepthMask(true);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glPopMatrix();
		}
	}

	private void renderLivingLabel(EntityOtherPlayerMP e, FontRenderer p_189692_0_, String p_189692_1_, float p_189692_2_, float p_189692_3_, float p_189692_4_, float p_189692_6_, float p_189692_7_, boolean p_189692_8_, boolean p_189692_9_) {
		RenderManager r = Client.getMinecraft().getRenderManager();
		GlStateManager.pushMatrix();
		GlStateManager.translate(p_189692_2_, p_189692_3_, p_189692_4_);
		GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
		float var10 = e.getDistanceToEntity(r.renderViewEntity);
		// GlStateManager.rotate(-p_189692_6_, 0.0F, 1.0F, 0.0F);
		// GlStateManager.rotate((float)(p_189692_8_ ? -1 : 1) * p_189692_7_,
		// 1.0F, 0.0F, 0.0F);
		float var13 = var10 / 3F;
		float var14 = 0.02366667F * var13;
		GL11.glRotatef(-r.playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(Minecraft.getMinecraft().gameSettings.thirdPersonView == 2 ? -r.playerViewX : r.playerViewX, 1.0F, 0.0F, 0.0F);
		GL11.glScalef(-var14, -var14, var14);
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		double health = e.getHealth() / 2;
		String c = health > 7 ? "\247a" : health >= 4 ? "\247e" : "\247c";
		p_189692_1_ = p_189692_1_ + " " + c + (int) Math.ceil(health);
		int i = p_189692_0_.getStringWidth(p_189692_1_) / 2;
		GlStateManager.disableTexture2D();
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		vertexbuffer.pos((double) (-i - 2), (double) (-2), 0.0D).color(0.0F, 0.0F, 0.0F, 0.20F).endVertex();
		vertexbuffer.pos((double) (-i - 2), (double) (9), 0.0D).color(0.0F, 0.0F, 0.0F, 0.20F).endVertex();
		vertexbuffer.pos((double) (i + 2), (double) (9), 0.0D).color(0.0F, 0.0F, 0.0F, 0.20F).endVertex();
		vertexbuffer.pos((double) (i + 2), (double) (-2), 0.0D).color(0.0F, 0.0F, 0.0F, 0.20F).endVertex();
		tessellator.draw();
		GlStateManager.enableTexture2D();
		int p = 0;
		for(ItemStack o : e.getEquipmentAndArmor()) {
			if(o != null)  {
				p++;
			}
		}
		p_189692_0_.drawStringWithShadow(p_189692_1_, -p_189692_0_.getStringWidth(p_189692_1_) / 2, 0, p_189692_9_ ? 553648127 : -1);
		GlStateManager.translate(-p*7+((p<=3) ? -2 : 0)+(((p==1))?-3:0), -8, 0);
		GlStateManager.scale(12.0F, -12.0F, 12.0F);
		ItemRenderer x = Client.getMinecraft().getItemRenderer();
		int z = 0;
		for(ItemStack o : e.getEquipmentAndArmor()) {
			if(o != null) {
				GlStateManager.translate(1.0, 0, 0);
				GlStateManager.disableLighting();
				x.renderItemSide(e, o, TransformType.GUI, false);
				z++;
			}
		}
		GlStateManager.enableLighting();
		GlStateManager.depthMask(true);
		GlStateManager.disableBlend();
		GlStateManager.enableDepth();
		GlStateManager.popMatrix();
	}

	private void trace(Minecraft mc, EntityOtherPlayerMP e, float par1, double opacity) {
		/*
		 * if (CubAimbot.get().isFriend(par1Entity.getName())) { opacity = 0.4;
		 * }
		 */
		RenderManager r = mc.getRenderManager();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glLineWidth(1F);
		float hp = e.getHealth();
		Color c = Color.decode("#" + (hp / 2 > 7 ? "55FF55" : hp / 2 >= 4 ? "FFFF55" : "FF5555"));
		GL11.glPushMatrix();
		GL11.glDepthMask(false);
		GL11.glColor4d((double) c.getRed() / 255.0, (double) c.getGreen() / 255.0, (double) c.getBlue() / 255.0,
				opacity);
		double var3 = e.lastTickPosX + (e.posX - e.lastTickPosX) * (double) par1;
		double var5 = e.lastTickPosY + (e.posY - e.lastTickPosY) * (double) par1;
		double var7 = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * (double) par1;
		Vec3d v = new Vec3d(0.0D, 0.0D, 1.0D).rotatePitch(-((float) Math.toRadians((double) mc.thePlayer.rotationPitch))).rotateYaw(-((float) Math.toRadians((double) mc.thePlayer.rotationYaw)));
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex3d(v.xCoord, mc.thePlayer.getEyeHeight() + v.yCoord, v.zCoord);
		GL11.glVertex3d(var3 - r.renderPosX, var5 - r.renderPosY, var7 - r.renderPosZ);
		GL11.glEnd();
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glPopMatrix();
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
}
