package me.botsko.mcmeprism.commands;

import me.botsko.mcmeprism.MCMEPrism;
import me.botsko.mcmeprism.actionlibs.ActionsQuery;
import me.botsko.mcmeprism.actionlibs.QueryParameters;
import me.botsko.mcmeprism.actionlibs.QueryResult;
import me.botsko.mcmeprism.appliers.PrismApplierCallback;
import me.botsko.mcmeprism.appliers.PrismProcessType;
import me.botsko.mcmeprism.appliers.Rollback;
import me.botsko.mcmeprism.commandlibs.CallInfo;
import me.botsko.mcmeprism.commandlibs.PreprocessArgs;
import me.botsko.mcmeprism.commandlibs.SubHandler;

import java.util.ArrayList;
import java.util.List;

public class RollbackCommand implements SubHandler {

    /**
	 * 
	 */
    private final MCMEPrism plugin;

    /**
     * 
     * @param plugin
     * @return
     */
    public RollbackCommand(MCMEPrism plugin) {
        this.plugin = plugin;
    }

    /**
     * Handle the command
     */
    @Override
    public void handle(final CallInfo call) {

        final QueryParameters parameters = PreprocessArgs.process( plugin, call.getSender(), call.getArgs(),
                PrismProcessType.ROLLBACK, 1, !plugin.getConfig().getBoolean( "prism.queries.never-use-defaults" ) );
        if( parameters == null ) { return; }
        parameters.setProcessType( PrismProcessType.ROLLBACK );
        parameters.setStringFromRawArgs( call.getArgs(), 1 );

        // determine if defaults were used
        final ArrayList<String> defaultsUsed = parameters.getDefaultsUsed();
        String defaultsReminder = "";
        if( !defaultsUsed.isEmpty() ) {
            defaultsReminder += " using defaults:";
            for ( final String d : defaultsUsed ) {
                defaultsReminder += " " + d;
            }
        }

        call.getSender().sendMessage(MCMEPrism.messenger.playerSubduedHeaderMsg( "Preparing results..." + defaultsReminder ) );

        /**
         * Run the query itself in an async task so the lookup query isn't done
         * on the main thread
         */
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {

                final ActionsQuery aq = new ActionsQuery( plugin );
                final QueryResult results = aq.lookup( parameters, call.getSender() );
                if( !results.getActionResults().isEmpty() ) {

                    call.getSender().sendMessage(MCMEPrism.messenger.playerHeaderMsg( "Beginning rollback..." ) );

                    // Perform rollback on the main thread
                    plugin.getServer().getScheduler().runTask( plugin, new Runnable() {
                        @Override
                        public void run() {
                            final Rollback rb = new Rollback( plugin, call.getSender(), results.getActionResults(),
                                    parameters, new PrismApplierCallback() );
                            rb.apply();
                        }
                    } );

                } else {
                    call.getSender()
                            .sendMessage(MCMEPrism.messenger
                                            .playerError( "Nothing found to rollback. Try using /prism l (args) first." ) );
                }
            }
        } );
    }

    @Override
    public List<String> handleComplete(CallInfo call) {
        return PreprocessArgs.complete( call.getSender(), call.getArgs() );
    }
}