package com.gmail.filoghost.quakecraft.objects.upgrades;

import com.gmail.filoghost.quakecraft.enums.UpgradeType;
import com.gmail.filoghost.quakecraft.world.Particle;
import org.bukkit.inventory.ItemStack;

public class Trail extends Upgrade {

	Particle particle;
	
	public Trail(Integer ID, String name, Particle particle, String description, ItemStack icon, int price) {
		super(ID, name, description, icon, price);
		this.particle = particle;
	}
	
	public Particle getParticle() {
		return particle;
	}
	
	@Override
	public UpgradeType getType() {
		return UpgradeType.TRAIL;
	}

}
