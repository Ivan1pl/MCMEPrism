package me.botsko.mcmeprism.wands;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import me.botsko.mcmeprism.MCMEPrism;
import me.botsko.mcmeprism.actionlibs.ActionsQuery;
import me.botsko.mcmeprism.actionlibs.QueryParameters;
import me.botsko.mcmeprism.actionlibs.QueryResult;
import me.botsko.mcmeprism.appliers.PrismApplierCallback;
import me.botsko.mcmeprism.appliers.PrismProcessType;
import me.botsko.mcmeprism.appliers.Restore;

public class RestoreWand extends QueryWandBase implements Wand {

    /**
     * 
     * @param plugin
     * @return
     */
    public RestoreWand(MCMEPrism plugin) {
        super( plugin );
    }

    /**
	 * 
	 */
    @Override
    public void playerLeftClick(Player player, Location loc) {
        if( loc != null ) {
            restore( player, loc );
        }
    }

    /**
	 * 
	 */
    @Override
    public void playerRightClick(Player player, Location loc) {
        if( loc != null ) {
            restore( player, loc );
        }
    }

    /**
     * 
     * @param player
     * @param block
     */
    protected void restore(Player player, Location loc) {

        final Block block = loc.getBlock();

        plugin.eventTimer.recordTimedEvent( "rollback wand used" );

        // Build params
        QueryParameters params;
        try {
            params = parameters.clone();
        } catch ( final CloneNotSupportedException ex ) {
            params = new QueryParameters();
            player.sendMessage(MCMEPrism.messenger
                    .playerError( "Error retrieving parameters. Checking with default parameters." ) );
        }

        params.setWorld( player.getWorld().getName() );
        params.setSpecificBlockLocation( block.getLocation() );
        params.setLimit( 1 );
        params.setProcessType( PrismProcessType.RESTORE );

        boolean timeDefault = false;
        for ( final String _default : params.getDefaultsUsed() ) {
            if( _default.startsWith( "t:" ) ) {
                timeDefault = true;
            }
        }
        if( timeDefault ) {
            params.setIgnoreTime( true );
        }

        final ActionsQuery aq = new ActionsQuery( plugin );
        final QueryResult results = aq.lookup( params, player );
        if( !results.getActionResults().isEmpty() ) {
            final Restore rb = new Restore( plugin, player, results.getActionResults(), params,
                    new PrismApplierCallback() );
            rb.apply();
        } else {
            final String space_name = ( block.getType().equals( Material.AIR ) ? "space" : block.getType().toString()
                    .replaceAll( "_", " " ).toLowerCase()
                    + ( block.getType().toString().endsWith( "BLOCK" ) ? "" : " block" ) );
            player.sendMessage(MCMEPrism.messenger.playerError( "Nothing to restore for this " + space_name + " found." ) );
        }
    }

    /**
	 * 
	 */
    @Override
    public void playerRightClick(Player player, Entity entity) {
        return;
    }
}
