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
        
        long time = System.currentTimeMillis();
        String playerName = event.getPlayer().getName();
        
        if (!lastAction.containsKey(playerName) || (time > lastAction.get(playerName) + plugin.config.moveDelay)) {
        	Double multiplier = HeavenActivity.Permissions.getPermissionDouble(
        			event.getPlayer().getWorld().getName(), playerName, "activity.multiplier.move");
        	if (multiplier == -1.0) multiplier = 1.0;
        	Double points = multiplier * plugin.config.movePoints;
        	
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
    	
    	String playerName = event.getPlayer().getName();
    	
    	Double chatCharPoints = event.getMessage().length() * plugin.config.chatCharPoints;
        Double chatCharMultiplier = HeavenActivity.Permissions.getPermissionDouble(
    			event.getPlayer().getWorld().getName(), playerName, "activity.multiplier.chat_char");
        if (chatCharMultiplier == -1.0) chatCharMultiplier = 1.0;
        chatCharPoints = chatCharMultiplier * chatCharPoints;
    	
    	Double final_points = plugin.config.chatPoints + chatCharPoints;
        
    	plugin.addActivity(playerName, final_points);
    	
    	// Tracking
    	plugin.chatCharPointsGiven += chatCharPoints;
    	plugin.chatPointsGiven += plugin.config.chatPoints;
    	
    }
    
    /**
     * Called when a player attempts to use a command
     *
     * @param event Relevant event details
     */
    @Override
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
    	
    	String playerName = event.getPlayer().getName();
    	
    	Double commandCharPoints = event.getMessage().length() * plugin.config.commandCharPoints;
    	Double commandCharMultiplier = HeavenActivity.Permissions.getPermissionDouble(
    			event.getPlayer().getWorld().getName(), playerName, "activity.multiplier.command_char");
    	if (commandCharMultiplier == -1.0) commandCharMultiplier = 1.0;
    	commandCharPoints = commandCharMultiplier * commandCharPoints;
    	
    	Double final_points = plugin.config.commandPoints + commandCharPoints;
    	plugin.addActivity(playerName, final_points);
    	
    	//HeavenActivity.logger.info("[cmd] " + event.getPlayer().getName() + ": " + event.getMessage());
    	
    	// Tracking
    	plugin.commandCharPointsGiven += commandCharPoints;
    	plugin.commandPointsGiven += plugin.config.commandPoints;
    	
    }
    
}
