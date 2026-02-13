package com.gmail.filoghost.quakecraft.world;

import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

public enum Particle {

    HUGE_EXPLOSION(org.bukkit.Particle.EXPLOSION_EMITTER),
    LARGE_EXPLODE(org.bukkit.Particle.EXPLOSION),

    FIREWORKS_SPARK(org.bukkit.Particle.FIREWORK),
    BUBBLE(org.bukkit.Particle.BUBBLE),
    CLOUD(org.bukkit.Particle.CLOUD),

    FLAME(org.bukkit.Particle.FLAME),
    LAVA(org.bukkit.Particle.LAVA),

    SMOKE(org.bukkit.Particle.SMOKE),
    LARGE_SMOKE(org.bukkit.Particle.LARGE_SMOKE),
    WITCH_MAGIC(org.bukkit.Particle.WITCH),
    MOB_SPELL(org.bukkit.Particle.ENTITY_EFFECT),
    HEART(org.bukkit.Particle.HEART),
    ANGRY_VILLAGER(org.bukkit.Particle.ANGRY_VILLAGER),
    HAPPY_VILLAGER(org.bukkit.Particle.HAPPY_VILLAGER),

    DRIP_WATER(org.bukkit.Particle.DRIPPING_WATER),

    END_ROD(org.bukkit.Particle.END_ROD),
    DRAGON_BREATH(org.bukkit.Particle.DRAGON_BREATH),

    NOTE(org.bukkit.Particle.NOTE),
    PORTAL(org.bukkit.Particle.REVERSE_PORTAL),
    INSTANT_EFFECT(org.bukkit.Particle.INSTANT_EFFECT),
    DUST(org.bukkit.Particle.DUST),
    ENCHANTMENT_TABLE(org.bukkit.Particle.ENCHANT);

    private final org.bukkit.Particle particle;

    Particle(org.bukkit.Particle particle) {
        this.particle = particle;
    }

    public org.bukkit.Particle getBukkitParticle() {
        return particle;
    }

    /* ---------------------------
     *  Display methods
     * --------------------------- */

    public void displaySingle(@NonNull Location loc) {
        display(loc, 0, 0, 0, 0, 1);
    }

    public void display(@NonNull Location loc,
                        double dx, double dy, double dz,
                        double speed, int amount) {

        World world = loc.getWorld();
        if (world == null) return;

        try {

            world.spawnParticle(particle, loc, amount, dx, dy, dz, speed);

        } catch (IllegalArgumentException ex) {

            world.spawnParticle(org.bukkit.Particle.CLOUD, loc, amount, dx, dy, dz, speed);
        }
    }
    public void displayPlayer(@NonNull Player player,
                              @NonNull Location loc,
                              double dx, double dy, double dz,
                              double speed, int amount) {

        player.spawnParticle(particle, loc, amount, dx, dy, dz, speed);
    }

    /* ---------------------------
     *  Special particles
     * --------------------------- */

    public static void blockDust(@NonNull Material material,
                                 @NonNull Location loc,
                                 int amount) {

        World world = loc.getWorld();
        if (world == null) return;

        world.spawnParticle(org.bukkit.Particle.BLOCK, loc,
                amount, material.createBlockData());
    }

    public static void blockCrack(@NonNull Material material,
                                  @NonNull Location loc,
                                  int amount) {

        World world = loc.getWorld();
        if (world == null) return;

        world.spawnParticle(org.bukkit.Particle.BLOCK, loc,
                amount, material.createBlockData());
    }

    public static void iconCrack(@NonNull Material material,
                                 @NonNull Location loc,
                                 int amount) {

        World world = loc.getWorld();
        if (world == null) return;

        world.spawnParticle(org.bukkit.Particle.ITEM, loc,
                amount, new org.bukkit.inventory.ItemStack(material));
    }
}