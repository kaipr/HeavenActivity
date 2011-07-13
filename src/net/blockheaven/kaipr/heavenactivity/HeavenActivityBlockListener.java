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
        
        final long time = System.currentTimeMillis();
        final String playerName = event.getPlayer().getName();
        
        if (!lastAction.containsKey(playerName) || (time > lastAction.get(playerName) + plugin.config.blockDelay)) {
            final Double points = plugin.getMultiplier(event.getPlayer(), "block_place") * plugin.config.blockPlacePoints;
            
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
        
        final long time = System.currentTimeMillis();
        final String playerName = event.getPlayer().getName();
        
        if (!lastAction.containsKey(playerName) || (time > lastAction.get(playerName) + plugin.config.blockDelay)) {
            Double points = plugin.getMultiplier(event.getPlayer(), "block_break") * plugin.config.blockBreakPoints;
            
            plugin.addActivity(playerName, points);
            
            lastAction.put(playerName, time);
            
            // Tracking
            plugin.blockBreakPointsGiven += points;
        }
        
    }
}
