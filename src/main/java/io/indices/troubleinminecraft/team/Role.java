package io.indices.troubleinminecraft.team;

import net.kyori.text.format.TextColor;
import org.bukkit.ChatColor;

import javax.annotation.Nonnull;

public enum Role {
    INNOCENT("innocent", ChatColor.GREEN, TextColor.GREEN),
    TRAITOR("traitor", ChatColor.DARK_RED, TextColor.DARK_RED),
    DETECTIVE("detective", ChatColor.BLUE, TextColor.BLUE);

    private final String name;
    private final ChatColor colour;
    private final TextColor textColor;

    Role(@Nonnull String name, @Nonnull ChatColor colour, @Nonnull TextColor textColor) {
        this.name = name;
        this.colour = colour;
        this.textColor = textColor;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    public ChatColor getColour() {
        return colour;
    }

    @Nonnull
    public TextColor getTextColour() {
        return textColor;
    }
}
