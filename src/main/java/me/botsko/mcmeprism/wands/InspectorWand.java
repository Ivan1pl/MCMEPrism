package me.botsko.mcmeprism.wands;

import java.util.ArrayList;

import me.botsko.mcmeprism.MCMEPrism;
import me.botsko.mcmeprism.actionlibs.ActionMessage;
import me.botsko.mcmeprism.actionlibs.ActionsQuery;
import me.botsko.mcmeprism.actionlibs.MatchRule;
import me.botsko.mcmeprism.actionlibs.QueryParameters;
import me.botsko.mcmeprism.actionlibs.QueryResult;
import me.botsko.mcmeprism.commandlibs.Flag;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class InspectorWand extends QueryWandBase implements Wand {

    /**
     * 
     * @param plugin
     */
    public InspectorWand(MCMEPrism plugin) {
        super( plugin );
    }

    /**
	 * 
	 */
    @Override
    public void playerLeftClick(Player player, Location loc) {
        showLocationHistory( player, loc );
    }

    /**
	 * 
	 */
    @Override
    public void playerRightClick(Player player, Location loc) {
        showLocationHistory( player, loc );
    }

    /**
     * 
     * @param player
     * @param block
     * @param loc
     */
    protected void showLocationHistory(final Player player, final Location loc) {

        final Block block = loc.getBlock();

        /**
         * Run the lookup itself in an async task so the lookup query isn't done
         * on the main thread
         */
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {

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
                params.setSpecificBlockLocation( loc );

                // Do we need a second location? (For beds, doors, etc)
                final Block sibling = us.dhmc.mcmeelixr.BlockUtils.getSiblingForDoubleLengthBlock( block );
                if( sibling != null ) {
                    params.addSpecificBlockLocation( sibling.getLocation() );
                }

                // Ignoring any actions via config?
                if( params.getActionTypes().size() == 0 ) {
                    @SuppressWarnings("unchecked")
                    final ArrayList<String> ignoreActions = (ArrayList<String>) plugin.getConfig().getList(
                            "prism.wands.inspect.ignore-actions" );
                    if( ignoreActions != null && !ignoreActions.isEmpty() ) {
                        for ( final String ignore : ignoreActions ) {
                            params.addActionType( ignore, MatchRule.EXCLUDE );
                        }
                    }
                }
                boolean timeDefault = false;
                for ( final String _default : params.getDefaultsUsed() ) {
                    if( _default.startsWith( "t:" ) ) {
                        timeDefault = true;
                    }
                }
                if( timeDefault ) {
                    params.setIgnoreTime( true );
                }

                // Query
                final ActionsQuery aq = new ActionsQuery( plugin );
                final QueryResult results = aq.lookup( params, player );
                if( !results.getActionResults().isEmpty() ) {
                    final String blockname = MCMEPrism.getItems().getAlias( block.getTypeId(), block.getData() );
                    player.sendMessage(MCMEPrism.messenger.playerHeaderMsg( ChatColor.GOLD + "--- Inspecting " + blockname
                            + " at " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + " ---" ) );
                    if( results.getActionResults().size() > 5 ) {
                        player.sendMessage(MCMEPrism.messenger.playerHeaderMsg( "Showing " + results.getTotalResults()
                                + " results. Page 1 of " + results.getTotal_pages() ) );
                    }
                    for ( final me.botsko.mcmeprism.actions.Handler a : results.getPaginatedActionResults() ) {
                        final ActionMessage am = new ActionMessage( a );
                        if( parameters.hasFlag( Flag.EXTENDED )
                                || plugin.getConfig().getBoolean( "prism.messenger.always-show-extended" ) ) {
                            am.showExtended();
                        }
                        player.sendMessage(MCMEPrism.messenger.playerMsg( am.getMessage() ) );
                    }
                } else {
                    final String space_name = ( block.getType().equals( Material.AIR ) ? "space" : block.getType()
                            .toString().replaceAll( "_", " " ).toLowerCase()
                            + ( block.getType().toString().endsWith( "BLOCK" ) ? "" : " block" ) );
                    player.sendMessage(MCMEPrism.messenger.playerError( "No history for this " + space_name + " found." ) );
                }
            }
        } );
    }

    /**
	 * 
	 */
    @Override
    public void playerRightClick(Player player, Entity entity) {
        return;
    }
}
