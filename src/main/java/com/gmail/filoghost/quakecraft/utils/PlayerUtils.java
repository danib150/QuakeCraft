package com.gmail.filoghost.quakecraft.utils;

import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

public class PlayerUtils {

    public static void sendExperiencePacket(Player player, float exp, int level) {
        if (player.isDead()) return;

        // clamp sicurezza (0-1)
        exp = Math.max(0.0f, Math.min(1.0f, exp));

        player.setExp(exp);
        player.setLevel(level);
    }

    public static void clearInventoryFully(@NonNull Player player) {
        player.setItemOnCursor(null);
        PlayerInventory playerInventory = player.getInventory();
        playerInventory.clear();
        playerInventory.setArmorContents(null);

        Inventory topOpenInventory = player.getOpenInventory().getTopInventory();
        if (topOpenInventory.getType() == InventoryType.CRAFTING) {
            topOpenInventory.clear();
        }
    }

}
