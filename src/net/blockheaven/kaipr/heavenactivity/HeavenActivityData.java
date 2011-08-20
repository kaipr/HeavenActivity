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
    
    public List<Map<String, Map<ActivitySource, Double>>> playersActivities = 
        new ArrayList<Map<String, Map<ActivitySource, Double>>>();
    
    public void initNewSequence() {
        synchronized(playersActivities) {
            if (playersActivities.size() == plugin.config.maxSequences) {
                Map<String, Map<ActivitySource, Double>> oldSequence = playersActivities.remove(0);
                // TODO: Collect stats
            }
            playersActivities.add(new HashMap<String, Map<ActivitySource, Double>>());
        }
    }
    
    /**
     * Adds given amount of activity to the given playerName
     * 
     * @param playerName
     * @param source
     * @param activity
     */
    public void addActivity(String playerName, ActivitySource source, Double activity) {
        
        activity = plugin.config.pointMultiplier * plugin.getMultiplier(playerName, source) * activity;
        playerName = playerName.toLowerCase();

        if (getCurrentSequence().containsKey(playerName)) {
            activity += getCurrentSequence().get(playerName).get(source);
        } else {
            getCurrentSequence().put(playerName, new HashMap<ActivitySource, Double>());
        }
        
        getCurrentSequence().get(playerName).put(source, activity);
        
    }
    
    /**
     * Calculates and returns activity of a given Player
     * 
     * @param player
     * @return
     */
    public int getActivity(Player player) {
        return getActivity(player.getName());
    }
    
    public int getActivity(String playerName) {
        return getActivity(playerName, playersActivities.size());
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
        Iterator<Map<String, Map<ActivitySource, Double>>> sequenceIterator = playersActivities.listIterator(startSequence);
        
        Double activityPoints = 0.0;
        while (sequenceIterator.hasNext()) {
            Iterator<Double> sourceIterator = sequenceIterator.next().get(playerName).values().iterator();
            while (sourceIterator.hasNext()) {
                activityPoints += sourceIterator.next();
            }
        }
        
        final int activity = (int)(activityPoints / sequences);
        
        return (activity > 100) ? 100 : activity;
    
    }
    
    private Map<String, Map<ActivitySource, Double>> getCurrentSequence() {
        return playersActivities.get(playersActivities.size() - 1);
    }

}
