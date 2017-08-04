package io.indices.troubleinminecraft.abilities;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import io.indices.troubleinminecraft.TroubleInMinecraftPlugin;
import com.voxelgameslib.voxelgameslib.components.ability.Ability;
import com.voxelgameslib.voxelgameslib.event.GameEvent;
import com.voxelgameslib.voxelgameslib.user.User;
import com.voxelgameslib.voxelgameslib.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

import javax.inject.Inject;

public class BodyArmourAbility extends Ability {
    @Inject
    private TroubleInMinecraftPlugin plugin;

    /**
     * @see Ability#Ability(User)
     */
    public BodyArmourAbility(User user) {
        super(user);
    }

    @Override
    public void start() {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

        protocolManager.addPacketListener(
                new PacketAdapter(plugin, ListenerPriority.NORMAL,
                        PacketType.Play.Server.ENTITY_EQUIPMENT) {
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        EnumWrappers.ItemSlot slot = event.getPacket().getItemSlots().read(0);
                        if (event.getPacket().getIntegers().read(0) == affected.getPlayer().getEntityId() && (slot == EnumWrappers.ItemSlot.HEAD || slot == EnumWrappers.ItemSlot.CHEST || slot == EnumWrappers.ItemSlot.LEGS || slot == EnumWrappers.ItemSlot.FEET)) { // just in case i want to add more armours
                            event.setCancelled(true);
                        }
                    }
                }
        );

        affected.getPlayer().getInventory().setChestplate(new ItemBuilder(Material.IRON_CHESTPLATE).amount(1).name(ChatColor.RED + "Standard-issue Traitor Armour").build());
    }

    @Override
    public void stop() {

    }

    @Override
    public void tick() {

    }

    @GameEvent
    public void takeArmourOff(InventoryClickEvent event) {
        if (event.getWhoClicked().getUniqueId().equals(affected.getUuid()) && event.getSlotType() == InventoryType.SlotType.ARMOR) {
            // u ain't takin' this armour off m8
            event.setCancelled(true);
        }
    }
}
