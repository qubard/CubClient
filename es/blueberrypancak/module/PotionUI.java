package es.blueberrypancak.module;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import es.blueberrypancak.Client;
import es.blueberrypancak.event.EventRender;
import es.blueberrypancak.event.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

@RegisterModule
public class PotionUI extends Module {

	@Subscribe
	public void onRender(EventRender e) {
		ScaledResolution res = Client.res();
		Minecraft mc = Client.getMinecraft();
		if(mc.inGameHasFocus) {
			int i = 0;
			Collection<PotionEffect> collection = mc.thePlayer.getActivePotionEffects();
			List<PotionEffect> sorted = Lists.reverse(Ordering.natural().sortedCopy(collection));
			for (PotionEffect potioneffect : sorted) {
				Potion potion = potioneffect.getPotion();
	            ++i;
	            String s1 = I18n.format(potion.getName(), new Object[0]);
	            if (potioneffect.getAmplifier() == 1) {
	                s1 = s1 + " " + I18n.format("enchantment.level.2", new Object[0]);
	            }
	            else if (potioneffect.getAmplifier() == 2) {
	                s1 = s1 + " " + I18n.format("enchantment.level.3", new Object[0]);
	            }
	            else if (potioneffect.getAmplifier() == 3) {
	                s1 = s1 + " " + I18n.format("enchantment.level.4", new Object[0]);
	            }
	            s1 = s1 + " \u00a7f" + Potion.getPotionDurationString(potioneffect, 1.0F);
	            mc.fontRendererObj.drawStringWithShadow(s1, res.getScaledWidth() - mc.fontRendererObj.getStringWidth(s1) - 4, res.getScaledHeight() - 8 * i - 4, potion.getLiquidColor());
	        }
		}
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
