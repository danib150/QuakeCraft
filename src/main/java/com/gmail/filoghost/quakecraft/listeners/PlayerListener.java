package com.gmail.filoghost.quakecraft.listeners;

import com.gmail.filoghost.quakecraft.Configuration;
import com.gmail.filoghost.quakecraft.QuakeCraft;
import com.gmail.filoghost.quakecraft.constants.Permissions;
import com.gmail.filoghost.quakecraft.enums.Gui;
import com.gmail.filoghost.quakecraft.objects.KillStreak;
import com.gmail.filoghost.quakecraft.objects.arenas.Arena;
import com.gmail.filoghost.quakecraft.objects.player.QuakePlayer;
import com.gmail.filoghost.quakecraft.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;


public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();

        Bukkit.getScheduler().runTaskLater(QuakeCraft.plugin, () -> {
            if (victim.isOnline() && victim.isDead()) {
                victim.spigot().respawn(); //TODO Check if works
            }
        }, 20L);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        //remove potion effects
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }

        Arena arena = QuakeCraft.getArenaByPlayer(player);
        if (arena == null) {
            event.setRespawnLocation(Configuration.lobby);

            QuakePlayer qp = QuakePlayer.get(player);
            Utils.giveLobbyStuff(qp);
            qp.displayCoins();
        } else {
            arena.handleRespawn(event);
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerJoin(PlayerJoinEvent event) {
        //event.setJoinMessage(null);
        Player player = event.getPlayer();
        QuakePlayer quakePlayer = QuakePlayer.load(player);
        Utils.toTheLobby(quakePlayer, false, true);

        if (player.hasPermission(Permissions.ADMIN)) {
            player.setGameMode(GameMode.CREATIVE);
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (QuakeCraft.isPlaying(player)) {
            QuakeCraft.getArenaByPlayer(player).removeGamer(player);
        }

        KillStreak.resetConsecutiveKills(player);
        QuakePlayer quakePlayer = QuakePlayer.get(player);

        if (QuakeCraft.autosaveTimer.needsSave(quakePlayer)) {
            QuakeCraft.autosaveTimer.removePlayer(quakePlayer);
            quakePlayer.saveAsync();
        }
        QuakePlayer.unload(player);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        Action action = event.getAction();
        Player player = event.getPlayer();

        if (action == Action.RIGHT_CLICK_BLOCK) {

            Block block = event.getClickedBlock();
            if (block != null && block.getState() instanceof Sign sign) {

                String line0 = sign.getSide(Side.FRONT).getLine(0);

                Arena arena = QuakeCraft.getArenaByName(line0);

                if (arena != null) {
                    event.setCancelled(true);
                    arena.addGamer(player);
                    return;
                }
            }
        }

        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack heldItem = event.getItem();
        if (heldItem == null) return;

        Arena arena = QuakeCraft.getArenaByPlayer(player);

        if (arena != null) {

            if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.GRASS_BLOCK) {
                event.setCancelled(true);
            }

            arena.rightClickEvent(player, heldItem);

        } else {

            Material type = heldItem.getType();

            if (type == Material.EMERALD) {
                Gui.SHOP_MAIN.open(player);

            } else if (type == Material.PAPER) {
                Gui.ARENAS.open(player);
            }
        }
    }
}
