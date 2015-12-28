/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.liquidcode.mc.lightvote;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.liquidcode.mc.ezpz.Ezpz;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author sl
 */
public class LightVote extends JavaPlugin {
    static String mainDirectory;
    
    private float requiredVotes = 2 / (float) 3;
    
    private Ezpz ezpz = null;
    private TickRunner tickRunner = new TickRunner(this);
    protected Map<World, Integer> worldTicks = new HashMap<>();
    
    @Override
    public void onEnable() {
        mainDirectory = "plugins/" + this.getName();
        
        try {
            loadConfig();
        } catch (InvalidConfigurationException | IOException ex) {
            getLogger().info("Exception was raised while loading config, using defaults.");
            loadDefaultConfig();
        }
        tickRunner.runTaskTimer(this, 20, 20);
        this.getServer().getPluginManager().registerEvents(new EventListener(this), this);
    }
    
    @Override
    public void onDisable() {
        // @TODO: Do something
    }
    
    public void touch(World world) {
        long timeOfDay = world.getTime() % 24000;
        
        // Is this a point in time where the player should be able to sleep?
        if (timeOfDay >= 12541 && timeOfDay <= 23458) {
            worldTicks.put(world, 0);
        }
    }
    
    public void loadDefaultConfig() {
        requiredVotes = 2 / (float) 3;
    }
    
    public void loadConfig() throws InvalidConfigurationException, FileNotFoundException, IOException {
        File file = new File(mainDirectory + File.separator + "config.yml");
        getLogger().info("Looking for config file: "+file.getAbsolutePath());
        if  (file.canRead()) {
            getLogger().info("Ok, found it.");
        } else {
            getLogger().warning("Unable to read file!");
            throw new IOException("File is unreadable");
        }
        
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (FileNotFoundException ex) {
            getLogger().warning("Couldn't find file!");
            throw ex;
        } catch (IOException ex) {
            getLogger().warning("IO error when loading config!");
            throw ex;
        } catch (InvalidConfigurationException ex) {
            ex.printStackTrace();
            getLogger().severe("Invalid configuration!");
            throw ex;
        }
        
        String type = config.getString("requiredvotes.type", null);
        if (type.equals("percentage")) {
            requiredVotes = (float) config.getDouble("requiredvotes.percentage");
            if (requiredVotes <= 0.0) {
                getLogger().warning("Invalid fraction for required votes, using 75% instead.");
                requiredVotes = 0.75f;
            }
            getLogger().info(String.format("Required votes: %.2f%% (percentage)",
                    requiredVotes * 100));
        } else if (type.equals("fraction")) {
            float numerator = ((float) config.getInt("requiredvotes.fraction.numerator"));
            float denominator = ((float) config.getInt("requiredvotes.fraction.denominator"));
            if (numerator <= 0 || denominator <= 0) {
                getLogger().warning("Invalid fraction for required votes, using 2 / 3 instead.");
                numerator = 2;
                denominator = 3;
            }
            requiredVotes = numerator / denominator;
            getLogger().info(String.format("Required votes: %.2f%% (fraction, %.0f / %.0f)",
                    requiredVotes * 100, numerator, denominator));
        }
    }
    
    public void checkIfEnoughVotes(String worldGroup) {
        int players = 0, votes = 0;
        float votePercentage = 0.0f;
        
        List<World> worlds = this.getWorlds(worldGroup);
        for (World world : worlds) {
            for (Player player : world.getPlayers()) {
                players++;
                
                if (player.isSleeping()) votes++;
            }
        }
        
        if (players == 0 || votes == 0) return;
        
        votePercentage = votes / (float) players;
        if (votePercentage >= requiredVotes) {
            long time = worlds.get(0).getTime();
            time += 24000 - (time % 24000);
            worlds.get(0).setTime(time);
        } else {
            int neededPlayers = (int) Math.ceil(players * requiredVotes) - votes;
            String plural = neededPlayers == 1 ? "" : "s";
            this.getServer().broadcastMessage(String.format(
                    "%sSystem » %s%s out of %s players are asleep. Need %s more player%s.",
                    ChatColor.DARK_AQUA, ChatColor.AQUA,
                    votes, players, neededPlayers, plural));
        }
    }

    private List<World> getWorlds(String worldGroup) {
        List<World> worlds = new ArrayList<>();
        
        if (this.ezpz != null)
            return this.ezpz.getWorldsInGroup(worldGroup);
       
        World world = this.getServer().getWorld(worldGroup);
        if (world != null) {
            worlds.add(world);
        }
        
        return worlds;
    }
}
