package io.indices.troubleinminecraft.util;

import io.indices.troubleinminecraft.team.Role;
import org.bukkit.ChatColor;

public class ChatUtils {

    public static String formatRoleName(Role role) {
        if (role.equals(Role.INNOCENT)) {
            return ChatColor.GREEN + "innocent";
        } else if (role.equals(Role.DETECTIVE)) {
            return ChatColor.BLUE + "detective";
        } else if (role.equals(Role.TRAITOR)) {
            return ChatColor.DARK_RED + "traitor";
        } else {
            // wat?
            return null;
        }
    }
}
