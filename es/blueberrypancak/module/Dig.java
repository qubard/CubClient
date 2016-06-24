package es.blueberrypancak.module;

import es.blueberrypancak.Client;
import es.blueberrypancak.event.EventBlockBreak;
import es.blueberrypancak.event.EventManager;
import es.blueberrypancak.event.EventRender;
import es.blueberrypancak.event.Subscribe;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

@RegisterModule(key=39,color=16760629,listed=true)
public class Dig extends Module {

	private int digCount, n;

	private long delay = 0;

	public Dig() {
		digCount = 0;
		n = 0;
	}

	@Subscribe
	public void onBlockBreak(EventBlockBreak e) {
		digCount++;
	}

	@Subscribe
	public void onRender(EventRender e) {
		Minecraft mc = Client.getMinecraft();
		if (isEnabled() && mc.thePlayer != null) {
			if (digCount != 0) {
				double frequency = .2;
				int red = (int) (Math.sin(frequency * n / 5) * 127 + 128);
				int green = (int) (Math.sin(frequency * n / 5 + 2 * Math.PI / 3) * 127 + 128);
				int blue = (int) (Math.sin(frequency * n / 5 + 4 * Math.PI / 3) * 127 + 128);
				int col = (red << 16) | (green << 8) | blue;
				n = (n + 1) % 160;
				mc.fontRendererObj.drawStringWithShadow("+" + digCount, 30, Client.res().getScaledHeight() - 10, col);
			}
			if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK && System.currentTimeMillis() >= delay) {
				BlockPos blockpos = mc.objectMouseOver.getBlockPos();

				if (mc.theWorld.getBlockState(blockpos).getMaterial() != Material.AIR
						&& mc.playerController.onPlayerDamageBlock(blockpos, mc.objectMouseOver.sideHit)) {
					delay = System.currentTimeMillis() + 50;
					mc.effectRenderer.addBlockHitEffects(blockpos, mc.objectMouseOver.sideHit);
					mc.thePlayer.swingArm(EnumHand.MAIN_HAND);
				}
			}
		}
	}

	@Override
	public void onEnabled() {
		digCount = 0;
	}

	@Override
	public void onDisabled() {
		digCount = 0;
		Client.getMinecraft().playerController.resetBlockRemoving();
	}

	@Override
	public String getName() {
		return "Dig";
	}
}
