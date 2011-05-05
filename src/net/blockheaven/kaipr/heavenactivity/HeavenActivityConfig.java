package net.blockheaven.kaipr.heavenactivity;

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
	public int sequenceInterval;
	public int notificationSequence;
	public int incomeSequence;
    public Double pointMultiplier;
    public Double chatPoints;
    public Double chatCharPoints;
    public Double commandPoints;
    public Double commandCharPoints;
    public Integer moveDelay;
    public Double movePoints;
    public Integer blockDelay;
    public Double blockPlacePoints;
    public Double blockBreakPoints;
    public boolean incomeEnabled;
    public Double incomeBaseValue;
    public int incomeTargetActivity;
    public int incomeActivityModifier;
    public Double incomeBalanceMultiplier;
	
    
	public HeavenActivityConfig(HeavenActivity plugin) {
		this.plugin = plugin;
		
        plugin.getDataFolder().mkdirs();
        config = plugin.getConfiguration();
        
        load();
	}
	
    public void load() {
        config.load();
        
        maxSequences                  = config.getInt("general.max_sequences", 15);
        sequenceInterval              = config.getInt("general.sequence_interval", 60);
        notificationSequence          = config.getInt("general.notification_sequence", 6);
        incomeSequence                = config.getInt("general.income_sequence", 15);
        pointMultiplier               = config.getDouble("general.point_multiplier", 1.0);
        
        incomeEnabled                 = config.getBoolean("income.enabled", false);
        incomeBaseValue               = config.getDouble("income.base_value", 8);
        incomeTargetActivity          = config.getInt("income.target_activity", 50);
        incomeActivityModifier        = config.getInt("income.activity_modifier", 75);
        incomeBalanceMultiplier       = config.getDouble("income.balance_multiplier", -0.00025);
        
        chatPoints                    = config.getDouble("chat.points", 1.0);
        chatCharPoints                = config.getDouble("chat.char_points", 0.49);
        commandPoints                 = config.getDouble("command.points", 1.0);
        commandCharPoints             = config.getDouble("command.char_points", 0.53);
        moveDelay                     = config.getInt("move.delay", 1100);
        movePoints                    = config.getDouble("move.points", 0.58);
        blockDelay                    = config.getInt("block.delay", 950);
        blockPlacePoints              = config.getDouble("block.place_points", 3.75);
        blockBreakPoints              = config.getDouble("block.break_points", 1.95);
        
        plugin.chatPointsGiven        = config.getDouble("stats.chat_points", 0.0);
        plugin.chatCharPointsGiven    = config.getDouble("stats.chat_char_points", 0.0);
        plugin.commandPointsGiven     = config.getDouble("stats.command_points", 0.0);
        plugin.commandCharPointsGiven = config.getDouble("stats.command_char_points", 0.0);
        plugin.movePointsGiven        = config.getDouble("stats.move_points", 0.0);
        plugin.blockPlacePointsGiven  = config.getDouble("stats.block_place_points", 0.0);
        plugin.blockBreakPointsGiven  = config.getDouble("stats.block_break_points", 0.0);
    }
	
	public void reloadAndSave() {
		config.load();
		
		config.setProperty("general.max_sequences", maxSequences);
		config.setProperty("general.sequence_interval", sequenceInterval);
		config.setProperty("general.notification_sequence", notificationSequence);
		config.setProperty("general.income_sequence", incomeSequence);
		config.setProperty("general.point_multiplier", pointMultiplier);
		
		config.setProperty("income.enabled", incomeEnabled);
		config.setProperty("income.base_value", incomeBaseValue);
		config.setProperty("income.target_activity", incomeTargetActivity);
		config.setProperty("income.activity_modifier", incomeActivityModifier);
		config.setProperty("income.balance_multiplier", incomeBalanceMultiplier);
		
		config.setProperty("chat.points", chatPoints);
		config.setProperty("chat.char_points", chatCharPoints);
		config.setProperty("command.points", commandPoints);
		config.setProperty("command.char_points", commandCharPoints);
		config.setProperty("move.delay", moveDelay);
		config.setProperty("move.points", movePoints);
		config.setProperty("block.delay", blockDelay);
		config.setProperty("block.place_points", blockPlacePoints);
		config.setProperty("block.break_points", blockBreakPoints);
		
		config.setProperty("stats.chat_points", plugin.chatPointsGiven);
    	config.setProperty("stats.chat_char_points", plugin.chatCharPointsGiven);
    	config.setProperty("stats.command_points", plugin.commandPointsGiven);
    	config.setProperty("stats.command_char_points", plugin.commandCharPointsGiven);
    	config.setProperty("stats.move_points", plugin.movePointsGiven);
    	config.setProperty("stats.block_place_points", plugin.blockPlacePointsGiven);
    	config.setProperty("stats.block_break_points", plugin.blockBreakPointsGiven);
    	
    	config.save();
	}
}
