package me.botsko.mcmeprism.commands;

import us.dhmc.mcmeelixr.TypeUtils;
import me.botsko.mcmeprism.MCMEPrism;
import me.botsko.mcmeprism.actionlibs.ActionMessage;
import me.botsko.mcmeprism.actionlibs.ActionsQuery;
import me.botsko.mcmeprism.actionlibs.QueryParameters;
import me.botsko.mcmeprism.actionlibs.QueryResult;
import me.botsko.mcmeprism.actions.Handler;
import me.botsko.mcmeprism.actions.PrismProcessAction;
import me.botsko.mcmeprism.appliers.PrismApplierCallback;
import me.botsko.mcmeprism.appliers.PrismProcessType;
import me.botsko.mcmeprism.appliers.Undo;
import me.botsko.mcmeprism.commandlibs.CallInfo;
import me.botsko.mcmeprism.commandlibs.Flag;
import me.botsko.mcmeprism.commandlibs.SubHandler;
import org.bukkit.ChatColor;

import java.util.List;

public class UndoCommand implements SubHandler {

    /**
	 * 
	 */
    private final MCMEPrism plugin;

    /**
     * 
     * @param plugin
     * @return
     */
    public UndoCommand(MCMEPrism plugin) {
        this.plugin = plugin;
    }

    /**
     * Handle the command
     */
    @Override
    public void handle(CallInfo call) {

        if( call.getArgs().length > 1 ) {

            final ActionsQuery aq = new ActionsQuery( plugin );

            int record_id = 0;
            if( TypeUtils.isNumeric( call.getArg( 1 ) ) ) {
                record_id = Integer.parseInt( call.getArg( 1 ) );
                if( record_id <= 0 ) {
                    call.getPlayer()
                            .sendMessage(MCMEPrism.messenger.playerError( "Record ID must be greater than zero." ) );
                    return;
                }
            } else {
                if( call.getArg( 1 ).equals( "last" ) ) {
                    record_id = aq.getUsersLastPrismProcessId( call.getPlayer().getName() );
                }
            }

            // Invalid id
            if( record_id == 0 ) {
                call.getPlayer().sendMessage(MCMEPrism.messenger.playerError( "Either you have no last process or an invalid ID." ) );
                return;
            }

            final PrismProcessAction process = aq.getPrismProcessRecord( record_id );
            if( process == null ) {
                call.getPlayer().sendMessage(MCMEPrism.messenger.playerError( "A process does not exists with that value." ) );
                return;
            }

            // We only support this for drains
            if( !process.getProcessChildActionType().equals( "prism-drain" ) ) {
                call.getPlayer().sendMessage(MCMEPrism.messenger.playerError( "You can't currently undo anything other than a drain process." ) );
                return;
            }

            // Pull the actual block change data for this undo event
            final QueryParameters parameters = new QueryParameters();
            parameters.setWorld( call.getPlayer().getWorld().getName() );
            parameters.addActionType( process.getProcessChildActionType() );
            parameters.addPlayerName( call.getPlayer().getName() );
            parameters.setParentId( record_id );
            parameters.setProcessType( PrismProcessType.UNDO );

            // make sure the distance isn't too far away

            final QueryResult results = aq.lookup( parameters, call.getPlayer() );
            if( !results.getActionResults().isEmpty() ) {

                call.getPlayer().sendMessage(MCMEPrism.messenger.playerHeaderMsg( "Undoing..." + ChatColor.GRAY + " Abandon ship!" ) );

                final Undo rb = new Undo( plugin, call.getPlayer(), results.getActionResults(), parameters,
                        new PrismApplierCallback() );
                rb.apply();

            } else {
                call.getPlayer().sendMessage(MCMEPrism.messenger.playerError( "Nothing found to undo. Must be a problem with Prism." ) );
            }

        } else {

            // Show the list
            // Process and validate all of the arguments
            final QueryParameters parameters = new QueryParameters();
            parameters.setAllowNoRadius( true );
            parameters.addActionType( "prism-process" );
            parameters.addPlayerName( call.getPlayer().getName() );
            parameters.setLimit( 5 ); // @todo config this, and move the logic
                                      // to queryparams

            final ActionsQuery aq = new ActionsQuery( plugin );
            final QueryResult results = aq.lookup( parameters, call.getPlayer() );
            if( !results.getActionResults().isEmpty() ) {
                call.getPlayer().sendMessage(MCMEPrism.messenger.playerHeaderMsg( "Showing " + results.getTotalResults()
                                + " results. Page 1 of " + results.getTotal_pages() ) );
                call.getPlayer().sendMessage(MCMEPrism.messenger.playerSubduedHeaderMsg( "Use /prism undo [id] to reverse a process" ) );
                final List<Handler> paginated = results.getPaginatedActionResults();
                if( paginated != null ) {
                    for ( final Handler a : paginated ) {
                        final ActionMessage am = new ActionMessage( a );
                        if( parameters.allowsNoRadius() || parameters.hasFlag( Flag.EXTENDED )
                                || plugin.getConfig().getBoolean( "prism.messenger.always-show-extended" ) ) {
                            am.showExtended();
                        }
                        call.getPlayer().sendMessage(MCMEPrism.messenger.playerMsg( am.getMessage() ) );
                    }
                } else {
                    call.getPlayer()
                            .sendMessage(MCMEPrism.messenger
                                            .playerError( "Pagination can't find anything. Do you have the right page number?" ) );
                }
            } else {
                call.getPlayer().sendMessage(MCMEPrism.messenger.playerError( "Nothing found." + ChatColor.GRAY
                                + " Either you're missing something, or we are." ) );
            }
        }
    }

    @Override
    public List<String> handleComplete(CallInfo call) {
        return null;
    }
}