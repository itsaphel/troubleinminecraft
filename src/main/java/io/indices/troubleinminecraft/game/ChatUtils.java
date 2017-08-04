package io.indices.troubleinminecraft.game;

import org.apache.commons.lang.StringUtils;

import io.indices.troubleinminecraft.team.Role;

public class ChatUtils {

    public static String formatRoleName(Role role) {
        return formatRoleName(role, false);
    }

    public static String formatRoleName(Role role, boolean capitalise) {
        return role != null ? role.getColour() + (capitalise ? StringUtils.capitalize(role.getName()) : role.getName()) : null;
    }
}
