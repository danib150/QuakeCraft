package com.gmail.filoghost.quakecraft.utils;

import com.gmail.filoghost.quakecraft.QuakeCraft;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class SettingsChecker {


	
	public static void run(QuakeCraft quakeCraft) {
		
		List<String> logs = new ArrayList<String>();

		if (Bukkit.getAllowEnd()) {
			logs.add("End is allowed. It's recommanded to disable it.");
		}
		
		if (Bukkit.getAllowNether()) {
			logs.add("Nether is allowed. It's recommanded to disable it.");
		}
		
		if (QuakeCraft.mainWorld.getAllowAnimals()) {
			logs.add("Animal spawning allowed. It's recommanded to disable it.");
		}
		
		if (QuakeCraft.mainWorld.getAllowMonsters()) {
			logs.add("Animal spawning allowed. It's recommanded to disable it.");
		}

		logs.forEach(s-> quakeCraft.getLogger().log(Level.WARNING, s));

	}
}
