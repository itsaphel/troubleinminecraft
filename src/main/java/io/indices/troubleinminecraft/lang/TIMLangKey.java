package io.indices.troubleinminecraft.lang;

import com.voxelgameslib.voxelgameslib.internal.lang.ExternalTranslatable;
import com.voxelgameslib.voxelgameslib.internal.lang.Translatable;
import javax.annotation.Nonnull;
import java.util.UUID;

public enum TIMLangKey implements ExternalTranslatable {

    TIM("Trouble in Minecraft"),

    ACTION_BAR_CREDITS("{gold}Credits: {credits}", "credits"),

    SCOREBOARD_TIM("{blue}TIM"),
    SCOREBOARD_KARMA("{red}{bold}Karma{/bold}"),
    SCOREBOARD_KILLS("{aqua}{bold}Kills{/bold}"),
    SCOREBOARD_PLAYERS_LEFT("{green}{bold}Players left{/bold}"),
    SCOREBOARD_ROLE("{gold}{bold}Role{/bold}"),

    YOU_ARE_A_TRAITOR("{red}You are a traitor! Work with your fellow traitors to kill the innocents. Watch out for the detectives, they have the tools to get you too."),
    YOUR_FELLOW_TRAITORS_ARE("{red}Your fellow traitors are: {dark_red}{traitors}{red}.", "traitors"),
    YOU_ARE_A_DETECTIVE("{blue}You are a detective! It is your job to save the innocents from the traitors."),
    YOUR_FELLOW_DETECTIVES_ARE("{blue}Your fellow detectives are: {dark_blue}{detectives}{blue}.", "detectives"),
    YOU_ARE_AN_INNOCENT("{green}You are an innocent. Find weapons and try to survive against the traitors. Work with the detectives to find and kill them. Stay alert!"),
    YOUR_DETECTIVES_ARE("{green}Your detectives are: {dark_blue}{detectives}{green}.", "detectives"),

    X_HAS_ENTERED_THE_TRAITOR_TESTER("{gold}{name} has entered the traitor tester! Results will be displayed momentarily.", "name"),

    THE_BODY_OF_X_HAS_BEEN_FOUND("{blue}The body of {yellow}{name} {blue}has been found!", "name"),

    TIME_RAN_OUT_INNOCENTS_WIN("{green}Time ran out! The innocents win!"),

    UNIDENTIFIED_BODY("Unidentified Body"),

    ITEM_BODY_ARMOUR_TITLE("Standard-issue Body Armour"),
    ITEM_CREEPER_EGG_TITLE("{red}Creeper Eggs"),
    ITEM_CREEPER_EGG_LORE("Spawn a creeper where your arrows lands."),
    ITEM_DISGUISER_TITLE("{red}Disguiser"),
    ITEM_DISGUISER_LORE("Disguise as a random player."),
    ITEM_KNIFE_TITLE("{red}Knife"),
    ITEM_KNIFE_LORE("Instantly kill the player you hit with it. One use only."),
    ITEM_KNOCKBACK_STICK_TITLE("{blue}The Detective's Stick"),
    ITEM_RADAR_TITLE("{green}Radar"),
    ITEM_RADAR_LORE("Points you towards the nearest innocent player."),
    ITEM_C4_TITLE("{red}C4"),
    ITEM_C4_LORE("Kills everyone in the area when it blows up."),
    ITEM_JIHAD_TITLE("{red}Jihad Bomb"),
    ITEM_JIHAD_LORE("Blow yourself and everyone near you up."),
    ITEM_FLARE_GUN_TITLE("{red}Flare Gun"),
    ITEM_FLARE_GUN_LORE("Burn dead bodies (zombies) so they can't be identified."),
    ITEM_HARPOON_TITLE("{red}Harpoon"),
    ITEM_HARPOON_LORE("Throwable. One shot. Instant kill."),

    ITEM_TRIPLE_HARPOON_TITLE("Triple Harpoon"),
    ITEM_TRIPLE_HARPOON_LORE("Throw 3 harpoons at once! Lethal!"),

    SHOP_TRAITOR_INV_TITLE("{red}Traitor Shop"),
    SHOP_DETECTIVE_INV_TITLE("{blue}Detective Shop"),
    SHOP_POINTS_SHOP_INV_TITLE("{gold}Points Shop"),
    SHOP_YOU_HAVE_BOUGHT_X("{green}You have bought a {yellow}{item}{green}.", "item"),
    SHOP_YOU_DO_NOT_HAVE_ENOUGH_CREDITS("{red}You do not have enough credits to purchase this item! You need {yellow}{credits} {red}more credits.", "credits");

    @Nonnull
    private final String defaultValue;

    @Nonnull
    private final String[] args;

    private static UUID uuid = UUID.randomUUID();

    TIMLangKey(@Nonnull String defaultValue, @Nonnull String... args) {
        this.defaultValue = defaultValue;
        this.args = args;
    }

    @Override
    @Nonnull
    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    @Nonnull
    public String[] getArgs() {
        return args;
    }

    @Override
    @Nonnull
    public UUID getUuid() {
        return uuid;
    }

    @Override
    @Nonnull
    public Translatable[] getValues() {
        return values();
    }
}
