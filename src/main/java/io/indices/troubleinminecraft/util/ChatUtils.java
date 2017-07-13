package io.indices.troubleinminecraft.util;

import io.indices.troubleinminecraft.game.Role;
import net.kyori.text.format.TextColor;

public class ChatUtils {

    public static String formatRoleName(Role role) {
        if (role.equals(Role.INNOCENT)) {
            return TextColor.GREEN + "innocent";
        } else if (role.equals(Role.DETECTIVE)) {
            return TextColor.BLUE + "detective";
        } else if (role.equals(Role.TRAITOR)) {
            return TextColor.DARK_RED + "traitor";
        } else {
            // wat?
            return null;
        }
    }
}
