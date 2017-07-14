package io.indices.troubleinminecraft.game;

import io.indices.troubleinminecraft.team.Role;
import org.apache.commons.lang.StringUtils;

public class ChatUtils {

    public static String formatRoleName(Role role) {
        return formatRoleName(role, false);
    }

    public static String formatRoleName(Role role, boolean capitalise) {
        return role != null ? role.getColour() + (capitalise ? StringUtils.capitalize(role.getName()) : role.getName()) : null;
    }
}
