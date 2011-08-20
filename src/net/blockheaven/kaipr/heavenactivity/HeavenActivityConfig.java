package net.blockheaven.kaipr.heavenactivity;

import java.util.List;

import org.bukkit.util.config.Configuration;

public class HeavenActivityConfig {

    /**
     * Plugin reference
     */
    protected HeavenActivity plugin;
    
    /**
     * Configuration object
     */
    protected Configuration config;
    
    /**
     * Configuration values
     */
    public int maxSequences;
    public int defaultSequences;
    public int sequenceInterval;
    public int notificationSequence;
    public int incomeSequence;
    public Double pointMultiplier;
    public boolean chatTracking;
    public boolean chatTrackCancelled;
    public Double chatPoints;
    public Double chatCharPoints;
    public boolean commandTracking;
    public boolean commandTrackCancelled;
    public Double commandPoints;
    public Double commandCharPoints;
    public boolean moveTracking;
    public Integer moveDelay;
    public Double movePoints;
    public boolean blockTracking;
    public Integer blockDelay;
    public Double blockPlacePoints;
    public Double blockBreakPoints;
    public boolean incomeEnabled;
    public int incomeMinActivity;
    public boolean incomeAllowNegative;
    public Double incomeBaseValue;
    public int incomeTargetActivity;
    public int incomeActivityModifier;
    public Double incomeBalanceMultiplier;
    public boolean logCommands;
    
    
    public HeavenActivityConfig(HeavenActivity plugin) {
        this.plugin = plugin;
        
        config = plugin.getConfiguration();
        
        load();
    }
    
    public void load() {
        config.load();
        
        maxSequences                  = config.getInt("general.max_sequences", 15);
        defaultSequences              = config.getInt("general.default_sequences", maxSequences);
        sequenceInterval              = config.getInt("general.sequence_interval", 60);
        notificationSequence          = config.getInt("general.notification_sequence", 6);
        incomeSequence                = config.getInt("general.income_sequence", 15);
        pointMultiplier               = config.getDouble("general.point_multiplier", 1.0);
        
        incomeEnabled                 = config.getBoolean("income.enabled", true);
        incomeMinActivity             = config.getInt("income.min_activity", 1);
        incomeAllowNegative           = config.getBoolean("income.allow_negative", true);
        incomeBaseValue               = config.getDouble("income.base_value", 8);
        incomeTargetActivity          = config.getInt("income.target_activity", 50);
        incomeActivityModifier        = config.getInt("income.activity_modifier", 75);
        incomeBalanceMultiplier       = config.getDouble("income.balance_multiplier", 0.0);
        
        chatTracking                  = config.getBoolean("chat.tracking", true);
        chatTrackCancelled            = config.getBoolean("chat.track_cancelled", true);
        chatPoints                    = config.getDouble("chat.points", 1.0);
        chatCharPoints                = config.getDouble("chat.char_points", 0.49);
        commandTracking               = config.getBoolean("command.tracking", true);
        commandTrackCancelled         = config.getBoolean("command.track_cancelled", true);
        commandPoints                 = config.getDouble("command.points", 1.0);
        commandCharPoints             = config.getDouble("command.char_points", 0.53);
        moveTracking                  = config.getBoolean("move.tracking", true);
        moveDelay                     = config.getInt("move.delay", 1100);
        movePoints                    = config.getDouble("move.points", 0.58);
        blockTracking                 = config.getBoolean("block.tracking", true);
        blockDelay                    = config.getInt("block.delay", 950);
        blockPlacePoints              = config.getDouble("block.place_points", 3.75);
        blockBreakPoints              = config.getDouble("block.break_points", 1.95);
        
        logCommands                   = config.getBoolean("general.log_commands", false);
    }
    
    public void reloadAndSave() {
        config.load();
        
        List<String> configNodes;
        
        configNodes = config.getKeys("general");
        if (!configNodes.contains("max_sequences"))
            config.setProperty("general.max_sequences", maxSequences);
        if (!configNodes.contains("sequence_interval"))
            config.setProperty("general.sequence_interval", sequenceInterval);
        if (!configNodes.contains("notification_sequence"))
            config.setProperty("general.notification_sequence", notificationSequence);
        if (!configNodes.contains("income_sequence"))
            config.setProperty("general.income_sequence", incomeSequence);
        if (!configNodes.contains("point_multiplier"))
            config.setProperty("general.point_multiplier", pointMultiplier);
        
        configNodes = config.getKeys("income");
        if (!configNodes.contains("enabled"))
            config.setProperty("income.enabled", incomeEnabled);
        if (!configNodes.contains("base_value"))
            config.setProperty("income.base_value", incomeBaseValue);
        if (!configNodes.contains("target_activity"))
            config.setProperty("income.target_activity", incomeTargetActivity);
        if (!configNodes.contains("activity_modifier"))
            config.setProperty("income.activity_modifier", incomeActivityModifier);
        if (!configNodes.contains("balance_multiplier"))
            config.setProperty("income.balance_multiplier", incomeBalanceMultiplier);
        
        config.save();
    }
    
    public Double pointsFor(ActivitySource source) {
        switch(source) {
        case MOVE:
            return movePoints;
        case BLOCK_BREAK:
            return blockBreakPoints;
        case BLOCK_PLACE:
            return blockPlacePoints;
        case CHAT:
            return chatPoints;
        case CHAT_CHAR:
            return chatCharPoints;
        case COMMAND:
            return commandPoints;
        case COMMAND_CHAR:
            return commandCharPoints;
        }
        
        return null;
    }
    
}
