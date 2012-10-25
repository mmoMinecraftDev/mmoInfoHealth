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

import java.util.HashMap;
import java.util.Map;

import mmo.Core.InfoAPI.MMOInfoEvent;
import mmo.Core.MMOPlugin;
import mmo.Core.MMOPlugin.Support;
import mmo.Core.util.EnumBitSet;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.PluginManager;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.ContainerType;
import org.getspout.spoutapi.gui.GenericContainer;
import org.getspout.spoutapi.gui.GenericGradient;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericTexture;
import org.getspout.spoutapi.gui.Gradient;
import org.getspout.spoutapi.gui.Label;
import org.getspout.spoutapi.gui.RenderPriority;
import org.getspout.spoutapi.gui.Screen;
import org.getspout.spoutapi.gui.Texture;
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.player.SpoutPlayer;

public class mmoInfoHealth extends MMOPlugin implements Listener {
	private static final Map<Player, Widget> healthbar = new HashMap<Player, Widget>();
	private static String config_displayas = "bar";
	private boolean forceUpdate = true;					 
	private static final Color greenBar = new Color(0,1f,0,1f);  
	private static final Color orangeBar = new Color(0.8039f,0.6784f,0f,1f); 
	private static final Color redBar = new Color(0.69f,0.09f,0.12f,1f); 
	
	public EnumBitSet mmoSupport(EnumBitSet support)
	{		
		support.set(MMOPlugin.Support.MMO_AUTO_EXTRACT);
		return support;
	}

	public void onEnable() {
		super.onEnable();
		this.pm.registerEvents(this, this);
	}	

	@Override
	public void loadConfiguration(final FileConfiguration cfg) {
		config_displayas = cfg.getString("displayas", config_displayas);		
	}

	@EventHandler
	public void onMMOInfo(MMOInfoEvent event)
	{
		if (event.isToken("health")) {
			SpoutPlayer player = event.getPlayer();
			if (player.hasPermission("mmo.info.health")) {
				if (config_displayas.equalsIgnoreCase("bar")) {				
					final CustomWidget widget = new CustomWidget();
					healthbar.put(player, widget);
					event.setWidget(plugin, widget);
					forceUpdate = true;
				} else { 
					final CustomLabel label = (CustomLabel)new CustomLabel().setResize(true).setFixed(true);
					label.setText("20/20");
					forceUpdate = true;
					healthbar.put(player, label);				
					event.setWidget(this.plugin, label);
				}				
				event.setIcon("health.png");				
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamage(EntityDamageEvent event) {			
		if(!event.isCancelled() && event.getEntity() instanceof Player) {
			forceUpdate = true;
		}				
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityRegainHealth(EntityRegainHealthEvent event) {			
		if(!event.isCancelled() && event.getEntity() instanceof Player) {
			forceUpdate = true;
		}				
	}
	
	public class CustomLabel extends GenericLabel {
		private transient int tick = 0;

		@Override
		public void onTick()
		{		
			if (forceUpdate) {
				setText(String.format(getScreen().getPlayer().getHealth() + "/" + getScreen().getPlayer().getMaxHealth()));
				forceUpdate = false;
			}
		}
	}

	public class CustomWidget extends GenericContainer {

		private final Gradient slider = new GenericGradient();
		private final Texture bar = new GenericTexture();

		public CustomWidget() {
			super();
			slider.setMargin(1).setPriority(RenderPriority.Normal).setHeight(5).setWidth(100).shiftXPos(1).shiftYPos(1);
			bar.setUrl("bar10.png").setPriority(RenderPriority.Lowest).setHeight(7).setWidth(103).shiftYPos(0);			
			this.setLayout(ContainerType.OVERLAY).setMinWidth(103).setMaxWidth(103).setWidth(103);			
			this.addChildren(slider, bar);
		}

		@Override
		public void onTick() {			
			if (forceUpdate) {
				final int playerHealth = Math.max(0, Math.min( 100, (int) (getScreen().getPlayer().getHealth()*5)));				
				if (playerHealth>66) {				 
					slider.setColor(greenBar);
				} else if (playerHealth>=33) {			
					slider.setColor(orangeBar);				
				} else {
					slider.setColor(redBar);  		
				}
				slider.setWidth(playerHealth);
				forceUpdate = false;
			}
		}	
	}
}