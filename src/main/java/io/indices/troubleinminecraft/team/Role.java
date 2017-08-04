package io.indices.troubleinminecraft.team;

import net.kyori.text.format.TextColor;

import org.bukkit.ChatColor;

public enum Role {
    INNOCENT("innocent", ChatColor.GREEN, TextColor.GREEN),
    TRAITOR("traitor", ChatColor.DARK_RED, TextColor.DARK_RED),
    DETECTIVE("detective", ChatColor.BLUE, TextColor.BLUE);

    private final String name;
    private final ChatColor colour;
    private final TextColor textColor;

    Role(String name, ChatColor colour, TextColor textColor) {
        this.name = name;
        this.colour = colour;
        this.textColor = textColor;
    }

    public String getName() {
        return name;
    }

    public ChatColor getColour() {
        return colour;
    }

    public TextColor getTextColour() {
        return textColor;
    }
}
