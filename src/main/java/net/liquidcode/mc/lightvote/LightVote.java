/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.liquidcode.mc.lightvote;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.liquidcode.mc.ezpz.Ezpz;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author sl
 */
public class LightVote extends JavaPlugin {
    private float requiredVotes = 2 / (float) 3;
    
    private Ezpz ezpz = null;
    private TickRunner tickRunner = new TickRunner(this);
    protected Map<World, Integer> worldTicks = new HashMap<>();
    
    @Override
    public void onEnable() {
        tickRunner.runTaskTimer(this, 20, 20);
        this.getServer().getPluginManager().registerEvents(new EventListener(this), this);
        this.getLogger().info("test1");
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
