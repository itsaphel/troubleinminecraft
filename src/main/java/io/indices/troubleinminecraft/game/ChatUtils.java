package io.indices.troubleinminecraft.game;

import io.indices.troubleinminecraft.team.Role;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nullable;

public class ChatUtils {

    @Nullable
    public static String formatRoleName(@Nullable Role role) {
        return formatRoleName(role, false);
    }

    @Nullable
    public static String formatRoleName(@Nullable Role role, boolean capitalise) {
        return role != null ? role.getColour() + (capitalise ? StringUtils.capitalize(role.getName()) : role.getName()) : null;
    }
}
