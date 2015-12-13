/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.liquidcode.mc.lightvote;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 *
 * @author sl
 */
public class EventListener implements Listener {
    private LightVote lightVote;

    public EventListener(LightVote lightVote) {
        this.lightVote = lightVote;
    }
    
    @EventHandler
    public void handle(PlayerBedLeaveEvent event) {
        lightVote.touch(event.getPlayer().getWorld());
    }
    
    @EventHandler
    public void handle(PlayerBedEnterEvent event) {
        lightVote.touch(event.getPlayer().getWorld());
    }
    
    @EventHandler
    public void handle(PlayerQuitEvent event) {
        lightVote.touch(event.getPlayer().getWorld());
    }
    
    @EventHandler
    public void handle(PlayerPortalEvent event) {
        lightVote.touch(event.getPlayer().getWorld());
    }
    
    @EventHandler
    public void handle(PlayerChangedWorldEvent event) {
        lightVote.touch(event.getPlayer().getWorld());
    }
    
    @EventHandler
    public void handle(PlayerTeleportEvent event) {
        lightVote.touch(event.getPlayer().getWorld());
    }
}
