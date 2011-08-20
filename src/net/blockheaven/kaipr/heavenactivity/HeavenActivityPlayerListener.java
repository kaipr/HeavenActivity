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
        if (event.getPlayer().isInsideVehicle() 
                || (event.getTo().getX() == event.getFrom().getX() 
                || event.getTo().getZ() == event.getFrom().getZ()))
            return;
        
        final long time = System.currentTimeMillis();
        final String playerName = event.getPlayer().getName();
        
        if (!lastAction.containsKey(playerName) || (time > lastAction.get(playerName) + plugin.config.moveDelay)) {
            plugin.data.addActivity(playerName, ActivitySource.MOVE);
            
            lastAction.put(playerName, time);
        }
        
    }
    
    @Override
    public void onPlayerChat(PlayerChatEvent event) {
        
        if (event.isCancelled() && !plugin.config.chatTrackCancelled)
            return;
        
        final String playerName = event.getPlayer().getName();
        plugin.data.addActivity(playerName, ActivitySource.CHAT);
        plugin.data.addActivity(playerName, ActivitySource.CHAT_CHAR, event.getMessage().length());
        
    }
    
    /**
     * Called when a player attempts to use a command
     *
     * @param event Relevant event details
     */
    @Override
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        
        if (event.isCancelled() && !plugin.config.commandTrackCancelled)
            return;
        
        final String playerName = event.getPlayer().getName();
        
        if (plugin.config.commandTracking) {
            plugin.data.addActivity(playerName, ActivitySource.COMMAND);
            plugin.data.addActivity(playerName, ActivitySource.COMMAND_CHAR, event.getMessage().length());
        }
        
        if (plugin.config.logCommands) {
            HeavenActivity.logger.info("[cmd] " + playerName + ": " + event.getMessage());
        }
        
    }
    
}
