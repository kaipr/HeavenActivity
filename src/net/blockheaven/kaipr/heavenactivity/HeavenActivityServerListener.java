package net.blockheaven.kaipr.heavenactivity;

import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;

import com.iCo6.*;

public class HeavenActivityServerListener extends ServerListener {
	
	private HeavenActivity plugin;

    public HeavenActivityServerListener(HeavenActivity plugin) {
        this.plugin = plugin;
    }


    @Override
    public void onPluginEnable(PluginEnableEvent event) {
    	if (HeavenActivity.iConomy == null && plugin.config.incomeEnabled) {
            Plugin iConomy = plugin.getServer().getPluginManager().getPlugin("iConomy");

            if (iConomy != null) {
                if (iConomy.isEnabled()) {
                    if (!iConomy.getDescription().getVersion().startsWith("6")) {
                    	HeavenActivity.logger.warning(
                    			"[HeavenActivity] This version needs iConomy 6 to work! If you get errors, upgrade iConomy or disable income!");
                    }
                    HeavenActivity.iConomy = (iConomy)iConomy;
                    HeavenActivity.logger.info("[HeavenActivity] hooked into iConomy.");
                }
            }
        }
    }

    @Override
    public void onPluginDisable(PluginDisableEvent event) {
    	if (HeavenActivity.iConomy != null) {
            if (event.getPlugin().getDescription().getName().equals("iConomy")) {
                HeavenActivity.iConomy = null;
                HeavenActivity.logger.info("[HeavenActivity] un-hooked from iConomy.");
            }
        }
    }
}
