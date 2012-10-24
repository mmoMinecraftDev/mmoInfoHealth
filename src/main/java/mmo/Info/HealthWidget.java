/*
 * This file is part of mmoInfoHealth <http://github.com/mmoMinecraftDev/mmoInfoFood>.
 *
 * mmoInfoHealth is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package mmo.Info;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.ContainerType;
import org.getspout.spoutapi.gui.GenericContainer;
import org.getspout.spoutapi.gui.GenericGradient;
import org.getspout.spoutapi.gui.GenericTexture;
import org.getspout.spoutapi.gui.Gradient;
import org.getspout.spoutapi.gui.RenderPriority;
import org.getspout.spoutapi.gui.Screen;
import org.getspout.spoutapi.gui.Texture;
import org.getspout.spoutapi.player.SpoutPlayer;

public class HealthWidget extends GenericContainer implements Listener {
			
		private final Gradient slider = new GenericGradient();
		private final Texture bar = new GenericTexture();		
		
		public HealthWidget() {
			super();
			slider.setMargin(1).setPriority(RenderPriority.Normal).setHeight(5).setWidth(20).shiftXPos(1).shiftYPos(1);
			bar.setUrl("bar10.png").setPriority(RenderPriority.Lowest).setHeight(7).setWidth(103).shiftYPos(0);			
			this.setLayout(ContainerType.OVERLAY).setMinWidth(100).setMaxWidth(100);
			this.addChildren(slider, bar);			
			slider.setColor(new Color(0.8039f,0.6784f,0f,1f)).setWidth(100); 
		}
					
		@EventHandler(priority = EventPriority.MONITOR)
		public void onEntityDamage(EntityDamageEvent event) {			
			if(event.isCancelled()) {
				return;
			}
			Entity entity = event.getEntity();
			
			if(!(entity instanceof Player)) {
				return;
			}
			SpoutPlayer sPlayer = (SpoutPlayer) event.getEntity();
			updateHealth(sPlayer);
		}
		
		public void updateHealth(SpoutPlayer sPlayer) {
			System.out.println("Updating Player Health");
			//final int playerHealth = (int) (getScreen().getPlayer().getHealth()*5);
			final int playerHealth = (int) sPlayer.getHealth()*5;
			System.out.println("Health = " + sPlayer.getHealth()*5);
			if (playerHealth<=66 && playerHealth>=33) {				 
				slider.setColor(new Color(0.8039f,0.6784f,0f,1f)).setWidth(playerHealth);  //Orange
				slider.setDirty(true);
				System.out.println("Middle Health Update");
			} else if (playerHealth>66) {			
				slider.setColor(new Color(0,1f,0,1f)).setWidth(playerHealth); //Green
				slider.setDirty(true);
				System.out.println("Above 66 Health Update");
			} else if (playerHealth<33) {
				slider.setColor(new Color(0.69f,0.09f,0.12f,1f)).setWidth(playerHealth);  //Red
				slider.setDirty(true);
				System.out.println("Below 33 Health Update");
			}			
		}
	}