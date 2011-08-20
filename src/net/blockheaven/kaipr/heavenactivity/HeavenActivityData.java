package net.blockheaven.kaipr.heavenactivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

public class HeavenActivityData {

    /**
     * Plugin reference
     */
    protected HeavenActivity plugin;
    
    public HeavenActivityData(HeavenActivity plugin) {
        this.plugin = plugin;
    }
    
    public List<Map<String, Map<ActivitySource, Integer>>> playersActivities = 
        new ArrayList<Map<String, Map<ActivitySource, Integer>>>();
    
    public void initNewSequence() {
        synchronized(playersActivities) {
            if (playersActivities.size() == plugin.config.maxSequences) {
                // TODO: Collect stats
                // Map<String, Map<ActivitySource, Integer>> oldSequence = playersActivities.remove(0);
                playersActivities.remove(0);
            }
            playersActivities.add(new HashMap<String, Map<ActivitySource, Integer>>());
        }
    }
    
    public void addActivity(String playerName, ActivitySource source) {
        addActivity(playerName, source, 1);
    }
    
    /**
     * Adds given amount of activity to the given playerName
     * 
     * @param playerName
     * @param source
     * @param activity
     */
    public void addActivity(String playerName, ActivitySource source, Integer count) {
        
        playerName = playerName.toLowerCase();

        if (getCurrentSequence().containsKey(playerName)) {
            count += getCurrentSequence().get(playerName).get(source);
        } else {
            getCurrentSequence().put(playerName, new HashMap<ActivitySource, Integer>());
        }
        
        getCurrentSequence().get(playerName).put(source, count);
        
    }
    
    /**
     * Calculates and returns activity of given Player
     * 
     * @param player
     * @return
     */
    public int getActivity(Player player) {
        return getActivity(player.getName());
    }
    
    /**
     * Calculates and returns activity of given playerName
     * 
     * @param playerName
     * @return
     */
    public int getActivity(String playerName) {
        return getActivity(playerName, plugin.config.defaultSequences);
    }
    
    /**
     * Calculates and returns activity of given playerName for the last given sequences
     * 
     * @param playerName
     * @param sequences
     * @return
     */
    public int getActivity(String playerName, int sequences) {
        
        playerName = playerName.toLowerCase();
        
        int startSequence = playersActivities.size() - sequences;
        if (startSequence < 0) startSequence = 0;
        
        final Iterator<Map<String, Map<ActivitySource, Integer>>> sequenceIterator = playersActivities.listIterator(startSequence);
        
        Double activityPoints = 0.0;
        
        while (sequenceIterator.hasNext()) {
            final Map<ActivitySource, Integer> playerSequence = sequenceIterator.next().get(playerName);
            final Iterator<ActivitySource> sourceIterator = playerSequence.keySet().iterator();
            
            while (sourceIterator.hasNext()) {
                final ActivitySource source = sourceIterator.next();
                activityPoints += playerSequence.get(source) * plugin.config.pointsFor(source) * plugin.getMultiplier(playerName, source);
            }
        }
        
        final int activity = (int)(activityPoints * plugin.config.pointMultiplier / sequences);
        
        return (activity > 100) ? 100 : activity;
    
    }
    
    private Map<String, Map<ActivitySource, Integer>> getCurrentSequence() {
        return playersActivities.get(playersActivities.size() - 1);
    }

}
