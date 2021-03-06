package me.botsko.mcmeprism.commands;

import us.dhmc.mcmeelixr.TypeUtils;
import me.botsko.mcmeprism.MCMEPrism;
import me.botsko.mcmeprism.commandlibs.CallInfo;
import me.botsko.mcmeprism.commandlibs.SubHandler;
import me.botsko.mcmeprism.events.BlockStateChange;
import me.botsko.mcmeprism.events.PrismBlocksDrainEvent;
import me.botsko.mcmeprism.utils.BlockUtils;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class DrainCommand implements SubHandler {

    /**
	 * 
	 */
    private final MCMEPrism plugin;

    /**
     * 
     * @param plugin
     * @return
     */
    public DrainCommand(MCMEPrism plugin) {
        this.plugin = plugin;
    }

    /**
     * Handle the command
     */
    @Override
    public void handle(CallInfo call) {

        String drain_type = "";
        int radius = plugin.getConfig().getInt( "prism.drain.default-radius" );
        if( call.getArgs().length == 3 ) {
            if( call.getArg( 1 ).equalsIgnoreCase( "water" ) || call.getArg( 1 ).equalsIgnoreCase( "lava" ) ) {
                drain_type = call.getArg( 1 );
            } else {
                call.getPlayer().sendMessage(MCMEPrism.messenger.playerError( "Invalid drain type. Must be lava, water, or left out." ) );
                return;
            }
            // Validate radius
            radius = validateRadius( call, call.getArg( 2 ) );
        } else if( call.getArgs().length == 2 ) {
            if( TypeUtils.isNumeric( call.getArg( 1 ) ) ) {
                radius = validateRadius( call, call.getArg( 1 ) );
            } else {
                if( call.getArg( 1 ).equalsIgnoreCase( "water" ) || call.getArg( 1 ).equalsIgnoreCase( "lava" ) ) {
                    drain_type = call.getArg( 1 );
                } else {
                    call.getPlayer().sendMessage(MCMEPrism.messenger.playerError( "Invalid drain type. Must be lava, water, or left out." ) );
                    return;
                }
            }
        }

        if( radius == 0 )
            return;

        // Build seeking message
        String msg = "Looking for " + drain_type + " within " + radius + " blocks.";
        if( drain_type.equals( "water" ) ) {
            msg += ChatColor.GRAY + " It's just too wet.";
        } else if( drain_type.equals( "lava" ) ) {
            msg += ChatColor.GRAY + " It's getting hot in here.";
        }
        call.getPlayer().sendMessage(MCMEPrism.messenger.playerHeaderMsg( msg ) );

        ArrayList<BlockStateChange> blockStateChanges = null;
        if( drain_type.isEmpty() ) {
            blockStateChanges = BlockUtils.drain( call.getPlayer().getLocation(), radius );
        } else if( drain_type.equals( "water" ) ) {
            blockStateChanges = BlockUtils.drainwater( call.getPlayer().getLocation(), radius );
        } else if( drain_type.equals( "lava" ) ) {
            blockStateChanges = BlockUtils.drainlava( call.getPlayer().getLocation(), radius );
        }

        if( blockStateChanges != null && !blockStateChanges.isEmpty() ) {

            // @todo remove the extra space in msg
            call.getPlayer().sendMessage(MCMEPrism.messenger.playerHeaderMsg( "Drained " + blockStateChanges.size() + " " + drain_type
                            + " blocks." ) );
            call.getPlayer().sendMessage(MCMEPrism.messenger.playerSubduedHeaderMsg( "Use /prism undo last if needed." ) );

            // Trigger the event
            final PrismBlocksDrainEvent event = new PrismBlocksDrainEvent( blockStateChanges, call.getPlayer(), radius );
            plugin.getServer().getPluginManager().callEvent( event );

        } else {
            call.getPlayer().sendMessage(MCMEPrism.messenger.playerError( "Nothing found to drain within that radius." ) );
        }
    }

    @Override
    public List<String> handleComplete(CallInfo call) {
        return null;
    }

    /**
     * 
     * @param call
     * @return
     */
    protected int validateRadius(CallInfo call, String radius_arg) {
        if( TypeUtils.isNumeric( radius_arg ) ) {
            final int _tmp_radius = Integer.parseInt( radius_arg );
            if( _tmp_radius > 0 ) {
                if( _tmp_radius > plugin.getConfig().getInt( "prism.drain.max-radius" ) ) {
                    call.getPlayer().sendMessage(MCMEPrism.messenger.playerError( "Radius exceeds max set in config." ) );
                    return 0;
                } else {
                    return _tmp_radius;
                }
            } else {
                call.getPlayer()
                        .sendMessage(MCMEPrism.messenger
                                        .playerError( "Radius must be greater than zero. Or leave it off to use the default. Use /prism ? for help." ) );
                return 0;
            }
        } else {
            call.getPlayer()
                    .sendMessage(MCMEPrism.messenger
                                    .playerError( "Radius must be a number. Or leave it off to use the default. Use /prism ? for help." ) );
            return 0;
        }
    }
}