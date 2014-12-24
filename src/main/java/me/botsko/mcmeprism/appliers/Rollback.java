package me.botsko.mcmeprism.appliers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import us.dhmc.mcmeelixr.EntityUtils;
import me.botsko.mcmeprism.MCMEPrism;
import me.botsko.mcmeprism.actionlibs.QueryParameters;
import me.botsko.mcmeprism.actions.Handler;
import me.botsko.mcmeprism.commandlibs.Flag;
import me.botsko.mcmeprism.events.BlockStateChange;
import me.botsko.mcmeprism.utils.BlockUtils;

public class Rollback extends Preview {

    /**
     * 
     * @param plugin
     * @return
     */
    public Rollback(MCMEPrism plugin, CommandSender sender, List<Handler> results, QueryParameters parameters,
            ApplierCallback callback) {
        super( plugin, sender, results, parameters, callback );
    }

    /**
     * Set preview move and then do a rollback
     * 
     * @return
     */
    @Override
    public void preview() {
        is_preview = true;
        apply();
    }

    /**
	 * 
	 */
    @Override
    public void apply() {

        if( player != null ) {

            // Remove any fire at this location
            if( plugin.getConfig().getBoolean( "prism.appliers.remove-fire-on-burn-rollback" )
                    && parameters.getActionTypes().containsKey( "block-burn" ) ) {
                if( !parameters.hasFlag( Flag.NO_EXT ) ) {
                    final ArrayList<BlockStateChange> blockStateChanges = BlockUtils.extinguish( player.getLocation(),
                            parameters.getRadius() );
                    if( blockStateChanges != null && !blockStateChanges.isEmpty() ) {
                        player.sendMessage(MCMEPrism.messenger.playerHeaderMsg( "Extinguishing fire!" + ChatColor.GRAY
                                + " Like a boss." ) );
                    }
                }
            }

            // Remove item drops in this radius
            if( plugin.getConfig().getBoolean( "prism.appliers.remove-drops-on-explode-rollback" )
                    && ( parameters.getActionTypes().containsKey( "tnt-explode" ) || parameters.getActionTypes()
                            .containsKey( "creeper-explode" ) ) ) {
                if( !parameters.hasFlag( Flag.NO_ITEMCLEAR ) ) {
                    final int removed = EntityUtils.removeNearbyItemDrops( player, parameters.getRadius() );
                    if( removed > 0 ) {
                        player.sendMessage(MCMEPrism.messenger.playerHeaderMsg( "Removed " + removed
                                + " drops in affected area." + ChatColor.GRAY + " Like a boss." ) );
                    }
                }
            }

            // Remove any liquid at this location
            ArrayList<BlockStateChange> drained = null;
            if( parameters.hasFlag( Flag.DRAIN ) ) {
                drained = BlockUtils.drain( player.getLocation(), parameters.getRadius() );
            }
            if( parameters.hasFlag( Flag.DRAIN_LAVA ) ) {
                drained = BlockUtils.drainlava( player.getLocation(), parameters.getRadius() );
            }
            if( parameters.hasFlag( Flag.DRAIN_WATER ) ) {
                drained = BlockUtils.drainwater( player.getLocation(), parameters.getRadius() );
            }
            if( drained != null && drained.size() > 0 ) {
                player.sendMessage(MCMEPrism.messenger.playerHeaderMsg( "Draining liquid!" + ChatColor.GRAY
                        + " Like a boss." ) );
            }
        }

        // Give the results to the changequeue
        super.apply();

    }
}