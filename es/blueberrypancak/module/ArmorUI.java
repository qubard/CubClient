package es.blueberrypancak.module;

import org.apache.commons.lang3.ArrayUtils;

import es.blueberrypancak.Client;
import es.blueberrypancak.event.EventRender;
import es.blueberrypancak.event.Subscribe;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

@RegisterModule
public class ArmorUI extends Module {

	@Subscribe
	public void onRender(EventRender e) {
		EntityPlayer player = Client.getMinecraft().thePlayer;
		ItemStack[] equipped = player.inventory.armorInventory;
		RenderItem r = Client.getMinecraft().getRenderItem();
		ScaledResolution res = Client.res();
		int barWidth = 60;
		int k = 0;
		if (player.isCreative()) return;
		for (int i = 0; i < equipped.length; i++) {
			ItemStack o = equipped[i];
			if (o != null) {
				float dura = (float) (o.getMaxDamage() - o.getItemDamage()) / o.getMaxDamage();
				int j = (int) Math.round(dura * barWidth);
				int z = (int) Math.round(255.0D - (double) o.getItemDamage() * 255.0D / (double) o.getMaxDamage());
				GlStateManager.disableDepth();
				GlStateManager.disableTexture2D();
				GlStateManager.disableAlpha();
				GlStateManager.disableBlend();
				Tessellator tessellator = Tessellator.getInstance();
				VertexBuffer vertexbuffer = tessellator.getBuffer();
				int dX = res.getScaledWidth() / 2 + 30;
				int dY = res.getScaledHeight() - 43 - k * 2 - (player.isInsideOfMaterial(Material.WATER) ? 10 : 0);
				draw(vertexbuffer, dX, dY, barWidth, 2, 0, 0, 0, 255);
				draw(vertexbuffer, dX, dY, barWidth, 1, (255 - z) / 4, 69, 0, 255);
				draw(vertexbuffer, barWidth - j + dX, dY, j, 1, 255 - z, z, 0, 255);
				GlStateManager.enableBlend();
				GlStateManager.enableAlpha();
				GlStateManager.enableTexture2D();
				GlStateManager.enableDepth();
				k++;
			}
		}
	}

	private void draw(VertexBuffer renderer, int x, int y, int width, int height, int red, int green, int blue, int alpha) {
		renderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		renderer.pos((double) (x + 0), (double) (y + 0), 0.0D).color(red, green, blue, alpha).endVertex();
		renderer.pos((double) (x + 0), (double) (y + height), 0.0D).color(red, green, blue, alpha).endVertex();
		renderer.pos((double) (x + width), (double) (y + height), 0.0D).color(red, green, blue, alpha).endVertex();
		renderer.pos((double) (x + width), (double) (y + 0), 0.0D).color(red, green, blue, alpha).endVertex();
		Tessellator.getInstance().draw();
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
