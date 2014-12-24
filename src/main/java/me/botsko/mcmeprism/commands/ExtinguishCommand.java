package me.botsko.mcmeprism.commands;

import us.dhmc.mcmeelixr.TypeUtils;
import me.botsko.mcmeprism.MCMEPrism;
import me.botsko.mcmeprism.commandlibs.CallInfo;
import me.botsko.mcmeprism.commandlibs.SubHandler;
import me.botsko.mcmeprism.events.BlockStateChange;
import me.botsko.mcmeprism.events.PrismBlocksExtinguishEvent;
import me.botsko.mcmeprism.utils.BlockUtils;

import java.util.ArrayList;
import java.util.List;

public class ExtinguishCommand implements SubHandler {

    /**
	 * 
	 */
    private final MCMEPrism plugin;

    /**
     * 
     * @param plugin
     * @return
     */
    public ExtinguishCommand(MCMEPrism plugin) {
        this.plugin = plugin;
    }

    /**
     * Handle the command
     */
    @Override
    public void handle(CallInfo call) {

        int radius = plugin.getConfig().getInt( "prism.ex.default-radius" );
        if( call.getArgs().length == 2 ) {
            if( TypeUtils.isNumeric( call.getArg( 1 ) ) ) {
                final int _tmp_radius = Integer.parseInt( call.getArg( 1 ) );
                if( _tmp_radius > 0 ) {
                    if( _tmp_radius > plugin.getConfig().getInt( "prism.ex.max-radius" ) ) {
                        call.getPlayer()
                                .sendMessage(MCMEPrism.messenger.playerError( "Radius exceeds max set in config." ) );
                        return;
                    } else {
                        radius = _tmp_radius;
                    }
                } else {
                    call.getPlayer()
                            .sendMessage(MCMEPrism.messenger
                                            .playerError( "Radius must be greater than zero. Or leave it off to use the default. Use /prism ? for help." ) );
                    return;
                }
            } else {
                call.getPlayer()
                        .sendMessage(MCMEPrism.messenger
                                        .playerError( "Radius must be a number. Or leave it off to use the default. Use /prism ? for help." ) );
                return;
            }
        }

        final ArrayList<BlockStateChange> blockStateChanges = BlockUtils.extinguish( call.getPlayer().getLocation(),
                radius );
        if( blockStateChanges != null && !blockStateChanges.isEmpty() ) {

            call.getPlayer().sendMessage(MCMEPrism.messenger.playerHeaderMsg( "Extinguished nearby fire! Cool!" ) );

            // Trigger the event
            final PrismBlocksExtinguishEvent event = new PrismBlocksExtinguishEvent( blockStateChanges,
                    call.getPlayer(), radius );
            plugin.getServer().getPluginManager().callEvent( event );

        } else {
            call.getPlayer().sendMessage(MCMEPrism.messenger.playerError( "No fires found within that radius to extinguish." ) );
        }
    }

    @Override
    public List<String> handleComplete(CallInfo call) {
        return null;
    }
}