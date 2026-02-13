package com.gmail.filoghost.quakecraft.world;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.List;

public class RayTrace {

    /**
     * Punto in vista del player:
     * - Se trova un blocco → ritorna la posizione esatta dell’impatto
     * - Se non trova nulla → ritorna un punto lontano davanti al player
     */
    public static Location getSight(Player player) {

        RayTraceResult result = player.rayTraceBlocks(100); // distanza max

        if (result != null && result.getHitPosition() != null) {
            return result.getHitPosition().toLocation(player.getWorld());
        }

        // fallback: punto lontano nella direzione della visuale
        Location eye = player.getEyeLocation();
        Vector dir = eye.getDirection().normalize();

        return eye.add(dir.multiply(100));
    }

    /**
     * Controlla se una location è dentro un blocco solido.
     */
    public static boolean isInsideBlock(Location loc) {
        Block block = loc.getBlock();
        return block.getType().isSolid();
    }

    /**
     * Controlla se il player sta soffocando dentro un blocco.
     */
    public static boolean isSuffocatingInsideBlock(Player player) {
        return player.getEyeLocation().getBlock().getType().isSolid();
    }

    /**
     * Raytrace da start a end: ritorna il primo blocco incontrato.
     * Null se non colpisce nulla.
     */
    public static Location getSight(Location start, Location end) {

        World world = start.getWorld();
        if (world == null) return null;

        Vector direction = end.toVector().subtract(start.toVector()).normalize();
        double distance = start.distance(end);

        RayTraceResult result = world.rayTraceBlocks(start, direction, distance);

        if (result != null && result.getHitPosition() != null) {
            return result.getHitPosition().toLocation(world);
        }

        return null;
    }

    /**
     * Controllo su tutti i player nel mondo.
     */
    public static SightInfo getSightIncludePlayers(Player player) {
        return getSightIncludePlayers(player, player.getWorld().getPlayers(), 0.3);
    }

    /**
     * Controllo su una lista ristretta di player.
     */
    public static SightInfo getSightIncludePlayers(Player player, List<Player> possibleTargets) {
        return getSightIncludePlayers(player, possibleTargets, 0.3);
    }

    /**
     * Raytrace che include anche i player.
     */
    public static SightInfo getSightIncludePlayers(Player player, List<Player> possibleTargets, double boundingBoxIncrement) {

        Location eye = player.getEyeLocation();
        Vector dir = eye.getDirection().normalize();

        double maxDistance = 100;

        RayTraceResult result = player.getWorld().rayTraceEntities(eye, dir, maxDistance, entity -> possibleTargets.contains(entity) && entity != player);

        if (result != null && result.getHitEntity() != null) {

            Entity hit = result.getHitEntity();

            if (hit instanceof Player target) {
                return new SightInfo(target, result.getHitPosition().toLocation(player.getWorld()));
            }
        }

        return new SightInfo(null, null);
    }
}