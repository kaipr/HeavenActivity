package net.blockheaven.kaipr.heavenactivity;

import net.blockheaven.kaipr.heavenactivity.register.payment.Methods;

import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;

import com.nijikokun.bukkit.Permissions.Permissions;

public class HeavenActivityServerListener extends ServerListener {
    
    /**
     * Plugin reference
     */
    private HeavenActivity plugin;
    
    /**
     * Register handler for economy plugins
     */
    private Methods ecoMethods;

    public HeavenActivityServerListener(HeavenActivity plugin) {
        this.plugin = plugin;
        this.ecoMethods = new Methods();
    }


    @Override
    public void onPluginEnable(PluginEnableEvent event) {
        
        if (HeavenActivity.Permissions == null) {
            Plugin permissions = plugin.getServer().getPluginManager().getPlugin("Permissions");
            
            if (permissions != null) {
                if (permissions.isEnabled()) {
                    HeavenActivity.Permissions = ((Permissions)permissions).getHandler();
                    HeavenActivity.logger.info("[HeavenActivity] hooked into Permissions");
                }
            }
        }
        
        if (!this.ecoMethods.hasMethod()) {
            if(this.ecoMethods.setMethod(event.getPlugin())) {
                // You might want to make this a public variable inside your MAIN class public Method Method = null;
                // then reference it through this.plugin.Method so that way you can use it in the rest of your plugin ;)
                HeavenActivity.ecoMethod = this.ecoMethods.getMethod();
                HeavenActivity.logger.info("[HeavenActivity] Payment method found (" + HeavenActivity.ecoMethod.getName() + " version: " + HeavenActivity.ecoMethod.getVersion() + ")");
            }
        }
        
    }

    @Override
    public void onPluginDisable(PluginDisableEvent event) {
        
        if (HeavenActivity.Permissions != null) {
            if (event.getPlugin().getDescription().getName().equals("Permissions")) {
                HeavenActivity.Permissions = null;
                HeavenActivity.logger.info("[HeavenActivity] un-hooked from Permissions.");
            }
        }
        
        if (this.ecoMethods != null && this.ecoMethods.hasMethod()) {
            Boolean check = this.ecoMethods.checkDisabled(event.getPlugin());

            if(check) {
                HeavenActivity.ecoMethod = null;
                HeavenActivity.logger.info("[HeavenActivity] Payment method was disabled. No longer accepting payments.");
            }
        }
        
    }
    
}
