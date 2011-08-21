package net.blockheaven.kaipr.heavenactivity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import net.blockheaven.kaipr.heavenactivity.register.payment.Method;
import net.blockheaven.kaipr.heavenactivity.register.payment.Method.MethodAccount;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijiko.permissions.PermissionHandler;


public class HeavenActivity extends JavaPlugin {
    /**
     * Logger for messages.
     */
    protected static final Logger logger = Logger.getLogger("Minecraft.HeavenActivity");
    
    /**
     * Configuration
     */
    public HeavenActivityConfig config;
    
    /**
     * Data
     */
    public HeavenActivityData data;
    
    /**
     * Permission handler
     */
    public static PermissionHandler Permissions;
    
    /**
     * Register-based economy handling method
     */
    public static Method ecoMethod;
    
    /**
     * Sequence update timer
     */
    public static Timer updateTimer = null;
    
    /**
     * The current sequence
     */
    public int currentSequence = 0;
    
    /**
     * Called when plugin gets enabled, initialize all the stuff we need
     */
    public void onEnable() {
        
        logger.info(getDescription().getName() + " "
                + getDescription().getVersion() + " enabled.");
        
        getDataFolder().mkdirs();
        
        config = new HeavenActivityConfig(this);
        data = new HeavenActivityData(this);
        
        startUpdateTimer();
        
        PlayerListener playerListener = new HeavenActivityPlayerListener(this);
        BlockListener blockListener = new HeavenActivityBlockListener(this);
        ServerListener serverListener = new HeavenActivityServerListener(this);

        PluginManager pm = getServer().getPluginManager();
        if (config.moveTracking)
            pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Monitor, this);
        if (config.commandTracking || config.logCommands)
            pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener, Priority.Monitor, this);
        if (config.chatTracking)
            pm.registerEvent(Event.Type.PLAYER_CHAT, playerListener, Priority.Monitor, this);
        if (config.blockTracking) {
            pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Monitor, this);
            pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Priority.Monitor, this);
        }
        pm.registerEvent(Event.Type.PLUGIN_ENABLE, serverListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLUGIN_DISABLE, serverListener, Priority.Monitor, this);
        
    }
    
    /**
     * Called when the plugin gets disabled, disable timers and save stats
     */
    public void onDisable() {
        config.reloadAndSave();
        stopUpdateTimer();
    }
    
    /**
     * Command handling
     */
    public boolean onCommand(CommandSender sender, Command cmd,
            String commandLabel, String[] args) {
        
        if (args.length == 0) {
            if (hasPermission(sender, "activity.view.own")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "[Activity] Activity is only tracked for players!");
                    return false;
                }
                final int activity = data.getActivity((Player) sender);
                sendMessage(sender, "Your current activity is: " + activityColor(activity) + activity + "%");
            } else {
                sendMessage(sender, ChatColor.RED + "You have no permission to see your activity.");
            }
        } else if (args[0].compareToIgnoreCase("list") == 0 || args[0].compareToIgnoreCase("listall") == 0) {
            if (hasPermission(sender, "activity.view.list")) {
                StringBuilder res = new StringBuilder();
                for (Player player : getServer().getOnlinePlayers()) {
                    final int activity = data.getActivity(player);
                    res.append(activityColor(activity) + player.getName() + " " + activity + "%");
                    res.append(ChatColor.GRAY + ", ");
                }
                sendMessage(sender, res.substring(0, res.length() - 2));
            } else {
                sendMessage(sender, ChatColor.RED + "You have no permission to see a list of online players' activity.");
            }
        } else if (args[0].compareToIgnoreCase("admin") == 0 && hasPermission(sender, "activity.admin")) {
            if (args.length == 1) {
                sendMessage(sender, ChatColor.RED + "/activity admin <reload>");
            } else if (args[1].compareToIgnoreCase("reload") == 0) {
                config.reloadAndSave();
                config.load();
                stopUpdateTimer();
                startUpdateTimer();
                sendMessage(sender, ChatColor.GREEN + "Reloaded");
            }
        } else if (args.length == 1) {
            if (hasPermission(sender, "activity.view.other")) {
               String playerName = matchSinglePlayer(sender, args[0]).getName();
               int activity = data.getActivity(playerName);
               sendMessage(sender, "Current activity of " + playerName + ": " + activityColor(activity) + activity + "%");
            } else {
                sendMessage(sender, ChatColor.RED + "You have no permission to see other's activity.");
            }
        }
            
        return true;
        
    }
    
    /**
     * Checks permission for a CommandSender
     * 
     * @param player
     * @param node
     * @return
     */
    public boolean hasPermission(CommandSender sender, String node) {
        if (sender instanceof ConsoleCommandSender)
            return true;
        return hasPermission((Player)sender, node);
    }
    
    /**
     * Checks permission for a Player
     * 
     * @param player
     * @param node
     * @return
     */
    public boolean hasPermission(Player player, String node) {
        if (player.isOp())
            return true;
        
        if (Permissions != null) {
            return Permissions.has(player, node);
        } else {
            return player.hasPermission(node);
        }
    }
    
    /**
     * Returns a cumulated multiplier set for the given player
     * 
     * @param player
     * @return
     */
    public Map<ActivitySource, Double> getCumulatedMultiplierSet(Player player) {
        Map<ActivitySource, Double> res = new HashMap<ActivitySource, Double>(ActivitySource.values().length);
        
        Iterator<String> multiplierSetNameIterator = config.multiplierSets.keySet().iterator();
        for (int i1 = config.multiplierSets.size(); i1 > 0; i1--) {
            final String multiplierSetName = multiplierSetNameIterator.next();
            
            if (!hasPermission(player, "activity.multiplier." + multiplierSetName))
                continue;
            
            final Map<ActivitySource, Double> multiplierSet = config.multiplierSets.get(multiplierSetName);
            Iterator<ActivitySource> sourceIterator = multiplierSet.keySet().iterator();
            
            for (int i2 = multiplierSet.size(); i2 > 0; i2--) {
                final ActivitySource source = sourceIterator.next();
                
                if (res.containsKey(source)) {
                    res.put(source, res.get(source) * multiplierSet.get(source));
                } else {
                    res.put(source, multiplierSet.get(source));
                }
            }
            
        }
        
        return res;
    }
    
    
//    public Set<String> getMultiplierSets(Player player) {
//        Set<String> res = new HashSet<String>();
//        
//        Iterator<String> multiplierIterator = config.multiplierSets.keySet().iterator();
//        while (multiplierIterator.hasNext()) {
//            final String multiplier = multiplierIterator.next();
//            if (hasPermission(player, "activity.multiplier." + multiplier)) {
//                res.add(multiplier);
//            }
//        }
//        
//        return res;
//    }
    
    /**
     * Sends a prefixed message to given CommandSender
     * 
     * @param sender
     * @param message
     */
    public void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.DARK_GRAY + "[Activity] " + ChatColor.GRAY + message);
    }
    
    /**
     * Sends a prefixed message to given Player
     * 
     * @param player
     * @param message
     */
    public void sendMessage(Player player, String message) {
        player.sendMessage(ChatColor.DARK_GRAY + "[Activity] " + ChatColor.GRAY + message);
    }
    
    /**
     * Match a single online player which name contains filter
     * 
     * @param sender
     * @param filter
     * @return
     */
    public Player matchSinglePlayer(CommandSender sender, String filter) {
        
        filter = filter.toLowerCase();
        for (Player player : getServer().getOnlinePlayers()) {
            if (player.getName().toLowerCase().contains(filter)) {
                return player;
            }
        }
        
        sender.sendMessage(ChatColor.RED + "No matching player found, matching yourself.");
        return (Player) sender;
        
    }
    
    /**
     * Initializes and starts the update timer
     */
    protected void startUpdateTimer() {
        
        updateTimer = new Timer();
        updateTimer.scheduleAtFixedRate(new TimerTask() {
            
            public void run() {
                
                // Give players info
                if (currentSequence % config.notificationSequence == 0) {
                    for (Player player : getServer().getOnlinePlayers()) {
                        if (hasPermission(player, "activity.notify.activity")) {
                            final int activity = data.getActivity(player.getName());
                            sendMessage(player, "Your current activity is: " + activityColor(activity) + activity + "%");
                        }
                    }
                }
                
                // Handle income
                if (currentSequence % config.incomeSequence == 0 && config.incomeEnabled) {
                    handleOnlineIncome();
                }
                
                ++currentSequence;
                data.initNewSequence();
            }
            
        }, 0, (config.sequenceInterval * 1000L));
        
        logger.info("[HeavenActivity] Update timer started");
        
    }
    
    /**
     * Stops the update timer
     */
    protected void stopUpdateTimer() {
        updateTimer.cancel();
        logger.info("[HeavenActivity] Update timer stopped");
    }

    /**
     * Gives income to online players
     */
    protected void handleOnlineIncome() {
        
        if (data.playersActivities.size() == 0)
            return;
        
        if (ecoMethod == null) {
            logger.warning("[HeavenActivity] Want to give income, but no economy plugin is active! Skipping...");
            return;
        }
        
        for (Player player : getServer().getOnlinePlayers()) {
            final int activity = data.getActivity(player);
            if ((int)activity >= config.incomeMinActivity) {
                MethodAccount account = ecoMethod.getAccount(player.getName());
                
                config.incomeExpression.setVariable("activity", activity);
                config.incomeExpression.setVariable("player_balance", account.balance());
                
                final Double amount = config.incomeExpression.getValue();
                
                if (amount > 0.0 || config.incomeAllowNegative) {
                    account.add(amount);
                
                    if (hasPermission(player, "activity.notify.income")) {
                        sendMessage(player, "You got " + activityColor(activity) + ecoMethod.format(amount) 
                            + ChatColor.GRAY + " income for being " 
                            + activityColor(activity) + activity + "% " + ChatColor.GRAY + "active.");
                        sendMessage(player, "Your Balance is now: " + ChatColor.WHITE 
                            + ecoMethod.format(account.balance()));
                    }
                    
                    continue;
                }
            }
            
            sendMessage(player, ChatColor.RED + "You were too lazy, no income for you this time!");
        }
        
    }
    
    protected ChatColor activityColor(int activity) {
        
        if (activity > 75) {
            return ChatColor.GREEN;
        } else if (activity < 25) {
            return ChatColor.RED;
        } else {
            return ChatColor.YELLOW;
        }
        
    }

}
