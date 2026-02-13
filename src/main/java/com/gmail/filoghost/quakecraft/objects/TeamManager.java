package com.gmail.filoghost.quakecraft.objects;

import com.gmail.filoghost.quakecraft.constants.Lang;
import com.gmail.filoghost.quakecraft.enums.TeamColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.*;

public class TeamManager {

    private final Scoreboard scoreboard;

    // Team entries per scoreboard
    private static final String RED_ENTRY = "§c█ §f§lRossi";
    private static final String BLUE_ENTRY = "§9█ §f§lBlu";

    // Player storage (UUID safe)
    private final Set<UUID> redPlayers = new HashSet<>();
    private final Set<UUID> bluePlayers = new HashSet<>();

    // Scoreboard objects
    private Objective sidebarObjective;
    private Objective personalKillsObjective;

    private Team scoreboardRedTeam;
    private Team scoreboardBlueTeam;

    public TeamManager(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
        resetAndCreate();
    }

    public int getRedSize() {
        return redPlayers.size();
    }

    public int getBlueSize() {
        return bluePlayers.size();
    }

    public TeamColor getTeamColor(Player player) {
        UUID uuid = player.getUniqueId();

        if (bluePlayers.contains(uuid)) return TeamColor.BLUE;
        if (redPlayers.contains(uuid)) return TeamColor.RED;

        return TeamColor.BLUE; // fallback safe
    }

    public boolean areEnemies(Player one, Player two) {
        if (one == null || two == null) return false;
        return getTeamColor(one) != getTeamColor(two);
    }

    public List<Player> getEnemies(Player player, Collection<Player> onlinePlayers) {
        TeamColor team = getTeamColor(player);

        List<Player> enemies = new ArrayList<>();
        for (Player p : onlinePlayers) {
            if (p != player && getTeamColor(p) != team) {
                enemies.add(p);
            }
        }
        return enemies;
    }

    /* ---------------------------
     *  TEAM ASSIGN
     * --------------------------- */

    public void autoAssign(Player player) {
        if (bluePlayers.size() > redPlayers.size()) {
            addToRed(player);
        } else {
            addToBlue(player);
        }
    }

    public void addToRed(Player player) {
        remove(player);

        redPlayers.add(player.getUniqueId());
        scoreboardRedTeam.addEntry(player.getName());

        player.sendMessage(Lang.QUAKE_PREFIX + "§fSei stato aggiunto alla squadra §crossa§f!");
    }

    public void addToBlue(Player player) {
        remove(player);

        bluePlayers.add(player.getUniqueId());
        scoreboardBlueTeam.addEntry(player.getName());

        player.sendMessage(Lang.QUAKE_PREFIX + "§fSei stato aggiunto alla squadra §9blu§f!");
    }

    public void remove(Player player) {
        UUID uuid = player.getUniqueId();

        redPlayers.remove(uuid);
        bluePlayers.remove(uuid);

        Team team = scoreboard.getEntryTeam(player.getName());
        if (team != null) {
            team.removeEntry(player.getName());
        }
    }

    /* ---------------------------
     *  KILLS SYSTEM
     * --------------------------- */

    public int getPersonalKills(Player player) {
        return personalKillsObjective.getScore(player.getName()).getScore();
    }

    public int getTeamKills(TeamColor color) {
        String entry = (color == TeamColor.BLUE) ? BLUE_ENTRY : RED_ENTRY;
        return sidebarObjective.getScore(entry).getScore();
    }

    public void addKill(Player killer) {

        // Personal kills
        Score personal = personalKillsObjective.getScore(killer.getName());
        personal.setScore(personal.getScore() + 1);

        // Team kills
        TeamColor team = getTeamColor(killer);
        String entry = (team == TeamColor.BLUE) ? BLUE_ENTRY : RED_ENTRY;

        Score teamScore = sidebarObjective.getScore(entry);
        teamScore.setScore(teamScore.getScore() + 1);
    }

    /* ---------------------------
     *  RESET & SCOREBOARD SETUP
     * --------------------------- */

    public void resetAndCreate() {

        redPlayers.clear();
        bluePlayers.clear();

        // Remove old objectives
        Objective old = scoreboard.getObjective("classifica");
        if (old != null) old.unregister();

        old = scoreboard.getObjective("uccisioni");
        if (old != null) old.unregister();

        // Remove old teams
        Team t = scoreboard.getTeam("red");
        if (t != null) t.unregister();

        t = scoreboard.getTeam("blue");
        if (t != null) t.unregister();

        // Sidebar objective (NON deprecated)
        sidebarObjective = scoreboard.registerNewObjective("classifica", Criteria.DUMMY, Lang.OBJECTIVE_CLASSIFICA_NAME);

        sidebarObjective.setDisplaySlot(DisplaySlot.SIDEBAR);

        sidebarObjective.getScore(BLUE_ENTRY).setScore(0);
        sidebarObjective.getScore(RED_ENTRY).setScore(0);

        // Personal kills objective (hidden)
        sidebarObjective = scoreboard.registerNewObjective("classifica", Criteria.DUMMY, Lang.OBJECTIVE_CLASSIFICA_NAME);


        // Teams
        scoreboardRedTeam = scoreboard.registerNewTeam("red");
        scoreboardRedTeam.setPrefix("§c");

        scoreboardBlueTeam = scoreboard.registerNewTeam("blue");
        scoreboardBlueTeam.setPrefix("§9");
    }
}