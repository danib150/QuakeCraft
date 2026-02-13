package com.gmail.filoghost.quakecraft.objects;

import com.gmail.filoghost.quakecraft.QuakeCraft;
import com.gmail.filoghost.quakecraft.constants.Lang;
import com.gmail.filoghost.quakecraft.enums.GameState;
import com.gmail.filoghost.quakecraft.objects.arenas.Arena;
import com.gmail.filoghost.quakecraft.utils.ParticleUtils;
import com.google.common.collect.Sets;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Set;

public class PowerUp {

	private final PowerUpEffect effect;
	private final Arena arena;

	public Hologram hologram;

	private BukkitTask pickupTask;
	private boolean destroyed = false;

	public static Set<PowerUp> active = Sets.newHashSet();

	public PowerUp(PowerUpEffect effectVar, Location where, Arena arenaVar) {
		this.effect = effectVar;
		this.arena = arenaVar;

		where = where.clone().add(0, 0.7, 0);

		hologram = DHAPI.createHologram("powerup_" + System.nanoTime(), where);
		DHAPI.addHologramLine(hologram,
				"§e§l" + effect.getName()
						.replace("à", "a'")
						.replace("è", "e'")
						.toUpperCase());
		DHAPI.addHologramLine(hologram, effect.getIcon());

		where.getWorld().playEffect(where, Effect.MOBSPAWNER_FLAMES, 100);

		startPickupChecker();

		active.add(this);
	}

	private void startPickupChecker() {
		pickupTask = Bukkit.getScheduler().runTaskTimer(QuakeCraft.plugin, () -> {

			if (destroyed) return;

			// se la game non è attiva → rimuovi
			if (arena.getState() != GameState.GAME) {
				destroy();
				return;
			}

			// se per qualche motivo l’hologram è nullo → rimuovi
			if (hologram == null) {
				destroy();
				return;
			}

			Location holoLoc = hologram.getLocation();

			for (Player player : arena.getGamers()) {
				if (player == null || !player.isOnline()) continue;

				if (!player.getWorld().equals(holoLoc.getWorld())) continue;

				// ~1.414 blocchi (sqrt(2)) perché distanceSquared <= 2.0
				if (player.getLocation().distanceSquared(holoLoc) <= 2.0) {

					arena.soundAll(Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
					effect.apply(player, arena);
					ParticleUtils.bigSmoke(holoLoc);

					arena.tellAll(Lang.QUAKE_PREFIX + "§a" + player.getName()
							+ " ha raccolto " + effect.getName());

					destroy();
					return;
				}
			}

		}, 0L, 2L);
	}

	public void destroy() {
		if (destroyed) return;
		destroyed = true;

		if (pickupTask != null) {
			pickupTask.cancel();
			pickupTask = null;
		}

		if (hologram != null) {
			try {
				// ✅ per hologram temporanei è più corretto di delete()
				hologram.destroy();
			} catch (Throwable t) {
				// fallback in caso di versioni strane
				try { hologram.delete(); } catch (Throwable ignored) {}
			}
			hologram = null;
		}

		active.remove(this);
	}

	public boolean exists() {
		return !destroyed;
	}

	public static void removeAll() {
		for (PowerUp powerUp : active) {
			powerUp.destroy();
		}
		active.clear();
	}
}