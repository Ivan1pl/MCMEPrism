package me.botsko.mcmeprism.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.botsko.mcmeprism.MCMEPrism;
import me.botsko.mcmeprism.wands.Wand;

public class WandUtils {

    public static boolean playerUsesWandOnClick(Player player, Location loc) {

        if( MCMEPrism.playersWithActiveTools.containsKey( player.getName() ) ) {

            final Wand wand = MCMEPrism.playersWithActiveTools.get( player.getName() );

            if( wand == null )
                return false;

            final int item_id = wand.getItemId();
            final byte item_subid = wand.getItemSubId();
            if( player.getItemInHand().getTypeId() == item_id && player.getItemInHand().getDurability() == item_subid ) {
                // Left click is for current location
                wand.playerLeftClick( player, loc );
                return true;
            }
        }

        return false;

    }

}