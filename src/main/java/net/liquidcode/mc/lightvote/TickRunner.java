/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.liquidcode.mc.lightvote;

import java.util.HashSet;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author sl
 */
public class TickRunner extends BukkitRunnable {
    private LightVote lightVote;

    public TickRunner(LightVote lightVote) {
        this.lightVote = lightVote;
    }

    @Override
    public void run() {
        for (World world : new HashSet<>(lightVote.worldTicks.keySet())) {
            int tick = lightVote.worldTicks.get(world);
            if (tick == 7) {
                lightVote.checkIfEnoughVotes(world.getName());
                lightVote.worldTicks.remove(world);
            } else {
                lightVote.worldTicks.put(world, tick + 1);
            }
        }
    }
}
