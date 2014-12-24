package me.botsko.mcmeprism.wands;

import me.botsko.mcmeprism.MCMEPrism;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class ProfileWand extends WandBase implements Wand {

    /**
	 * 
	 */
    @Override
    public void playerLeftClick(Player player, Location loc) {
        if( loc != null ) {
            showLocationProfile( player, loc );
        }
    }

    /**
	 * 
	 */
    @Override
    public void playerRightClick(Player player, Location loc) {
        if( loc != null ) {
            showLocationProfile( player, loc );
        }
    }

    /**
     * 
     * @param player
     * @param block
     * @param loc
     */
    protected void showLocationProfile(Player player, Location loc) {

        final Block block = loc.getBlock();

        player.sendMessage(MCMEPrism.messenger.playerHeaderMsg( "Location Profile" ) );

        player.sendMessage(MCMEPrism.messenger.playerMsg( "Name: " + block.getType().toString().toLowerCase() ) );
        player.sendMessage(MCMEPrism.messenger.playerMsg("Alias: "
                + MCMEPrism.getItems().getAlias( block.getTypeId(), block.getData() ) ) );
        player.sendMessage(MCMEPrism.messenger.playerMsg( "ID: " + block.getTypeId() + ":" + block.getData() ) );
        player.sendMessage(MCMEPrism.messenger.playerMsg( "Coords: " + block.getX() + " " + block.getY() + " "
                + block.getZ() ) );

    }

    /**
	 * 
	 */
    @Override
    public void playerRightClick(Player player, Entity entity) {
        if( entity != null ) {
            player.sendMessage(MCMEPrism.messenger.playerHeaderMsg( "Entity Profile" ) );
            player.sendMessage(MCMEPrism.messenger.playerMsg( "Name: " + entity.getType().toString().toLowerCase() ) );
            player.sendMessage(MCMEPrism.messenger.playerMsg( "ID: " + entity.getEntityId() ) );
            player.sendMessage(MCMEPrism.messenger.playerMsg( "Coords: " + entity.getLocation().getBlockX() + " "
                    + entity.getLocation().getBlockY() + " " + entity.getLocation().getBlockZ() ) );
        }
    }
}