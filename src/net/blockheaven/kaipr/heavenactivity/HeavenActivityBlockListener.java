package net.blockheaven.kaipr.heavenactivity;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

public class HeavenActivityBlockListener extends BlockListener {
    
	protected HeavenActivity plugin;
	
	protected Map<String, Long> lastAction = new HashMap<String, Long>();
	
    /**
     * Construct the listener.
     * 
     * @param plugin
     */
    public HeavenActivityBlockListener(HeavenActivity plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
    	
    	if (event.isCancelled())
    		return;
    	
    	long time = System.currentTimeMillis();
        String playerName = event.getPlayer().getName();
        
        if (!lastAction.containsKey(playerName) || (time > lastAction.get(playerName) + plugin.config.blockDelay)) {
        	Double multiplier = HeavenActivity.Permissions.getPermissionDouble(
        			event.getPlayer().getWorld().getName(), playerName, "activity.multiplier.block_place");
        	if (multiplier == -1.0) multiplier = 1.0;
        	Double points = multiplier * plugin.config.blockPlacePoints;
        	
        	plugin.addActivity(playerName, points);
        	
        	lastAction.put(playerName, time);
        	
        	// Tracking
        	plugin.blockPlacePointsGiven += points;
        }
        
    }
    
    @Override
    public void onBlockBreak(BlockBreakEvent event) {
    	
    	if (event.isCancelled())
    		return;
    	
    	long time = System.currentTimeMillis();
        String playerName = event.getPlayer().getName();
        
        if (!lastAction.containsKey(playerName) || (time > lastAction.get(playerName) + plugin.config.blockDelay)) {
        	Double multiplier = HeavenActivity.Permissions.getPermissionDouble(
        			event.getPlayer().getWorld().getName(), playerName, "activity.multiplier.block_break");
        	if (multiplier == -1.0) multiplier = 1.0;
        	Double points = multiplier * plugin.config.blockBreakPoints;
        	
        	plugin.addActivity(playerName, points);
        	
        	lastAction.put(playerName, time);
        	
        	// Tracking
        	plugin.blockBreakPointsGiven += points;
        }
        
    }
}
