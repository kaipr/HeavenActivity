package net.blockheaven.kaipr.heavenactivity;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;

public class HeavenActivityPlayerListener extends PlayerListener {

	protected HeavenActivity plugin;
	
	protected Map<String, Long> lastAction = new HashMap<String, Long>();
    
    /**
     * Construct the listener.
     * 
     * @param plugin
     */
    public HeavenActivityPlayerListener(HeavenActivity plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void onPlayerMove(PlayerMoveEvent event) {
    	
    	if (event.isCancelled())
    		return;
    	
    	// Ignore jumping and driving
        if (event.getTo().getY() > event.getFrom().getY() 
        		|| event.getTo().getY() < event.getFrom().getY()
        		|| event.getPlayer().isInsideVehicle())
            return;
        
        final long time = System.currentTimeMillis();
        final String playerName = event.getPlayer().getName();
        
        if (!lastAction.containsKey(playerName) || (time > lastAction.get(playerName) + plugin.config.moveDelay)) {
        	final Double points = plugin.getMultiplier(event.getPlayer(), "move") * plugin.config.movePoints;
        	
        	plugin.addActivity(playerName, points);
        	
        	lastAction.put(playerName, time);
        	
        	// Tracking
        	plugin.movePointsGiven += points;
        }
        
    }
    
    @Override
    public void onPlayerChat(PlayerChatEvent event) {
    	
    	if (event.isCancelled())
    		return;
    	
    	final String playerName = event.getPlayer().getName();
    	
    	final Double points = plugin.getMultiplier(event.getPlayer(), "chat_char") 
    	    * event.getMessage().length() * plugin.config.chatCharPoints;
        
    	plugin.addActivity(playerName, points + plugin.config.chatPoints);
    	
    	// Tracking
    	plugin.chatCharPointsGiven += points;
    	plugin.chatPointsGiven += plugin.config.chatPoints;
    	
    }
    
    /**
     * Called when a player attempts to use a command
     *
     * @param event Relevant event details
     */
    @Override
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
    	
    	final String playerName = event.getPlayer().getName();
    	
    	final Double points = plugin.getMultiplier(event.getPlayer(), "command_char") 
    	    * event.getMessage().length() * plugin.config.commandCharPoints;

    	plugin.addActivity(playerName, points + plugin.config.commandPoints);
    	
    	if (plugin.config.logCommands) {
    	    HeavenActivity.logger.info("[cmd] " + event.getPlayer().getName() + ": " + event.getMessage());
    	}
    	
    	// Tracking
    	plugin.commandCharPointsGiven += points;
    	plugin.commandPointsGiven += plugin.config.commandPoints;
    	
    }
    
}
